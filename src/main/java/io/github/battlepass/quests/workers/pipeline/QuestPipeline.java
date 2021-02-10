package io.github.battlepass.quests.workers.pipeline;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.LogContainer;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.workers.pipeline.steps.QuestValidationStep;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.entity.Player;

import java.math.BigInteger;

public class QuestPipeline {
    private final QuestValidationStep questValidationStep;
    private final DebugLogger logger;
    private final UserCache userCache;
    private final QuestCache questCache;
    private final boolean banPermissionEnabled;

    public QuestPipeline(BattlePlugin plugin) {
        this.questValidationStep = new QuestValidationStep(plugin);
        this.logger = plugin.getDebugLogger();
        this.userCache = plugin.getUserCache();
        this.questCache = plugin.getQuestCache();
        this.banPermissionEnabled = plugin.getConfig("settings").bool("enable-ban-permission");
    }

    public void handle(String name, Player player, int progress, QuestResult questResult, Replacer replacer, boolean overrideUpdate) {
        this.handle(name, player, BigInteger.valueOf(progress), questResult, replacer, overrideUpdate);
    }

    public void handle(String name, Player player, BigInteger progress, QuestResult questResult, Replacer replacer, boolean overrideUpdate) {
        if (player == null) {
            this.logger.log("(PIPELINE) Player null issue for quest type ".concat(name));
            return;
        }
        if (this.banPermissionEnabled && player.hasPermission("battlepass.block") && !player.isOp()) {
            this.logger.log(LogContainer.of("(PIPELINE) Player %battlepass-player% is blocked from the battlepass so dropping them.", player));
            return;
        }
        this.logger.log(LogContainer.of("(PIPELINE) Type " + name + " for %battlepass-player% entered. " + questResult, player));
        this.userCache.get(player.getUniqueId()).thenAccept(maybeUser -> maybeUser.ifPresent(user -> {
            this.questValidationStep.processCompletion(player, user, name, progress, questResult, this.questCache.getAllQuests(), overrideUpdate);
        })).exceptionally(ex -> {
            this.logger.log("(PIPELINE) Generic Error ".concat(ex.getMessage()));
            ex.printStackTrace();
            return null;
        });
    }
}