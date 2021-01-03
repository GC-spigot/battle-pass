package io.github.battlepass.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.enums.Category;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.quests.variable.Variable;
import io.github.battlepass.validator.QuestValidator;
import lombok.SneakyThrows;
import me.hyfe.simplespigot.cache.SimpleCache;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.item.SpigotItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QuestCache extends SimpleCache<String, Map<String, Quest>> {
    private final BattlePlugin plugin;
    private final Path dataFolder;
    private final QuestValidator questValidator;
    private final Set<Integer> notifyAt;
    private final Set<Quest> weeklyQuests = Sets.newHashSet();
    private final Set<String> placeholderTypes = Sets.newHashSet();
    private boolean questsFinishedLoading = false;
    private int maxWeek = 0;

    public QuestCache(BattlePlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder().toPath();
        this.questValidator = plugin.getQuestValidator();
        this.notifyAt = Sets.newHashSet(plugin.getConfig("settings").list("current-season.notify-at-percentages"));
    }

    public Set<Quest> getAllQuests() {
        Set<Quest> allQuests = Sets.newHashSet(this.weeklyQuests);
        allQuests.addAll(this.plugin.getDailyQuestReset().getCurrentQuests());
        return allQuests;
    }

    public Quest getQuest(String categoryId, String questId) {
        Quest output = this.getQuests(categoryId).get(questId);
        if (!this.questsFinishedLoading) {
            this.plugin.log("(QuestCache) MAJOR -> getQuest method called before quests finished loading.");
        }
        this.plugin.log("(QuestCache) getQuest method returned ".concat(output == null ? "null" : String.valueOf(output)));
        return output;
    }

    @NotNull
    public Map<String, Quest> getQuests(String categoryId) {
        return this.get(categoryId).orElseGet(() -> this.set(categoryId, Maps.newLinkedHashMap()));
    }

    @Nullable
    public Map<String, Quest> getQuestsVerbatim(String categoryId) {
        return this.get(categoryId).orElse(null);
    }

    public int getMaxWeek() {
        return this.maxWeek;
    }

    @SneakyThrows
    public void cache() {
        this.createDefaultFiles();
        Set<File> questCollections = Files.walk(this.dataFolder.resolve("quests"))
                .map(Path::toFile)
                .filter(file -> file.getName().endsWith("-quests.yml"))
                .collect(Collectors.toSet());
        for (File questsFile : questCollections) {
            String id = questsFile.getName().replace("-quests.yml", "");
            if (!id.contains("daily") && !id.contains("week")) {
                BattlePlugin.logger().warning("Failed to load the ".concat(id).concat(" quests"));
                continue;
            }
            Config questsConfig = new Config(this.plugin, questsFile, true);
            AtomicInteger failureCounter = new AtomicInteger();
            for (String key : questsConfig.keys("quests", false)) {
                Quest quest = this.parseSection(questsConfig, key, id, "quests.".concat(key).concat("."));
                if (!this.questValidator.checkQuest(quest, failureCounter)) {
                    continue;
                }
                if (quest.getType().startsWith("placeholderapi_")) { // PlaceholderAPI quest
                    this.placeholderTypes.add(quest.getType());
                }
                this.getQuests(id).put(key, quest);
                if (id.contains("week")) {
                    this.weeklyQuests.add(quest);
                }
            }
            this.maxWeek = Math.max(this.maxWeek, Category.stripWeek(id));
            BattlePlugin.logger().info("Finished loading the " + id + " quests. ".concat(failureCounter.intValue() == 0 ? "All quests loaded successfully."
                    : failureCounter.toString() + " quests failed to load. See the console for more info."));
        }
        this.questsFinishedLoading = true;
    }

    public Set<String> getPlaceholderTypes() {
        return this.placeholderTypes;
    }

    private Quest parseSection(Config config, String questId, String categoryId, String section) {
        String name = config.string(section.concat("name"));
        ItemStack item = SpigotItem.toItem(config, section.concat("item"));
        String type = config.string(section.concat("type"));
        BigInteger requiredProgress = new BigInteger(config.forcedString(section.concat("required-progress")));
        int points = config.integer(section.concat("points"));
        String exclusiveTo = config.string(section.concat("exclusive"));
        boolean antiAbuse = config.bool(section.concat("anti-abuse"));
        Set<String> whitelistedWorlds = Sets.newHashSet(config.stringList(section.concat("whitelisted-worlds")));
        Set<String> blacklistedWorlds = Sets.newHashSet(config.stringList(section.concat("blacklisted-worlds")));
        return new Quest(questId, categoryId, name, item, type, requiredProgress,
                Variable.of(config, section), points, this.notifyAt, whitelistedWorlds, blacklistedWorlds, exclusiveTo, antiAbuse);
    }

    private void createDefaultFiles() {
        Path path = this.dataFolder.resolve("quests");
        if (path.toFile().exists()) {
            return;
        }
        this.plugin.saveResource("quests/daily-quests.yml", false);
        this.plugin.saveResource("quests/week-1-quests.yml", false);
        this.plugin.saveResource("quests/week-2-quests.yml", false);
        this.plugin.saveResource("quests/week-3-quests.yml", false);
        this.plugin.saveResource("quests/week-4-quests.yml", false);
    }
}
