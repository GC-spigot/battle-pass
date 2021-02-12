package io.github.battlepass.quests.quests.external.executor;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.service.base.ExternalQuestContainer;
import io.github.battlepass.quests.service.executor.QuestExecutionBuilder;
import me.hyfe.simplespigot.text.replacer.Replace;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

public class ExternalQuestExecutor extends ExternalQuestContainer {

    @Deprecated
    public ExternalQuestExecutor(BattlePlugin plugin, String pluginName) {
        super(plugin, pluginName);
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
        executionBuilder.buildAndExecute();
    }

    @Deprecated
    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result, Replace replace) {
        this.execute(name, player, progress, result, replace, false);
    }

    @Deprecated
    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result) {
        this.execute(name, player, progress, result, replacer -> replacer);
    }
}
