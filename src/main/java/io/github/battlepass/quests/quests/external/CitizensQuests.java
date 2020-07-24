package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CitizensQuests extends ExternalQuestExecutor {

    public CitizensQuests(BattlePlugin plugin) {
        super(plugin, "citizens");
    }

    @EventHandler(ignoreCancelled = true)
    public void onNpcRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        String name = event.getNPC().getName();

        this.execute("click", player, result -> result.root(name));
    }

    @EventHandler(ignoreCancelled = true)
    public void onNpcDeath(NPCDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        String name = event.getNPC().getName();
        int damage = (int) event.getDamage();

        this.execute("damage", player, damage, result -> result.root(name));
        if (event.getNPC().getEntity().isDead()) {
            this.execute("kill", player, result -> result.root(name));
        }
    }
}
