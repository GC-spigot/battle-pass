package io.github.battlepass.quests.workers.pipeline;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.workers.pipeline.steps.QuestValidationStep;
import me.hyfe.simplespigot.text.Replacer;
import org.bukkit.entity.Player;

public class QuestPipeline {
    private final QuestValidationStep questValidationStep;
    private final UserCache userCache;
    private final QuestCache questCache;

    public QuestPipeline(BattlePlugin plugin) {
        this.questValidationStep = new QuestValidationStep(plugin);
        this.userCache = plugin.getUserCache();
        this.questCache = plugin.getQuestCache();
    }

    public void handle(String name, Player player, int progress, QuestResult questResult, Replacer replacer, boolean overrideUpdate) {
        if (player == null) {
            return;
        }
        this.userCache.get(player.getUniqueId()).thenAccept(maybeUser -> maybeUser.ifPresent(user -> {
                this.questValidationStep.process(player, user, name, progress, questResult, this.questCache.getAllQuests(), overrideUpdate);
        })).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}