package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementQuests extends QuestContainer {

    public MovementQuests(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if ((event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())) {
            return;
        }
        Material blockAtLocation = player.getLocation().getBlock().getType();
        this.executionBuilder("move")
                .player(player)
                .progressSingle()
                .buildAndExecute();
        if (blockAtLocation.toString().toLowerCase().contains("water")) {
            this.executionBuilder("swim")
                    .player(player)
                    .progressSingle()
                    .buildAndExecute();
            return;
        }
        if (ServerVersion.isOver_V1_12() && player.isGliding()) {
            this.executionBuilder("glide")
                    .player(player)
                    .progressSingle()
                    .buildAndExecute();
            return;
        }
        if (player.isFlying()) {
            this.executionBuilder("fly")
                    .player(player)
                    .progressSingle()
                    .buildAndExecute();
            return;
        }
        this.executionBuilder("ground-move")
                .player(player)
                .progressSingle()
                .buildAndExecute();
        this.executionBuilder(player.isSneaking() ? "sneak" : player.isSprinting() ? "sprint" : "walk")
                .player(player)
                .progressSingle()
                .buildAndExecute();
    }
}
