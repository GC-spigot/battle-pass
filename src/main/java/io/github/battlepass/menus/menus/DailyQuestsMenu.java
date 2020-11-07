package io.github.battlepass.menus.menus;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.menus.UserDependent;
import io.github.battlepass.menus.service.extensions.PageableConfigMenu;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import io.github.battlepass.service.Percentage;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.logging.Level;

public class DailyQuestsMenu extends PageableConfigMenu<Quest> implements UserDependent {
    private final DailyQuestReset dailyQuestReset;
    private final QuestController questController;
    private final Lang lang;
    private final User user;

    public DailyQuestsMenu(BattlePlugin plugin, Config config, Player player) {
        super(plugin, config, player, replacer -> replacer);
        this.dailyQuestReset = plugin.getDailyQuestReset();
        this.questController = plugin.getQuestController();
        this.lang = plugin.getLang();
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
        if (!this.plugin.areDailyQuestsEnabled()) {
            return;
        }
        this.addUpdater(plugin, 20);
    }

    @Override
    public void redraw() {
        this.drawPageableItems(() -> this.drawConfigItems(replacer -> replacer
                .set("time_left", this.dailyQuestReset.asString())
                .set("daily_time_left", this.dailyQuestReset.asString())));
    }

    @Override
    public MenuItem pageableItem(Quest quest) {
        try {
            return MenuItem.builderOf(Text.modify(quest.getItemStack(), replacer -> replacer
                    .set("total_progress", this.questController.getQuestProgress(this.user, quest))
                    .set("required_progress", quest.getRequiredProgress())
                    .set("percentage_progress", Percentage.getPercentage(this.questController.getQuestProgress(this.user, quest), quest.getRequiredProgress()).concat("%"))
                    .set("progress_bar", Percentage.getPercentage(this.questController.getQuestProgress(this.user, quest), quest.getRequiredProgress()))))
                    .build();
        } catch (Exception e) {
            BattlePlugin.logger().log(Level.INFO, "Quest: " + quest);
            BattlePlugin.logger().log(Level.INFO, "Error whilst building menu item for quest: " + quest.getId() + " category " + quest.getCategoryId() + " name " + quest.getName());
            BattlePlugin.logger().log(Level.INFO, "Quest Item: " + quest.getItemStack());
            e.printStackTrace();
            return MenuItem.builderOf(new ItemStack(Material.BARRIER)).build();
        }
    }

    @Override
    public ImmutablePair<Collection<Quest>, Collection<Integer>> elementalValues() {
        return ImmutablePair.of(this.dailyQuestReset.getCurrentQuests(), MenuService.parseSlots(this, this.config, "quest-slots"));
    }

    @Override
    public boolean isUserViable() {
        return this.user != null;
    }

    private String getPercentage(BigInteger progress, BigInteger requiredProgress) {
        return new BigDecimal(progress).divide(new BigDecimal(requiredProgress), RoundingMode.CEILING).multiply(BigDecimal.valueOf(100)).toString(); // TODO probs broken
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
