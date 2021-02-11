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

@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface QuestExecutionBuilder {

    /**
     * If you're instantiating this, check {@link QuestContainer#executionBuilder(String)} first as this may be easier.
     *
     * @param container The instance of the {@link QuestContainer} this should be created from.
     * @param questType The name of the quest to be executed (e.g break). This will automatically prefix plugin name if an instance of {@link ExternalQuestContainer}
     * @return A {@link QuestExecutionBuilder} for you to build information into and execute.
     */
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

    /**
     * Builds and executes the {@link QuestExecutionBuilder} with the set values
     *
     * @throws IllegalStateException if player, questType or progress is not set.
     */
    void buildAndExecute() throws IllegalStateException;

    /**
     * Generally you shouldn't need this method, you should just need {@link QuestExecutionBuilder#buildAndExecute()}.
     * This is just here for some off chance you need to build without executing so you can avoid a hacky method.
     *
     * @return a built version of your {@link QuestExecutionBuilder}.
     * @throws IllegalStateException if player, questType or progress is not set.
     */
    @NotNull
    @CheckReturnValue
    QuestExecution build() throws IllegalStateException;

    /**
     * @param player The player that will be executed for.
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder player(@NotNull Player player);

    /**
     * Only here for temporary use by BattlePass to allow old quest formats to work from external plugins.
     * This will probably be removed by 06/20201
     */
    @Deprecated
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder questResult(@NotNull UnaryOperator<QuestResult> resultOperator);

    /**
     * @param root The root variable for use in configuration to filter
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder root(@NotNull String root);

    /**
     * @param rootBlock The root variable for use in configuration to filter
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder root(@NotNull Block rootBlock);

    /**
     * @param rootItem The root variable for use in configuration to filter
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder root(@NotNull ItemStack rootItem);

    /**
     * @param key   The identifier to use for the sub root
     * @param value The value to use for the sub root
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder subRoot(@NotNull String key, @NotNull String value);

    /**
     * Just adds "item" as key and {@link ItemStack#getType()#toString()} as the value
     *
     * @param itemStack The {@link ItemStack} to add a sub root for
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder subRoot(@NotNull ItemStack itemStack);

    /**
     * Sets the progress of the execution to 1.
     * Equivalent to {@code QuestExecutionBuilder.progress(BigInteger.ONE)}
     *
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder progressSingle();

    /**
     * Sets the progress of the execution
     *
     * @param progress The amount to progress quests by
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder progress(@NotNull BigInteger progress);

    /**
     * Sets the progress of the execution.
     * This calls {@link BigInteger#valueOf(long)} so if you're setting the progress to 1, please use {@link QuestExecutionBuilder#progressSingle()}
     *
     * @param progress The amount to progress quests by
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    default QuestExecutionBuilder progress(@NotNull int progress) {
        CheckHelper.notNull(progress, "Quest execution progress");
        return this.progress(BigInteger.valueOf(progress));
    }

    /**
     * Sets overrideUpdate to true. This will mean the progress of the quest will be set to the progress in this class.
     * This is useful if you're doing reach X island level quests or similar and just use a loop every 30 seconds.
     *
     * @return modified instance to chain
     */
    @CheckReturnValue
    @NotNull
    QuestExecutionBuilder overrideUpdate();
}
