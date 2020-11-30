package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import su.nightexpress.moneyhunters.api.events.PlayerJobExpGainEvent;
import su.nightexpress.moneyhunters.api.events.PlayerJobLevelUpEvent;

public class MoneyHuntersQuests extends ExternalQuestExecutor {

    public MoneyHuntersQuests(BattlePlugin plugin) {
        super(plugin, "moneyhunters");
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(PlayerJobLevelUpEvent event) {
        Player player = event.getPlayer();
        String jobName = event.getJob().getName();

        this.execute("level_up", player, event.getNewLevel(), result -> {
            return result.root(jobName);
        }, replacer -> replacer, true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onExperienceGain(PlayerJobExpGainEvent event) {
        Player player = event.getPlayer();
        String jobName = event.getJob().getName();

        this.execute("gain_exp", player, event.getExp(), result -> result.root(jobName));
    }
}
