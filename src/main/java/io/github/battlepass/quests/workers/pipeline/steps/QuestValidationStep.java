package io.github.battlepass.quests.workers.pipeline.steps;

import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.api.events.user.UserQuestProgressionEvent;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.enums.Category;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.LogContainer;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.objects.quests.variable.Variable;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.service.executor.QuestExecution;
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
    private final QuestCompletionStep completionStep;
    private final BattlePlugin plugin;
    private final BattlePassApi api;
    private final DebugLogger debugLogger;
    private final QuestController controller;
    private final Set<String> whitelistedWorlds;
    private final Set<String> blacklistedWorlds;
    private final boolean lockPreviousWeeks;
    private final boolean requirePreviousCompletion;
    private final boolean disableDailiesOnSeasonEnd;
    private final boolean disableNormalsOnSeasonEnd;
    private final ReentrantLock questLock = Locks.newReentrantLock();


    // TODO at some point this all needs to be cleaned up. Things are repeated, bad bad.
    public QuestValidationStep(BattlePlugin plugin) {
        Config settings = plugin.getConfig("settings");
        this.antiAbuseProcessor = new AntiAbuseProcessor(plugin);
        this.completionStep = new QuestCompletionStep(plugin);
        this.plugin = plugin;
        this.api = plugin.getLocalApi();
        this.controller = plugin.getQuestController();
        this.whitelistedWorlds = Sets.newHashSet(settings.stringList("whitelisted-worlds"));
        this.blacklistedWorlds = Sets.newHashSet(settings.stringList("blacklisted-worlds"));
        this.lockPreviousWeeks = settings.bool("current-season.unlocks.lock-previous-weeks");
        this.requirePreviousCompletion = settings.bool("current-season.unlocks.require-previous-completion");
        this.disableDailiesOnSeasonEnd = settings.bool("season-finished.stop-daily-quests");
        this.disableNormalsOnSeasonEnd = settings.bool("season-finished.stop-other-quests");
        this.debugLogger = plugin.getDebugLogger();
    }

    public void processCompletion(QuestExecution questExecution, Collection<Quest> quests) {
        Player player = questExecution.getPlayer();
        String playerWorld = player.getWorld().getName();
        if (this.areServerQuestsBlocked()) {
            this.debugLogger.log(LogContainer.of("(PIPELINE) Didn't progress for %battlepass-player% as season has ended and dailies & normals are disabled."));
            return;
        }
        if (this.isWorldServerBlocked(playerWorld)) {
            return;
        }
        this.antiAbuseProcessor.applyMeasures(questExecution, quests);
        for (Quest quest : quests) {
            if (!questExecution.getQuestType().equalsIgnoreCase(quest.getType())) {
                continue;
            }
            if (this.isQuestSeasonEndBlocked(quest)) {
                this.debugLogger.log(LogContainer.of("(PIPELINE) Didn't progress for %battlepass-player% and quests of this type are disabled on season end."));
                continue;
            }
            this.questLock.lock();
            try {
                this.proceed(questExecution, quest);
            } finally {
                this.questLock.unlock();
            }
        }
    }

    public boolean proceed(QuestExecution questExecution, Quest quest) {
        BigInteger originalProgress = this.controller.getQuestProgress(questExecution.getUser(), quest);
        if (!this.isQuestValid(questExecution, quest)) {
            return false;
        }
        Variable subVariable = quest.getVariable();
        QuestResult questResult = questExecution.getQuestResult();
        if (questResult == null || questResult.isEligible(questExecution.getPlayer(), subVariable)) {
            UserQuestProgressionEvent event = new UserQuestProgressionEvent(questExecution, quest);
            this.plugin.runSync(() -> {
                Bukkit.getPluginManager().callEvent(event);
            });
            event.ifNotCancelled(eventConsumer -> this.completionStep.process(questExecution, quest, originalProgress, eventConsumer.getAddedProgress()));
            return true;
        }
        return false;
    }

    public boolean isQuestPrimarilyValid(User user, Quest quest, BigInteger progress, boolean overrideUpdate) {
        if (this.areServerQuestsBlocked() ||
                this.isQuestSeasonEndBlocked(quest) ||
                this.controller.isQuestDone(user, quest) ||
                (overrideUpdate && this.isProgressIdentical(quest, user, progress)) ||
                this.isPassTypeQuestBlocked(quest, user)
        ) return false;
        int week = Category.stripWeek(quest.getCategoryId());
        boolean isDaily = week == 0;
        if (!isDaily && !user.bypassesLockedWeeks() && (week > this.api.getCurrentWeek() || (this.lockPreviousWeeks && week < this.api.getCurrentWeek()))) {
            return false;
        }
        return !this.requirePreviousCompletion || isDaily || user.bypassesLockedWeeks() || !this.isPreviousWeekBlocked(user, week);
    }

    public boolean isQuestValid(QuestExecution questExecution, Quest quest) {
        if (!this.isQuestPrimarilyValid(questExecution.getUser(), quest, questExecution.getProgress(), questExecution.shouldOverrideUpdate())) {
            return false;
        }
        Player player = questExecution.getPlayer();
        String playerWorld = player.getWorld().getName();
        return !this.isWorldQuestBlocked(quest, playerWorld);
    }

    private boolean areServerQuestsBlocked() {
        return this.api.hasSeasonEnded() && this.disableDailiesOnSeasonEnd && this.disableNormalsOnSeasonEnd;
    }

    private boolean isWorldServerBlocked(String worldName) {
        return (!this.whitelistedWorlds.isEmpty() && !this.whitelistedWorlds.contains(worldName)) || this.blacklistedWorlds.contains(worldName);
    }

    private boolean isQuestSeasonEndBlocked(Quest quest) {
        return this.api.hasSeasonEnded() && (
                (quest.getCategoryId().contains("daily") && this.disableDailiesOnSeasonEnd)
                        || (quest.getCategoryId().contains("week") && this.disableNormalsOnSeasonEnd)
        );
    }

    private boolean isWorldQuestBlocked(Quest quest, String worldName) {
        return quest.getBlacklistedWorlds().contains(worldName) ||
                (!quest.getWhitelistedWorlds().isEmpty() && !quest.getWhitelistedWorlds().contains(worldName));
    }

    private boolean isPassTypeQuestBlocked(Quest quest, User user) {
        return quest.getExclusiveTo() != null && !quest.getExclusiveTo().equalsIgnoreCase(user.getPassId());
    }

    private boolean isProgressIdentical(Quest quest, User user, BigInteger progress) {
        return this.controller.getQuestProgress(user, quest).compareTo(progress) == 0; // 0 is equal to
    }

    private boolean isPreviousWeekBlocked(User user, int week) {
        int previousWeek = week - 1;
        while (previousWeek > 0) {
            if (!this.controller.isWeekDone(user, previousWeek)) {
                return true;
            }
            previousWeek--;
        }
        return false;
    }
}