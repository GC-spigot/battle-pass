package io.github.battlepass.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UserController {
    private final QuestCache questCache;
    private final QuestController questController;
    private final Cache<UUID, Integer> completedQuests = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

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
        int questsDone = 0;
        Map<String, Quest> quests = this.questCache.getQuestsVerbatim(category);
        if (quests == null) {
            return -1;
        }
        for (Quest quest : quests.values()) {
            if (this.questController.isQuestDone(user, quest)) {
                questsDone++;
            }
        }
        return questsDone;
    }
}
