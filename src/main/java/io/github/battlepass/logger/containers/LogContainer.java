package io.github.battlepass.logger.containers;

import io.github.battlepass.quests.service.executor.QuestExecution;
import org.bukkit.entity.Player;

public abstract class LogContainer implements Comparable<LogContainer> {
    private final long time;

    public LogContainer() {
        this.time = System.currentTimeMillis();
    }

    public static LogContainer of(String message) {
        return new BasicContainer(message);
    }

    public static LogContainer of(String message, Player player) {
        return new BasicPlayerContainer(message, player);
    }

    public static LogContainer of(QuestExecution questExecution) {
        return new QuestExecutionContainer(questExecution);
    }

    public long getTime() {
        return this.time;
    }

    public abstract String toString();

    @Override
    public int compareTo(LogContainer container) {
        return Long.compare(container.getTime(), this.time);
    }
}
