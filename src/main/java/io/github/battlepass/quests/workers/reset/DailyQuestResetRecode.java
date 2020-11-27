package io.github.battlepass.quests.workers.reset;

import com.google.common.collect.Lists;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.api.events.server.DailyQuestsRefreshEvent;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.enums.Category;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.validator.DailyQuestValidator;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DailyQuestResetRecode {
    private final BattlePlugin plugin;
    private final BattlePassApi api;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final int amount;
    private final ZoneId timeZone;
    private ZonedDateTime resetTime;

    protected final QuestCache questCache;
    protected final UserCache userCache;
    protected Set<Quest> currentQuests;
    protected Set<Quest> permanentQuests;

    public DailyQuestResetRecode(BattlePlugin plugin, Set<Quest> currentQuests) {
        DailyQuestValidator validator = plugin.getDailyQuestValidator();
        Config settings = plugin.getConfig("settings");
        this.plugin = plugin;
        this.api = plugin.getLocalApi();
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
    }

    public Set<Quest> getCurrentQuests() {
        return this.currentQuests;
    }

    public ZonedDateTime getResetTime() {
        return this.resetTime;
    }

    public void start() {
        if (this.shouldNotDoDailyQuests()) {
            this.currentQuests.clear();
            return;
        }
        this.executorService.schedule(() -> {
            this.reset();
            this.start();
            for (User user : this.plugin.getUserCache().values()) {
                Player player = Bukkit.getPlayer(user.getUuid());
                if (player != null && player.isOnline()) {
                    this.plugin.getLang().external("daily-quest-reset").to(player);
                }
            }
        }, this.now().until(this.resetTime.plusDays(this.now().until(this.resetTime, ChronoUnit.SECONDS) <= 0 ? 1 : 0), ChronoUnit.SECONDS), TimeUnit.SECONDS);
        /*
        this.executorService.scheduleAtFixedRate(() -> {
            this.reset();
            this.start();
            for (User user : this.plugin.getUserCache().values()) {
                Player player = Bukkit.getPlayer(user.getUuid());
                if (player != null && player.isOnline()) {
                    this.plugin.getLang().external("daily-quest-reset").to(player);
                }
            }
        }, this.now().until(this.resetTime.plusDays(this.now().until(this.resetTime, ChronoUnit.SECONDS) <= 0 ? 1 : 0), ChronoUnit.SECONDS), TimeUnit.DAYS.toMinutes(1), TimeUnit.SECONDS);
         */
    }

    public void reset() {
        if (this.shouldNotDoDailyQuests()) {
            this.currentQuests.clear();
            return;
        }
        this.userCache.asyncModifyAll(user -> user.getQuestStore().asMap().put(Category.DAILY.id(), new ConcurrentHashMap<>()));
        this.currentQuests = this.permanentQuests;
        int max = Math.min(this.questCache.getQuests(Category.DAILY.id()).size(), this.amount) - this.permanentQuests.size();
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

    /*public String asString() {
        return Simple.time().format(TimeUnit.SECONDS, this.between());
    }*/

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
}
