package io.github.battlepass.commands.bp;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.menus.UserDependent;
import me.hyfe.simplespigot.command.command.SimpleCommand;
import me.hyfe.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public class BpCommand extends SimpleCommand<Player> {
    private final BattlePlugin plugin;

    public BpCommand(BattlePlugin plugin) {
        super(plugin, "battlepass", false);
        this.plugin = plugin;
        this.setSubCommands(
                new HelpSub(plugin),
                new LicenseSub(plugin),
                new StatsSub(plugin),
                new MenuSub(plugin, "portal"),
                new MenuSub(plugin, "daily-quests", "daily"),
                new MenuSub(plugin, "quests", "missions"),
                new MenuSub(plugin, "rewards")
        );
    }

    @Override
    public void onExecute(Player sender, String[] args) {
        Menu menu = this.plugin.getMenuFactory().createMenu("portal", sender);
        if (!(menu instanceof UserDependent) || ((UserDependent) menu).isUserViable()) {
            menu.show();
        }
    }
}
