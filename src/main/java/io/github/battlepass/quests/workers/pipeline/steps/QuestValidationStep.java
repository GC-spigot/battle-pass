package io.github.battlepass.quests.workers.pipeline.steps;

import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.api.events.user.UserQuestProgressionEvent;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.enums.Category;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.objects.quests.variable.Variable;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.pipeline.processors.AntiAbuseProcessor;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.service.Locks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class QuestValidationStep {
    private final AntiAbuseProcessor antiAbuseProcessor;
    private final CompletionStep completionStep;
    private final BattlePlugin plugin;
    private final BattlePassApi api;
    private final QuestController controller;
    private final QuestCache questCache;
    private final Set<String> whitelistedWorlds;
    private final Set<String> blacklistedWorlds;
    private final boolean lockPreviousWeeks;
    private final boolean requirePreviousCompletion;
    private final boolean disableDailiesOnSeasonEnd;
    private final boolean disableNormalsOnSeasonEnd;
    private final ReentrantLock questLock = Locks.newReentrantLock();

    public QuestValidationStep(BattlePlugin plugin) {
        Config settings = plugin.getConfig("settings");
        this.antiAbuseProcessor = new AntiAbuseProcessor(plugin);
        this.completionStep = new CompletionStep(plugin);
        this.plugin = plugin;
        this.api = plugin.getLocalApi();
        this.controller = plugin.getQuestController();
        this.questCache = plugin.getQuestCache();
        this.whitelistedWorlds = Sets.newHashSet(settings.stringList("whitelisted-worlds"));
        this.blacklistedWorlds = Sets.newHashSet(settings.stringList("blacklisted-worlds"));
        this.lockPreviousWeeks = settings.bool("current-season.unlocks.lock-previous-weeks");
        this.requirePreviousCompletion = settings.bool("current-season.unlocks.require-previous-completion");
        this.disableDailiesOnSeasonEnd = settings.bool("season-finished.stop-daily-quests");
        this.disableNormalsOnSeasonEnd = settings.bool("season-finished.stop-other-quests");
    }

    public void processCompletion(Player player, User user, String name, BigInteger progress, QuestResult questResult, Collection<Quest> quests, boolean overrideUpdate) {
        String playerWorld = player.getWorld().getName();
        boolean seasonEnded = this.api.hasSeasonEnded();
        if (seasonEnded && this.disableDailiesOnSeasonEnd && this.disableNormalsOnSeasonEnd) {
            return;
        }
        if ((!this.whitelistedWorlds.isEmpty() && !this.whitelistedWorlds.contains(playerWorld)) || this.blacklistedWorlds.contains(playerWorld)) {
            return;
        }
        this.antiAbuseProcessor.applyMeasures(player, user, quests, name, progress, questResult);
        for (Quest quest : quests) {
            if (!name.equalsIgnoreCase(quest.getType())) {
                continue;
            }
            if (seasonEnded && (quest.getCategoryId().contains("daily") && this.disableDailiesOnSeasonEnd) || (quest.getCategoryId().contains("week") && this.disableNormalsOnSeasonEnd)) {
                continue;
            }
            Set<String> questWhitelistedWorlds = quest.getWhitelistedWorlds();
            if ((!questWhitelistedWorlds.isEmpty() && !questWhitelistedWorlds.contains(playerWorld)) || quest.getBlacklistedWorlds().contains(playerWorld)) {
                continue;
            }
            this.questLock.lock();
            try {
                this.proceed(player, user, quest, progress, questResult, overrideUpdate);
            } finally {
                this.questLock.unlock();
            }
        }
    }

    public boolean proceed(Player player, User user, Quest quest, BigInteger progress, QuestResult questResult, boolean overrideUpdate) {
        BigInteger originalProgress = this.controller.getQuestProgress(user, quest);
        if (overrideUpdate && originalProgress.compareTo(progress) == 0) {
            return false;
        }
        if (!this.isQuestValid(player, user, quest, progress, overrideUpdate) || this.controller.isQuestDone(user, quest)) {
            return false;
        }
        Variable subVariable = quest.getVariable();
        if (questResult == null || questResult.isEligible(player, subVariable)) {
            UserQuestProgressionEvent event = new UserQuestProgressionEvent(user, quest, progress);
            this.plugin.runSync(() -> {
                Bukkit.getPluginManager().callEvent(event);
            });
            event.ifNotCancelled(eventConsumer -> this.completionStep.process(player, user, quest, originalProgress, eventConsumer.getAddedProgress(), overrideUpdate));
            return true;
        }
        return false;
    }

    public boolean isQuestValid(Player player, User user, Quest quest, BigInteger progress, boolean overrideUpdate) {
        String playerWorld = player.getWorld().getName();
        boolean seasonEnded = this.api.hasSeasonEnded();
        if (seasonEnded && this.disableDailiesOnSeasonEnd && this.disableNormalsOnSeasonEnd) {
            return false;
        }
        if ((!this.whitelistedWorlds.isEmpty() && !this.whitelistedWorlds.contains(playerWorld)) || this.blacklistedWorlds.contains(playerWorld)) {
            return false;
        }
        if (seasonEnded && (quest.getCategoryId().contains("daily") && this.disableDailiesOnSeasonEnd) || (quest.getCategoryId().contains("week") && this.disableNormalsOnSeasonEnd)) {
            return false;
        }
        Set<String> questWhitelistedWorlds = quest.getWhitelistedWorlds();
        if ((!questWhitelistedWorlds.isEmpty() && !questWhitelistedWorlds.contains(playerWorld)) || quest.getBlacklistedWorlds().contains(playerWorld)) {
            return false;
        }
        BigInteger originalProgress = this.controller.getQuestProgress(user, quest);
        if (overrideUpdate && originalProgress.compareTo(progress) <= 0) { // since 0 == equal and -1 == less
            return false;
        }
        if (this.controller.isQuestDone(user, quest)) {
            return false;
        }
        String exclusiveTo = quest.getExclusiveTo();
        if (exclusiveTo != null && !exclusiveTo.equalsIgnoreCase(user.getPassId())) {
            return false;
        }
        int week = Category.stripWeek(quest.getCategoryId());
        boolean isDaily = week == 0;
        if (!isDaily && !user.bypassesLockedWeeks() && (week > this.api.currentWeek() || (this.lockPreviousWeeks && week < this.api.currentWeek()))) {
            return false;
        }
        if (this.requirePreviousCompletion && !isDaily && !user.bypassesLockedWeeks()) {
            int previousWeek = week - 1;
            if (previousWeek > 1) {
                while (previousWeek > 0) {
                    if (!this.controller.isWeekDone(user, previousWeek)) {
                        return false;
                    }
                    previousWeek--;
                }
            }
        }
        return true;
    }
}