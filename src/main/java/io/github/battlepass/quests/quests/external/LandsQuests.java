package io.github.battlepass.quests.quests.external;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import me.angeschossen.lands.api.events.ChunkPostClaimEvent;
import me.angeschossen.lands.api.events.LandCreateEvent;
import me.angeschossen.lands.api.events.LandDeleteEvent;
import me.angeschossen.lands.api.events.LandInvitePlayerEvent;
import me.angeschossen.lands.api.events.LandTrustPlayerEvent;
import me.angeschossen.lands.api.events.LandUntrustPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class LandsQuests extends ExternalQuestExecutor {

    public LandsQuests(BattlePlugin plugin) {
        super(plugin, "lands");
    }

    @EventHandler(ignoreCancelled = true)
    public void onLandJoin(LandTrustPlayerEvent event) {
        Player player = Bukkit.getPlayer(event.getTargetUUID());

        this.execute("join", player, result -> result);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLandLeave(LandUntrustPlayerEvent event) {
        Player player = Bukkit.getPlayer(event.getTargetUUID());

        this.execute("leave", player, result -> result);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLandCreate(LandCreateEvent event) {
        Player player = event.getLandPlayer().getPlayer();

        this.execute("create", player, result -> result);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLandDisband(LandDeleteEvent event) {
        Player player = Bukkit.getPlayer(event.getLand().getOwnerUID());

        this.execute("disband", player, result -> result);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLandInvite(LandInvitePlayerEvent event) {
        Player invitedPlayer = Bukkit.getPlayer(event.getTargetUUID());

        this.execute("invited", invitedPlayer, result -> result);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLandClaim(ChunkPostClaimEvent event) {
        Player player = event.getLandPlayer().getPlayer();

        this.execute("claim_chunk", player, result -> result);
    }
}
