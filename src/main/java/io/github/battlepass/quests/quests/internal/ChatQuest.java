package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import me.hyfe.simplespigot.plugin.SimplePlugin;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatQuest extends QuestExecutor {
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

            this.execute("chat-stripped", player, result -> result.root(Text.decolorize(message)), replacer -> replacer.set("message", message));
            this.execute("chat", player, result -> result.root(message), replacer -> replacer.set("message", message));
        });
    }
}
