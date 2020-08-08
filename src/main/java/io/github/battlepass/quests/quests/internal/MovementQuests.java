package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.QuestExecutor;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementQuests extends QuestExecutor {

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
        this.execute("move", player, QuestResult::none);
        if (blockAtLocation.toString().toLowerCase().contains("water")) {
            this.execute("swim", player, QuestResult::none);
            return;
        }
        if (ServerVersion.isOver_V1_12() && player.isGliding()) {
            this.execute("glide", player, QuestResult::none);
            return;
        }
        if (player.isFlying()) {
            this.execute("fly", player, QuestResult::none);
            return;
        }
        this.execute("ground-move", player, QuestResult::none);
        this.execute(player.isSneaking() ? "sneak" : player.isSprinting() ? "sprint" : "walk", player, QuestResult::none);
    }
}
