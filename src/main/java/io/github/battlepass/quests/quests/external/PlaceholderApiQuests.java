package io.github.battlepass.quests.quests.external;

import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.LogContainer;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Set;

public class PlaceholderApiQuests extends ExternalQuestExecutor {
    private final BattlePlugin plugin;
    private final Set<String> integerPlaceholders = Sets.newHashSet();
    private final Set<String> matchPlaceholders = Sets.newHashSet();

    public PlaceholderApiQuests(BattlePlugin plugin, Set<String> placeholderTypes) {
        super(plugin, "placeholderapi");
        this.plugin = plugin;
        DebugLogger logger = plugin.getDebugLogger();
        for (String placeholder : placeholderTypes) {
            String reducedPlaceholder = placeholder.substring(15);
            if (reducedPlaceholder.startsWith("integer_")) {
                String toAdd = reducedPlaceholder.substring(8);
                logger.log(LogContainer.of("(PlaceholderAPI Quests) Adding placeholderapi integer quest with placeholder ".concat(toAdd)));
                this.integerPlaceholders.add(toAdd);
            } else if (reducedPlaceholder.startsWith("match_")) {
                String toAdd = reducedPlaceholder.substring(6);
                logger.log(LogContainer.of("(PlaceholderAPI Quests) Adding placeholderapi match quest with placeholder ".concat(toAdd)));
                this.matchPlaceholders.add(toAdd);
            } else {
                BattlePlugin.logger().warning("Failed to parse PlaceholderAPI quest variable: ".concat(placeholder));
            }
        }
        this.placeholderRun();
    }

    private void placeholderRun() {
        Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (String placeholder : this.matchPlaceholders) {
                    String placeholderValue = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, "%" + placeholder + "%");
                    this.execute("match_".concat(placeholder), player, result -> result.root(placeholderValue));
                }
                for (String placeholder : this.integerPlaceholders) {
                    String placeholderValue = PlaceholderAPI.setPlaceholders((OfflinePlayer) player, "%" + placeholder + "%");
                    String numberOnlyPlaceholder = placeholderValue.replaceAll("[^\\d.]", "");
                    if (!numberOnlyPlaceholder.isEmpty()) {
                        this.execute("integer_".concat(placeholder), player, new BigInteger(placeholderValue), QuestResult::none,
                                replace -> replace.set("placeholder_value", placeholderValue), true);
                    }
                }
            }
        }, 40, 40);
    }
}
