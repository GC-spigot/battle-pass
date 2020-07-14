package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import n3kas.ae.api.EnchantApplyEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AdvancedEnchantmentsQuests extends ExternalQuestExecutor {

    public AdvancedEnchantmentsQuests(BattlePlugin plugin) {
        super(plugin, "advancedenchantments");
    }

    @EventHandler(ignoreCancelled = true)
    public void onCustomEnchantApply(EnchantApplyEvent event) {
        Player player = event.getPlayer();
        int level = event.getLevel();
        this.execute("enchant", player, result -> result.root(String.valueOf(level)));
    }
}
