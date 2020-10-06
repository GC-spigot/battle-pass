package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishingQuest extends QuestExecutor {

    public FishingQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFishCaught(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getCaught();
        PlayerFishEvent.State state = event.getState();

        if (state.equals(PlayerFishEvent.State.CAUGHT_FISH) && entity instanceof Item) {
            ItemStack itemStack = ((Item) entity).getItemStack();
            this.execute("fish", player, result -> result.root(itemStack), replacer -> replacer.set("caught", itemStack.getType()));
        }
    }
}
