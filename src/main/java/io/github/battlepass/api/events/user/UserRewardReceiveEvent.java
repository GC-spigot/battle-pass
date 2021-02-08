package io.github.battlepass.api.events.user;

import io.github.battlepass.objects.pass.Tier;
import io.github.battlepass.objects.reward.Reward;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.annotations.NotNull;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.function.Consumer;

public class UserRewardReceiveEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final User user;
    private final Tier tierObject;
    private final Reward<?> reward;
    private boolean isCancelled;

    public UserRewardReceiveEvent(User user, Tier tierObject, Reward<?> reward) {
        this.user = user;
        this.tierObject = tierObject;
        this.reward = reward;
    }

    public User getUser() {
        return this.user;
    }

    public Tier getTierObject() {
        return this.tierObject;
    }

    public Reward<?> getReward() {
        return this.reward;
    }

    public void ifNotCancelled(Consumer<UserRewardReceiveEvent> event) {
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
