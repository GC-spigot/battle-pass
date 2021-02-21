package io.github.battlepass.bstats;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.user.UserQuestProgressionEvent;
import lombok.SneakyThrows;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BStats implements Listener {
    private int questProgressions = 0;

    @SneakyThrows
    public BStats(BattlePlugin plugin) {
        plugin.registerListeners(this);
        Metrics metrics = new Metrics(plugin, 9650);
        metrics.addCustomChart(new SimplePie("number_of_weeks", () -> {
            return String.valueOf(plugin.getQuestCache().getMaxWeek());
        }));
        metrics.addCustomChart(new SingleLineChart("single_quest_progressions", () -> {
            int questProgressions = this.questProgressions;
            this.questProgressions = 0;
            return questProgressions;
        }));
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuestProgress(UserQuestProgressionEvent event) {
        this.questProgressions++;
    }
}
