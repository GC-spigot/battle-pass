package io.github.battlepass.commands.bp;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.lang.Lang;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class HelpSub extends BpSubCommand<CommandSender> {
    private final String helpMessage;

    public HelpSub(BattlePlugin plugin) {
        super(plugin, true);

        Lang lang = plugin.getLang();
        if (lang.has("help-command")) {
            this.helpMessage = lang.external("help-command").toString();
        } else {
            this.helpMessage = "\n&eBattlePass &7by Hyfe and Zak Shearman\n"
                    .concat("/battlepass - Opens the portal menu.")
                    .replace("- ", "&8- &7")
                    .replace("/battlepass", "&e/battlepass")
                    .replace(".", ".\n");
        }

        this.addFlatWithAliases("help", "?");
    }

    @Override
    public void onExecute(CommandSender sender, String[] strings) {
        Text.sendMessage(sender, this.helpMessage);
    }
}
