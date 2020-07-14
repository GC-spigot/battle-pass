package io.github.battlepass.quests.quests.external;

import com.spawnchunk.auctionhouse.events.AuctionItemEvent;
import com.spawnchunk.auctionhouse.events.ItemAction;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AuctionHouseKludgeQuests extends ExternalQuestExecutor {

    public AuctionHouseKludgeQuests(BattlePlugin plugin) {
        super(plugin, "auctionhouse_kludge");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAuctionItemInteract(AuctionItemEvent event) {
        Player seller = event.getSeller().getPlayer();
        int price = Math.round(event.getPrice());
        ItemAction action = event.getItemAction();
        Material item = event.getItem().getType();

        if (seller == null || action == null) {
            return;
        }
        switch (action) {
            case ITEM_LISTED:
                this.execute("list", seller, result -> {
                    return result.root(item.toString());
                });
                break;
            case ITEM_SOLD:
                Player buyer = event.getBuyer().getPlayer();
                if (buyer == null) {
                    return;
                }
                this.execute("buy", buyer, result -> {
                    return result.root(item.toString());
                });

                this.execute("profit", seller, price, result -> {
                    return result.root(item.toString());
                });

                this.execute("spend", buyer, price, result -> {
                    return result.root(item.toString());
                });
                break;
        }
    }
}
