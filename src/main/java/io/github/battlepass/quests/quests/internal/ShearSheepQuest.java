package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ShearSheepQuest extends QuestContainer {

    public ShearSheepQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        String entityName = event.getEntity().getCustomName();
        if (!(entity instanceof Sheep)) {
            return;
        }
        this.executionBuilder("shear")
                .player(player)
                .root(entityName)
                .progressSingle()
                .buildAndExecute();
    }
}
