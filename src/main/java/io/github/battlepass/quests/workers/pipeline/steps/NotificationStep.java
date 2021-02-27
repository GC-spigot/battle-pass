package io.github.battlepass.quests.workers.pipeline.steps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.actions.Action;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.service.Services;
import io.github.battlepass.service.bossbar.BossBar;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationStep implements Listener {
    private final List<Action> completionActions = Lists.newArrayList();
    private final Map<UUID, BossBar> bossBars = Maps.newConcurrentMap();
    private final RewardStep rewardStep;
    private final BattlePlugin plugin;
    private final Lang lang;
    private final String notificationMethod;
    private final boolean bossBarEnabled;
    private final boolean useNotifyPercentages;
    private final int bossBarLength;
    private final Set<String> disabledBossQuests;

    public NotificationStep(BattlePlugin plugin) {
        Config settings = plugin.getConfig("settings");
        this.completionActions.addAll(settings.stringList("quest-completed-actions").stream().map(Action::parse).collect(Collectors.toList()));
        this.notificationMethod = settings.string("current-season.notification-method");
        this.bossBarEnabled = settings.bool("boss-bar.enabled");
        this.useNotifyPercentages = settings.bool("boss-bar.use-notify-percentages");
        this.bossBarLength = this.bossBarEnabled ? settings.bool("boss-bar.persistent") ? Integer.MAX_VALUE / 2 : settings.integer("boss-bar.hide-after") : -1;
        this.disabledBossQuests = Sets.newHashSet(settings.stringList("boss-bar.disabled-quest-types"));
        this.plugin = plugin;
        this.lang = plugin.getLang();
        this.rewardStep = new RewardStep(plugin);

        this.plugin.registerListeners(this);
    }

    public void process(Player player, User user, Quest quest, BigInteger originalProgress, BigInteger updatedProgress) {
        if (originalProgress.compareTo(updatedProgress) == 0) {
            return;
        }
        // this.questController.isQuestDone was removed as a check here as I think below does the same with just... less computation?
        // If any weird behaviour happens with messages it's below
        if (updatedProgress.compareTo(quest.getRequiredProgress()) > -1) {
            this.plugin.runSync(() -> {
                Action.executeSimple(player, this.completionActions, this.plugin, new Replacer().set("player", player.getName()).set("quest_name", quest.getName()).set("quest_category", quest.getCategoryId()));
            });
            this.sendBossBarIfEnabled(player, quest, 100, updatedProgress, true);
            String message = this.lang.questCompleteMessage(quest);
            if (this.notificationMethod.contains("chat")) {
                Text.sendMessage(player, message);
            }
            if (this.notificationMethod.contains("action bar")) {
                Services.sendActionBar(player, message);
            }
            this.rewardStep.process(user, quest);
        } else {
            if (!this.useNotifyPercentages) {
                double progress = Services.getPercentage(updatedProgress, quest.getRequiredProgress()).doubleValue();
                this.sendBossBarIfEnabled(player, quest, progress, updatedProgress, false);
            }
            for (BigInteger notifyAt : quest.getNotifyAt().keySet()) {
                int compared = updatedProgress.compareTo(notifyAt);
                if (compared == 0 || (notifyAt.compareTo(originalProgress) > 0 && compared > -1)) {
                    String message = this.lang.questProgressedMessage(quest, updatedProgress);
                    if (this.useNotifyPercentages) {
                        this.sendBossBarIfEnabled(player, quest, quest.getNotifyAt().get(notifyAt), updatedProgress, false);
                    }
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

    private void sendBossBarIfEnabled(Player player, Quest quest, double percentageProgress, BigInteger progress, boolean completed) {
        if (!this.bossBarEnabled || this.disabledBossQuests.contains(quest.getType())) {
            return;
        }
        String message;
        if (completed) {
            message = this.lang.questBossCompleteMessage(quest);
        } else {
            message = this.lang.questBossProgressedMessage(quest, progress);
        }
        BossBar bossBar = this.bossBars.get(player.getUniqueId());
        if (bossBar == null) {
            bossBar = BossBar.Builder.create(this.plugin, player, message);
            this.bossBars.put(player.getUniqueId(), bossBar);
        } else {
            bossBar.setTitle(message);
        }
        bossBar.setProgress(percentageProgress);
        bossBar.schedule(this.bossBarLength);
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        this.bossBars.remove(event.getPlayer().getUniqueId());
    }
}
