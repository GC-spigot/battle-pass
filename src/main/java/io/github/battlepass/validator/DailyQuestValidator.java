package io.github.battlepass.validator;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.objects.quests.Quest;

public class DailyQuestValidator {
    private final DebugLogger logger;

    public DailyQuestValidator(BattlePlugin plugin) {
        this.logger = plugin.getDebugLogger();
    }

    public boolean checkQuest(Quest quest) {
        if (quest == null) {
            BattlePlugin.logger().severe("Failed to load a daily quest. Make sure you haven't deleted any (if so /bpa refresh daily quests will fix this)" +
                    " and that there aren't any broken daily quests.");
            this.logger.log("Failed to load a daily quest - quest was null.");
            return false;
        }
        return true;
    }
}
