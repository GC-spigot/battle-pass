package io.github.battlepass.storage;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.enums.Category;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import io.github.battlepass.validator.DailyQuestValidator;
import me.hyfe.simplespigot.json.TypeTokens;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.storage.storage.load.Deserializer;
import me.hyfe.simplespigot.storage.storage.load.Serializer;

import java.util.List;
import java.util.stream.Collectors;

public class DailyQuestStorage extends Storage<DailyQuestReset> {
    private final BattlePlugin plugin;
    private final DailyQuestValidator validator;

    public DailyQuestStorage(BattlePlugin plugin) {
        super(plugin, factory -> factory.create(plugin.getConfigStore().commons().get("storageMethod"), path -> path.resolve("misc-storage")));
        this.plugin = plugin;
        this.validator = plugin.getDailyQuestValidator();
    }

    @Override
    public Serializer<DailyQuestReset> serializer() {
        return ((dailyQuestReset, json, gson) -> {
            json.addProperty("current-quests", gson.toJson(dailyQuestReset.getCurrentQuests().stream().map(Quest::getId).collect(Collectors.toList())));
            return json;
        });
    }

    @Override
    public Deserializer<DailyQuestReset> deserializer() {
        return (json, gson) -> {
            List<String> currentQuests = gson.fromJson(json.get("current-quests").getAsString(), TypeTokens.findType());
            return new DailyQuestReset(this.plugin, currentQuests
                    .stream()
                    .map(id -> this.plugin.getQuestCache().getQuest(Category.DAILY.id(), id))
                    .filter(this.validator::checkQuest)
                    .collect(Collectors.toSet()));
        };
    }
}
