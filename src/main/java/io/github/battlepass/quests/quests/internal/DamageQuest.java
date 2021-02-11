package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageQuest extends QuestContainer {

    public DamageQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();
        int damage = (int) Math.round(event.getDamage());

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens") && CitizensAPI.getNPCRegistry().isNPC(player)) {
            return;
        }
        this.executionBuilder("damage-player")
                .player(player)
                .progress(damage)
                .buildAndExecute();
    }
}
