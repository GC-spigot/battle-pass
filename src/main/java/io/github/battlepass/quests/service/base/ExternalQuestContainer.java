package io.github.battlepass.quests.service.base;

import io.github.battlepass.BattlePlugin;
import me.hyfe.simplespigot.annotations.NotNull;

public class ExternalQuestContainer extends QuestContainer {
    protected final String prefix;

    /**
     * Should be used for external plugins to avoid conflicts with other quests and also make it easier for users to find the quest type names.
     *
     * @param plugin     {@link BattlePlugin} instance. If you create a new one you are a sin :|.
     * @param pluginName The name of the plugin for the quest. This is prefixed on quest types (pluginName_questType)
     */
    protected ExternalQuestContainer(@NotNull BattlePlugin plugin, @NotNull String pluginName) {
        super(plugin);
        this.prefix = pluginName.concat("_");
    }

    @NotNull
    public String getPrefix() {
        return this.prefix;
    }
}
