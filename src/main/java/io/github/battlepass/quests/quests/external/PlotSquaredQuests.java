package io.github.battlepass.quests.quests.external;

import com.github.intellectualsites.plotsquared.bukkit.events.PlayerClaimPlotEvent;
import com.github.intellectualsites.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.github.intellectualsites.plotsquared.bukkit.events.PlayerPlotTrustedEvent;
import com.github.intellectualsites.plotsquared.bukkit.events.PlayerTeleportToPlotEvent;
import com.github.intellectualsites.plotsquared.bukkit.events.PlotRateEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Set;
import java.util.UUID;

public class PlotSquaredQuests extends ExternalQuestExecutor {

    public PlotSquaredQuests(BattlePlugin plugin) {
        super(plugin, "plotsquared");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlotClaim(PlayerClaimPlotEvent event) {
        Player player = event.getPlayer();
        this.execute("claim", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlotEnter(PlayerEnterPlotEvent event) {
        Player player = event.getPlayer();
        Set<UUID> owners = event.getPlot().getOwners();
        if (owners.size() == 1) {
            String ownerName = Bukkit.getOfflinePlayer((UUID) owners.toArray()[0]).getName();
            this.execute("visit", player, result -> result.root(ownerName));
            return;
        }
        this.execute("visit", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlotTrust(PlayerPlotTrustedEvent event) {
        Player executor = event.getInitiator();
        Player target = Bukkit.getPlayer(event.getPlayer());
        this.execute("trust_player", executor, QuestResult::none);
        this.execute("become_trusted", target, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlotRate(PlotRateEvent event) {
        Player player = Bukkit.getPlayer(event.getRater().getUUID());
        this.execute("rate", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleportToPlot(PlayerTeleportToPlotEvent event) {
        Player player = event.getPlayer();
        Set<UUID> owners = event.getPlot().getOwners();
        if (owners.size() == 1) {
            String ownerName = Bukkit.getOfflinePlayer((UUID) owners.toArray()[0]).getName();
            this.execute("teleport", player, result -> result.root(ownerName));
            return;
        }
        this.execute("teleport", player, QuestResult::none);
    }
}
