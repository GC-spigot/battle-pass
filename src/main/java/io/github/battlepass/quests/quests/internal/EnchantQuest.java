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
        int enchantLevel;
        if (!iterator.hasNext()) {
            enchantment = "none";
            enchantLevel = 0;
        } else if (ServerVersion.isOver_V1_12()) {
            Enchantment enchant = iterator.next();
            String possibleName = enchant.getKey().getNamespace();
            enchantment = possibleName.startsWith("minecraft:") ? possibleName.split("minecraft:")[1] : possibleName;
            enchantLevel = event.getEnchantsToAdd().get(enchant);
        } else {
            Enchantment enchant = iterator.next();
            enchantLevel = event.getEnchantsToAdd().get(enchant);
            enchantment = enchant.getName();
        }

        this.execute("enchant", player, result -> {
            result.subRoot("cost", String.valueOf(levelCost));
            result.subRoot("level", String.valueOf(enchantLevel));
            result.subRoot(item);
            return result.root(enchantment);
        });
    }
}
