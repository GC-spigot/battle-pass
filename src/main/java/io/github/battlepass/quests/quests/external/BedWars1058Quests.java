package io.github.battlepass.quests.quests.external;

import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerFirstSpawnEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.events.shop.ShopBuyEvent;
import com.andrei1058.bedwars.api.events.upgrades.UpgradeBuyEvent;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.quests.variable.QuestResult;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class BedWars1058Quests extends ExternalQuestExecutor {

    public BedWars1058Quests(BattlePlugin plugin) {
        super(plugin, "bedwars1058");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBedBreak(PlayerBedBreakEvent event) {
        Player player = event.getPlayer();
        String victimTeam = event.getVictimTeam().getName();
        String arenaName = event.getArena().getDisplayName();

        if (victimTeam == null || arenaName == null) {
            return;
        }
        this.execute("break_beds", player, result -> {
            return result.root(victimTeam);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKill(PlayerKillEvent event) {
        Player killer = event.getKiller();
        Player victim = event.getVictim();
        PlayerKillEvent.PlayerKillCause deathCause = event.getCause();
        String arenaName = event.getArena().getDisplayName();

        if (killer == null || victim == null || deathCause == null || arenaName == null) {
            return;
        }
        this.execute("kill_players", killer, result -> {
            return result.root(deathCause.toString());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFirstSpawn(PlayerFirstSpawnEvent event) {
        Player player = event.getPlayer();
        String arenaName = event.getArena().getDisplayName();
        String teamName = event.getTeam().getName();

        if (arenaName == null || teamName == null) {
            return;
        }
        this.execute("play_games", player, result -> {
            return result.root(arenaName);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopBuy(ShopBuyEvent event) {
        Player player = event.getBuyer();
        this.execute("buy_items", player, QuestResult::none);
    }

    @EventHandler(ignoreCancelled = true)
    public void onUpgradeBuy(UpgradeBuyEvent event) {
        Player player = event.getPlayer();
        this.execute("buy_upgrades", player, QuestResult::none);
    }
}
