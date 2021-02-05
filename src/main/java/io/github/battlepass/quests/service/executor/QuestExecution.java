package io.github.battlepass.quests.service.executor;

import io.github.battlepass.objects.quests.variable.ExecutableQuestResult;
import io.github.battlepass.objects.user.User;
import org.bukkit.entity.Player;

import java.math.BigInteger;

public class QuestExecution {
    private final Player player;
    private final String questType;
    private final BigInteger progress;
    private final boolean overrideUpdate;
    private final ExecutableQuestResult questResult;
    private User user;

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

    public boolean shouldOverrideUpdate() {
        return this.overrideUpdate;
    }

    public ExecutableQuestResult getQuestResult() {
        return this.questResult;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "player=" + this.player.getName() +
                ", questType='" + this.questType + '\'' +
                ", progress=" + this.progress.toString() +
                ", overrideUpdate=" + this.overrideUpdate +
                ", questResult=" + this.questResult.toString();
    }
}
