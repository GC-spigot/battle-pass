package io.github.battlepass.commands.bpdebug;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.logger.DebugLogger;
import org.bukkit.command.CommandSender;

public class CreateSub extends BpSubCommand<CommandSender> {
    private final DebugLogger logger;

    public CreateSub(BattlePlugin plugin) {
        super(plugin);
        this.logger = plugin.getDebugLogger();

        this.inheritPermission();
        this.addFlat("create");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        String fileName = this.logger.dump(null);
        this.lang.local("debug-dumped", fileName).to(sender);
    }
}
