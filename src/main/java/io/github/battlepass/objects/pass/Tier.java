package io.github.battlepass.objects.pass;

import me.hyfe.simplespigot.annotations.Nullable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Tier {
    private final int tier;
    private final int requiredPoints;
    private final List<String> rewardIds;
    private final ItemStack lockedTierItem;
    private final ItemStack unlockedTierItem;
    private final ItemStack claimedTierItem;
    private final ItemStack doesntHavePassItem;

    public Tier(int number, int requiredPoints, List<String> rewardIds, ItemStack lockedTierItem, ItemStack unlockedTierItem, ItemStack claimedTierItem, ItemStack doesntHavePassItem) {
        this.tier = number;
        this.requiredPoints = requiredPoints;
        this.rewardIds = rewardIds;
        this.lockedTierItem = lockedTierItem;
        this.unlockedTierItem = unlockedTierItem;
        this.claimedTierItem = claimedTierItem;
        this.doesntHavePassItem = doesntHavePassItem;
    }

    public int getTier() {
        return this.tier;
    }

    public int getRequiredPoints() {
        return this.requiredPoints;
    }

    public List<String> getRewardIds() {
        return this.rewardIds;
    }

    @Nullable
    public ItemStack getItem(String itemKey) {
        switch (itemKey) {
            case "claimed-tier-item":
                return this.claimedTierItem;
            case "unlocked-tier-item":
                return this.unlockedTierItem;
            case "locked-tier-item":
                return this.lockedTierItem;
            case "doesnt-have-pass-item":
                return this.doesntHavePassItem;
            default:
                return null;
        }
    }
}
