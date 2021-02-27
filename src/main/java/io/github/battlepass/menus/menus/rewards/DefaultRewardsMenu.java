package io.github.battlepass.menus.menus.rewards;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.cache.RewardCache;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.menus.PageMethods;
import io.github.battlepass.menus.UserDependent;
import io.github.battlepass.menus.service.extensions.ConfigMenu;
import io.github.battlepass.objects.pass.PassType;
import io.github.battlepass.objects.pass.Tier;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DefaultRewardsMenu extends ConfigMenu implements PageMethods, UserDependent {
    private final BattlePassApi api;
    private final PassLoader passLoader;
    private final RewardCache rewardCache;
    private final DailyQuestReset dailyQuestReset;
    private final List<Integer> freeTierSlots;
    private final List<Integer> premiumTierSlots;
    private final Collection<Integer> progressTrackSlots;
    private final Map<Integer, Set<Integer>> freeCachedPageIndexes = Maps.newHashMap();
    private final Map<Integer, Set<Integer>> premiumCachedPageIndexes = Maps.newHashMap();
    private final boolean autoReceiveRewards;
    private final boolean hideTiersWithoutRewards;
    private final boolean closeOnClaim;
    private final User user;

    private int page = 1;

    public DefaultRewardsMenu(BattlePlugin plugin, Config config, Player player) {
        super(plugin, config, player);
        this.api = plugin.getLocalApi();
        this.passLoader = plugin.getPassLoader();
        this.rewardCache = plugin.getRewardCache();
        this.dailyQuestReset = plugin.getDailyQuestReset();
        this.freeTierSlots = Lists.newArrayList(MenuService.parseSlots(this, this.config, "free-reward-slots"));
        this.premiumTierSlots = Lists.newArrayList(MenuService.parseSlots(this, this.config, "premium-reward-slots"));
        this.progressTrackSlots = MenuService.parseSlots(this, this.config, "progress-track-slots");
        this.autoReceiveRewards = this.plugin.getConfig("settings").bool("current-season.auto-receive-rewards");
        this.closeOnClaim = config.bool("close-on-reward-claim");
        this.hideTiersWithoutRewards = config.bool("hide-tiers-without-rewards");
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
    }

    @Override
    public void redraw() {
        this.drawAndComputePageableItems(() -> this.drawConfigItems(replacer -> replacer
                .set("daily_time_left", this.dailyQuestReset.asString())
                .tryAddPapi(this.player)));
    }

    @Override
    public boolean isUserViable() {
        return this.user != null;
    }

    @Override
    public void nextPage(Runnable runnable) {
        boolean hasPaged = false;
        if (this.passLoader.passTypeOfId("free").getTiers().lastKey() >= this.freeTierSlots.size() * this.page + 1) {
            this.page++;
            runnable.run();
            hasPaged = true;
        }
        if (!hasPaged && this.passLoader.passTypeOfId("premium").getTiers().lastKey() >= this.premiumTierSlots.size() * this.page + 1) {
            this.page++;
            runnable.run();
        }
    }

    @Override
    public void previousPage(Runnable runnable) {
        this.page--;
        runnable.run();
    }

    @Override
    public int getPage() {
        return this.page;
    }

    private void drawAndComputePageableItems(Runnable runBeforeSet) {
        this.drawRewards("free", runBeforeSet, this.freeTierSlots, this.freeCachedPageIndexes);
        this.drawRewards("premium", () -> {}, this.premiumTierSlots, this.premiumCachedPageIndexes);
        boolean drawPastMaxTier = this.config.bool("draw-past-max-tier");
        int iterations = 0;
        for (int slot : this.progressTrackSlots) {
            int tier = (this.page - 1) * this.progressTrackSlots.size() + iterations + 1;
            if (tier > this.passLoader.getMaxTier() && !drawPastMaxTier) {
                this.flush(slot);
                continue;
            }
            this.item(MenuItem
                    .builderOf(SpigotItem.toItem(
                            this.config,
                            "static-items.progress-track-".concat(this.user.getTier() < tier ? "locked" : "unlocked").concat("-item"),
                            replacer -> replacer.set("tier", tier).tryAddPapi(this.player)))
                    .rawSlot(slot)
                    .build());
            iterations++;
        }
    }

    private void drawRewards(String passId, Runnable runBeforeSet, List<Integer> slots, Map<Integer, Set<Integer>> cachedPageIndexes) {
        TreeMap<Integer, Tier> tiers = this.passLoader.passTypeOfId(passId).getTiers();
        PassType passType = this.passLoader.passTypeOfId(passId);
        cachedPageIndexes.computeIfAbsent(this.page, key -> {
            Set<Integer> indexes = Sets.newLinkedHashSetWithExpectedSize(slots.size());
            for (int slot = 0; slot < slots.size(); slot++) {
                int index = (this.page - 1) * slots.size() + slot + 1;
                if (tiers.containsKey(index) && !(this.hideTiersWithoutRewards && tiers.get(index).getRewardIds().isEmpty())) {
                    indexes.add(index);
                }
            }
            return indexes;
        });
        for (int slot : slots) {
            this.flush(slot);
        }
        runBeforeSet.run();
        for (int index : cachedPageIndexes.get(this.page)) {
            Tier tier = tiers.get(index);
            if (tier == null) {
                continue;
            }
            this.item(MenuItem
                    .builderOf(passType.tierToItem(this.plugin, this.rewardCache, this.user, passId, tier, this.user.getTier() >= index))
                    .rawSlot(slots.get(index - (slots.size() * (this.page - 1) + 1)))
                    .onClick((menuItem, clickType) -> {
                        int tierNum = tier.getTier();
                        if (!this.autoReceiveRewards && this.user.hasPendingTier(passId, tierNum)) {
                            if (this.closeOnClaim) {
                                this.close();
                            }
                            this.api.reward(this.user, passId, tier.getTier(), true);
                            this.user.getPendingTiers(passId).remove(tierNum);
                            if (!this.closeOnClaim) {
                                this.redraw();
                            }
                        }
                    })
                    .build()
            );
        }
    }
}
