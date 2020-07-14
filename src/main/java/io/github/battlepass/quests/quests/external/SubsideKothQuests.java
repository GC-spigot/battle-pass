package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import subside.plugins.koth.captureentities.Capper;
import subside.plugins.koth.captureentities.CappingPlayer;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothLeftEvent;

public class SubsideKothQuests extends ExternalQuestExecutor {

    public SubsideKothQuests(BattlePlugin plugin) {
        super(plugin, "koth");
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothEnd(KothEndEvent event) {
        String kothName = event.getRunningKoth().getKoth().getName();
        Capper<?> capper = event.getWinner();
        if (!(capper instanceof CappingPlayer)) {
            return;
        }
        Player player = Bukkit.getPlayer(((OfflinePlayer) capper.getObject()).getUniqueId());
        this.execute("win_cap", player, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothCap(KothLeftEvent event) {
        String kothName = event.getRunningKoth().getKoth().getName();
        Capper<?> capper = event.getCapper();
        if (!(capper instanceof CappingPlayer)) {
            return;
        }
        Player player = Bukkit.getPlayer(((OfflinePlayer) capper.getObject()).getUniqueId());
        int timeCaptured = event.getAmountSecondsCapped();
        this.execute("capture", player, timeCaptured, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
    }
}
