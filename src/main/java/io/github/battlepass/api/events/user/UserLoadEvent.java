package io.github.battlepass.api.events.user;

import io.github.battlepass.objects.user.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserLoadEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final User user;
    private final boolean newUser;

    public UserLoadEvent(User user, boolean newUser) {
        this.user = user;
        this.newUser = newUser;
    }

    public User getUser() {
        return this.user;
    }

    public boolean isNewUser() {
        return this.newUser;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
