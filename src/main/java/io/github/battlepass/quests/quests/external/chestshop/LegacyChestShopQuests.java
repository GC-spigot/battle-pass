package io.github.battlepass.quests.quests.external.chestshop;

import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class LegacyChestShopQuests extends ExternalQuestExecutor {

    public LegacyChestShopQuests(BattlePlugin plugin) {
        super(plugin, "chestshop");
    }

    @EventHandler(ignoreCancelled = true)
    public void afterShopCreate(ShopCreatedEvent event) {
        Player player = event.getPlayer();

        this.execute("create", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTransaction(TransactionEvent event) {
        Player client = event.getClient();
        Player shopOwner = Bukkit.getPlayer(event.getOwnerAccount().getUuid());
        int spentAmount = (int) event.getPrice();

        if (client == null || shopOwner == null) {
            return;
        }
        if (event.getTransactionType() == TransactionEvent.TransactionType.BUY) {
            this.execute("buy", client, result -> result.root(shopOwner.getName()));
            this.execute("sell", shopOwner, result -> result.root(client.getName()));
            this.execute("spend", client, spentAmount, result -> result.root(shopOwner.getName()));
            this.execute("profit", shopOwner, spentAmount, result -> result.root(client.getName()));
        } else {
            this.execute("buy", shopOwner, result -> result.root(client.getName()));
            this.execute("sell", client, result -> result.root(shopOwner.getName()));
            this.execute("spend", shopOwner, spentAmount, result -> result.root(client.getName()));
            this.execute("profit", client, spentAmount, result -> result.root(shopOwner.getName()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void afterShopCreate(ShopDestroyedEvent event) {
        Player player = event.getDestroyer();

        this.execute("destroy", player, QuestResult::none);
    }
}
