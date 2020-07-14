package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ItemBreakQuest extends QuestExecutor {

    public ItemBreakQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemBreak(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack brokenItem = event.getBrokenItem();

        this.execute("item-break", player, result -> result.root(brokenItem), replacer -> replacer.set("item", brokenItem.getType()));
    }
}
