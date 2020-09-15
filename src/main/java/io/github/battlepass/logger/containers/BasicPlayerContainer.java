package io.github.battlepass.logger.containers;

import org.bukkit.entity.Player;

public class BasicPlayerContainer extends BasicContainer {
    private final String playerName;

    public BasicPlayerContainer(String message, Player player) {
        super(message);
        this.playerName = player.getName();
    }

    public String getPlayerName() {
        return this.playerName;
    }

    @Override
    public String toString() {
        return String.format(super.toString(), this.playerName);
    }
}
