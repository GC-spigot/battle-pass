package io.github.battlepass;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.actions.Action;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.cache.RewardCache;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.cache.listener.ConnectionListener;
import io.github.battlepass.cache.listener.UserLoadListener;
import io.github.battlepass.commands.AliasesListener;
import io.github.battlepass.commands.bp.BpCommand;
import io.github.battlepass.commands.bpa.BpaCommand;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.enums.Category;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.Zone;
import io.github.battlepass.logger.containers.LogContainer;
import io.github.battlepass.menus.MenuFactory;
import io.github.battlepass.menus.service.MenuIllustrator;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.placeholders.PlaceholderApiHook;
import io.github.battlepass.quests.workers.pipeline.QuestPipeline;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import io.github.battlepass.registry.ArgumentRegistry;
import io.github.battlepass.registry.QuestRegistry;
import io.github.battlepass.storage.DailyQuestStorage;
import io.github.battlepass.storage.UserStorage;
import io.github.battlepass.v2.V2Detector;
import io.github.battlepass.validator.DailyQuestValidator;
import io.github.battlepass.validator.QuestValidator;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.menu.listener.MenuListener;
import me.hyfe.simplespigot.plugin.SpigotPlugin;
import me.hyfe.simplespigot.storage.StorageSettings;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class BattlePlugin extends SpigotPlugin {
    private static BattlePassApi api;
    private DebugLogger debugLogger;
    private DailyQuestValidator dailyQuestValidator;
    private QuestValidator questValidator;
    private BattlePassApi localApi;
    private PassLoader passLoader;
    private UserCache userCache;
    private QuestCache questCache;
    private RewardCache rewardCache;
    private QuestPipeline questPipeline;
    private QuestRegistry questRegistry;
    private QuestController questController;
    private DailyQuestReset dailyQuestReset;
    private MenuFactory menuFactory;
    private MenuIllustrator menuIllustrator;
    private Storage<User> userStorage;
    private Storage<DailyQuestReset> resetStorage;
    private Cache<String, Map<Integer, Set<Action>>> actionCache;
    private PlaceholderApiHook placeholderApiHook;
    private Lang lang;
    private ZonedDateTime seasonStartDate;
    private int placeholderRuns = 0;

    @Override
    public void onEnable() {
        this.debugLogger = new DebugLogger(this);
        V2Detector v2Detector = new V2Detector(this);
        if (v2Detector.runV2Operations()) {
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        this.configRelations();
        this.load();
        this.placeholders();
    }

    @Override
    public void onDisable() {
        V2Detector v2Detector = new V2Detector(this);
        if (!v2Detector.isV2()) {
            this.unload();
        }
    }

    public static BattlePassApi getApi() {
        return api;
    }

    public BattlePassApi getLocalApi() {
        return this.localApi;
    }

    public DebugLogger getDebugLogger() {
        return this.debugLogger;
    }

    public QuestValidator getQuestValidator() {
        return this.questValidator;
    }

    public DailyQuestValidator getDailyQuestValidator() {
        return this.dailyQuestValidator;
    }

    public PassLoader getPassLoader() {
        return this.passLoader;
    }

    public UserCache getUserCache() {
        return this.userCache;
    }

    public QuestCache getQuestCache() {
        return this.questCache;
    }

    public RewardCache getRewardCache() {
        return this.rewardCache;
    }

    public QuestPipeline getQuestPipeline() {
        return this.questPipeline;
    }

    public QuestRegistry getQuestRegistry() {
        return this.questRegistry;
    }

    public QuestController getQuestController() {
        return this.questController;
    }

    public DailyQuestReset getDailyQuestReset() {
        return this.dailyQuestReset;
    }

    public MenuFactory getMenuFactory() {
        return this.menuFactory;
    }

    public MenuIllustrator getMenuIllustrator() {
        return this.menuIllustrator;
    }

    public Storage<User> getUserStorage() {
        return this.userStorage;
    }

    public Storage<DailyQuestReset> getResetStorage() {
        return this.resetStorage;
    }

    public Lang getLang() {
        return this.lang;
    }

    public Cache<String, Map<Integer, Set<Action>>> getActionCache() {
        return this.actionCache;
    }

    public ZonedDateTime getSeasonStartDate() {
        return this.seasonStartDate;
    }

    public Config getConfig(String name) {
        return this.getConfigStore().getConfig(name);
    }

    public boolean areDailyQuestsEnabled() {
        Config settings = this.getConfig("settings");
        return !settings.has("daily-quests-enabled") || settings.bool("daily-quests-enabled");
    }

    public void log(String message) {
        this.debugLogger.log(message);
    }

    public void log(LogContainer container) {
        this.debugLogger.log(container);
    }

    public void log(Zone zone, String message) {
        this.debugLogger.log(zone, message);
    }

    public void reload() {
        this.getConfigStore().reloadReloadableConfigs();
        this.lang.reload();
        this.unload();
        this.load();
        if (this.placeholderApiHook == null) {
            this.placeholderRuns = 0;
            this.placeholders();
        } else {
            this.placeholderApiHook.reload(this);
        }
        System.gc();
    }

    private void load() {
        this.setStorageSettings();
        this.setSeasonStartDate();

        this.questValidator = new QuestValidator();
        this.dailyQuestValidator = new DailyQuestValidator(this);

        this.userStorage = new UserStorage(this);
        this.resetStorage = new DailyQuestStorage(this);
        this.rewardCache = new RewardCache(this);
        this.passLoader = new PassLoader(this);
        this.questCache = new QuestCache(this);
        this.questController = new QuestController(this);
        this.userCache = new UserCache(this);
        this.menuFactory = new MenuFactory(this);
        this.questRegistry = new QuestRegistry(this);

        this.rewardCache.cache();
        this.passLoader.load();
        this.questCache.cache();
        this.userCache.loadOnline();

        this.localApi = new BattlePassApi(this);
        api = this.localApi;

        this.questPipeline = new QuestPipeline(this);
        this.menuFactory = new MenuFactory(this);
        this.menuIllustrator = new MenuIllustrator();

        this.dailyQuestReset = this.resetStorage.load("daily-data");
        this.dailyQuestReset = this.dailyQuestReset == null ? new DailyQuestReset(this, Sets.newHashSet()) : this.dailyQuestReset;
        this.actionCache = CacheBuilder.newBuilder().expireAfterAccess(20, TimeUnit.SECONDS).build();

        this.dailyQuestReset.start();
        this.getSavingController().addSavable(this.userCache, this.getConfig("settings").integer("storage-options.auto-save-interval") * 20);

        this.registerRegistries(
                new ArgumentRegistry(this),
                this.questRegistry
        );
        this.registerListeners(
                new AliasesListener(this),
                new MenuListener(),
                new ConnectionListener(this),
                new UserLoadListener(this)
        );
        this.runSync(() -> {
            this.getCommandBase().getCommands().clear();
            this.registerCommands(
                    new BpaCommand(this),
                    new BpCommand(this)
            );
        });
    }

    private void unload() {
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        this.userCache.save();
        this.userCache.getSubCache().invalidateAll();
        this.resetStorage.save("daily-data", this.dailyQuestReset);
        this.userStorage.closeBack();
        this.resetStorage.closeBack();
        if (this.placeholderApiHook != null) {
            this.placeholderApiHook.unregister();
        }
        for (UUID uuid : this.menuFactory.getInsideMenu()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            player.closeInventory();
        }
    }

    private void placeholders() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            this.placeholderApiHook = new PlaceholderApiHook(this);
            this.placeholderApiHook.register();
            return;
        }
        if (this.placeholderApiHook == null && this.placeholderRuns < 10) {
            this.placeholderRuns++;
            Bukkit.getScheduler().runTaskLater(this, this::placeholders, 100);
        }
    }

    private void configRelations() {
        this.log(Zone.START, "Initializing config relations.");
        this.getConfigStore()
                .config("settings", Path::resolve, true)
                .config("lang", Path::resolve, true)
                .config("rewards", Path::resolve, true)
                .config("portal-menu", (path, name) -> path.resolve("menus").resolve("portal"), true)
                .config("daily-quests-menu", (path, name) -> path.resolve("menus").resolve("daily-quests"), true)
                .config("quest-overview-menu", (path, name) -> path.resolve("menus").resolve("quest-overview"), true)
                .config("week-menu", (path, name) -> path.resolve("menus").resolve("week"), true)
                .config("rewards-menu", (path, name) -> path.resolve("menus").resolve("rewards"), true)
                .common("storage-type", "settings", config -> config.string("storage-options.storage-method"));
        this.log(Zone.START, "Loading Lang.");
        this.lang = new Lang(this);
    }

    private void setStorageSettings() {
        Config config = this.getConfig("settings");
        StorageSettings storageSettings = this.getStorageSettings();
        storageSettings.setAddress(config.string("storage-options.address"));
        storageSettings.setDatabase(config.string("storage-options.database"));
        storageSettings.setPrefix(config.string("storage-options.prefix"));
        storageSettings.setUsername(config.string("storage-options.username"));
        storageSettings.setPassword(config.string("storage-options.password"));
        storageSettings.setConnectionTimeout(config.integer("storage-options.pool-settings.connection-timeout"));
        storageSettings.setMaximumLifetime(config.integer("storage-options.pool-settings.maximum-lifetime"));
        storageSettings.setMaximumPoolSize(config.integer("storage-options.pool-settings.maximum-pool-size"));
        storageSettings.setMinimumIdle(config.integer("storage-options.pool-settings.minimum-idle"));
        storageSettings.setProperties(Maps.newHashMap());
    }

    private void setSeasonStartDate() {
        Config config = this.getConfig("settings");
        String[] date = config.string("current-season.start-date").split("/");
        String[] time = config.string("current-season.start-time").split(":");
        int year = Integer.parseInt(date[2]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[0]);
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        String zoneId = config.string("current-season.time-zone");
        this.seasonStartDate = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.of(zoneId));
    }
}
