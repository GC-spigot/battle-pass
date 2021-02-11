package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatQuest extends QuestContainer {
    private final SimplePlugin plugin;

    public ChatQuest(BattlePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        this.plugin.runSync(() -> {
            Player player = event.getPlayer();
            String message = event.getMessage().toLowerCase();

            this.executionBuilder("chat-stripped")
                    .player(player)
                    .root(Text.decolorize(message))
                    .progressSingle()
                    .buildAndExecute();
            this.executionBuilder("chat")
                    .player(player)
                    .root(message)
                    .progressSingle()
                    .buildAndExecute();
        });
    }
}
