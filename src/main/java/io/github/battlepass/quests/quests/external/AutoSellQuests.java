package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import me.clip.autosell.events.AutoSellEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AutoSellQuests extends ExternalQuestExecutor {

    public AutoSellQuests(BattlePlugin plugin) {
        super(plugin, "autosell");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(AutoSellEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        super.execute("break", player, result -> result.root(block), replacer -> replacer.set("block", block.getType()));
    }
}
