package io.github.battlepass.commands.bp;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import org.bukkit.entity.Player;

public class MenuSub extends BpSubCommand<Player> {
    private final String menuName;

    public MenuSub(BattlePlugin plugin, String menuName) {
        super(plugin, false);
        this.addFlatWithAliases("open", "menu");
        this.addFlat(menuName);
        this.menuName = menuName;
    }

    @Override
    public void onExecute(Player player, String[] strings) {
        this.plugin.getMenuFactory().createMenu(this.menuName, player).show();
    }
}
