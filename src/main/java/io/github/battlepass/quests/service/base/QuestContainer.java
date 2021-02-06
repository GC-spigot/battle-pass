package io.github.battlepass.quests.service.base;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.executor.QuestExecutionBuilder;
import io.github.battlepass.quests.workers.pipeline.QuestPipeline;
import org.bukkit.event.Listener;

public class QuestContainer implements Listener {
    private final QuestPipeline questPipeline;

    public QuestContainer(BattlePlugin plugin) {
        this.questPipeline = plugin.getQuestPipeline();
    }

    protected QuestExecutionBuilder executionBuilder(String questType) {
        return QuestExecutionBuilder.of(this, questType);
    }

    public QuestPipeline getQuestPipeline() {
        return this.questPipeline;
    }
}
