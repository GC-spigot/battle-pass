package io.github.battlepass.storage;

import com.google.gson.reflect.TypeToken;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.user.QuestStore;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;
import me.hyfe.simplespigot.uuid.FastUuid;
import org.bukkit.Bukkit;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;

public class UserStorage extends Storage<User> {

    public UserStorage(BattlePlugin plugin) {
        super(plugin, factory -> factory.create(plugin.getConfigStore().commons().get("storage-type"), path -> path.resolve("users"), ""));
    }

    @Override
    public Serializer<User> serializer() {
        return (user, json, gson) -> {
            json.addProperty("uuid", FastUuid.toString(user.getUuid()));
            json.addProperty("quests", gson.toJson(user.getQuestStore()));
            json.addProperty("pass-id", user.getPassId());
            json.addProperty("tier", user.getTier());
            json.addProperty("points", user.getPoints().toString());
            json.addProperty("currency", user.getCurrency().toString());
            json.addProperty("bypass-locked-weeks", user.bypassesLockedWeeks());
            json.addProperty("pending-rewards", gson.toJson(user.getPendingTiers()));
            return json;
        };
    }

    @Override
    public Deserializer<User> deserializer() {
        return (json, gson) -> {
            UUID uuid = FastUuid.parse(json.get("uuid").getAsString());
            try {
                QuestStore questStore = gson.fromJson(json.get("quests").getAsString(), QuestStore.class);
                String passId = json.get("pass-id").getAsString();
                int tier = json.get("tier").getAsInt();
                BigInteger points = new BigInteger(json.get("points").getAsString());
                BigInteger currency = json.has("currency") ? new BigInteger(json.get("currency").getAsString()) : BigInteger.ZERO;
                boolean bypassLockedWeeks = json.has("bypass-locked-weeks") && json.get("bypass-locked-weeks").getAsBoolean();
                Map<String, TreeSet<Integer>> pendingTiers = gson.fromJson(json.get("pending-rewards").getAsString(), new TypeToken<HashMap<String, TreeSet<Integer>>>(){}.getType());
                return new User(uuid, questStore, tier, points, currency, passId, bypassLockedWeeks, pendingTiers);
            } catch (Exception ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Error whilst loading player data file: " + uuid + ".json", ex);
                return null;
            }
        };
    }
}
