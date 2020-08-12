package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import me.badbones69.crazyenvoy.api.events.OpenEnvoyEvent;
import me.badbones69.crazyenvoy.api.events.UseFlareEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class CrazyEnvoyQuests extends ExternalQuestExecutor {

    public CrazyEnvoyQuests(BattlePlugin plugin) {
        super(plugin, "crazyenvoy");
    }

    @EventHandler(ignoreCancelled = true)
    public void onOpenEnvoy(OpenEnvoyEvent event) {
        Player player = event.getPlayer();
        String tierName = event.getTier().getName();

        if (tierName == null) {
            return;
        }
        this.execute("open_envoy", player, result -> result.root(tierName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onUseFlare(UseFlareEvent event) {
        Player player = event.getPlayer();

        this.execute("use_flare", player, QuestResult::none);
    }
}
