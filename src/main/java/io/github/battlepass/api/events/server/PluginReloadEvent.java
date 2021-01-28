package io.github.battlepass.api.events.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Due to the nature of reloading, you must re hook into the plugin in most cases
 * if it is reloaded.
 *
 * Classes such as the {@link io.github.battlepass.cache.UserCache}, {@link io.github.battlepass.registry.quest.QuestRegistry} and more
 * are recreated when the plugin is reloaded. If you have them referenced
 * as values they will no longer work.
 *
 * @since 3.10
 */
public class PluginReloadEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
