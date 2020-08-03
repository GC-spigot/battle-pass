package io.github.battlepass.objects.quests.variable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface QuestResult {

    QuestResult root(String root);

    QuestResult root(Block rootBlock);

    //QuestResult root(Entity entity);

    QuestResult root(ItemStack rootItem);

    //QuestResult subRoot(String subRoot, String value);

    QuestResult none();

    String getRoot();

    boolean isEligible(Player player, Variable variable);
}