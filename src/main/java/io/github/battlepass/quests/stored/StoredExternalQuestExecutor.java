package io.github.battlepass.quests.stored;

import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;

public abstract class StoredExternalQuestExecutor<D extends QuestDataHolder> extends ExternalQuestExecutor implements StoredQuestFramework<D> {
    private final SimplePlugin plugin;
    private final Storage<D> dataStorage;
    private final Map<UUID, D> dataMap = Maps.newHashMap();

    protected StoredExternalQuestExecutor(BattlePlugin plugin, String pluginName) {
        super(plugin, pluginName);
        this.plugin = plugin;
        Serializer<D> serializer = this.serialize();
        Deserializer<D> deserializer = this.deserialize();
        this.dataStorage = new Storage<>(plugin, factory -> factory.create(plugin.getConfigStore().commons().get("storage-type"),
                path -> path.resolve("stored-quest-data"),
                pluginName)) {
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

    @Override
    public SimplePlugin getPlugin() {
        return this.plugin;
    }

    public Storage<D> getDataStorage() {
        return this.dataStorage;
    }

    @Override
    public Map<UUID, D> getDataMap() {
        return this.dataMap;
    }
}
