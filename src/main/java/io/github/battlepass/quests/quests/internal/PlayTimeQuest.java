package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayTimeQuest extends QuestContainer {

    public PlayTimeQuest(BattlePlugin plugin) {
        super(plugin);
        this.run(plugin);
    }

    public void run(BattlePlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.executionBuilder("playtime")
                        .player(player)
                        .progress(5)
                        .buildAndExecute();
            }
        }, 100, 100);
    }
}
