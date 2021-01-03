package io.github.battlepass.commands.bpdebug;

import io.github.battlepass.BattlePlugin;
import me.hyfe.simplespigot.command.command.SimpleCommand;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class BpDebugCommand extends SimpleCommand<CommandSender> {

    public BpDebugCommand(BattlePlugin plugin) {
        super(plugin, "battlepassdebug", "battlepass.admin", true);
        this.noPermissionLang(sender -> plugin.getLang().external("no-permission").asString());
        this.setSubCommands(
                new StartSub(plugin),
                new StopSub(plugin),
                new CreatePlayerSub(plugin),
                new CreateSub(plugin),
                new ClearSub(plugin)
        );
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Text.sendMessage(sender, ("\n"
                + "\n&bBattlePass Debug Help:\n"
                + "/bpd - This page."
                + "/bpd start - Starts logging actions."
                + "/bpd stop - Stops logging actions."
                + "/bpd create - Creates a debug dump."
                + "/bpd create <player> - Creates a debug dump for a specific player."
                + "/bpd clear - Clears the current log.")
                .replace("- ", "&8- &7")
                .replace("/bpd", "&e/bpd")
                .replace(".", ".\n"));
    }
}
