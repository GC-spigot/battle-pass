package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class EnchantQuest extends QuestExecutor {

    public EnchantQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();
        int levelCost = event.getExpLevelCost();
        Iterator<Enchantment> iterator = event.getEnchantsToAdd().keySet().iterator();
        String enchantment;
        if (!iterator.hasNext()) {
            enchantment = "none";
        } else if (ServerVersion.isOver_V1_12()) {
            String possible = iterator.next().getKey().toString();
            enchantment = possible.startsWith("minecraft:") ? possible.split("minecraft:")[1] : possible;
        } else {
            enchantment = iterator.next().getName();
        }

        this.execute("enchant", player, result -> {
            result.subRoot("cost", String.valueOf(levelCost));
            result.subRoot(item);
            return result.root(enchantment);
        });
    }
}
