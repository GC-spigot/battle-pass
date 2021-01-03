package io.github.battlepass.commands.bpadmin;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import org.bukkit.command.CommandSender;

public class ReloadSub extends BpSubCommand<CommandSender> {

    public ReloadSub(BattlePlugin plugin) {
        super(plugin);

        this.inheritPermission();
        this.addFlat("reload");
    }

    @Override
    public void onExecute(CommandSender commandSender, String[] args) {
        long whenStart = System.currentTimeMillis();
        this.plugin.reload();
        this.lang.local("successful-reload", System.currentTimeMillis() - whenStart).to(commandSender);
    }
}