package io.github.battlepass.quests.workers.pipeline.steps;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import org.bukkit.entity.Player;

import java.math.BigInteger;

public class CompletionStep {
    private final QuestController controller;
    private final NotificationStep notificationStep;

    public CompletionStep(BattlePlugin plugin) {
        this.notificationStep = new NotificationStep(plugin);
        this.controller = plugin.getQuestController();
    }

    public void process(Player player, User user, Quest quest, BigInteger originalProgress, BigInteger progressIncrement, boolean overrideUpdate) {
        BigInteger updatedProgress;
        if (overrideUpdate) {
            BigInteger newTotalProgress = progressIncrement.min(quest.getRequiredProgress());
            updatedProgress = newTotalProgress;
            this.controller.setQuestProgress(user, quest, newTotalProgress);
        } else {
            updatedProgress = this.controller.addQuestProgress(user, quest, progressIncrement);
        }
        this.notificationStep.process(player, user, quest, originalProgress, updatedProgress);
    }
}