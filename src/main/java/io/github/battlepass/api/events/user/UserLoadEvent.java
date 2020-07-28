package io.github.battlepass.api.events.user;

import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.annotations.NotNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull
    HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
