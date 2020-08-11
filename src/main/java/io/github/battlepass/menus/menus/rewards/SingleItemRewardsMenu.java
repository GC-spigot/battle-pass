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
import io.github.battlepass.objects.pass.Tier;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.tuple.MutablePair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SingleItemRewardsMenu extends ConfigMenu implements PageMethods, UserDependent {
    private final BattlePassApi api;
    private final PassLoader passLoader;
    private final RewardCache rewardCache;
    private final DailyQuestReset dailyQuestReset;
    private final List<Integer> rewardSlots;
    private final Map<Integer, Set<Integer>> cachedPageIndexes = Maps.newHashMap();
    private final boolean autoReceiveRewards;
    private final User user;

    private int page = 1;

    public SingleItemRewardsMenu(BattlePlugin plugin, Config config, Player player) {
        super(plugin, config, player);
        this.api = plugin.getLocalApi();
        this.passLoader = plugin.getPassLoader();
        this.rewardCache = plugin.getRewardCache();
        this.dailyQuestReset = plugin.getDailyQuestReset();
        this.rewardSlots = Lists.newArrayList(MenuService.parseSlots(this, this.config, "reward-slots"));
        this.autoReceiveRewards = this.plugin.getConfig("settings").bool("current-season.auto-receive-rewards");
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
    }

    @Override
    public void redraw() {
        this.computeAndDrawRewards(() -> this.drawConfigItems(replacer -> replacer.set("daily_time_left", this.dailyQuestReset.asString())));
    }

    @Override
    public void nextPage(Runnable runnable) {
        int maxTier = Math.max(this.passLoader.passTypeOfId("free").getTiers().lastKey(), this.passLoader.passTypeOfId("premium").getTiers().lastKey());
        if (maxTier >= this.rewardSlots.size() * this.page + 1) {
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

    @Override
    public boolean isUserViable() {
        return this.user != null;
    }

    public void computeAndDrawRewards(Runnable runBeforeSet) {
        Map<Integer, MutablePair<Tier, Tier>> allTiers = Maps.newTreeMap(); // tier as int, <free tier, premium tier>
        for (Map.Entry<Integer, Tier> entry : this.passLoader.passTypeOfId("free").getTiers().entrySet()) {
            allTiers.put(entry.getKey(), MutablePair.of(entry.getValue(), null));
        }
        for (Map.Entry<Integer, Tier> entry : this.passLoader.passTypeOfId("premium").getTiers().entrySet()) {
            if (allTiers.containsKey(entry.getKey())) {
                allTiers.get(entry.getKey()).setValue(entry.getValue());
            } else {
                allTiers.put(entry.getKey(), MutablePair.of(null, entry.getValue()));
            }
        }
        this.cachedPageIndexes.computeIfAbsent(this.page, key -> {
            Set<Integer> indexes = Sets.newLinkedHashSetWithExpectedSize(this.rewardSlots.size());
            for (int slot = 0; slot < this.rewardSlots.size(); slot++) {
                int index = (this.page - 1) * this.rewardSlots.size() + slot + 1;
                if (allTiers.containsKey(index)) {
                    indexes.add(index);
                }
            }
            return indexes;
        });
        for (int slot : this.rewardSlots) {
            this.flush(slot);
        }
        runBeforeSet.run();
        for (int index : this.cachedPageIndexes.get(this.page)) {
            MutablePair<Tier, Tier> freePremiumPair = allTiers.get(index);
            int tierNum = freePremiumPair.getKey().getTier();
            boolean hasPendingPremium = this.user.hasPendingTier("premium", tierNum);
            boolean hasPendingFree = this.user.hasPendingTier("free", tierNum);
            this.item(MenuItem.builderOf(this.getTierItem(freePremiumPair, hasPendingPremium, hasPendingFree))
                    .rawSlot(this.rewardSlots.get(index - (this.rewardSlots.size() * (this.page - 1) + 1)))
                    .onClick((menuItem, clickType) -> {
                        if (!this.autoReceiveRewards && (hasPendingFree || hasPendingPremium)) {
                            if (hasPendingPremium) {
                                this.api.reward(this.user, "premium", tierNum, true);
                                this.user.getPendingTiers("premium").remove(tierNum);
                            }
                            if (hasPendingFree) {
                                this.api.reward(this.user, "free", tierNum, true);
                                this.user.getPendingTiers("free").remove(tierNum);
                            }
                            this.redraw();
                        }
                    })
                    .build());
        }
    }

    private ItemStack getTierItem(MutablePair<Tier, Tier> freePremiumPair, boolean hasPendingPremium, boolean hasPendingFree) {
        /*Replace replace = replacer -> replacer
                .set("")
        ItemStack itemStack = SpigotItem.toItem(this.config, "items." + +"-tier-item");
        return
        if (freePremiumPair.getValue())*/
        return null;
    }
}
