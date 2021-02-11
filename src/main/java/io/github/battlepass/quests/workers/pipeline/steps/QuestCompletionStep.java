package io.github.battlepass.quests.workers.pipeline.steps;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.service.executor.QuestExecution;

import java.math.BigInteger;

public class QuestCompletionStep {
    private final QuestController controller;
    private final NotificationStep notificationStep;

    public QuestCompletionStep(BattlePlugin plugin) {
        this.notificationStep = new NotificationStep(plugin);
        this.controller = plugin.getQuestController();
    }

    public void process(QuestExecution questExecution, Quest quest, BigInteger originalProgress, BigInteger progressIncrement) {
        User user = questExecution.getUser();
        BigInteger updatedProgress;
        if (questExecution.shouldOverrideUpdate()) {
            BigInteger newTotalProgress = progressIncrement.min(quest.getRequiredProgress());
            updatedProgress = newTotalProgress;
            this.controller.setQuestProgress(user, quest, newTotalProgress);
        } else {
            updatedProgress = this.controller.addQuestProgress(user, quest, progressIncrement);
        }
        this.notificationStep.process(questExecution.getPlayer(), user, quest, originalProgress, updatedProgress);
    }
}