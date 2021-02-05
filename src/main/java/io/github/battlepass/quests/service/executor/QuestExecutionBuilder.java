package io.github.battlepass.quests.service.executor;

import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.service.base.ExternalQuestContainer;
import io.github.battlepass.quests.service.base.QuestContainer;
import io.github.battlepass.service.CheckHelper;
import me.hyfe.simplespigot.annotations.NotNull;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.CheckReturnValue;
import java.math.BigInteger;
import java.util.function.UnaryOperator;

public interface QuestExecutionBuilder {

    void buildAndExecute() throws IllegalStateException;

    @NotNull
    QuestExecution build() throws IllegalStateException;

    @CheckReturnValue
    @NotNull
    static QuestExecutionBuilder of(@NotNull QuestContainer container, @NotNull String questType) {
        String buildType;
        if (container instanceof ExternalQuestContainer) {
            buildType = ((ExternalQuestContainer) container).getPrefix().concat(questType);
        } else {
            buildType = questType;
        }
        return new QuestExecutionBuilderImpl(container.getQuestPipeline(), buildType);
    }

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder player(@NotNull Player player);

    @Deprecated
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder questResult(@NotNull UnaryOperator<QuestResult> resultOperator);

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder root(@NotNull String root);

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder root(@NotNull Block rootBlock);

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder root(@NotNull ItemStack rootItem);

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder subRoot(@NotNull String key, @NotNull String value);

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder subRoot(@NotNull ItemStack itemStack);

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder progressSingle();

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder progress(@NotNull BigInteger progress);

    @CheckReturnValue
    @NotNull
    default QuestExecutionBuilder progress(@NotNull int progress) {
        CheckHelper.notNull(progress, "Quest execution progress");
        return this.progress(BigInteger.valueOf(progress));
    }

    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder overrideUpdate();
}
