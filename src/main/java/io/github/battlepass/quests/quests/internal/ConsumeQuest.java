package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import me.hyfe.simplespigot.version.MultiMaterial;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ConsumeQuest extends QuestExecutor {

    public ConsumeQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        this.execute("consume", player, result -> result.root(itemStack), replacer -> replacer.set("item", itemStack.getType()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null || !block.getType().equals(MultiMaterial.CAKE.getMaterial()) || player.getFoodLevel() >= 20) {
            return;
        }
        this.execute("consume", player, result -> result.root(block), replacer -> replacer.set("item", block.getType()));
    }
}
