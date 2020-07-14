package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.QuestExecutor;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageQuest extends QuestExecutor {

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

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            if (CitizensAPI.getNPCRegistry().isNPC(player)) {
                return;
            }
        }
        this.execute("damage-player", player, damage, QuestResult::none, replacer -> replacer.set("damage", damage));
    }
}
