package io.github.battlepass.logger.containers;

import io.github.battlepass.quests.service.executor.QuestExecution;
import org.bukkit.entity.Player;

public class QuestExecutionContainer extends LogContainer {
    private final QuestExecution questExecution;

    public QuestExecutionContainer(QuestExecution questExecution) {
        this.questExecution = questExecution;
    }

    public Player getPlayer() {
        return this.questExecution.getPlayer();
    }

    @Override
    public String toString() {
        return "(PIPELINE) Quest entered ".concat(this.questExecution.toString());
    }
}
