package io.github.battlepass.quests.quests.external;

import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import io.github.battlepass.objects.quests.variable.QuestResult;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.logging.Level;

public class PlaceholderApiQuests extends ExternalQuestExecutor {
    private final BattlePlugin plugin;
    private final Set<String> integerPlaceholders = Sets.newHashSet();
    private final Set<String> matchPlaceholders = Sets.newHashSet();

    public PlaceholderApiQuests(BattlePlugin plugin) {
        super(plugin, "placeholderapi");
        this.plugin = plugin;
        for (String placeholder : plugin.getQuestCache().getPlaceholderTypes()) {
            String reducedPlaceholder = placeholder.substring(15);
            if (reducedPlaceholder.startsWith("integer_")) {
                this.integerPlaceholders.add(reducedPlaceholder.substring(8));
            } else if (reducedPlaceholder.startsWith("match_")) {
                this.matchPlaceholders.add(reducedPlaceholder.substring(6));
            } else {
                Bukkit.getLogger().log(Level.WARNING, "Failed to parse PlaceholderAPI quest variable: ".concat(placeholder));
            }
        }
        this.placeholderRun();
    }

    private void placeholderRun() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (String placeholder : this.matchPlaceholders) {
                    String placeholderValue = PlaceholderAPI.setPlaceholders(player, "%".concat(placeholder).concat("%"));
                    this.execute("match_".concat(placeholder), player, result -> result.root(placeholderValue));
                }
                for (String placeholder : this.integerPlaceholders) {
                    String placeholderValue = PlaceholderAPI.setPlaceholders(player, "%".concat(placeholder).concat("%"));
                    if (StringUtils.isNumeric(placeholderValue)) {
                        this.execute("integer_".concat(placeholder), player, Integer.parseInt(placeholderValue), QuestResult::none,
                                replace -> replace.set("placeholder_value", placeholderValue), true);
                    }
                }
            }
        }, 40, 40);
    }
}
