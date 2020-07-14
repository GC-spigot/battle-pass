package io.github.battlepass.quests.quests.external;

import com.benzimmer123.koth.events.KothCaptureEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class BenzimmerKothQuests extends ExternalQuestExecutor {

    public BenzimmerKothQuests(BattlePlugin plugin) {
        super(plugin, "koth");
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothCapture(KothCaptureEvent event) {
        Player player = event.getCapper();
        String kothName = event.getKOTH().getName();

        this.execute("win_cap", player, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
    }

    /*public void onKothEnd(KothEndEvent event) {

    }*/
}
