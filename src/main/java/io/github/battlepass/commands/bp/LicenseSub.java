package io.github.battlepass.commands.bp;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class LicenseSub extends BpSubCommand<CommandSender> {

    public LicenseSub(BattlePlugin plugin) {
        super(plugin, true);
        this.addFlatWithAliases("license", "about");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Text.sendMessage(sender, "&7This server is running &eBattlePass v" + this.plugin.getDescription().getVersion() +  " &7by Hyfe and Zak Shearman"
        + "\n\n&eUser ID:&f %%__USER__%%"
        + "\n&eDownload ID:&f %%__NONCE__%%");
    }
}
