package io.github.battlepass.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.objects.user.StatsUser;
import io.github.battlepass.objects.user.User;
import org.bukkit.Bukkit;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class TopUsersCache {
    private final BattlePlugin plugin;
    private final BattlePassApi api;
    private final UserCache userCache;
    private final DebugLogger debugLogger;
    private final List<StatsUser> topUsers = Lists.newCopyOnWriteArrayList();
    private final Set<UUID> changedUuids = Sets.newConcurrentHashSet();

    public TopUsersCache(BattlePlugin plugin, Set<UUID> uuidsToLoad) {
        this.plugin = plugin;
        this.api = plugin.getLocalApi();
        this.userCache = plugin.getUserCache();
        this.debugLogger = plugin.getDebugLogger();
        this.load(uuidsToLoad);
    }

    public TopUsersCache(BattlePlugin plugin) {
        this.plugin = plugin;
        this.api = plugin.getLocalApi();
        this.userCache = plugin.getUserCache();
        this.debugLogger = plugin.getDebugLogger();
        this.load();
    }

    public List<StatsUser> getTopUsers(int startPosition, int endPosition) {
        List<StatsUser> statsUsers = Lists.newArrayList();
        for (int i = startPosition - 1; i <= endPosition && endPosition <= this.topUsers.size(); i++) {
            statsUsers.add(this.topUsers.get(i));
        }
        return statsUsers;
    }

    private void load(Set<UUID> uuidsToLoad) {
        this.userCache.asyncMassModify(uuidsToLoad, user -> {
            this.topUsers.add(new StatsUser(user.getUuid(), Bukkit.getOfflinePlayer(user.getUuid()).getName(), user.getTier(), user.getPassId(), user.getPoints(), this.getTotalPoints(user)));
        });
        // TODO because of async, the sort and cleanup is run before the users are inserted. Any onceDone method?
        // All ordering is done tho.
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.sort();
            this.cleanup();
        }, 60);
        Bukkit.getLogger().log(Level.INFO, "[BattlePass] Loaded users into the leaderboard (general load).");
    }

    private void load() {
        this.userCache.asyncModifyAll(user -> {
            this.topUsers.add(new StatsUser(user.getUuid(), Bukkit.getOfflinePlayer(user.getUuid()).getName(), user.getTier(), user.getPassId(), user.getPoints(), this.getTotalPoints(user)));
        });
        this.sort();
        this.cleanup();
        Bukkit.getLogger().log(Level.INFO, "[BattlePass] Loaded users into the leaderboard (first time).");
    }

    public Set<UUID> getChangedUuids() {
        return this.changedUuids;
    }

    public void addChangedUuid(UUID uuid) {
        this.changedUuids.add(uuid);
    }

    public List<StatsUser> getTopUsers() {
        return this.topUsers;
    }

    public String getSerializedUuids() {
        StringBuilder builder = new StringBuilder();
        for (StatsUser statsUser : this.topUsers) {
            builder.append(statsUser.getUuid()).append(";");
        }
        for (UUID changedUuid : this.changedUuids) {
            builder.append(changedUuid).append(";");
        }
        return builder.toString();
    }

    private void sort() {
        this.topUsers.sort(Collections.reverseOrder());
    }

    private void cleanup() {
        while (this.topUsers.size() > 100) {
            this.topUsers.remove(100);
        }
    }

    private BigInteger getTotalPoints(User user) {
        int tier = user.getTier();
        BigInteger totalPoints = new BigInteger("0");
        for (int i = 1; i < tier; i++) {
            totalPoints = totalPoints.add(new BigInteger(String.valueOf(this.api.getRequiredPoints(i, user.getPassId()))));
        }
        return totalPoints;
    }
}
