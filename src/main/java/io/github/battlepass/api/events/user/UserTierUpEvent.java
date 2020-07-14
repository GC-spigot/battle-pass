package io.github.battlepass.api.events.user;

import io.github.battlepass.objects.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserTierUpEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final User user;
    private final int tier;

    public UserTierUpEvent(User user, int tier) {
        this.user = user;
        this.tier = tier;
    }

    public User getUser() {
        return this.user;
    }

    public int getTier() {
        return this.tier;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
