package io.github.battlepass.quests.workers.reset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.server.DailyQuestsRefreshEvent;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.enums.Category;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.service.simple.Simple;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class DailyQuestReset {
    private final BattlePlugin plugin;
    private final int amount;
    private final ZoneId timeZone;
    private ZonedDateTime whenReset;

    protected final QuestCache questCache;
    protected final UserCache userCache;
    protected Set<Quest> currentQuests;

    public DailyQuestReset(BattlePlugin plugin, Set<Quest> currentQuests, Function<DailyQuestReset, ZonedDateTime> whenReset) {
        Config settings = plugin.getConfig("settings");
        this.plugin = plugin;
        this.questCache = plugin.getQuestCache();
        this.userCache = plugin.getUserCache();
        this.currentQuests = currentQuests;
        this.amount = settings.integer("current-season.daily-quest-amount");
        this.timeZone = ZoneId.of(settings.string("current-season.time-zone"));
        this.whenReset = this.parseTime(this.now().withSecond(0), settings.string("current-season.daily-quest-reset-time"));
    }

    public DailyQuestReset(BattlePlugin plugin, Set<Quest> currentQuests) {
        this(plugin, currentQuests, DailyQuestReset::now);
        this.reset();
    }

    public Set<Quest> getCurrentQuests() {
        return this.currentQuests;
    }

    public ZonedDateTime getWhenReset() {
        return this.whenReset;
    }

    public void start() {
        if (!this.plugin.areDailyQuestsEnabled()) {
            return;
        }
        if (this.between() <= 0) {
            this.whenReset = this.whenReset.plusDays(1);
        }
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.reset();
            this.start();
            for (User user : this.plugin.getUserCache().getSubCache().asMap().values()) {
                Player player = Bukkit.getPlayer(user.getUuid());
                if (player != null && player.isOnline()) {
                    this.plugin.getLang().external("daily-quest-reset").to(player);
                }
            }
        }, this.between() * 20 + 40);
    }

    public void reset() {
        if (!this.plugin.areDailyQuestsEnabled()) {
            return;
        }
        this.userCache.asyncModifyAll(user -> user.getQuestStore().asMap().put(Category.DAILY.id(), Maps.newConcurrentMap()));
        this.currentQuests.clear();
        int max = Math.min(this.questCache.getQuests(Category.DAILY.id()).size(), this.amount);
        int iterations = 0;
        List<Quest> allQuests = Lists.newArrayList(this.questCache.getQuests(Category.DAILY.id()).values());
        Collections.shuffle(allQuests);
        for (Quest quest : allQuests) {
            if (iterations >= max) {
                break;
            }
            this.currentQuests.add(quest);
            iterations++;
        }
        Bukkit.getPluginManager().callEvent(new DailyQuestsRefreshEvent(this.currentQuests));
    }

    public String asString() {
        return Simple.time().format(TimeUnit.SECONDS, this.between());
    }

    private ZonedDateTime now() {
        return ZonedDateTime.now().withZoneSameInstant(this.timeZone);
    }

    private long between() {
        return ChronoUnit.SECONDS.between(this.now(), this.whenReset);
    }

    private ZonedDateTime parseTime(ZonedDateTime date, String time) {
        String[] timeSplit = time.split(":");
        return date.withHour(StringUtils.isNumeric(timeSplit[0]) ? Integer.parseInt(timeSplit[0]) : 0).withMinute(timeSplit.length > 1 ? StringUtils.isNumeric(timeSplit[1]) ? Integer.parseInt(timeSplit[1]) : 0 : 0);
    }
}
