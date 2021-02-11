package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import me.hyfe.simplespigot.version.MultiMaterial;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ConsumeQuest extends QuestContainer {

    public ConsumeQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        this.executionBuilder("consume")
                .player(player)
                .root(itemStack)
                .progressSingle()
                .buildAndExecute();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null || !block.getType().equals(MultiMaterial.CAKE.getMaterial()) || player.getFoodLevel() >= 20) {
            return;
        }
        this.executionBuilder("consume")
                .player(player)
                .root(block)
                .progressSingle()
                .buildAndExecute();
    }
}
