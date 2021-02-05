package io.github.battlepass.quests.workers.pipeline.processors;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.LogContainer;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.service.executor.QuestExecution;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Collection;

public class AntiAbuseProcessor {
    private final QuestController controller;
    private final DebugLogger debugLogger;

    public AntiAbuseProcessor(BattlePlugin plugin) {
        this.controller = plugin.getQuestController();
        this.debugLogger = plugin.getDebugLogger();
    }

    public void applyMeasures(QuestExecution questExecution, Collection<Quest> quests) {
        String questType = questExecution.getQuestType();
        Player player = questExecution.getPlayer();
        User user = questExecution.getUser();
        for (Quest quest : quests) {
            if (!quest.isAntiAbuse()
                    || this.controller.isQuestDone(user, quest)
                    || (!questType.equals("block-break") && !questType.equals("block-place"))
                    || questType.equalsIgnoreCase(quest.getType())
                    || !questExecution.getQuestResult().isEligible(player, quest.getVariable())) {
                return;
            }
            this.debugLogger.log(LogContainer.of("Anti abuse measures applied for player %battlepass-player% on quest " + quest.getCategoryId() + ":" + quest.getId(), player));
            BigInteger currentProgress = this.controller.getQuestProgress(user, quest);
            if (currentProgress.compareTo(BigInteger.ZERO) > 0) {
                this.controller.setQuestProgress(user, quest, currentProgress.subtract(questExecution.getProgress()).max(BigInteger.ZERO).min(quest.getRequiredProgress()));
            }
        }
    }
}
