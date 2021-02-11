package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class RideMobQuest extends QuestContainer {

    public RideMobQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getVehicle() == null || (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())) {
            return;
        }
        Entity entity = player.getVehicle();
        String entityType = entity.getType().toString();
        this.executionBuilder("ride-mob")
                .player(player)
                .root(entityType)
                .progressSingle()
                .buildAndExecute();
    }
}
