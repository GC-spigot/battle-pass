package io.github.battlepass.quests.service.base;

import io.github.battlepass.BattlePlugin;

public class ExternalQuestContainer extends QuestContainer {
    protected final String prefix;

    protected ExternalQuestContainer(BattlePlugin plugin, String pluginName) {
        super(plugin);
        this.prefix = pluginName.concat("_");
    }

    public String getPrefix() {
        return this.prefix;
    }
}
