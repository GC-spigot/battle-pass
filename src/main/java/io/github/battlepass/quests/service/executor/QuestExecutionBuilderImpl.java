package io.github.battlepass.quests.service.executor;

import io.github.battlepass.objects.quests.variable.ExecutableQuestResult;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.workers.pipeline.QuestPipeline;
import io.github.battlepass.service.Checks;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

public class QuestExecutionBuilderImpl implements QuestExecutionBuilder {
    private final QuestPipeline pipeline;
    private Player player;

    private final String questType;
    private BigInteger progress;
    private boolean overrideUpdate;

    private ExecutableQuestResult questResult;

    public QuestExecutionBuilderImpl(QuestPipeline pipeline, String questType) {
        this.pipeline = pipeline;
        this.questType = questType;
    }

    @Override
    public void execute() {
        QuestExecution execution = new QuestExecution(this.player, this.questType, this.progress, this.overrideUpdate, this.questResult);
        // this.pipeline.handle(execution);
        // TODO
    }

    @Override
    public QuestExecutionBuilder player(Player player) {
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
        if (this.questResult == null)
            this.questResult = new ExecutableQuestResult();
        this.questResult.root(root);
        return this;
    }

    @Override
    public QuestExecutionBuilder root(Block rootBlock) {
        Checks.notNull(rootBlock, "Quest execution blocks");
        if (this.questResult == null)
            this.questResult = new ExecutableQuestResult();
        this.questResult.root(rootBlock);
        return this;
    }

    @Override
    public QuestExecutionBuilder root(ItemStack rootItem) {
        Checks.notNull(rootItem, "Quest execution items");
        if (this.questResult == null)
            this.questResult = new ExecutableQuestResult();
        this.questResult.root(rootItem);
        return this;
    }

    @Override
    public QuestExecutionBuilder subRoot(String key, String value) {
        if (this.questResult == null)
            this.questResult = new ExecutableQuestResult();
        this.questResult.subRoot(key, value);
        return this;
    }

    @Override
    public QuestExecutionBuilder subRoot(ItemStack itemStack) {
        Checks.notNull(itemStack, "Quest execution sub root items");
        return this.subRoot("item", itemStack.getType().toString());
    }

    @Override
    public QuestExecutionBuilder progressSingle() {
        return this.progress(BigInteger.ONE);
    }

    @Override
    public QuestExecutionBuilder progress(BigInteger progress) {
        Checks.notNull(progress, "Quest execution progress");
        this.progress = progress;
        return this;
    }

    @Override
    public QuestExecutionBuilder overrideUpdate() {
        this.overrideUpdate = true;
        return this;
    }
}
