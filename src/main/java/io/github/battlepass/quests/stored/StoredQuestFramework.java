package io.github.battlepass.quests.stored;

import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;

import java.util.Map;
import java.util.UUID;

public interface StoredQuestFramework<D> {

    SimplePlugin getPlugin();

    Storage<D> getDataStorage();

    Map<UUID, D> getDataMap();

    Serializer<D> serialize();

    Deserializer<D> deserialize();

    D createDataHolder(UUID uuid);

    // With join quests, do we need to factor in the fact that latency might mean the user's quest data is not yet loaded?
    // Maybe add a way to show requests in progress and if so then just complete the task once their data holder is loaded.
    default void load(UUID uuid) {
        this.getPlugin().runAsync(() -> {
            D dataHolder = this.getDataStorage().load(uuid.toString());
            if (dataHolder == null) {
                dataHolder = this.createDataHolder(uuid);
            }
            this.getDataMap().put(uuid, dataHolder);
        });
    }
}
