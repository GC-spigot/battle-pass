package io.github.battlepass.quests.quests.external;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.perms.Relation;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class FactionsUuidQuests extends ExternalQuestExecutor {

    public FactionsUuidQuests(BattlePlugin plugin) {
        super(plugin, "factions");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        FPlayers fPlayers = FPlayers.getInstance();
        Player victim = (Player) event.getEntity();
        Player killer = victim.getKiller();
        FPlayer factionKiller = fPlayers.getByPlayer(killer);
        FPlayer factionVictim = fPlayers.getByPlayer(victim);

        if (factionKiller.getRelationTo(factionVictim) == Relation.ENEMY) {
            this.execute("kill_enemy", killer, QuestResult::none);
        }
    }
}
