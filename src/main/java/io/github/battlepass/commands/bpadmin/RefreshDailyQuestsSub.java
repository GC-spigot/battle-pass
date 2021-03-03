package io.github.battlepass.commands.bpadmin;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import org.bukkit.command.CommandSender;

public class RefreshDailyQuestsSub extends BpSubCommand<CommandSender> {
    private long lastUseTime;

    public RefreshDailyQuestsSub(BattlePlugin plugin) {
        super(plugin);

        this.inheritPermission();
        this.addFlats("refresh", "daily", "quests");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        long millisSinceUse = System.currentTimeMillis() - this.lastUseTime;
        if (System.currentTimeMillis() - this.lastUseTime > 5000) {
            this.lastUseTime = System.currentTimeMillis();
            this.plugin.getDailyQuestReset().reset();
            this.lang.local("successful-refresh-daily").to(sender);
        } else {
            this.lang.local("cooldown-has-seconds", 5 - Math.max(Integer.parseInt(String.valueOf(millisSinceUse / 1000)), 1)).to(sender);
        }
    }
}