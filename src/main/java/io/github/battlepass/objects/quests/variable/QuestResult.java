package io.github.battlepass.objects.quests.variable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface QuestResult {

    QuestResult root(String root);

    QuestResult root(Block rootBlock);

    QuestResult root(ItemStack rootItem);

    QuestResult subRoot(String subRoot, String value);

    QuestResult subRoot(ItemStack itemStack);

    QuestResult none();

    boolean isEligible(Player player, Variable variable);

    String getEffectiveRoot();

    Map<String, String> getSubRoots();

    String toString();
}