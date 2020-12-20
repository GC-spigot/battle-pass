package io.github.battlepass.cache.listener;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ConnectionListener implements Listener {
    private final BattlePlugin plugin;
    private final UserCache userCache;
    private final Lang lang;
    private final boolean bungeeFix;
    private final boolean notificationEnabled;

    public ConnectionListener(BattlePlugin plugin) {
        this.plugin = plugin;
        this.userCache = plugin.getUserCache();
        this.lang = plugin.getLang();
        this.bungeeFix = plugin.getConfig("settings").bool("storage-options.bungee-fix");
        this.notificationEnabled = this.lang.has("collectable-rewards-notification");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.bungeeFix) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                this.loadPlayer(player);
            }, 10);
        } else {
            this.loadPlayer(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.userCache.unload(event.getPlayer().getUniqueId(), true);
    }

    private void loadPlayer(Player player) {
        CompletableFuture<User> completableUser = this.userCache.load(player.getUniqueId());
        if (!this.notificationEnabled) {
            return;
        }
        completableUser.thenAccept(user -> {
            Predicate<String> isNotify = passId -> user.getPendingTiers(passId) != null && !user.getPendingTiers(passId).isEmpty();
            if (isNotify.test("free") || isNotify.test("premium")) {
                this.messageDelay(() -> {
                    this.lang.external("collectable-rewards-notification", replacer -> replacer.set("player", player.getName())).to(player);
                });
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void messageDelay(Runnable runnable) {
        Config settings = this.plugin.getConfig("settings");
        if (settings.has("join-message-delay")) {
            int delay = settings.integer("join-message-delay");
            Bukkit.getScheduler().runTaskLater(this.plugin, runnable, 20L * delay);
        } else {
            runnable.run();
        }
    }
}