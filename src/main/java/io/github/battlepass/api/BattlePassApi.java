package io.github.battlepass.api;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.actions.Action;
import io.github.battlepass.api.events.user.UserRewardReceiveEvent;
import io.github.battlepass.api.events.user.UserTierUpEvent;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.RewardCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.pass.PassType;
import io.github.battlepass.objects.pass.Tier;
import io.github.battlepass.objects.reward.Reward;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.registry.QuestRegistry;
import me.hyfe.simplespigot.text.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BattlePassApi {
    private final BattlePlugin plugin;
    private final PassLoader passLoader;
    private final UserCache userCache;
    private final RewardCache rewardCache;
    private final QuestCache questCache;
    private final QuestRegistry questRegistry;

    public BattlePassApi(BattlePlugin plugin) {
        this.plugin = plugin;
        this.passLoader = plugin.getPassLoader();
        this.userCache = plugin.getUserCache();
        this.rewardCache = plugin.getRewardCache();
        this.questCache = plugin.getQuestCache();
        this.questRegistry = plugin.getQuestRegistry();
    }

    public QuestRegistry getQuestRegistry() {
        return this.questRegistry;
    }

    public CompletableFuture<Optional<User>> getUser(UUID uuid) {
        return this.userCache.get(uuid);
    }

    public Optional<Reward<?>> getReward(String rewardId) {
        return this.rewardCache.get(rewardId);
    }

    public boolean hasSeasonEnded() {
        ZonedDateTime startTime = this.plugin.getSeasonStartDate();
        return ZonedDateTime.now().isAfter(startTime.plusWeeks(this.questCache.getMaxWeek()));
    }

    public long currentWeek() {
        ZoneId zoneId = ZoneId.of(this.plugin.getConfig("settings").string("current-season.time-zone"));
        long daysBetween = ChronoUnit.DAYS.between(this.plugin.getSeasonStartDate(), ZonedDateTime.now().withZoneSameInstant(zoneId));
        return daysBetween < 0 ? 0 : (daysBetween / 7) + 1;
    }

    public long currentDisplayWeek() {
        return Math.min(this.currentWeek(), this.questCache.getMaxWeek());
    }

    public void setPassId(User user, String passId) {
        String currentPassId = user.getPassId();
        if (currentPassId.equals(passId)) {
            return;
        }
        user.setPassId(passId);
        if (passId.equals("premium")) {
            for (int tier = 1; tier <= user.getTier(); tier++) {
                this.reward(user, "premium", tier, false);
            }
        }
    }

    public Tier getTier(int tier, String passId) {
        return this.passLoader.getPassTypes().get(passId).getTiers().get(tier);
    }

    public int getRequiredPoints(int tier, String passId) {
        PassType passType = this.passLoader.getPassTypes().get(passId);
        Tier tierObject = passType.getTiers().get(tier);
        return tierObject == null ? passType.getDefaultPointsRequired() : tierObject.getRequiredPoints();
    }

    public void givePoints(User user, int points) {
        int maxTier = this.passLoader.getMaxTier();
        if (user.getTier() >= maxTier) {
            return;
        }
        user.updatePoints(current -> current.add(BigInteger.valueOf(points)));
        this.updateUserTier(user);
    }

    /**
     * Updates the tier of a user / fixes it if they're over the points of that tier. Say they have 100/50 points, it will tier them up and they will have 50 points
     * @param user The user to update their tier for.
     */
    public void updateUserTier(User user) {
        int maxTier = this.passLoader.getMaxTier();
        for (int tier = user.getTier() + 1; tier <= maxTier; tier++) {
            int required = this.getRequiredPoints(user.getTier(), user.getPassId());
            if (user.getPoints().compareTo(BigInteger.valueOf(required)) >= 0) {
                user.updatePoints(current -> current.subtract(BigInteger.valueOf(required)));
                Bukkit.getPluginManager().callEvent(new UserTierUpEvent(user, tier));
                user.updateTier(current -> current + 1);
                this.reward(user, tier, false);
                Player player = Bukkit.getPlayer(user.getUuid());
                if (player != null) {
                    Action.executeSimple(player, this.passLoader.getPassTypes().get(user.getPassId()).getTierUpActions(), this.plugin, new Replacer().set("tier", tier));
                }
            } else {
                break;
            }
        }
        if (user.getTier() >= maxTier) {
            user.updatePoints(current -> BigInteger.ZERO);
        }
    }

    public void reward(User user, int tier, boolean ignoreRestrictions) {
        this.passLoader.getPassTypes().values().stream().map(PassType::getId).forEach(passId -> this.reward(user, passId, tier, ignoreRestrictions));
    }

    public void reward(User user, String passId, int tier, boolean ignoreRestrictions) {
        boolean autoReceiveRewards = this.plugin.getConfig("settings").bool("current-season.auto-receive-rewards");
        Tier tierObject = this.getTier(tier, passId);
        if (tierObject == null) {
            return;
        }
        if (user.hasPassId(passId)) {
            Player player = user.getPlayer();
            if (player == null || (!ignoreRestrictions && !autoReceiveRewards)) {
                user.addPendingTier(passId, tier);
            } else {
                for (String rewardId : tierObject.getRewardIds()) {
                    Optional<Reward<?>> maybeReward = this.rewardCache.get(rewardId);
                    if (!maybeReward.isPresent()) {
                        continue;
                    }
                    UserRewardReceiveEvent event = new UserRewardReceiveEvent(user, tierObject, maybeReward.get());
                    Bukkit.getPluginManager().callEvent(event);
                    event.ifNotCancelled(consumerEvent -> maybeReward.get().reward(player));
                }
            }
        }
    }
}
