package io.github.battlepass.quests.quests.external;

import com.electro2560.dev.cluescrolls.events.PlayerClueCompletedEvent;
import com.electro2560.dev.cluescrolls.events.PlayerScrollCompletedEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ClueScrollsQuests extends ExternalQuestExecutor {

    public ClueScrollsQuests(BattlePlugin plugin) {
        super(plugin, "cluescrolls");
    }

    @EventHandler(ignoreCancelled = true)
    public void onClueCompleted(PlayerClueCompletedEvent event) {
        Player player = event.getPlayer();
        String clueType = event.getClueType();

        this.execute("complete_clue", player, result -> result.root(clueType));
    }

    @EventHandler(ignoreCancelled = true)
    public void onScrollCompleted(PlayerScrollCompletedEvent event) {
        Player player = event.getPlayer();
        String tierType = event.getTierType();

        this.execute("complete_scroll", player, result -> result.root(tierType));
    }
}
