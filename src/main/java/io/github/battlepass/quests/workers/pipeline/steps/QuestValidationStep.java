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
import me.hyfe.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

public class QuestValidationStep {
    private final CompletionStep completionStep;
    private final BattlePlugin plugin;
    private final BattlePassApi api;
    private final QuestController controller;
    private final QuestCache questCache;
    private final Set<String> whitelistedWorlds;
    private final Set<String> blacklistedWorlds;
    private final boolean lockPreviousWeeks;
    private final boolean requirePreviousCompletion;

    public QuestValidationStep(BattlePlugin plugin) {
        Config settings = plugin.getConfig("settings");
        this.completionStep = new CompletionStep(plugin);
        this.plugin = plugin;
        this.api = plugin.getLocalApi();
        this.controller = plugin.getQuestController();
        this.questCache = plugin.getQuestCache();
        this.whitelistedWorlds = Sets.newHashSet(settings.stringList("whitelisted-worlds"));
        this.blacklistedWorlds = Sets.newHashSet(settings.stringList("blacklisted-worlds"));
        this.lockPreviousWeeks = settings.bool("current-season.unlocks.lock-previous-weeks");
        this.requirePreviousCompletion = settings.bool("current-season.unlocks.require-previous-completion");
    }

    public void process(Player player, User user, String name, int progress, QuestResult questResult, Collection<Quest> quests, boolean overrideUpdate) {
        String playerWorld = player.getWorld().getName();
        if ((!this.whitelistedWorlds.isEmpty() && !this.whitelistedWorlds.contains(playerWorld)) || this.blacklistedWorlds.contains(playerWorld)) {
            return;
        }
        for (Quest quest : quests) {
            if (!name.equalsIgnoreCase(quest.getType())) {
                continue;
            }
            Set<String> questWhitelistedWorlds = quest.getWhitelistedWorlds();
            if ((!questWhitelistedWorlds.isEmpty() && !questWhitelistedWorlds.contains(playerWorld)) || quest.getBlacklistedWorlds().contains(playerWorld)) {
                continue;
            }
            Variable subVariable = quest.getVariable();
            if (!this.controller.isQuestDone(user, quest) && questResult.isEligible(player, subVariable)) {
                int week = Category.stripWeek(quest.getCategoryId());
                boolean isDaily = week == 0;
                if (!isDaily && !user.bypassesLockedWeeks() && (week > this.api.currentWeek() || (this.lockPreviousWeeks && week < this.api.currentWeek()))) {
                    continue;
                }
                if (this.requirePreviousCompletion && !isDaily && !user.bypassesLockedWeeks()) {
                    String targetedCategoryId = "week-" + (week - 1);
                    if (this.questCache.getSubCache().asMap().containsKey(targetedCategoryId)) {
                        boolean failed = false;
                        for (Quest requiredQuest : this.questCache.getQuests(targetedCategoryId).values()) {
                            if (!this.controller.isQuestDone(user, requiredQuest)) {
                                failed = true;
                                break;
                            }
                        }
                        if (failed) {
                            continue;
                        }
                    }
                }
                int questProgress = this.controller.getQuestProgress(user, quest);
                UserQuestProgressionEvent event = new UserQuestProgressionEvent(user, quest, progress);
                this.plugin.runSync(() -> {
                    Bukkit.getPluginManager().callEvent(event);
                });
                event.ifNotCancelled(eventConsumer -> this.completionStep.process(user, quest, questProgress, eventConsumer.getProgression(), overrideUpdate));
            }
        }
    }
}