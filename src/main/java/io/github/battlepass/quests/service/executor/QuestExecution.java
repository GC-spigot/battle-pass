package io.github.battlepass.quests.service.executor;

import io.github.battlepass.objects.quests.variable.ExecutableQuestResult;
import org.bukkit.entity.Player;

import java.math.BigInteger;

public class QuestExecution {
    private final Player player;
    private final String questType;
    private final BigInteger progress;
    private final boolean overrideUpdate;
    private final ExecutableQuestResult questResult;

    public QuestExecution(Player player, String questType, BigInteger progress, boolean overrideUpdate, ExecutableQuestResult questResult) {
        this.player = player;
        this.questType = questType;
        this.progress = progress;
        this.overrideUpdate = overrideUpdate;
        this.questResult = questResult;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getQuestType() {
        return this.questType;
    }

    public BigInteger getProgress() {
        return this.progress;
    }

    public boolean isOverrideUpdate() {
        return this.overrideUpdate;
    }

    public ExecutableQuestResult getQuestResult() {
        return this.questResult;
    }
}
