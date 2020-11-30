package io.github.battlepass.quests.quests.external;

import com.nisovin.shopkeepers.api.events.ShopkeeperOpenUIEvent;
import com.nisovin.shopkeepers.api.events.ShopkeeperTradeEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ShopkeepersQuests extends ExternalQuestExecutor {

    public ShopkeepersQuests(BattlePlugin plugin) {
        super(plugin, "shopkeepers");
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopkeeperTrade(ShopkeeperTradeEvent event) {
        Player player = event.getPlayer();
        String keeperUuid = event.getShopkeeper().getUniqueId().toString().toLowerCase();

        this.execute("trade", player, result -> result.root(keeperUuid));
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopkeeperOpen(ShopkeeperOpenUIEvent event) {
        Player player = event.getPlayer();
        String keeperUuid = event.getShopkeeper().getUniqueId().toString().toLowerCase();

        this.execute("open", player, result -> result.root(keeperUuid));
    }
}
