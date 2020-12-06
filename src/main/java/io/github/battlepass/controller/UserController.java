package io.github.battlepass.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import lombok.SneakyThrows;
import me.hyfe.simplespigot.tuple.ImmutablePair;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UserController {
    private final QuestCache questCache;
    private final QuestController questController;
    private final Cache<UUID, Integer> completedQuests = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
    private final Map<UUID, Map<String, ImmutablePair<Long, Integer>>> completedQuestsPerWeek = Maps.newHashMap();

    public UserController(BattlePlugin plugin) {
        this.questCache = plugin.getQuestCache();
        this.questController = plugin.getQuestController();
    }

    @SneakyThrows
    public int getQuestsDone(User user, boolean useCache) {
        int questsDone = 0;
        if (useCache) {
            return this.completedQuests.get(user.getUuid(), () -> this.getQuestsDone(user, false));
        }
        for (Quest quest : this.questCache.getAllQuests()) {
            if (this.questController.isQuestDone(user, quest)) {
                questsDone++;
            }
        }
        return questsDone;
    }

    public int getQuestsDone(User user, String category) {
        if (this.completedQuestsPerWeek.containsKey(user.getUuid())) {
            ImmutablePair<Long, Integer> cachedDone = this.completedQuestsPerWeek.get(user.getUuid()).get(category);
            if (cachedDone != null && System.currentTimeMillis() - cachedDone.getKey() < 30000) {
                return cachedDone.getValue();
            } else {
                int calculated = this.getDoneForWeek(user, category);
                this.completedQuestsPerWeek.get(user.getUuid()).put(category, ImmutablePair.of(System.currentTimeMillis(), calculated));
                return calculated;
            }
        } else {
            int calculated = this.getDoneForWeek(user, category);
            Map<String, ImmutablePair<Long, Integer>> cacheMap = Maps.newHashMap();
            cacheMap.put(category, ImmutablePair.of(System.currentTimeMillis(), calculated));
            this.completedQuestsPerWeek.put(user.getUuid(), cacheMap);
            return calculated;
        }
    }

    private int getDoneForWeek(User user, String category) {
        Map<String, Quest> quests = this.questCache.getQuestsVerbatim(category);
        if (quests == null) {
            return -1;
        }
        int questsDone = 0;
        for (Quest quest : quests.values()) {
            if (this.questController.isQuestDone(user, quest)) {
                questsDone++;
            }
        }
        return questsDone;
    }
}
