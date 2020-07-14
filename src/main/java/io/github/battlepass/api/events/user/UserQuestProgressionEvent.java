package io.github.battlepass.api.events.user;

import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class UserQuestProgressionEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final User user;
    private final Quest quest;
    private int progression;
    private boolean isCancelled;

    public UserQuestProgressionEvent(User user, Quest quest, int progression) {
        this.user = user;
        this.quest = quest;
        this.progression = progression;
    }

    public User getUser() {
        return this.user;
    }

    public Quest getQuest() {
        return this.quest;
    }

    public int getProgression() {
        return this.progression;
    }

    public void setProgression(int progression) {
        this.progression = progression;
    }

    public void ifNotCancelled(Consumer<UserQuestProgressionEvent> event) {
        event.accept(this);
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
