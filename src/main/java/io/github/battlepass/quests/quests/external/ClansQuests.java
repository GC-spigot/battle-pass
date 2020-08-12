package io.github.battlepass.quests.quests.external;

import Clans.Events.ClanCreateEvent;
import Clans.Events.ClanJoinEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ClansQuests extends ExternalQuestExecutor {

    public ClansQuests(BattlePlugin plugin) {
        super(plugin, "clans");
    }

    @EventHandler(ignoreCancelled = true)
    public void onClanCreate(ClanCreateEvent event) {
        Player player = event.getOwner();
        this.execute("create", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClanInvite(ClanJoinEvent event) {
        Player player = event.getPlayer();
        this.execute("join", player, QuestResult::none);
    }
}
