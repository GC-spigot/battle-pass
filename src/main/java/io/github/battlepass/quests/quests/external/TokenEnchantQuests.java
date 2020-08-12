package io.github.battlepass.quests.quests.external;

import com.vk2gpz.tokenenchant.event.TEEnchantEvent;
import com.vk2gpz.tokenenchant.event.TETokenChangeEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class TokenEnchantQuests extends ExternalQuestExecutor {

    public TokenEnchantQuests(BattlePlugin plugin) {
        super(plugin, "tokenenchant");
    }

    @EventHandler(ignoreCancelled = true)
    public void onTokensChange(TETokenChangeEvent event) {
        double gainedTokens = event.getNewTokenValue() - event.getOldTokenValue();
        if (gainedTokens < 1) {
            return;
        }
        Player player = event.getOfflinePlayer().getPlayer();
        this.execute("gain", player, (int) gainedTokens, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemEnchant(TEEnchantEvent event) {
        Player player = event.getPlayer();
        String enchantName = event.getCEHandler().getDisplayName();
        int amount = event.getNewLevel() - event.getOldLevel();
        this.execute("enchant", player, amount, result -> result.root(enchantName));
    }
}
