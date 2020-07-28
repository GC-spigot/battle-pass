package io.github.battlepass.menus;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.menus.menus.DailyQuestsMenu;
import io.github.battlepass.menus.menus.PortalMenu;
import io.github.battlepass.menus.menus.QuestOverviewMenu;
import io.github.battlepass.menus.menus.RewardMenu;
import me.hyfe.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

public class MenuFactory {
    private final BattlePlugin plugin;

    public MenuFactory(BattlePlugin plugin) {
        this.plugin = plugin;
    }

    public Menu createMenu(String menuName, Player player) {
        switch (menuName) {
            case "portal":
                return new PortalMenu(this.plugin, this.plugin.getConfig("portal-menu"), player);
            case "daily":
            case "daily-quests":
                return new DailyQuestsMenu(this.plugin, this.plugin.getConfig("daily-quests-menu"), player);
            case "rewards":
                return new RewardMenu(this.plugin, this.plugin.getConfig("rewards-menu"), player);
            case "missions":
            case "quests":
            case "quest-overview":
                return new QuestOverviewMenu(this.plugin, this.plugin.getConfig("quest-overview-menu"), player);
        }
        return null;
    }
}
