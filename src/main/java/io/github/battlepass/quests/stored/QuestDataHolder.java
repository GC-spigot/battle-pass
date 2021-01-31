package io.github.battlepass.quests.stored;

import java.util.UUID;

public abstract class QuestDataHolder {
    protected final UUID uuid;

    protected QuestDataHolder(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
