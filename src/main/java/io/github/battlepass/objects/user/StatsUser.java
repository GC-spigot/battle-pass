package io.github.battlepass.objects.user;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.UUID;

public class StatsUser implements Comparable<StatsUser> {
    private final UUID uuid;
    private final String name;
    private final int tier;
    private final String passId;
    private final BigInteger points;
    private final BigInteger totalPoints;

    public StatsUser(UUID uuid, String name, int tier, String passId, BigInteger points, BigInteger totalPoints) {
        this.uuid = uuid;
        this.name = name;
        this.tier = tier;
        this.passId = passId;
        this.points = points;
        this.totalPoints = totalPoints;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public int getTier() {
        return this.tier;
    }

    public String getPassId() {
        return this.passId;
    }

    public BigInteger getPoints() {
        return this.points;
    }

    public BigInteger getTotalPoints() {
        return this.totalPoints;
    }

    @Override
    public int compareTo(@NotNull StatsUser user) {
        return this.totalPoints.compareTo(user.getTotalPoints());
    }
}
