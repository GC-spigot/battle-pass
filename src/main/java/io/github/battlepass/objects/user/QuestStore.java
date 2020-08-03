package io.github.battlepass.objects.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuestStore {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> quests = new ConcurrentHashMap<>();

    public Map<String, ConcurrentHashMap<String, Integer>> asMap() {
        return this.quests;
    }
}
