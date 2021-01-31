package io.github.battlepass.quests;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.ExecutableQuestResult;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.workers.pipeline.QuestPipeline;
import me.hyfe.simplespigot.text.replacer.Replace;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

public class QuestExecutor implements Listener {
    private final QuestPipeline questPipeline;

    protected QuestExecutor(BattlePlugin plugin) {
        this.questPipeline = plugin.getQuestPipeline();
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace, boolean overrideUpdate) {
        this.questPipeline.handle(name, player, progress, result.apply(new ExecutableQuestResult()), replace.apply(new Replacer()), overrideUpdate);
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace) {
        this.questPipeline.handle(name, player, progress, result.apply(new ExecutableQuestResult()), replace.apply(new Replacer()), false);
    }

    protected void execute(String name, Player player, UnaryOperator<QuestResult> result, Replace replace) {
        this.execute(name, player, BigInteger.ONE, result, replace, false);
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result) {
        this.execute(name, player, progress, result, replacer -> replacer, false);
    }

    protected void execute(String name, Player player, UnaryOperator<QuestResult> result) {
        this.execute(name, player, BigInteger.ONE, result);
    }

    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result, Replace replace, boolean overrideUpdate) {
        this.questPipeline.handle(name, player, progress, result.apply(new ExecutableQuestResult()), replace.apply(new Replacer()), overrideUpdate);
    }

    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result, Replace replace) {
        this.questPipeline.handle(name, player, progress, result.apply(new ExecutableQuestResult()), replace.apply(new Replacer()), false);
    }

    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result) {
        this.execute(name, player, progress, result, replacer -> replacer, false);
    }
}
