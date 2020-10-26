package io.github.battlepass.quests.quests.external;

import com.benzimmer123.koth.api.events.KothLoseCapEvent;
import com.benzimmer123.koth.api.events.KothWinEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class BenzimmerKothQuests extends ExternalQuestExecutor {

    public BenzimmerKothQuests(BattlePlugin plugin) {
        super(plugin, "koth");
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothCapture(KothWinEvent event) {
        Player player = event.getCapper();
        int capTime = event.getCaptureTime();
        String kothName = event.getKOTH().getName(false);

        this.execute("capture", player, capTime, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
        this.execute("win_cap", player, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onKothEnd(KothLoseCapEvent event) {
        Player player = event.getCapper();
        int capTime = event.getCaptureTime();
        String kothName = event.getKOTH().getName(false);

        this.execute("capture", player, capTime, result -> result.root(kothName), replacer -> replacer.set("koth_name", kothName));
    }
}
