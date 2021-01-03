package io.github.battlepass.commands.bpdebug;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.logger.DebugLogger;
import org.bukkit.command.CommandSender;

public class StopSub extends BpSubCommand<CommandSender> {
    private final DebugLogger logger;

    public StopSub(BattlePlugin plugin) {
        super(plugin, true);
        this.logger = plugin.getDebugLogger();

        this.inheritPermission();
        this.addFlat("stop");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!this.logger.isEnabled()) {
            this.lang.local("debug-not-running").to(sender);
            return;
        }
        this.logger.setEnabled(false);
        this.lang.local("debug-stop").to(sender);
    }
}
