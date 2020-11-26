package io.github.battlepass.quests.workers.pipeline.steps;

import com.google.common.collect.Lists;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.actions.Action;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.service.Services;
import me.hyfe.simplespigot.text.Replacer;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationStep {
    private final List<Action> completionActions = Lists.newArrayList();
    private final String notificationMethod;
    private final BattlePlugin plugin;
    private final Lang lang;
    private final RewardStep rewardStep;

    public NotificationStep(BattlePlugin plugin) {
        this.completionActions.addAll(plugin.getConfig("settings").stringList("quest-completed-actions").stream().map(Action::parse).collect(Collectors.toList()));
        this.notificationMethod = plugin.getConfig("settings").string("current-season.notification-method");
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.rewardStep = new RewardStep(plugin);
    }

    public void process(User user, Quest quest, BigInteger originalProgress, BigInteger updatedProgress) {
        Player player = user.getPlayer();
        if (player == null) {
            return;
        }
        Action.executeSimple(player, this.completionActions, this.plugin, new Replacer().set("player", player.getName()).set("quest_name", quest.getName()).set("quest_category", quest.getCategoryId()));
        // this.questController.isQuestDone was removed as a check here as I think below does the same with just... less computation?
        // If any weird behaviour happens with messages it's below
        if (updatedProgress.compareTo(quest.getRequiredProgress()) > -1) {
            String message = this.lang.questCompleteMessage(quest);
            if (this.notificationMethod.contains("chat")) {
                Text.sendMessage(player, message);
            }
            if (this.notificationMethod.contains("action bar")) {
                Services.sendActionBar(player, message);
            }
            this.rewardStep.process(user, quest);
        } else {
            for (BigInteger notifyAt : quest.getNotifyAt()) {
                int compared = updatedProgress.compareTo(notifyAt);
                if (compared == 0 || (notifyAt.compareTo(originalProgress) > 0 && compared > -1)) {
                    String message = this.lang.questProgressedMessage(quest, updatedProgress);
                    if (this.notificationMethod.contains("chat")) {
                        Text.sendMessage(player, message);
                    }
                    if (this.notificationMethod.contains("action bar")) {
                        Services.sendActionBar(player, message);
                    }
                    break;
                }
            }
        }
    }
}
