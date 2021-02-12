package io.github.battlepass.menus.menus;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.menus.MenuFactory;
import io.github.battlepass.menus.UserDependent;
import io.github.battlepass.menus.service.extensions.PageableConfigMenu;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class QuestOverviewMenu extends PageableConfigMenu<Integer> implements UserDependent {
    private final BattlePassApi api;
    private final MenuFactory menuFactory;
    private final QuestCache questCache;
    private final QuestController questController;
    private final DailyQuestReset dailyQuestReset;
    private final User user;
    private final Lang lang;

    public QuestOverviewMenu(BattlePlugin plugin, Config config, Player player) {
        super(plugin, config, player, replacer -> replacer.tryAddPapi(player));
        this.api = plugin.getLocalApi();
        this.menuFactory = plugin.getMenuFactory();
        this.questCache = plugin.getQuestCache();
        this.questController = plugin.getQuestController();
        this.dailyQuestReset = plugin.getDailyQuestReset();
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
        this.lang = plugin.getLang();
        this.addUpdater(plugin, 20);
    }

    @Override
    public void redraw() {
        this.drawPageableItems(() -> this.drawConfigItems(replacer -> replacer.set("daily_time_left", this.dailyQuestReset.asString()).tryAddPapi(this.player)));
    }

    @Override
    public MenuItem pageableItem(Integer weekInt) {
        Config settings = this.plugin.getConfig("settings");
        long currentWeek = this.api.getCurrentWeek();
        boolean isWeekFuture = weekInt > currentWeek;
        boolean isPreviousWeekBlocked = settings.bool("current-season.unlocks.require-previous-completion") && this.config.has("static-items.requires-previous-completion-item") && !this.questController.isWeekDone(this.user, weekInt - 1);
        boolean isLockPreviousWeeksBlocked = settings.bool("current-season.unlocks.lock-previous-weeks") && currentWeek > weekInt;
        boolean userBypasses = this.user.bypassesLockedWeeks();
        boolean locked = !userBypasses && (isWeekFuture || isPreviousWeekBlocked || isLockPreviousWeeksBlocked || this.api.getCurrentWeek() < 1);
        return MenuItem.builderOf(SpigotItem.toItem(
                this.config, "static-items." + (locked ? isWeekFuture ? "locked-week" : "requires-previous-completion" : "week") + "-item", replacer -> replacer
                        .set("week", weekInt)
                        .set("status", this.lang.external("week-status-".concat(!locked ? "un" : "").concat("locked")).asString()
                                .concat(userBypasses ? " &o&7(&cBYPASSING&7)" : "")).tryAddPapi(this.player)))
                .onClick((menuItem, clickType) -> {
                    if (!locked) {
                        WeekMenu weekMenu = new WeekMenu(this.plugin, this.plugin.getConfig("week-menu"), this.player, weekInt);
                        this.menuFactory.getOpenMenus().put(this.player.getUniqueId(), weekMenu);
                        weekMenu.show();
                    }
                }).build();
    }

    @Override
    public ImmutablePair<Collection<Integer>, Collection<Integer>> elementalValues() {
        return ImmutablePair.of(IntStream.range(1, this.questCache.getMaxWeek() + 1).boxed().collect(Collectors.toSet()), MenuService.parseSlots(this, this.config, "week-slots"));
    }

    @Override
    public boolean isUserViable() {
        return this.user != null;
    }
}
