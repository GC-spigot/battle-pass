package io.github.battlepass.menus.menus;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.menus.UserDependent;
import io.github.battlepass.menus.service.extensions.PageableConfigMenu;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import io.github.battlepass.service.Services;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.item.MenuItem;
import me.hyfe.simplespigot.menu.service.MenuService;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class DailyQuestsMenu extends PageableConfigMenu<Quest> implements UserDependent {
    private final DailyQuestReset dailyQuestReset;
    private final QuestController questController;
    private final Lang lang;
    private final User user;

    public DailyQuestsMenu(BattlePlugin plugin, Config config, Player player) {
        super(plugin, config, player, replacer -> replacer.tryAddPapi(player));
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
                .set("daily_time_left", this.dailyQuestReset.asString())
                .tryAddPapi(this.player)));
    }

    @Override
    public MenuItem pageableItem(Quest quest) {
        try {
            return MenuItem.builderOf(Text.modify(quest.getItemStack(), replacer -> replacer
                    .set("total_progress", this.questController.getQuestProgress(this.user, quest))
                    .set("required_progress", quest.getRequiredProgress())
                    .set("percentage_progress", Services.getPercentageString(this.questController.getQuestProgress(this.user, quest), quest.getRequiredProgress()).concat("%"))
                    .set("progress_bar", Services.getProgressBar(this.questController.getQuestProgress(this.user, quest), quest.getRequiredProgress(), this.lang))
                    .tryAddPapi(this.player)))
                    .build();
        } catch (Exception e) {
            BattlePlugin.logger().info("Quest: " + quest);
            BattlePlugin.logger().info("Error whilst building menu item for quest: " + quest.getId() + " category " + quest.getCategoryId() + " name " + quest.getName());
            BattlePlugin.logger().info("Quest Item: " + quest.getItemStack());
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
}
