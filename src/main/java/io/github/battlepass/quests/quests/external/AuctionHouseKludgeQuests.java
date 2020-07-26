package io.github.battlepass.quests.quests.external;

import com.spawnchunk.auctionhouse.events.AuctionItemEvent;
import com.spawnchunk.auctionhouse.events.ItemAction;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class AuctionHouseKludgeQuests extends ExternalQuestExecutor {

    public AuctionHouseKludgeQuests(BattlePlugin plugin) {
        super(plugin, "auctionhouse_kludge");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAuctionItemInteract(AuctionItemEvent event) {
        Player seller = event.getSeller().getPlayer();
        int price = Math.round(event.getPrice());
        ItemAction action = event.getItemAction();
        ItemStack itemStack = event.getItem();

        if (seller == null || action == null) {
            return;
        }
        switch (action) {
            case ITEM_LISTED:
                this.execute("list", seller, itemStack.getAmount(), result -> result.root(itemStack));
                this.execute("list_singular", seller, result -> result.root(itemStack));
                break;
            case ITEM_SOLD:
                Player buyer = event.getBuyer().getPlayer();
                if (buyer == null) {
                    return;
                }

                this.execute("buy_singular", buyer, result -> result.root(itemStack));
                this.execute("sell_singular", seller, result -> result.root(itemStack));
                this.execute("buy", buyer, itemStack.getAmount(), result -> result.root(itemStack));
                this.execute("sell", seller, itemStack.getAmount(), result -> result.root(itemStack));
                this.execute("profit", seller, price, result -> result.root(itemStack));
                this.execute("spend", buyer, price, result -> result.root(itemStack));
                break;
        }
    }
}
