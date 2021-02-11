package io.github.battlepass.quests.workers.pipeline;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.LogContainer;
import io.github.battlepass.objects.quests.variable.ExecutableQuestResult;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.service.executor.QuestExecution;
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

    /**
     * This method should never have been used but it's deprecated in case. Use the new quest system.
     */
    @Deprecated
    public void handle(String name, Player player, int progress, QuestResult questResult, Replacer replacer, boolean overrideUpdate) {
        this.handle(new QuestExecution(player, name, BigInteger.valueOf(progress), overrideUpdate, (ExecutableQuestResult) questResult));
    }

    public void handle(QuestExecution questExecution) {
        Player player = questExecution.getPlayer();
        if (this.banPermissionEnabled && player.hasPermission("battlepass.block") && !player.hasPermission("battlepass.admin")) {
            this.logger.log(LogContainer.of("(PIPELINE) Player %battlepass-player% is blocked from the battlepass so dropping them.", player));
            return;
        }
        this.logger.log(LogContainer.of(questExecution));
        this.userCache.get(player.getUniqueId()).thenAccept(maybeUser -> maybeUser.ifPresent(user -> {
            questExecution.setUser(user);
            this.questValidationStep.processCompletion(questExecution, this.questCache.getAllQuests());
        })).exceptionally(ex -> {
            this.logger.log("(PIPELINE) Generic Error ".concat(ex.getMessage()));
            ex.printStackTrace();
            return null;
        });
    }
}