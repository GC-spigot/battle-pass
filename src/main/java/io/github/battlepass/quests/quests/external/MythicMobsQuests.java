package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class MythicMobsQuests extends ExternalQuestExecutor {

    public MythicMobsQuests(BattlePlugin plugin) {
        super(plugin, "mythicmobs");
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobDeath(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getKiller();
        String mobTypeName = event.getMobType().getInternalName();
        this.execute("kill_mob", player, result -> {
            return result.root(mobTypeName);
        });
    }
}
