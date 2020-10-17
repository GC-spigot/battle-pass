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
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.logging.Level;

public class WeekMenu extends PageableConfigMenu<Quest> {
    private final int week;
    private final QuestCache questCache;
    private final QuestController questController;
    private final DailyQuestReset dailyQuestReset;
    private final boolean glowOnCompletion;
    private final Lang lang;
    private final User user;

    public WeekMenu(BattlePlugin plugin, Config config, Player player, int week) {
        super(plugin, config, player, replacer -> replacer.set("week", week));
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
                    .set("percentage_progress", this.getPercentage(this.questController.getQuestProgress(this.user, quest), quest.getRequiredProgress()).concat("%"))
                    .set("progress_bar", this.getProgressBar(this.questController.getQuestProgress(this.user, quest), quest.getRequiredProgress()))))
                    .build();
        } catch (Exception e) {
            BattlePlugin.logger().log(Level.WARNING, "Quest: ".concat(String.valueOf(quest)));
            BattlePlugin.logger().log(Level.WARNING, "Error whilst building menu item for quest: " + quest.getId() + " category " + quest.getCategoryId() + " name " + quest.getName());
            BattlePlugin.logger().log(Level.WARNING, "Quest Item: ".concat(String.valueOf(quest.getItemStack())));
            e.printStackTrace();
            return MenuItem.builderOf(new ItemStack(Material.BARRIER)).build();
        }
    }

    @Override
    public ImmutablePair<Collection<Quest>, Collection<Integer>> elementalValues() {
        return ImmutablePair.of(this.questCache.getQuests(Category.WEEKLY.id(this.week)).values(), MenuService.parseSlots(this, this.config, "quest-slots"));
    }

    private String getPercentage(double progress, double requiredProgress) {
        return String.valueOf((int) ((progress / requiredProgress) * 100));
    }

    private String getProgressBar(int progress, int requiredProgress) {
        float progressFloat = (float) progress / requiredProgress;
        float complete = 30 * progressFloat;
        float incomplete = 30 - complete;
        String progressBar = Text.modify(this.lang.external("progress-bar.complete-color").asString());
        for (int i = 0; i < complete; i++) {
            progressBar = progressBar.concat(this.lang.external("progress-bar.symbol").asString());
        }
        progressBar = progressBar.concat(this.lang.external("progress-bar.incomplete-color").asString());
        for (int i = 0; i < incomplete; i++) {
            progressBar = progressBar.concat(this.lang.external("progress-bar.symbol").asString());
        }
        return progressBar;
    }
}
