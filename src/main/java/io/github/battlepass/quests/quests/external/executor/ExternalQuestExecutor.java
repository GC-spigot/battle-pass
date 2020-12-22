package io.github.battlepass.quests.quests.external.executor;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.QuestExecutor;
import me.hyfe.simplespigot.text.replacer.Replace;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.function.UnaryOperator;

public class ExternalQuestExecutor extends QuestExecutor {
    private final String prefix;

    public ExternalQuestExecutor(BattlePlugin plugin, String pluginName) {
        super(plugin);
        this.prefix = pluginName.concat("_");
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace, boolean overrideUpdate) {
        super.execute(this.prefix.concat(name), player, progress, result, replace, overrideUpdate);
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result, Replace replace) {
        super.execute(this.prefix.concat(name), player, progress, result, replace);
    }

    protected void execute(String name, Player player, UnaryOperator<QuestResult> result, Replace replace) {
        this.execute(name, player, BigInteger.ONE, result, replace);
    }

    protected void execute(String name, Player player, int progress, UnaryOperator<QuestResult> result) {
        this.execute(name, player, progress, result, replacer -> replacer);
    }

    protected void execute(String name, Player player, UnaryOperator<QuestResult> result) {
        this.execute(name, player, BigInteger.ONE, result);
    }

    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result, Replace replace, boolean overrideUpdate) {
        super.execute(this.prefix.concat(name), player, progress, result, replace, overrideUpdate);
    }

    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result, Replace replace) {
        super.execute(this.prefix.concat(name), player, progress, result, replace);
    }

    protected void execute(String name, Player player, BigInteger progress, UnaryOperator<QuestResult> result) {
        this.execute(name, player, progress, result, replacer -> replacer);
    }
}
