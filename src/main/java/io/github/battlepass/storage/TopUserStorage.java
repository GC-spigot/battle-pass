package io.github.battlepass.storage;

import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.TopUsersCache;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;
import me.hyfe.simplespigot.uuid.FastUuid;

import java.util.Set;
import java.util.UUID;

public class TopUserStorage extends Storage<TopUsersCache> {
    private final BattlePlugin plugin;

    public TopUserStorage(BattlePlugin plugin) {
        super(plugin, factory -> factory.create(plugin.getConfigStore().commons().get("storageMethod"), path -> path.resolve("misc-storage")));
        this.plugin = plugin;
    }

    @Override
    public Serializer<TopUsersCache> serializer() {
        return (cache, json, gson) -> {
            json.addProperty("top-users", cache.getSerializedTopUuids());
            json.addProperty("changed-uuids", cache.getSerializedChangedUuids());
            return json;
        };
    }

    @Override
    public Deserializer<TopUsersCache> deserializer() {
        return (json, gson) -> {
            String topUsers = json.get("top-users").getAsString();
            String changedStringUuids = json.get("changed-uuids").getAsString();

            Set<UUID> topUserUuids = Sets.newHashSet();
            for (String stringUuid : topUsers.split(";")) {
                if (!(stringUuid.length() < 10)) {
                    topUserUuids.add(FastUuid.parse(stringUuid));
                }
            }
            Set<UUID> changedUuids = Sets.newHashSet();
            for (String stringUuid : changedStringUuids.split(";")) {
                if (!(stringUuid.length() < 10)) {
                    changedUuids.add(FastUuid.parse(stringUuid));
                }
            }
            return new TopUsersCache(this.plugin, topUserUuids, changedUuids);
        };
    }
}
