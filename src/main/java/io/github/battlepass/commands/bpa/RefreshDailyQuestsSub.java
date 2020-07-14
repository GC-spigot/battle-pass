package io.github.battlepass.commands.bpa;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import org.bukkit.command.CommandSender;

public class RefreshDailyQuestsSub extends BpSubCommand<CommandSender> {

    public RefreshDailyQuestsSub(BattlePlugin plugin) {
        super(plugin);
        this.inheritPermission();
        this.addFlats("refresh", "daily", "quests");
    }

    @Override
    public void onExecute(CommandSender commandSender, String[] args) {
        this.plugin.getDailyQuestReset().reset();
        this.lang.local("successful-refresh-daily").to(commandSender);
    }
}