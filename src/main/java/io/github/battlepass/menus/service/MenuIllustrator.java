package io.github.battlepass.menus.service;

import com.google.common.cache.Cache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.actions.*;
import io.github.battlepass.menus.MenuFactory;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.Menu;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.text.Replace;
import me.hyfe.simplespigot.text.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

public class MenuIllustrator {

    public void draw(Menu menu, Config config, MenuFactory menuFactory, Player player, Cache<String, Map<Integer, Set<Action>>> actionCache, Map<String, Runnable> customActions, Replace replace) {
        Function<Integer, Set<Action>> actionSupplier = slot -> {
            String id = menu.getClass().getSimpleName();
            Map<Integer, Set<Action>> actionsMap = actionCache.getIfPresent(id);
            if (actionsMap != null && actionsMap.containsKey(slot)) {
                return actionsMap.get(slot);
            } else {
                Set<Action> actions = Sets.newLinkedHashSet();
                for (String action : config.stringList(String.format("menu.%s.actions", slot))) {
                    actions.add(Action.parse(action));
                }
                try {
                    actionCache.get(id, Maps::newHashMap).put(slot, actions);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return actions;
            }
        };
        if (config.has("menu")) {
            for (String key : config.keys("menu", false)) {
                for (int slot : MenuService.parseSlots(menu, config, "menu.", key)) {
                    if (slot > menu.getRows() * 9 - 1) {
                        if (menu.getMenuState().isRaw()) {
                            Bukkit.getLogger().log(Level.SEVERE, String.format("The specified slot %d in the menu %s is greater\n then the amount of slots in the menu (%d). Skipping the slot...", slot, menu.getTitle(), menu.getRows() * 9 - 1));
                        }
                        continue;
                    }
                    MenuItem.builder()
                            .rawSlot(slot)
                            .item(config, String.format("menu.%s.item", key), replace)
                            .onClick((menuItem, clickType) -> {
                                Replacer replacer = new Replacer().set("player", player.getName());
                                for (Action action : actionSupplier.apply(slot)) {
                                    if (action instanceof MenuAction) {
                                        ((MenuAction) action).accept(menuFactory, menu, player);
                                    } else if (action instanceof MessageAction) {
                                        ((MessageAction) action).accept(player, null);
                                    } else if (action instanceof SoundAction) {
                                        ((SoundAction) action).accept(player);
                                    } else if (action instanceof CommandAction) {
                                        ((CommandAction) action).accept(player, replacer);
                                    } else if (action instanceof ConsoleCommandAction) {
                                        ((ConsoleCommandAction) action).accept(replacer);
                                    } else if (action instanceof DynamicAction) {
                                        for (Map.Entry<String, Runnable> customAction : customActions.entrySet()) {
                                            ((DynamicAction) action).accept(customAction.getKey(), customAction.getValue());
                                        }
                                    }
                                }
                            })
                            .buildTo(menu);
                }
            }
        }
    }
}