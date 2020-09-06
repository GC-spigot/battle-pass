package io.github.battlepass.objects.pass;

import java.util.List;

public class Tier {
    private final int tier;
    private final int requiredPoints;
    private final List<String> rewardIds;

    public Tier(int number, int requiredPoints, List<String> rewardIds) {
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

    public List<String> getRewardIds() {
        return this.rewardIds;
    }
}
