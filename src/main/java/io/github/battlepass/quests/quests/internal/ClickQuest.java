package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import io.github.battlepass.quests.service.executor.QuestExecutionBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClickQuest extends QuestContainer {

    public ClickQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        QuestExecutionBuilder executionBuilder;
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                executionBuilder = this.executionBuilder("right-click");
                if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) {
                    break;
                }
                this.executionBuilder("right-click-block")
                        .player(player)
                        .root(clickedBlock)
                        .progressSingle()
                        .buildAndExecute();
                break;
            case RIGHT_CLICK_AIR:
                executionBuilder = this.executionBuilder("right-click");
                break;
            case LEFT_CLICK_AIR:
                executionBuilder = this.executionBuilder("left-click");
                break;
            case LEFT_CLICK_BLOCK:
                executionBuilder = this.executionBuilder("left-click");
                if (clickedBlock == null || clickedBlock.getType().equals(Material.AIR)) {
                    return;
                }
                this.executionBuilder("left-click-block")
                        .player(player)
                        .root(clickedBlock)
                        .progressSingle()
                        .buildAndExecute();
                break;
            default:
                return;
        }
        executionBuilder.player(player)
                .progressSingle()
                .buildAndExecute();
    }
}
