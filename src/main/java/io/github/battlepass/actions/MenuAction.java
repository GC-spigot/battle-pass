package io.github.battlepass.actions;

import io.github.battlepass.menus.MenuFactory;
import io.github.battlepass.menus.PageMethods;
import io.github.battlepass.menus.UserDependent;
import me.hyfe.simplespigot.menu.Menu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

public class MenuAction extends Action {

    public MenuAction(String condition, String value) {
        super(condition, value);
    }

    public synchronized void accept(MenuFactory menuFactory, Menu menu, Player player) {
        AtomicBoolean didRun = new AtomicBoolean(false);
        this.checkCondition(menu, "!", (currentPage, conditionPage) -> !currentPage.equals(conditionPage), () -> {
            this.runAction(menuFactory, menu, player);
            didRun.set(true);
        });
        if (!didRun.get()) {
            this.checkCondition(menu, "=", Integer::equals, () -> {
                this.runAction(menuFactory, menu, player);
            });
        }
    }

    private void runAction(MenuFactory menuFactory, Menu menu, Player player) {
        if (this.value.equalsIgnoreCase("close")) {
            menu.close();
            return;
        }
        Menu createdMenu = menuFactory.createMenu(this.value, player);
        if (createdMenu == null) {
            if (menu instanceof PageMethods) {
                if (this.value.equalsIgnoreCase("previous-page")) {
                    ((PageMethods) menu).previousPage(menu::redraw);
                } else if (this.value.equalsIgnoreCase("next-page")) {
                    ((PageMethods) menu).nextPage(menu::redraw);
                }
            }
        } else {
            if (!(createdMenu instanceof UserDependent) || ((UserDependent) createdMenu).isUserViable()) {
                createdMenu.show();
            }
        }
    }

    private void checkCondition(Menu menu, String splitter, BiPredicate<Integer, Integer> predicate, Runnable ifTrue) {
        if (this.condition.contains(splitter)) {
            String[] splitCondition = this.condition.replace(" ", "").split(splitter);
            if (splitCondition[0].equalsIgnoreCase("page") &&
                    StringUtils.isNumeric(splitCondition[1]) &&
                    menu instanceof PageMethods &&
                    predicate.test(((PageMethods) menu).getPage(), Integer.parseInt(splitCondition[1]))) {
                ifTrue.run();
            }
        } else if (this.condition.isEmpty()) {
            ifTrue.run();
        }
    }
}