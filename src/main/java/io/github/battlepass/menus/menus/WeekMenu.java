package io.github.battlepass.menus.menus;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.enums.Category;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.menus.service.extensions.PageableConfigMenu;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import io.github.battlepass.service.Services;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class WeekMenu extends PageableConfigMenu<Quest> {
    private final int week;
    private final QuestCache questCache;
    private final QuestController questController;
    private final DailyQuestReset dailyQuestReset;
    private final boolean glowOnCompletion;
    private final Lang lang;
    private final User user;

    public WeekMenu(BattlePlugin plugin, Config config, Player player, int week) {
        super(plugin, config, player, replacer -> replacer.set("week", week).tryAddPapi(player));
        this.week = week;
        this.questCache = plugin.getQuestCache();
        this.questController = plugin.getQuestController();
        this.dailyQuestReset = plugin.getDailyQuestReset();
        this.glowOnCompletion = plugin.getConfig("settings").bool("current-season.quest-glow-on-completion");
        this.lang = plugin.getLang();
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
    }

    @Override
    public MenuItem pageableItem(Quest quest) {
        try {
            ItemStack itemStack = quest.getItemStack();
            if (this.glowOnCompletion && this.questController.isQuestDone(this.user, quest)) {
                itemStack = new SpigotItem.Builder().itemStack(itemStack).glow().build();
            }
            return MenuItem.builderOf(Text.modify(itemStack, replacer -> replacer
                    .set("daily_time_left", this.dailyQuestReset.asString())
                    .set("total_progress", this.questController.getQuestProgress(this.user, quest))
                    .set("required_progress", quest.getRequiredProgress())
                    .set("percentage_progress", () -> Services.getPercentageString(this.questController.getQuestProgress(this.user, quest).min(quest.getRequiredProgress()), quest.getRequiredProgress()).concat("%"))
                    .set("progress_bar", () -> Services.getProgressBar(this.questController.getQuestProgress(this.user, quest).min(quest.getRequiredProgress()), quest.getRequiredProgress(), this.lang))
                    .tryAddPapi(this.player)))
                    .build();
        } catch (Exception e) {
            BattlePlugin.logger().warning("Quest: ".concat(String.valueOf(quest)));
            BattlePlugin.logger().warning("Error whilst building menu item for quest: " + quest.getId() + " category " + quest.getCategoryId() + " name " + quest.getName());
            BattlePlugin.logger().warning("Quest Item: ".concat(String.valueOf(quest.getItemStack())));
            e.printStackTrace();
            return MenuItem.builderOf(new ItemStack(Material.BARRIER)).build();
        }
    }

    @Override
    public ImmutablePair<Collection<Quest>, Collection<Integer>> elementalValues() {
        return ImmutablePair.of(this.questCache.getQuests(Category.WEEKLY.id(this.week)).values(), MenuService.parseSlots(this, this.config, "quest-slots"));
    }
}
