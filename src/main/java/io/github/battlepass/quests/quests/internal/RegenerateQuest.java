package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class RegenerateQuest extends QuestContainer {

    public RegenerateQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onGainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        EntityRegainHealthEvent.RegainReason regainReason = event.getRegainReason();
        int gainAmount = (int) Math.round(event.getAmount());

        this.executionBuilder("regenerate")
                .player(player)
                .progress(gainAmount)
                .root(regainReason.toString())
                .progressSingle()
                .buildAndExecute();
    }
}
