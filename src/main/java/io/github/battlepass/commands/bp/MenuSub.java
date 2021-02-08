package io.github.battlepass.commands.bp;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import me.hyfe.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public class MenuSub extends BpSubCommand<Player> {
    private final String menuName;

    public MenuSub(BattlePlugin plugin, String menuName, String... aliases) {
        super(plugin, false);
        this.menuName = menuName;

        this.addFlatWithAliases("open", "menu");
        this.addFlatWithAliases(menuName, aliases);
    }

    @Override
    public void onExecute(Player player, String[] strings) {
        Menu menu = this.plugin.getMenuFactory().createMenu(this.menuName, player);
        if (menu == null) {
            this.lang.external("disallowed-permission").to(player);
            return;
        }
        menu.show();
    }
}
