package io.github.battlepass.menus;

import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.menus.menus.DailyQuestsMenu;
import io.github.battlepass.menus.menus.PortalMenu;
import io.github.battlepass.menus.menus.QuestOverviewMenu;
import io.github.battlepass.menus.menus.rewards.DefaultRewardsMenu;
import me.hyfe.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class MenuFactory {
    private final BattlePlugin plugin;
    private final Set<UUID> insideMenu = Sets.newHashSet();

    public MenuFactory(BattlePlugin plugin) {
        this.plugin = plugin;
    }

    public Menu createMenu(String menuName, Player player) {
        switch (menuName) {
            case "portal":
                return this.initiateMenu(player, () -> new PortalMenu(this.plugin, this.plugin.getConfig("portal-menu"), player));
            case "daily":
            case "daily-quests":
                return this.initiateMenu(player, () -> new DailyQuestsMenu(this.plugin, this.plugin.getConfig("daily-quests-menu"), player));
            case "rewards":
                return this.initiateMenu(player, () -> new DefaultRewardsMenu(this.plugin, this.plugin.getConfig("rewards-menu"), player));
            case "missions":
            case "quests":
            case "quest-overview":
                return this.initiateMenu(player, () -> new QuestOverviewMenu(this.plugin, this.plugin.getConfig("quest-overview-menu"), player));
        }
        return null;
    }

    public Set<UUID> getInsideMenu() {
        return this.insideMenu;
    }

    private Menu initiateMenu(Player player, Supplier<Menu> supplier) {
        this.insideMenu.add(player.getUniqueId());
        Menu menu = supplier.get();
        menu.setCloseAction(() -> this.insideMenu.remove(player.getUniqueId()));
        return menu;
    }
}
