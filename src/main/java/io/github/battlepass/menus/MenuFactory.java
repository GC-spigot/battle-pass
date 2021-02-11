package io.github.battlepass.menus;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.menus.menus.DailyQuestsMenu;
import io.github.battlepass.menus.menus.PortalMenu;
import io.github.battlepass.menus.menus.QuestOverviewMenu;
import io.github.battlepass.menus.menus.rewards.DefaultRewardsMenu;
import me.hyfe.simplespigot.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class MenuFactory {
    private final BattlePlugin plugin;
    private final Lang lang;
    private final Map<UUID, Menu> openMenus = Maps.newHashMap();
    private final Map<Collection<String>, Function<Player, Menu>> menus = Maps.newHashMap();
    private final boolean banPermissionEnabled;

    public MenuFactory(BattlePlugin plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.banPermissionEnabled = plugin.getConfig("settings").bool("enable-ban-permission");
        this.putDefaults();
    }

    public Menu createMenu(String menuName, Player player) {
        if (this.banPermissionEnabled && player.hasPermission("battlepass.block") && !player.hasPermission("battlepass.admin")) {
            this.lang.external("disallowed-permission").to(player);
            return null;
        }
        for (Map.Entry<Collection<String>, Function<Player, Menu>> entry : this.menus.entrySet()) {
            if (entry.getKey().contains(menuName)) {
                return this.initiateMenu(player, () -> entry.getValue().apply(player));
            }
        }
        return null;
    }

    public Map<UUID, Menu> getOpenMenus() {
        return this.openMenus;
    }

    /**
     * Returns a mutable map so you can technically make your own menu. See the source for examples of how to do this
     *
     * @return A map of the names of the menu to a function giving you the Player where you return your Menu.
     */
    public Map<Collection<String>, Function<Player, Menu>> getMenus() {
        return this.menus;
    }

    private Menu initiateMenu(Player player, Supplier<Menu> supplier) {
        Menu menu = supplier.get();
        this.openMenus.put(player.getUniqueId(), menu);
        menu.setCloseAction(() -> this.openMenus.remove(player.getUniqueId()));
        return menu;
    }

    private void putDefaults() {
        this.menus.put(Sets.newHashSet("portal"), player -> new PortalMenu(this.plugin, this.plugin.getConfig("portal-menu"), player));
        this.menus.put(Sets.newHashSet("daily", "daily-quests"), player -> new DailyQuestsMenu(this.plugin, this.plugin.getConfig("daily-quests-menu"), player));
        this.menus.put(Sets.newHashSet("rewards"), player -> new DefaultRewardsMenu(this.plugin, this.plugin.getConfig("rewards-menu"), player));
        this.menus.put(Sets.newHashSet("missions", "quests", "quest-overview"), player -> new QuestOverviewMenu(this.plugin, this.plugin.getConfig("quest-overview-menu"), player));
    }
}
