package io.github.battlepass.quests.quests.external;

import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import ro.Fr33styler.TheLab.Api.GameEndEvent;
import ro.Fr33styler.TheLab.Api.GameJoinEvent;

import java.util.Map;

public class TheLabQuests extends ExternalQuestExecutor {

    public TheLabQuests(BattlePlugin plugin) {
        super(plugin, "thelab");
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameEnd(GameEndEvent event) {
        Player[] players = event.getTop();
        Map<Player, Integer> positions = Maps.newHashMap();
        for (int i = 0; i < players.length; i++) {
            positions.put(players[i], i + 1);
        }
        for (Player player : players) {
            if (positions.get(player) == 1) {
                this.execute("win", player, QuestResult::none);
            }
            this.execute("finish", player, result -> result.root(String.valueOf(positions.get(player))));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameJoin(GameJoinEvent event) {
        Player player = event.getPlayer();

        this.execute("join", player, QuestResult::none);
    }
}
