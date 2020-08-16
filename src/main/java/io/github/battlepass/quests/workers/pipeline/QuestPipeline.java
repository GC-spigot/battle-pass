package io.github.battlepass.quests.workers.pipeline;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.LogContainer;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.workers.pipeline.steps.QuestValidationStep;
import me.hyfe.simplespigot.text.Replacer;
import org.bukkit.entity.Player;

public class QuestPipeline {
    private final QuestValidationStep questValidationStep;
    private final DebugLogger logger;
    private final UserCache userCache;
    private final QuestCache questCache;

    public QuestPipeline(BattlePlugin plugin) {
        this.questValidationStep = new QuestValidationStep(plugin);
        this.logger = plugin.getDebugLogger();
        this.userCache = plugin.getUserCache();
        this.questCache = plugin.getQuestCache();
    }

    public void handle(String name, Player player, int progress, QuestResult questResult, Replacer replacer, boolean overrideUpdate) {
        if (player == null) {
            this.logger.log("(PIPELINE) Player null issue for quest type ".concat(name));
            return;
        }
        this.logger.log(LogContainer.of("(PIPELINE) Quest type " + name + " for player %player% has entered the pipeline. Root: " + questResult.getEffectiveRoot(), player));
        this.userCache.get(player.getUniqueId()).thenAccept(maybeUser -> maybeUser.ifPresent(user -> {
            this.questValidationStep.process(player, user, name, progress, questResult, this.questCache.getAllQuests(), overrideUpdate);
        })).exceptionally(ex -> {
            this.logger.log("(PIPELINE) Generic Error ".concat(ex.getMessage()));
            ex.printStackTrace();
            return null;
        });
    }
}