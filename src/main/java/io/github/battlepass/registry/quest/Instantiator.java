package io.github.battlepass.registry.quest;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import io.github.battlepass.quests.service.base.QuestContainer;

/**
 * Used to cleanly pass in BattlePlugin to a class extending {@link QuestExecutor}.
 *
 * @param <T> {@link QuestExecutor} or {@link io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor}
 */
@FunctionalInterface
public interface Instantiator<T extends QuestContainer> {
    T init(BattlePlugin plugin);
}
