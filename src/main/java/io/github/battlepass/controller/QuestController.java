package io.github.battlepass.controller;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class QuestController {
    private final QuestCache questCache;
    private final Function<User, Map<String, ConcurrentHashMap<String, Integer>>> questMap = user -> user.getQuestStore().asMap();

    public QuestController(BattlePlugin plugin) {
        this.questCache = plugin.getQuestCache();
    }

    public Map<String, Integer> getQuests(User user, String categoryId) {
        return this.questMap.apply(user).get(categoryId);
    }

    public boolean isQuestDone(User user, Quest quest) {
        return this.getQuestProgress(user, quest) >= quest.getRequiredProgress();
    }

    public boolean isWeekDone(User user, int week) {
        String categoryId = "week-".concat(String.valueOf(week));
        Map<String, Integer> weekQuests = this.getQuests(user, categoryId);
        if (weekQuests == null) {
            return week == 0;
        }
        for (Map.Entry<String, Integer> entry : weekQuests.entrySet()) {
            Quest quest = this.questCache.getQuest(categoryId, entry.getKey());
            if (entry.getValue() < quest.getRequiredProgress()) {
                return false;
            }
        }
        return true;
    }

    public boolean resetQuest(User user, Quest quest) {
        Map<String, Integer> quests = this.getQuests(user, quest.getCategoryId());
        if (quests == null) {
            return false;
        }
        quests.remove(quest.getId());
        return true;
    }

    public int getQuestProgress(User user, Quest quest) {
        return this.failedIndex(user, quest).equals(FailedIndex.NONE) ? this.getQuests(user, quest.getCategoryId()).get(quest.getId()) : 0;
    }

    public int setQuestProgress(User user, Quest quest, int progress) {
        this.fillFailedIndexes(user, quest);
        this.getQuests(user, quest.getCategoryId()).put(quest.getId(), Math.min(quest.getRequiredProgress(), progress));
        return this.getQuestProgress(user, quest);
    }

    /**
     * Adds progress to a quest and
     * @param user The user to apply to
     * @param quest The quest to add progress for
     * @param progress The amount of progress to add
     * @param reward Whether to give the player points from this or not (default is no).
     * @return The new progress of the quest.
     */
    public int addQuestProgress(User user, Quest quest, int progress, boolean reward) {
        int initialProgress = this.getQuestProgress(user, quest);
        if (initialProgress < quest.getRequiredProgress()) {
            int modifiedProgress = this.setQuestProgress(user, quest, Math.min(initialProgress + progress, quest.getRequiredProgress()));
            if (modifiedProgress >= quest.getRequiredProgress() && reward) {
                user.updatePoints(current -> current.add(BigInteger.valueOf(quest.getPoints())));
            }
            return modifiedProgress;
        }
        return initialProgress;
    }

    public int addQuestProgress(User user, Quest quest, int progress) {
        return this.addQuestProgress(user, quest, progress, false);
    }

    private void fillFailedIndexes(User user, Quest quest) {
        Predicate<FailedIndex> failedIndex = index -> this.failedIndex(user, quest).equals(index);
        if (failedIndex.test(FailedIndex.CATEGORY_LAYER)) {
            this.questMap.apply(user).put(quest.getCategoryId(), new ConcurrentHashMap<>());
        }
        if (failedIndex.test(FailedIndex.QUEST_LAYER)) {
            this.getQuests(user, quest.getCategoryId()).put(quest.getId(), 0);
        }
    }

    private FailedIndex failedIndex(User user, Quest quest) {
        if (!this.questMap.apply(user).containsKey(quest.getCategoryId())) {
            return FailedIndex.CATEGORY_LAYER;
        }
        if (!this.getQuests(user, quest.getCategoryId()).containsKey(quest.getId())) {
            return FailedIndex.QUEST_LAYER;
        }
        return FailedIndex.NONE;
    }

    private enum FailedIndex {
        CATEGORY_LAYER, QUEST_LAYER, NONE
    }
}
