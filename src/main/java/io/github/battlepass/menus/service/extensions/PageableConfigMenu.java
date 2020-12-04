package io.github.battlepass.menus.service.extensions;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.menus.PageMethods;
import io.github.battlepass.menus.service.MenuIllustrator;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.PageableMenu;
import me.hyfe.simplespigot.text.replacer.Replace;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.UnaryOperator;

public abstract class PageableConfigMenu<T> extends PageableMenu<T> implements PageMethods {
    protected final BattlePlugin plugin;
    protected final Config config;
    protected final MenuIllustrator illustrator;
    private Map<String, Runnable> customActions;

    public PageableConfigMenu(BattlePlugin plugin, Config config, Player player, UnaryOperator<Replacer> titleReplacer) {
        super(player, titleReplacer.apply(new Replacer()).applyTo(config.string("menu-title")), config.integer("menu-rows"));
        this.plugin = plugin;
        this.config = config;
        this.illustrator = plugin.getMenuIllustrator();
    }

    @Override
    public void redraw() {
        this.drawPageableItems(() -> this.drawConfigItems(null));
    }

    public void drawConfigItems(Replace replace) {
        this.illustrator.draw(this, this.config, this.plugin.getMenuFactory(), this.player, this.plugin.getActionCache(), this.customActions, replace);
    }

    public void customAction(String value, Runnable runnable) {
        this.customActions.put(value, runnable);
    }
}