package io.github.battlepass.api;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.actions.Action;
import io.github.battlepass.api.events.user.UserPassChangeEvent;
import io.github.battlepass.api.events.user.UserRewardReceiveEvent;
import io.github.battlepass.api.events.user.UserTierUpEvent;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.RewardCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.exceptions.PassNotFoundException;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.pass.PassType;
import io.github.battlepass.objects.pass.Tier;
import io.github.battlepass.objects.reward.Reward;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.registry.quest.QuestRegistry;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BattlePassApiImpl implements BattlePassApi {
    private final BattlePlugin plugin;
    private final Lang lang;
    private final PassLoader passLoader;
    private final UserCache userCache;
    private final RewardCache rewardCache;
    private final QuestCache questCache;
    private final QuestRegistry questRegistry;
    private final Config settingsConfig;
    private final Config freePassConfig;
    private final boolean useImprovedTierPoints;

    public BattlePassApiImpl(BattlePlugin plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.passLoader = plugin.getPassLoader();
        this.userCache = plugin.getUserCache();
        this.rewardCache = plugin.getRewardCache();
        this.questCache = plugin.getQuestCache();
        this.questRegistry = plugin.getQuestRegistry();
        this.settingsConfig = plugin.getConfig("settings");
        this.freePassConfig = plugin.getPassLoader().passTypeOfId("free").getConfig();
        this.useImprovedTierPoints = this.settingsConfig.bool("fixes.use-improved-tier-points");
    }

    @Override
    public QuestRegistry getQuestRegistry() {
        return this.questRegistry;
    }

    @Override
    public CompletableFuture<Optional<User>> getUser(UUID uuid) {
        return this.userCache.get(uuid);
    }

    @Override
    public CompletableFuture<User> getOrLoadUser(UUID uuid) {
        return this.userCache.load(uuid);
    }

    @Override
    public Optional<Reward<?>> getReward(String rewardId) {
        return this.rewardCache.get(rewardId);
    }

    @Override
    public boolean hasSeasonEnded() {
        return ZonedDateTime.now().isAfter(this.plugin.getSeasonEndDate());
    }

    @Override
    public boolean hasSeasonStarted() {
        return this.plugin.getSeasonStartDate().isBefore(ZonedDateTime.now().withZoneSameInstant(this.getZone()));
    }

    @Override
    public String getWeekFormatted() {
        String finishedSection = "season-finished-message";
        return this.hasSeasonEnded() ? this.lang.has(finishedSection) ? this.lang.external(finishedSection).asString()
                : String.valueOf(this.getCurrentDisplayWeek()) : String.valueOf(this.getCurrentDisplayWeek());
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.of(this.plugin.getConfig("settings").string("current-season.time-zone"));
    }

    @Override
    public long getCurrentWeek() {
        if (!this.hasSeasonStarted()) {
            return -1;
        }
        ZoneId zoneId = this.getZone();
        long daysBetween = ChronoUnit.DAYS.between(this.plugin.getSeasonStartDate(), ZonedDateTime.now().withZoneSameInstant(zoneId));
        return daysBetween < 0 ? 0 : (daysBetween / 7) + 1;
    }

    @Override
    public long getCurrentDisplayWeek() {
        return Math.min(this.getCurrentWeek(), this.questCache.getMaxWeek());
    }

    @Override
    public ZonedDateTime getSeasonEndDate() {
        return this.plugin.getSeasonEndDate();
    }

    @Override
    public ZonedDateTime getSeasonStartDate() {
        return this.plugin.getSeasonStartDate();
    }

    @Override
    public void setPassId(User user, String passId) throws PassNotFoundException {
        String currentPassId = user.getPassId();
        if (currentPassId.equals(passId)) {
            return;
        }
        if (!passId.equals("free") && !passId.equals("premium")) {
            throw new PassNotFoundException(passId);
        }
        UserPassChangeEvent event = new UserPassChangeEvent(user, passId);
        this.plugin.runSync(() -> Bukkit.getPluginManager().callEvent(event));
        event.ifNotCancelled(consumerEvent -> {
            user.setPassId(consumerEvent.getNewPassId());
            if (passId.equals("free")) {
                user.getPendingTiers().remove("premium");
            }
            if (passId.equals("premium")) {
                if (this.freePassConfig.bool("dont-give-premium-free-rewards")) {
                    user.getPendingTiers().remove("free");
                }
                for (int tier = 1; tier <= user.getTier(); tier++) {
                    this.reward(user, "premium", tier, false);
                }
            }
        });
    }

    @Override
    public Tier getTier(int tier, String passId) throws PassNotFoundException {
        if (!passId.equals("free") && !passId.equals("premium")) {
            throw new PassNotFoundException(passId);
        }
        return this.passLoader.getPassTypes().get(passId).getTiers().get(tier);
    }

    @Override
    public int getRequiredPoints(int tier, String passId) {
        if (this.useImprovedTierPoints) {
            tier += 1;
        }
        PassType passType = this.passLoader.getPassTypes().get(passId);
        if (passType == null) {
            throw new PassNotFoundException(passId);
        }
        Tier tierObject = passType.getTiers().get(tier);
        return tierObject == null ? passType.getDefaultPointsRequired() : tierObject.getRequiredPoints();
    }

    @Override
    public void givePoints(User user, int points) {
        int maxTier = this.passLoader.getMaxTier();
        if (user.getTier() >= maxTier) {
            this.rewardCurrency(user, points);
            return;
        }
        user.updatePoints(current -> current.add(BigInteger.valueOf(points)));
        this.updateUserTier(user);
    }

    @Override
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
                    Action.executeSimple(player, this.passLoader.getPassTypes().get(user.getPassId()).getTierUpActions(), this.plugin, new Replacer()
                            .set("tier", tier)
                            .set("player", player.getName()));
                }
            } else {
                break;
            }
        }
        if (user.getTier() >= maxTier) {
            user.updatePoints(current -> {
                this.rewardCurrency(user, current.intValue());
                return BigInteger.ZERO;
            });
        }
    }

    @Override
    public void reward(User user, int tier, boolean ignoreRestrictions) {
        this.passLoader.getPassTypes().values().stream().map(PassType::getId).forEach(passId -> this.reward(user, passId, tier, ignoreRestrictions));
    }

    @Override
    public void reward(User user, String passId, int tier, boolean ignoreRestrictions) {
        boolean autoReceiveRewards = this.settingsConfig.bool("current-season.auto-receive-rewards");
        Tier tierObject = this.getTier(tier, passId);
        if (tierObject == null) {
            return;
        }
        if (user.hasPassId(passId) && (user.getPassId().equals("free") || passId.equals("premium") || !this.freePassConfig.bool("dont-give-premium-free-rewards"))) {
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
                    this.plugin.runSync(() -> Bukkit.getPluginManager().callEvent(event));
                    event.ifNotCancelled(consumerEvent -> maybeReward.get().reward(player, tier));
                }
            }
        }
    }

    @Override
    public void rewardCurrency(User user, int points) {
        String method = this.settingsConfig.string("reward-excess-points.method");
        if (method == null || method.isEmpty() || method.equalsIgnoreCase("none")) {
            return;
        }
        int rewardAmount = points * this.settingsConfig.integer("reward-excess-points.currency-per-point.".concat(user.getPassId()));
        switch (method.toLowerCase()) {
            case "vault":
                if (this.plugin.getEconomy() != null) {
                    this.plugin.getEconomy().depositPlayer(user.getPlayer(), rewardAmount);
                }
                break;
            case "internal":
                this.plugin.runSync(() -> user.updateCurrency(current -> current.add(BigInteger.valueOf(rewardAmount))));
                break;
            default:
                this.plugin.getLogger().severe("Unknown reward method 'reward-excess-points.method'.");
                break;
        }
    }
}
