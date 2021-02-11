package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class GainExpQuest extends QuestContainer {

    public GainExpQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        int gainAmount = event.getAmount();
        if (gainAmount <= 0) {
            return;
        }

        this.executionBuilder("gain-experience")
                .player(player)
                .progress(gainAmount)
                .progressSingle()
                .buildAndExecute();
    }
}
