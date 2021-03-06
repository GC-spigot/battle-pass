package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.QuestExecutor;
import io.github.battlepass.service.Services;
import me.hyfe.simplespigot.version.MultiMaterial;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Iterator;
import java.util.Map;
import java.util.function.UnaryOperator;

public class EnchantQuests extends QuestExecutor {

    public EnchantQuests(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();
        int levelCost = event.getExpLevelCost();

        Map<Enchantment, Integer> toAdd = event.getEnchantsToAdd();
        for (Enchantment enchantment : toAdd.keySet()) {
            String name = ServerVersion.isOver_V1_12() ? this.getEnchantName(enchantment) : enchantment.getName();
            int level = toAdd.get(enchantment);
            UnaryOperator<QuestResult> resultOperator = result -> {
                result.subRoot("cost", String.valueOf(levelCost));
                result.subRoot("level", String.valueOf(level));
                result.subRoot(item);
                return result.root(name);
            };
            this.execute("enchant", player, resultOperator);
            this.execute("enchant-all", player, resultOperator);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnvilEnchant(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player) || !(event.getInventory() instanceof AnvilInventory) ||
                event.getSlotType() != InventoryType.SlotType.RESULT || event.getClick() == ClickType.MIDDLE) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        AnvilInventory inventory = (AnvilInventory) event.getInventory();
        ItemStack book = inventory.getItem(1);
        if (book == null || book.getType() != MultiMaterial.ENCHANTED_BOOK.getMaterial() || (
                (event.getClick().toString().contains("SHIFT") && Services.getEmptySlotCountInInventory(player) < 1)) // If they don't shift, they pick up the item so they don't need inv space
        ) {
            return;
        }
        ItemStack enchantedItem = event.getCurrentItem();
        for (Map.Entry<Enchantment, Integer> entry : ((EnchantmentStorageMeta) book.getItemMeta()).getStoredEnchants().entrySet()) {
            String enchantName = this.getEnchantName(entry.getKey());
            UnaryOperator<QuestResult> resultOperator = result -> {
                result.subRoot("level", String.valueOf(entry.getValue()));
                result.subRoot(enchantedItem);
                return result.root(enchantName);
            };
            this.execute("enchant-anvil", ((Player) event.getWhoClicked()).getPlayer(), resultOperator);
            this.execute("enchant-all", ((Player) event.getWhoClicked()).getPlayer(), resultOperator);
        }
    }

    private String getEnchantName(Enchantment enchantment) {
        if (ServerVersion.isOver_V1_12()) {
            String possibleName = enchantment.getKey().getKey();
            return possibleName.startsWith("minecraft:") ? possibleName.split("minecraft:")[1] : possibleName;
        }
        return enchantment.getName();
    }
}
