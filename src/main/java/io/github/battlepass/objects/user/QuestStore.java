package io.github.battlepass.objects.user;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuestStore {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, BigInteger>> quests = new ConcurrentHashMap<>();

    public Map<String, ConcurrentHashMap<String, BigInteger>> asMap() {
        return this.quests;
    }

    public void clear() {
        this.quests.clear();
    }
}
