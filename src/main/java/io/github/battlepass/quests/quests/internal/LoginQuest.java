package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginQuest extends QuestExecutor {
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

        if (this.plugin.getConfig("settings").bool("database-settings.bungee-fix")) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.execute("login", player, QuestResult::none), 20);
        } else {
            this.execute("login", player, QuestResult::none);
        }
    }
}
