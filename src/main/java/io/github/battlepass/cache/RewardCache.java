package io.github.battlepass.cache;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.reward.CommandReward;
import io.github.battlepass.objects.reward.ItemReward;
import io.github.battlepass.objects.reward.Reward;
import me.hyfe.simplespigot.cache.SimpleCache;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.config.ConfigLoader;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RewardCache extends SimpleCache<String, Reward<?>> {
    private final BattlePlugin plugin;

    public RewardCache(BattlePlugin plugin) {
        this.plugin = plugin;
    }

    public void cache() {
        Config config = this.plugin.getConfig("rewards");
        ConfigLoader.reader(config)
                .readWrap(reader -> {
                    reader.keyLoop(rewardId -> {
                        String type = reader.string("type");
                        String name = reader.string("name") == null ? "Undefined" : reader.string("name");
                        List<String> loreAddon = reader.list("lore-addon");
                        if (type.equalsIgnoreCase("command")) {
                            this.set(rewardId, new CommandReward(rewardId, name, loreAddon, HashMultiset.create(reader.list("commands"))));
                        } else if (type.equalsIgnoreCase("item")) {
                            Multiset<ItemStack> items = HashMultiset.create();
                            reader.keyLoop(rewardId.concat(".items"), itemKey -> items.add(reader.getItem("")));
                            this.set(rewardId, new ItemReward(rewardId, name, loreAddon, items));
                        }
                    });
                });
    }
}
