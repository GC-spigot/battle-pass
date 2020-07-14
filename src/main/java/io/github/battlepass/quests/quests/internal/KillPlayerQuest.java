package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillPlayerQuest extends QuestExecutor {

    public KillPlayerQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player victim = event.getEntity();

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
            if (npcRegistry.isNPC(killer) || npcRegistry.isNPC(victim)) {
                return;
            }
        }
        this.execute("kill-player", killer, result -> result.root(victim.getName()), replacer -> replacer.set("victim", victim.getName()));
    }
}
