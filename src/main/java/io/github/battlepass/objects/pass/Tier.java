package io.github.battlepass.objects.pass;

import java.util.Set;

public class Tier {
    private final int tier;
    private final int requiredPoints;
    private final Set<String> rewardIds;

    public Tier(int number, int requiredPoints, Set<String> rewardIds) {
        this.tier = number;
        this.requiredPoints = requiredPoints;
        this.rewardIds = rewardIds;
    }

    public int getTier() {
        return this.tier;
    }

    public int getRequiredPoints() {
        return this.requiredPoints;
    }

    public Set<String> getRewardIds() {
        return this.rewardIds;
    }
}
