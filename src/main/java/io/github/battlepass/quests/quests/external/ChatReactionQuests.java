package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import me.clip.chatreaction.events.ReactionWinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ChatReactionQuests extends ExternalQuestExecutor {

    public ChatReactionQuests(BattlePlugin plugin) {
        super(plugin, "chatreaction");
    }

    @EventHandler(ignoreCancelled = true)
    public void onReactionWin(ReactionWinEvent event) {
        Player player = event.getWinner();

        this.execute("win", player, QuestResult::none);
    }
}
