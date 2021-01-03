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
                + "/bpdebug - This page."
                + "/bpdebug create - Creates a debug dump."
                + "/bpdebug create <player> - Creates a debug dump for a specific player."
                + "/bpdebug start - Starts logging actions."
                + "/bpdebug stop - Stops logging actions."
                + "/bpdebug clear - ?.")
                .replace("- ", "&8- &7")
                .replace("/bpa", "&e/bpa")
                .replace(".", ".\n"));
    }
}
