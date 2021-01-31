package io.github.battlepass.quests.stored;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;

public abstract class StoredQuestExecutor<D extends QuestDataHolder> extends QuestExecutor {
    private final Storage<D> dataStorage;

    protected StoredQuestExecutor(BattlePlugin plugin, String categoryIdentifier) {
        super(plugin);
        Serializer<D> serializer = this.serialize();
        Deserializer<D> deserializer = this.deserialize();
        this.dataStorage = new Storage<>(plugin, factory -> factory.create(plugin.getConfigStore().commons().get("storage-type"),
                path -> path.resolve("stored-quest-data"),
                categoryIdentifier)) {
            @Override
            public Serializer<D> serializer() {
                return serializer;
            }

            @Override
            public Deserializer<D> deserializer() {
                return deserializer;
            }
        };
    }

    public abstract Serializer<D> serialize();

    public abstract Deserializer<D> deserialize();
}
