package io.github.battlepass.quests.quests.external;

import com.hazebyte.crate.api.event.RewardReceiveEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CrateReloadedQuests extends ExternalQuestExecutor {

    public CrateReloadedQuests(BattlePlugin plugin) {
        super(plugin, "cratereloaded");
    }

    @EventHandler(ignoreCancelled = true)
    public void onRewardReceive(RewardReceiveEvent event) {
        Player player = event.getPlayer();
        String crateName = event.getCrate().getCrateName();

        this.execute("open", player, result -> result.root(crateName));
    }
}
