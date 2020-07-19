package io.github.battlepass.logger.containers;

import org.bukkit.entity.Player;

public abstract class LogContainer implements Comparable<LogContainer> {
    private final Long time;

    public static LogContainer of(String message) {
        return new BasicContainer(message);
    }

    public static LogContainer of(String message, Player player) {
        return new BasicPlayerContainer(message, player);
    };

    public LogContainer() {
        this.time = System.currentTimeMillis();
    }

    public Long getTime() {
        return this.time;
    }

    public abstract String toString();

    @Override
    public int compareTo(LogContainer container) {
        return Long.compare(container.getTime(), this.time);
    }
}
