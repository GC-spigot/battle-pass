package io.github.battlepass.quests;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.ExecutableQuestResult;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.service.base.QuestContainer;
import io.github.battlepass.quests.service.executor.QuestExecutionBuilder;
import io.github.battlepass.quests.workers.pipeline.QuestPipeline;
import me.hyfe.simplespigot.text.replacer.Replace;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

public class QuestExecutor extends QuestContainer {

    public QuestExecutor(BattlePlugin plugin) {
        super(plugin);
    }

    @Deprecated
    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace, boolean overrideUpdate) {
        this.execute(name, player, BigInteger.valueOf(progress), result, replace, overrideUpdate);
    }

    @Deprecated
    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace) {
        this.execute(name, player, progress, result, replace, false);
    }

    @Deprecated
    protected void execute(String name, Player player, UnaryOperator<QuestResult> result, Replace replace) {
        this.execute(name, player, BigInteger.ONE, result, replace, false);
    }

    @Deprecated
    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result) {
        this.execute(name, player, progress, result, replacer -> replacer, false);
    }

    @Deprecated
    protected void execute(String name, Player player, UnaryOperator<QuestResult> result) {
        this.execute(name, player, BigInteger.ONE, result);
    }

    @Deprecated
    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result, Replace replace, boolean overrideUpdate) {
        QuestExecutionBuilder executionBuilder = QuestExecutionBuilder.of(this, name)
                .player(player)
                .progress(progress)
                .questResult(result);
        if (overrideUpdate)
            executionBuilder.overrideUpdate();
        executionBuilder.execute();
    }

    @Deprecated
    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result, Replace replace) {
        this.execute(name, player, progress, result, replace, false);
    }

    @Deprecated
    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result) {
        this.execute(name, player, progress, result, replacer -> replacer, false);
    }
}
