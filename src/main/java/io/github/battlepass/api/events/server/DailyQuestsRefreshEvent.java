package io.github.battlepass.api.events.server;

import io.github.battlepass.objects.quests.Quest;
import me.hyfe.simplespigot.annotations.NotNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Set;

public class DailyQuestsRefreshEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Set<Quest> newDailyQuests;

    public DailyQuestsRefreshEvent(Set<Quest> newDailyQuests) {
        this.newDailyQuests = newDailyQuests;
    }

    public Set<Quest> getNewDailyQuests() {
        return this.newDailyQuests;
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
