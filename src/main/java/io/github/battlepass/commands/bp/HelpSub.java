package io.github.battlepass.commands.bp;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class HelpSub extends BpSubCommand<CommandSender> {

    public HelpSub(BattlePlugin plugin) {
        super(plugin, true);

        this.addFlatWithAliases("help", "?");
    }

    @Override
    public void onExecute(CommandSender sender, String[] strings) {
        Text.sendMessage(sender, "\n&eBattlePass &7by Hyfe and Zak Shearman\n"
                .concat("/battlepass - Opens the portal menu.")
                .replace("- ", "&8- &7")
                .replace("/battlepass", "&e/battlepass")
                .replace(".", ".\n"));
    }
}
