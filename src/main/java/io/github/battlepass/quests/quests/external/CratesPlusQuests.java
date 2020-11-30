package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import plus.crates.Events.CrateOpenEvent;

public class CratesPlusQuests extends ExternalQuestExecutor {

    public CratesPlusQuests(BattlePlugin plugin) {
        super(plugin, "cratesplus");
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrateOpen(CrateOpenEvent event) {
        Player player = event.getPlayer();
        String crateName = event.getCrate().getName();
        Location location = event.getBlockLocation();
        if (crateName == null || location == null) {
            return;
        }
        this.execute("open", player, result -> result.root(crateName));
    }
}
