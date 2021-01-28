package io.github.battlepass.cache.listener;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.api.events.user.UserLoadEvent;
import io.github.battlepass.objects.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UserLoadListener implements Listener {
    private final BattlePassApi api;

    public UserLoadListener(BattlePlugin plugin) {
        this.api = plugin.getLocalApi();
    }

    @EventHandler(ignoreCancelled = true)
    public void onUserLoad(UserLoadEvent event) {
        User user = event.getUser();
        if (event.isNewUser()) {
            this.api.reward(user, 1, false);
        }
    }
}
