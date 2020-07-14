package io.github.battlepass.objects.quests;

import com.google.common.collect.Sets;
import io.github.battlepass.objects.quests.variable.Variable;
import me.hyfe.simplespigot.annotations.Nullable;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class Quest {
    private final String id;
    private final String categoryId;
    private final String name;
    private final ItemStack itemStack;
    private final String type;
    private final int requiredProgress;
    private final Variable variable;
    private final int points;
    private final Set<Integer> notifyAt = Sets.newHashSet();
    private final Set<String> whitelistedWorlds;
    private final Set<String> blacklistedWorlds;
    private final String exclusiveTo;

    public Quest(String id,
                 String categoryId,
                 String name,
                 ItemStack itemStack,
                 String type,
                 int requiredProgress,
                 Variable variable,
                 int points,
                 Set<Integer> notifyAt,
                 Set<String> whitelistedWorlds,
                 Set<String> blacklistedWorlds,
                 @Nullable String exclusiveTo) {
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
        for (int percentage : notifyAt) {
            this.notifyAt.add(this.requiredProgress * percentage / 100);
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

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public String getType() {
        return this.type;
    }

    public int getRequiredProgress() {
        return this.requiredProgress;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public int getPoints() {
        return this.points;
    }

    public Set<Integer> getNotifyAt() {
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
}
