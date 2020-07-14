package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class GainExpQuest extends QuestExecutor {

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

        this.execute("gain-experience", player, gainAmount, QuestResult::none, replacer -> replacer.set("amount", gainAmount));
    }
}
