package io.github.battlepass.quests.quests.internal;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.service.base.QuestContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillMobQuest extends QuestContainer {

    public KillMobQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (entity instanceof Player) {
            return;
        }
        String stringEntity = entity.getType().toString().replace("Craft", "");
        int entityAmount = 1;
        if (event.getEntity().getType() != EntityType.ARMOR_STAND && Bukkit.getPluginManager().isPluginEnabled("WildStacker")) {
            entityAmount = WildStackerAPI.getEntityAmount(event.getEntity());
        }
        this.executionBuilder("kill-mob")
                .player(player)
                .root(stringEntity)
                .progress(entityAmount)
                .buildAndExecute();
    }
}
