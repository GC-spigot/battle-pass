package io.github.battlepass.objects.quests.variable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface QuestResult {

    QuestResult root(String root);

    QuestResult root(Block rootBlock);

    QuestResult root(ItemStack rootItem);

    QuestResult none();

    boolean isEligible(Player player, Variable variable);
}