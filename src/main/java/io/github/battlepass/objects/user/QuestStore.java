package io.github.battlepass.objects.user;

import com.google.common.collect.Maps;

import java.util.Map;

public class QuestStore {
    private final Map<String, Map<String, Integer>> quests = Maps.newConcurrentMap();

    public Map<String, Map<String, Integer>> asMap() {
        return this.quests;
    }
}
