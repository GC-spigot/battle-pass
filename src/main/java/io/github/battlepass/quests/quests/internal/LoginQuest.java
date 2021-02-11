package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginQuest extends QuestContainer {
    private final BattlePlugin plugin;

    public LoginQuest(BattlePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.isOnline()) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> this
                        .executionBuilder("login")
                        .player(player)
                        .progressSingle()
                        .buildAndExecute()
                , 40);
    }
}
