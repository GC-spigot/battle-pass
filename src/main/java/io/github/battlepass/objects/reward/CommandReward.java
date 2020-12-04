package io.github.battlepass.objects.reward;

import com.google.common.collect.Multiset;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandReward extends Reward<String> {

    public CommandReward(String id, String name, List<String> loreAddon, Multiset<String> set) {
        super(id, name, loreAddon, set);
    }

    @Override
    public void reward(Player player, int tier) {
        for (String command : this.set) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Replacer.to(command, replacer -> replacer.set("player", player.getName()).set("tier", tier)));
        }
    }
}
