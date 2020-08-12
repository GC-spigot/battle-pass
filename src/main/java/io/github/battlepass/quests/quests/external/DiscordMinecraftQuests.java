package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import me.enderaura.dcmc.api.event.AccountLinkedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class DiscordMinecraftQuests extends ExternalQuestExecutor {

    public DiscordMinecraftQuests(BattlePlugin plugin) {
        super(plugin, "discordminecraft");
    }

    @EventHandler(ignoreCancelled = true)
    public void onAccountLink(AccountLinkedEvent event) {
        Player player = event.getPlayer().getPlayer();

        this.execute("link", player, QuestResult::none);
    }
}
