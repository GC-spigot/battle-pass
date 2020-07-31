package io.github.battlepass.objects.pass;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.actions.Action;
import io.github.battlepass.cache.RewardCache;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import me.hyfe.simplespigot.text.Text;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PassType {
    private final Config config;
    private final String id;
    private final String name;
    private final String requiredPermission;
    private final int defaultPointsRequired;
    private final Map<Integer, Tier> tiers = Maps.newTreeMap();
    private final Set<Action> tierUpActions = Sets.newHashSet();

    public PassType(String id, Config config) {
        this.id = id;
        this.config = config;
        this.name = config.string("name");
        this.requiredPermission = config.string("required-permission");
        this.defaultPointsRequired = config.integer("default-points-required");
        for (String key : config.keys("tiers", false)) {
            if (!StringUtils.isNumeric(key)) {
                continue;
            }
            int tier = Integer.parseInt(key);
            int requiredPoints = config.has("tiers.".concat(key).concat(".required-points")) ? config.integer("tiers.".concat(key).concat(".required-points")) : this.defaultPointsRequired;
            Set<String> rewardIds = Sets.newHashSet(config.stringList("tiers.".concat(key).concat(".rewards")));
            this.tiers.put(tier, new Tier(tier, requiredPoints, rewardIds));
        }
        for (String action : config.stringList("tier-up-actions")) {
            this.tierUpActions.add(Action.parse(action));
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getRequiredPermission() {
        return this.requiredPermission;
    }

    public int getDefaultPointsRequired() {
        return this.defaultPointsRequired;
    }

    public TreeMap<Integer, Tier> getTiers() {
        return (TreeMap<Integer, Tier>) this.tiers;
    }

    public Set<Action> getTierUpActions() {
        return this.tierUpActions;
    }

    public ItemStack tierToItem(RewardCache rewardCache, User user, String passId, Tier tier, boolean hasTier) {
        boolean hasPass = user.hasPassId(passId);
        boolean hasClaimed = user.getPendingTiers(passId) != null && !user.getPendingTiers(passId).contains(tier.getTier());
        ItemStack itemStack = SpigotItem.toItem(this.config, "items.".concat(hasPass ? (hasTier ? (hasClaimed ? "claimed-tier-item" : "unlocked-tier-item") : "locked-tier-item") : "doesnt-have-pass-item"),
                replacer -> replacer.set("tier", tier.getTier()));
        if (itemStack == null) {
            itemStack = SpigotItem.toItem(this.config, "items.".concat(hasTier ? (hasClaimed ? "claimed-tier-item" : "unlocked-tier-item") : "locked-tier-item"),
                    replacer -> replacer.set("tier", tier.getTier()));
        }
        if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
            return itemStack;
        }
        List<String> updatedLore = Lists.newArrayList();
        ItemMeta itemMeta = itemStack.getItemMeta();
        for (String line : itemMeta.getLore()) {
            if (line.contains("%lore_addon%")) {
                for (String rewardId : tier.getRewardIds()) {
                    rewardCache.get(rewardId).ifPresent(reward -> updatedLore.addAll(Text.modify(reward.getLoreAddon())));
                }
            } else {
                updatedLore.add(line);
            }
        }
        itemMeta.setLore(updatedLore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
