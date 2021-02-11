package io.github.battlepass.quests.quests.internal;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftQuest extends QuestContainer {

    public CraftQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();

        if (itemStack == null) {
            return;
        }
        int amount = itemStack.getAmount();
        if (event.isShiftClick()) {
            int roomResult = this.roomCheck(player, itemStack, event.getRecipe(), event.getInventory());
            if (roomResult == -420 || roomResult == 0) {
                return;
            }
            amount = Math.min(roomResult, itemStack.getType().getMaxStackSize());
        }
        this.executionBuilder("craft")
                .player(player)
                .progress(amount)
                .root(itemStack).buildAndExecute();
    }

    private int roomCheck(Player player, ItemStack checkItem, Recipe recipe, CraftingInventory inventory) {
        if (checkItem == null) {
            return 0;
        }
        int result = recipe.getResult().getAmount() * this.craftCount(inventory.getMatrix());
        if (player.getInventory().firstEmpty() != -1) {
            if (checkItem.getMaxStackSize() == 1) {
                int emptySlots = 0;
                for (int i = 0; i < 36; i++) {
                    ItemStack item = player.getInventory().getItem(i);
                    if (item == null || item.getType().equals(Material.AIR)) {
                        emptySlots++;
                    }
                }
                return emptySlots;
            } else {
                return result;
            }
        }
        Multiset<Integer> amounts = HashMultiset.create();
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || !itemStack.getType().equals(checkItem.getType()) || itemStack.getAmount() >= itemStack.getMaxStackSize()) {
                continue;
            }
            amounts.add(itemStack.getAmount());
        }
        if (amounts.size() > 0) {
            int totalSum = 0;
            for (int amount : amounts) {
                totalSum += amount;
            }
            return Math.min(amounts.size() * 64 - totalSum, result);
        } else {
            return -420; // -420 = false, -69 = true
        }
    }

    private int craftCount(ItemStack[] itemArray) {
        int itemsChecked = 0;
        int amountToCraft = 1;
        for (ItemStack itemStack : itemArray) {
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                continue;
            }
            amountToCraft = itemsChecked == 0 ? itemStack.getAmount() : Math.min(amountToCraft, itemStack.getAmount());
            itemsChecked++;
        }
        return amountToCraft;
    }
}
