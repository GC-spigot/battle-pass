package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class RideMobQuest extends QuestExecutor {

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
        String entityName = entity.getCustomName();
        this.execute("ride-mob", player, 1, result -> {
            return result.root(entityType);
        }, replacer -> {
            replacer.set("entity", entityType);
            replacer.set("name", entityName);
            return replacer;
        });
    }
}
