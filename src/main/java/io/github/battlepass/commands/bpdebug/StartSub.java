package io.github.battlepass.commands.bpdebug;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.logger.DebugLogger;
import org.bukkit.command.CommandSender;

public class StartSub extends BpSubCommand<CommandSender> {
    private final DebugLogger logger;

    public StartSub(BattlePlugin plugin) {
        super(plugin, true);
        this.logger = plugin.getDebugLogger();

        this.inheritPermission();
        this.addFlat("start");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (this.logger.isEnabled()) {
            this.lang.local("debug-already-running").to(sender);
            return;
        }
        this.logger.setEnabled(true);
        this.lang.local("debug-start").to(sender);
    }
}
