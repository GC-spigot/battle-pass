package io.github.battlepass.quests.quests.internal;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

public class SmeltQuest extends QuestContainer {

    public SmeltQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (!event.getInventory().getType().toString().contains("FURNACE")
                || !event.getSlotType().equals(InventoryType.SlotType.RESULT)
                || cursorItem == null
                || currentItem == null) {
            return;
        }
        int amount = currentItem.getAmount();

        if (event.isShiftClick()) {
            int result = this.hasRoomForItem(player, currentItem);
            if (result == -420 || result == 0) {
                return;
            }
            amount = result;
        } else if (cursorItem.getType().equals(currentItem.getType()) && cursorItem.getAmount() + currentItem.getAmount() > currentItem.getMaxStackSize()) {
            return;
        }

        this.executionBuilder("smelt")
                .player(player)
                .progress(amount)
                .root(currentItem)
                .progressSingle()
                .buildAndExecute();
    }

    private int hasRoomForItem(Player player, ItemStack checkItem) {
        if (checkItem == null) {
            return 0;
        }
        int result = Math.min(this.getMaterialSpace(player, checkItem.getType()), checkItem.getAmount());
        if (player.getInventory().firstEmpty() != -1) {
            if (checkItem.getMaxStackSize() == 1) {
                int emptySlots = 0;
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if (itemStack == null) {
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
            return -420; // -420 = false
        }
    }

    private int getMaterialSpace(Player player, Material material) {
        AtomicInteger freeSpace = new AtomicInteger();
        for (ItemStack itemStack : player.getInventory()) {
            freeSpace.addAndGet(itemStack == null ?
                    material.getMaxStackSize() :
                    itemStack.getType().equals(material) ?
                            material.getMaxStackSize() - itemStack.getAmount() :
                            0);
        }
        return freeSpace.get();
    }
}
