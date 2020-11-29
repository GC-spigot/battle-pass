package io.github.battlepass.objects.quests;

import com.google.common.collect.Maps;
import io.github.battlepass.objects.quests.variable.Variable;
import me.hyfe.simplespigot.annotations.Nullable;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class Quest {
    private final String id;
    private final String categoryId;
    private final String name;
    private final String type;
    private final BigInteger requiredProgress;
    private final Variable variable;
    private final int points;
    private final Map<BigInteger, Integer> notifyAt = Maps.newHashMap();
    private final Set<String> whitelistedWorlds;
    private final Set<String> blacklistedWorlds;
    private final String exclusiveTo;
    private final boolean antiAbuse;
    private ItemStack itemStack;

    public Quest(String id,
                 String categoryId,
                 String name,
                 ItemStack itemStack,
                 String type,
                 BigInteger requiredProgress,
                 Variable variable,
                 int points,
                 Set<Integer> notifyPercentages,
                 Set<String> whitelistedWorlds,
                 Set<String> blacklistedWorlds,
                 @Nullable String exclusiveTo,
                 boolean antiAbuse) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.itemStack = itemStack;
        this.type = type;
        this.requiredProgress = requiredProgress;
        this.variable = variable;
        this.points = points;
        this.whitelistedWorlds = whitelistedWorlds;
        this.blacklistedWorlds = blacklistedWorlds;
        this.exclusiveTo = exclusiveTo;
        this.antiAbuse = antiAbuse;
        for (int percentage : notifyPercentages) {
            this.notifyAt.put(this.requiredProgress.multiply(BigInteger.valueOf(percentage)).divide(BigInteger.valueOf(100)), percentage);
        }
    }

    public String getId() {
        return this.id;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public BigInteger getRequiredProgress() {
        return this.requiredProgress;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public int getPoints() {
        return this.points;
    }

    public Map<BigInteger, Integer> getNotifyAt() {
        return this.notifyAt;
    }

    public Set<String> getWhitelistedWorlds() {
        return this.whitelistedWorlds;
    }

    public Set<String> getBlacklistedWorlds() {
        return this.blacklistedWorlds;
    }

    public String getExclusiveTo() {
        return this.exclusiveTo;
    }

    public boolean isAntiAbuse() {
        return this.antiAbuse;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
