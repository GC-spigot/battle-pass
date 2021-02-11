package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTameEvent;

public class TameQuest extends QuestContainer {

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

        this.executionBuilder("tame")
                .player(player)
                .root(entity.getType().toString())
                .subRoot("name", entityName)
                .progressSingle()
                .buildAndExecute();
    }
}
