package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import net.brcdev.shopgui.event.ShopPostTransactionEvent;
import net.brcdev.shopgui.shop.ShopItem;
import net.brcdev.shopgui.shop.ShopManager;
import net.brcdev.shopgui.shop.ShopTransactionResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ShopGuiPlusQuests extends ExternalQuestExecutor {

    public ShopGuiPlusQuests(BattlePlugin plugin) {
        super(plugin, "shopguiplus");
    }

    @EventHandler(ignoreCancelled = true)
    public void afterShopTransaction(ShopPostTransactionEvent event) {
        /*ShopTransactionResult transaction = event.getResult();
        if (transaction.getResult() != ShopTransactionResult.ShopTransactionResultType.SUCCESS) {
            return;
        }
        Player player = transaction.getPlayer();
        ShopManager.ShopAction shopAction = transaction.getShopAction();
        ShopItem shopItem = transaction.getShopItem();
        String shopId = shopItem.getId();

        if (shopAction == ShopManager.ShopAction.BUY) {
            super.execute("buy", player, );
        } else {

        }*/
    }
}
