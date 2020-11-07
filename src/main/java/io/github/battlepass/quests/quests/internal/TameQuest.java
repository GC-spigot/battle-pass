package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;

public class TameQuest extends QuestExecutor {

    public TameQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTaming(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getOwner();
        Entity entity = event.getEntity();
        String entityName = event.getEntity().getCustomName();

        this.execute("tame", player, result -> {
            result.subRoot("name", entityName);
            return result.root(entity.getType().toString());
        });
    }
}
