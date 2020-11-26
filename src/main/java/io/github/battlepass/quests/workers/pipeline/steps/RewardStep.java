package io.github.battlepass.quests.workers.pipeline.steps;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;

public class RewardStep {
    private final BattlePlugin plugin;

    public RewardStep(BattlePlugin plugin) {
        this.plugin = plugin;
    }

    public void process(User user, Quest quest) {
        this.plugin.runSync(() -> this.plugin.getLocalApi().givePoints(user, quest.getPoints()));
    }
}