package io.github.battlepass.commands.bpa.debugger;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.logger.DebugLogger;
import org.bukkit.command.CommandSender;

public class DebugDumpSub extends BpSubCommand<CommandSender> {
    private final DebugLogger logger;

    public DebugDumpSub(BattlePlugin plugin) {
        super(plugin, true);
        this.logger = plugin.getDebugLogger();

        this.inheritPermission();
        this.addFlats("debug", "dump");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        String fileName = this.logger.dump(null);
        this.lang.local("debug-dumped", fileName).to(sender);
    }
}
