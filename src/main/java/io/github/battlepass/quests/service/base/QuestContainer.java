package io.github.battlepass.quests.service.base;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.executor.QuestExecutionBuilder;
import io.github.battlepass.quests.workers.pipeline.QuestPipeline;
import me.hyfe.simplespigot.annotations.NotNull;
import org.bukkit.event.Listener;

public class QuestContainer implements Listener {
    private final QuestPipeline questPipeline;

    /**
     * Normally only used internally by BattlePass but just using a QuestContainer is fine as long as it's not in a public plugin.
     * Alternatively see {@link ExternalQuestContainer} for use with your plugin.
     *
     * @param plugin {@link BattlePlugin} instance. This is provided in {@link io.github.battlepass.registry.quest.Instantiator} when you register your quest in the {@link io.github.battlepass.registry.quest.QuestRegistry}
     */
    public QuestContainer(@NotNull BattlePlugin plugin) {
        this.questPipeline = plugin.getQuestPipeline();
    }

    /**
     * It's fine to call {@link QuestExecutionBuilder#of} yourself but this will just make life easier.
     *
     * @param questType The name for execution (called type in quest configs). This will have pluginName_ prefixed if you're using an {@link ExternalQuestContainer}
     * @return A {@link QuestExecutionBuilder} for you to build information into and execute.
     */
    @NotNull
    protected QuestExecutionBuilder executionBuilder(@NotNull String questType) {
        return QuestExecutionBuilder.of(this, questType);
    }

    @NotNull
    public QuestPipeline getQuestPipeline() {
        return this.questPipeline;
    }
}
