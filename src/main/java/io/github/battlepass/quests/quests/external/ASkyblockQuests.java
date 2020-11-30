package io.github.battlepass.quests.quests.external;

import com.wasteofplastic.askyblock.events.IslandNewEvent;
import com.wasteofplastic.askyblock.events.WarpCreateEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ASkyblockQuests extends ExternalQuestExecutor {

    public ASkyblockQuests(BattlePlugin plugin) {
        super(plugin, "askyblock");
    }

    @EventHandler(ignoreCancelled = true)
    public void onNewIsland(IslandNewEvent event) {
        Player player = event.getPlayer();
        String schematicName = event.getSchematicName().getName();
        if (schematicName == null) {
            return;
        }
        this.execute("create_island", player, result -> result.root(schematicName));
    }

    @EventHandler(ignoreCancelled = true)
    public void onWarpCreate(WarpCreateEvent event) {
        Player player = Bukkit.getPlayer(event.getCreator());

        this.execute("warp", player, QuestResult::none);
        this.execute("create_warp", player, QuestResult::none);
    }
}
