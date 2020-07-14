package io.github.battlepass.quests.quests.internal;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.QuestExecutor;
import me.hyfe.simplespigot.version.MultiMaterial;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MilkQuest extends QuestExecutor {

    public MilkQuest(BattlePlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMilk(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof Cow) || !player.getItemInHand().getType().equals(MultiMaterial.BUCKET.getMaterial())) {
            return;
        }
        this.execute("milk", player, QuestResult::none);
    }
}
