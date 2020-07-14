package io.github.battlepass.commands;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.lang.Lang;
import me.hyfe.simplespigot.command.command.SubCommand;
import org.bukkit.command.CommandSender;

public abstract class BpSubCommand<T extends CommandSender> extends SubCommand<T> {
    protected final BattlePlugin plugin;
    protected final Lang lang;

    public BpSubCommand(BattlePlugin plugin, String permission, boolean isConsole) {
        super(plugin, permission, isConsole);
        this.plugin = plugin;
        this.lang = plugin.getLang();
    }

    public BpSubCommand(BattlePlugin plugin) {
        this(plugin, "", true);
    }

    public BpSubCommand(BattlePlugin plugin, String permission) {
        this(plugin, permission, true);
    }

    public BpSubCommand(BattlePlugin plugin, boolean isConsole) {
        this(plugin, "", isConsole);
    }
}