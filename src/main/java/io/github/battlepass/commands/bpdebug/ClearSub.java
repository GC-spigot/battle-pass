package io.github.battlepass.commands.bpdebug;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.logger.DebugLogger;
import org.bukkit.command.CommandSender;

public class ClearSub extends BpSubCommand<CommandSender> {
    private final DebugLogger logger;

    public ClearSub(BattlePlugin plugin) {
        super(plugin);
        this.logger = plugin.getDebugLogger();

        this.inheritPermission();
        this.addFlat("clear");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        this.logger.clear();
        this.lang.local("debug-cleared").to(sender);
    }
}
