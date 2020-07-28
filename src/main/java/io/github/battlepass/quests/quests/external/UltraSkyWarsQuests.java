package io.github.battlepass.quests.quests.external;

import io.github.Leonardo0013YT.UltraSkyWars.api.events.GameFinishEvent;
import io.github.Leonardo0013YT.UltraSkyWars.api.events.GameStartEvent;
import io.github.Leonardo0013YT.UltraSkyWars.api.events.GameWinEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class UltraSkyWarsQuests extends ExternalQuestExecutor {

    public UltraSkyWarsQuests(BattlePlugin plugin) {
        super(plugin, "ultraskywars");
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameStart(GameStartEvent event) {
        String mapName = event.getGame().getName();
        for (Player player : event.getPlayers()) {
            this.execute("start", player, result -> result.root(mapName));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameEnd(GameFinishEvent event) {
        String mapName = event.getGame().getName();
        for (Player player : event.getPlayers()) {
            this.execute("finish", player, result -> result.root(mapName));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameWin(GameWinEvent event) {
        String mapName = event.getGame().getName();
        for (Player player : event.getWinner().getMembers()) {
            this.execute("win", player, result -> result.root(mapName));
        }
    }
}
