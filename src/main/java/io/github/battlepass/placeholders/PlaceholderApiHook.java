package io.github.battlepass.placeholders;


import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.controller.UserController;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hyfe.simplespigot.service.simple.Simple;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PlaceholderApiHook extends PlaceholderExpansion {
    private Lang lang;
    private BattlePassApi api;
    private UserCache userCache;
    private PassLoader passLoader;
    private QuestCache questCache;
    private DailyQuestReset dailyQuestReset;
    private UserController userController;
    private ZonedDateTime seasonStartDate;
    private ZonedDateTime seasonEndDate;

    public PlaceholderApiHook(BattlePlugin plugin) {
        this.setClassValues(plugin);
        BattlePlugin.logger().info("Register PlaceholderAPI placeholders");
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String placeholder) {
        switch (placeholder) {
            case "test":
                return "successful";
            case "quest_amount":
                return String.valueOf(this.questCache.getAllQuests().size());
            case "time_to_daily_reset":
                return this.dailyQuestReset.asString();
            case "week":
                return String.valueOf(this.api.getCurrentDisplayWeek());
            case "time_to_next_week":
                return this.api.hasSeasonEnded() ?
                        "00:00" :
                        Simple.time().format(TimeUnit.SECONDS, ChronoUnit.SECONDS.between(ZonedDateTime.now().withZoneSameInstant(this.api.getZone()), this.seasonStartDate.plusWeeks(this.api.getCurrentWeek())));
            case "time_to_season_end":
                String finishedSection = "season-finished-message";
                return this.api.hasSeasonEnded() ?
                        this.lang.has(finishedSection) ?
                                this.lang.external(finishedSection).asString() :
                                "Finished" :
                        Simple.time().format(TimeUnit.SECONDS, ChronoUnit.SECONDS.between(ZonedDateTime.now().withZoneSameInstant(this.api.getZone()), this.seasonEndDate));
            default:
                break;
        }
        if (offlinePlayer == null) {
            BattlePlugin.logger().warning("Could not get placeholder ".concat(placeholder).concat(" (player null)"));
            return "???";
        }
        Optional<User> optionalUser = this.userCache.getSync(offlinePlayer.getUniqueId());
        if (!optionalUser.isPresent()) {
            return "??? User not present";
        }
        User user = optionalUser.get();
        switch (placeholder) {
            case "points":
            case "experience":
                return user.getPoints().toString();
            case "tier":
                return String.valueOf(user.getTier());
            case "pass_type":
                return this.passLoader.passTypeOfId(user.getPassId()).getName();
            case "pass_id":
                return user.getPassId();
            case "balance":
            case "currency":
                return user.getCurrency().toString();
            case "completed_quests":
                return String.valueOf(this.userController.getQuestsDone(user, true));
            default:
                break;
        }
        if (placeholder.startsWith("completed_quests_")) {
            String category = placeholder.substring(17).replace(" ", "");
            if (category.isEmpty()) {
                return "category empty";
            }
            return String.valueOf(this.userController.getQuestsDone(user, category));
        }
        return "Invalid Placeholder";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "battlepass";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Hyfe/Zak";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public void tryUnregister() {
        try {
            this.unregister();
        } catch (Throwable ex) {
            BattlePlugin.logger().warning("Please update to the latest version of PlaceholderAPI. Currently there seems to be some issues.");
        }
    }

    public void setClassValues(BattlePlugin plugin) {
        this.lang = plugin.getLang();
        this.api = plugin.getLocalApi();
        this.userCache = plugin.getUserCache();
        this.passLoader = plugin.getPassLoader();
        this.questCache = plugin.getQuestCache();
        this.dailyQuestReset = plugin.getDailyQuestReset();
        this.userController = plugin.getUserController();
        this.seasonStartDate = plugin.getSeasonStartDate();
        this.seasonEndDate = plugin.getSeasonEndDate();
    }
}
