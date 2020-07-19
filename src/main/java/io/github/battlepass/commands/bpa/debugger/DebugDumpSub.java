package io.github.battlepass.commands.bpa.debugger;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.logger.DebugLogger;
import me.hyfe.simplespigot.command.command.SubCommand;
import org.bukkit.command.CommandSender;

public class DebugDumpSub extends SubCommand<CommandSender> {
    private final DebugLogger logger;
    private final Lang lang;

    public DebugDumpSub(BattlePlugin plugin) {
        super(plugin, true);
        this.logger = plugin.getDebugLogger();
        this.lang = plugin.getLang();

        this.inheritPermission();
        this.addFlats("debug", "dump");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        String fileName = this.logger.dump(null);
        this.lang.local("debug-dumped", fileName).to(sender);
    }
}
