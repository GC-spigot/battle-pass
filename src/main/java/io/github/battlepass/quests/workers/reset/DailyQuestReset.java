package io.github.battlepass.quests.workers.reset;

import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.api.events.server.DailyQuestsRefreshEvent;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.enums.Category;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.validator.DailyQuestValidator;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.service.simple.Simple;
import me.hyfe.simplespigot.service.simple.services.TimeService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DailyQuestReset {
    private final BattlePlugin plugin;
    private final BattlePassApi api;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final int amount;
    private final ZoneId timeZone;
    private ScheduledFuture<?> resetTask;
    private ZonedDateTime resetTime;

    private final TimeService.SimpleTimeFormat timeFormat;
    protected final QuestCache questCache;
    protected final UserCache userCache;
    protected Set<Quest> currentQuests;
    protected Set<Quest> permanentQuests;
    protected List<Quest> availableQuests;

    public DailyQuestReset(BattlePlugin plugin, Set<Quest> currentQuests) {
        DailyQuestValidator validator = plugin.getDailyQuestValidator();
        Config settings = plugin.getConfig("settings");
        this.plugin = plugin;
        this.api = plugin.getLocalApi();
        this.timeFormat = this.createTimeFormat(plugin.getLang());
        this.questCache = plugin.getQuestCache();
        this.userCache = plugin.getUserCache();
        this.amount = settings.integer("current-season.daily-quest-amount");
        this.timeZone = ZoneId.of(settings.string("current-season.time-zone"));
        this.resetTime = this.parseTime(this.now().withSecond(0), settings.string("current-season.daily-quest-reset-time"));
        this.currentQuests = currentQuests;
        this.permanentQuests = settings.stringList("permanent-daily-quest-ids")
                .stream()
                .map(id -> this.questCache.getQuest(Category.DAILY.id(), id))
                .filter(validator::checkQuest)
                .collect(Collectors.toSet());
        this.availableQuests = this.questCache.getQuests(Category.DAILY.id()).values().stream()
                .filter(quest -> !this.permanentQuests.contains(quest))
                .collect(Collectors.toList());
    }

    public Set<Quest> getCurrentQuests() {
        return this.currentQuests;
    }

    public void start() {
        if (this.shouldNotDoDailyQuests()) {
            this.currentQuests.clear();
            return;
        }
        this.resetTask = this.executorService.schedule(() -> {
            this.reset();
            this.start();
            for (User user : this.plugin.getUserCache().values()) {
                Player player = Bukkit.getPlayer(user.getUuid());
                if (player != null && player.isOnline()) {
                    this.plugin.getLang().external("daily-quest-reset").to(player);
                }
            }
        }, this.timeToResetSeconds(), TimeUnit.SECONDS);
    }

    public void stop() {
        this.resetTask.cancel(true);
    }

    public void reset() {
        if (this.shouldNotDoDailyQuests()) {
            this.currentQuests.clear();
            return;
        }
        this.userCache.asyncModifyAll(user -> user.getQuestStore().asMap().put(Category.DAILY.id(), new ConcurrentHashMap<>()));
        this.currentQuests = Sets.newHashSet(this.permanentQuests);
        int max = Math.min(this.availableQuests.size(), this.amount - this.permanentQuests.size());
        int iterations = 0;
        Collections.shuffle(this.availableQuests);
        for (Quest quest : this.availableQuests) {
            if (iterations >= max) {
                break;
            }
            this.currentQuests.add(quest);
            iterations++;
        }
        Bukkit.getPluginManager().callEvent(new DailyQuestsRefreshEvent(this.currentQuests));
    }

    public long timeToResetSeconds() {
        if (this.now().until(this.resetTime, ChronoUnit.SECONDS) <= 0) {
            this.resetTime = this.resetTime.plusDays(1);
        }
        return this.now().until(this.resetTime, ChronoUnit.SECONDS);
    }

    public String asString() {
        TimeService timeService = Simple.time();
        long timeToReset = this.timeToResetSeconds();
        return this.timeFormat == null ? timeService.format(TimeUnit.SECONDS, timeToReset) : timeService.format(this.timeFormat, TimeUnit.SECONDS, timeToReset);
    }

    private boolean shouldNotDoDailyQuests() {
        Config settings = this.plugin.getConfig("settings");
        String configKey = "season-finished.stop-daily-quests";
        return !this.plugin.areDailyQuestsEnabled() || this.api.hasSeasonEnded() && settings.has(configKey) && settings.bool(configKey);
    }

    private ZonedDateTime now() {
        return ZonedDateTime.now().withZoneSameInstant(this.timeZone);
    }

    private ZonedDateTime parseTime(ZonedDateTime date, String time) {
        String[] timeSplit = time.split(":");
        return date.withHour(StringUtils.isNumeric(timeSplit[0]) ? Integer.parseInt(timeSplit[0]) : 0).withMinute(timeSplit.length > 1 ? StringUtils.isNumeric(timeSplit[1]) ? Integer.parseInt(timeSplit[1]) : 0 : 0);
    }

    private TimeService.SimpleTimeFormat createTimeFormat(Lang lang) {
        return lang.has("daily-quest-time-format") ? new TimeService.SimpleTimeFormat(
                null,
                lang.external("daily-quest-time-format.with-months-weeks").toString(),
                lang.external("daily-quest-time-format.with-weeks-days").toString(),
                lang.external("daily-quest-time-format.with-days-hours").toString(),
                lang.external("daily-quest-time-format.with-hours-minutes").toString(),
                lang.external("daily-quest-time-format.with-minutes-seconds").toString(),
                lang.external("daily-quest-time-format.with-seconds").toString()
        ) : null;
    }
}
