package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakQuest extends QuestExecutor {

    public BlockBreakQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        super.execute("block-break", player, result -> result.root(block), replacer -> replacer.set("block", block.getType()));
        if(block.getBlockData() instanceof Ageable) {
        	Ageable ageable = (Ageable) block.getBlockData();
        	if(ageable.getAge() == ageable.getMaximumAge()) super.execute("harvest", player, result -> result.root(block));
        }
    }
}
