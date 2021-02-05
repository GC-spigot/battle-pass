package io.github.battlepass.api.events.user;

import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.annotations.NotNull;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.function.Consumer;

public class UserPassChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final User user;
    private final String oldPassId;
    private String newPassId;
    private boolean isCancelled;

    public UserPassChangeEvent(User user, String newPassId) {
        this.user = user;
        this.oldPassId = user.getPassId();
        this.newPassId = newPassId;
    }

    public User getUser() {
        return this.user;
    }

    public String getOldPassId() {
        return this.oldPassId;
    }

    public String getNewPassId() {
        return this.newPassId;
    }

    public void setNewPassId(String newPassId) {
        this.newPassId = newPassId;
    }

    public void ifNotCancelled(Consumer<UserPassChangeEvent> event) {
        if (!this.isCancelled) {
            event.accept(this);
        }
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
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
