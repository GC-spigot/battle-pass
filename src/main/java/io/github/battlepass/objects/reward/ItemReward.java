package io.github.battlepass.objects.reward;

import com.google.common.collect.Multiset;
import me.hyfe.simplespigot.service.simple.Simple;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemReward extends Reward<ItemStack> {

    public ItemReward(String id, String name, List<String> loreAddon, Multiset<ItemStack> set) {
        super(id, name, loreAddon, set);
    }

    @Override
    public void reward(Player player, int tier) {
        Simple.spigot().giveItem(player, this.set);
    }
}
