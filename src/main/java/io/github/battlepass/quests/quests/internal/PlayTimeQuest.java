package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayTimeQuest extends QuestExecutor {

    public PlayTimeQuest(BattlePlugin plugin) {
        super(plugin);
        this.run(plugin);
    }

    public void run(BattlePlugin plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.execute("playtime", player, 5, result -> result);
            }
        }, 100, 100);
    }
}
