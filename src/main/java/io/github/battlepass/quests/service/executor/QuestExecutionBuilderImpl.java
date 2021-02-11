package io.github.battlepass.quests.service.executor;

import io.github.battlepass.objects.quests.variable.ExecutableQuestResult;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.workers.pipeline.QuestPipeline;
import io.github.battlepass.service.CheckHelper;
import me.hyfe.simplespigot.annotations.Nullable;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

public class QuestExecutionBuilderImpl implements QuestExecutionBuilder {
    private final QuestPipeline pipeline;
    private final String questType;

    private Player player;
    private BigInteger progress;
    private boolean overrideUpdate;

    private ExecutableQuestResult questResult;

    public QuestExecutionBuilderImpl(QuestPipeline pipeline, String questType) {
        this.pipeline = pipeline;
        this.questType = questType;
    }

    @Override
    public void buildAndExecute() {
        if (this.player != null) {
            QuestExecution execution = this.build();
            this.pipeline.handle(execution);
        }
    }

    @Override
    public QuestExecution build() {
        String baseMessage = "QuestExecution Build -> %s must be set.";
        if (this.player == null) {
            throw new IllegalStateException(String.format(baseMessage, "Player"));
        }
        if (this.questType == null || this.questType.isEmpty()) {
            throw new IllegalStateException(String.format(baseMessage, "Quest type"));
        }
        if (this.progress == null) {
            throw new IllegalStateException(String.format(baseMessage, "Progress"));
        }
        if (this.questResult == null) {
            this.questResult = new ExecutableQuestResult().root("none");
        }
        return new QuestExecution(this.player, this.questType, this.progress, this.overrideUpdate, this.questResult);
    }

    @Override
    public QuestExecutionBuilder player(@Nullable Player player) {
        this.player = player;
        return this;
    }

    @Deprecated
    @Override
    public QuestExecutionBuilder questResult(UnaryOperator<QuestResult> resultOperator) {
        this.questResult = (ExecutableQuestResult) resultOperator.apply(new ExecutableQuestResult());
        return this;
    }

    @Override
    public QuestExecutionBuilder root(String root) {
        if (this.questResult == null) {
            this.questResult = new ExecutableQuestResult();
        }
        this.questResult.root(root);
        return this;
    }

    @Override
    public QuestExecutionBuilder root(Block rootBlock) {
        CheckHelper.notNull(rootBlock, "Quest execution blocks");
        if (this.questResult == null) {
            this.questResult = new ExecutableQuestResult();
        }
        this.questResult.root(rootBlock);
        return this;
    }

    @Override
    public QuestExecutionBuilder root(ItemStack rootItem) {
        CheckHelper.notNull(rootItem, "Quest execution items");
        if (this.questResult == null) {
            this.questResult = new ExecutableQuestResult();
        }
        this.questResult.root(rootItem);
        return this;
    }

    @Override
    public QuestExecutionBuilder subRoot(String key, String value) {
        if (this.questResult == null) {
            this.questResult = new ExecutableQuestResult();
        }
        this.questResult.subRoot(key, value);
        return this;
    }

    @Override
    public QuestExecutionBuilder subRoot(ItemStack itemStack) {
        CheckHelper.notNull(itemStack, "Quest execution sub root items");
        return this.subRoot("item", itemStack.getType().toString());
    }

    @Override
    public QuestExecutionBuilder progressSingle() {
        return this.progress(BigInteger.ONE);
    }

    @Override
    public QuestExecutionBuilder progress(BigInteger progress) {
        CheckHelper.notNull(progress, "Quest execution progress");
        this.progress = progress;
        return this;
    }

    @Override
    public QuestExecutionBuilder overrideUpdate() {
        this.overrideUpdate = true;
        return this;
    }
}
