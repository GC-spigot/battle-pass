package io.github.battlepass.objects.user;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hyfe.simplespigot.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

public class User {
    private final UUID uuid;
    private final QuestStore questStore;
    private String passId;
    private int tier;
    private BigInteger points;
    private BigInteger currency;
    private boolean bypassLockedWeeks;
    private final Map<String, TreeSet<Integer>> pendingTiers;

    public User(UUID uuid) {
        this(uuid, new QuestStore(), 1, BigInteger.ZERO, BigInteger.ZERO, "free", false, Maps.newHashMap());
    }

    public User(UUID uuid, QuestStore questStore, int tier, BigInteger points, BigInteger currency, String passId, boolean bypassLockedWeeks, Map<String, TreeSet<Integer>> pendingTiers) {
        this.uuid = uuid;
        this.questStore = questStore;
        this.tier = tier;
        this.passId = passId;
        this.points = points;
        this.currency = currency;
        this.bypassLockedWeeks = bypassLockedWeeks;
        this.pendingTiers = pendingTiers;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public QuestStore getQuestStore() {
        return this.questStore;
    }

    public String getPassId() {
        return this.passId;
    }

    public void setPassId(String passId) {
        this.passId = passId;
    }

    public boolean hasPassId(String passId) {
        return this.passId.equalsIgnoreCase(passId) || (this.passId.equalsIgnoreCase("premium") && passId.equalsIgnoreCase("free"));
    }

    public int getTier() {
        return this.tier;
    }

    public int updateTier(IntUnaryOperator current) {
        this.tier = current.applyAsInt(this.tier);
        return this.tier;
    }

    public BigInteger getPoints() {
        return this.points;
    }

    public BigInteger updatePoints(UnaryOperator<BigInteger> current) {
        this.points = current.apply(this.points);
        return this.points;
    }

    public BigInteger getCurrency() {
        return this.currency;
    }

    public BigInteger updateCurrency(UnaryOperator<BigInteger> current) {
        this.currency = current.apply(this.currency);
        return this.currency;
    }

    public boolean bypassesLockedWeeks() {
        return this.bypassLockedWeeks;
    }

    public boolean toggleBypassLockedWeeks() {
        this.bypassLockedWeeks = !this.bypassLockedWeeks;
        return this.bypassLockedWeeks;
    }

    public Map<String, TreeSet<Integer>> getPendingTiers() {
        return this.pendingTiers;
    }

    public TreeSet<Integer> getPendingTiers(String passId) {
        return this.pendingTiers.get(passId);
    }

    public boolean hasPendingTier(String passId, int tier) {
        if (this.pendingTiers.containsKey(passId)) {
            return this.pendingTiers.get(passId).contains(tier);
        }
        return false;
    }

    public void addPendingTier(String passId, int tier) {
        this.pendingTiers.putIfAbsent(passId, Sets.newTreeSet());
        this.pendingTiers.get(passId).add(tier);
    }
}
