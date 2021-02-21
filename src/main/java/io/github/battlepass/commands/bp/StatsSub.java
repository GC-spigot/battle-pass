package io.github.battlepass.commands.bp;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.loader.PassLoader;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.entity.Player;

import java.math.BigInteger;

public class StatsSub extends BpSubCommand<Player> {
    private final UserCache userCache;
    private final PassLoader passLoader;
    private final BattlePassApi api;
    private final boolean blockPermissionEnabled;

    public StatsSub(BattlePlugin plugin) {
        super(plugin);
        this.userCache = plugin.getUserCache();
        this.passLoader = plugin.getPassLoader();
        this.api = plugin.getLocalApi();
        this.blockPermissionEnabled = plugin.getConfig("settings").bool("enable-ban-permission");

        this.addFlat("stats");
    }

    @Override
    public void onExecute(Player sender, String[] args) {
        if (this.blockPermissionEnabled && sender.hasPermission("battlepass.block") && !sender.hasPermission("battlepass.admin")) {
            this.lang.external("disallowed-permission").to(sender);
            return;
        }
        if (!this.lang.has("stats-command")) {
            sender.sendMessage(Text.modify("&cThe stats command is not configured."));
            return;
        }
        this.userCache.get(sender.getUniqueId()).thenAccept(optionalUser -> {
            optionalUser.ifPresent(user -> {
                String userPassId = user.getPassId();
                String passDisplayName = this.passLoader.passTypeOfId(userPassId).getName();
                int tier = user.getTier();
                int totalPoints = 0;
                for (int i = 1; i < tier; i++) {
                    totalPoints += this.api.getRequiredPoints(i, userPassId);
                }
                BigInteger finalTotalPoints = user.getPoints().add(new BigInteger(String.valueOf(totalPoints)));
                this.lang.external("stats-command", replacer -> replacer
                        .set("player", sender.getName())
                        .set("pass_type", passDisplayName)
                        .set("pass_id", userPassId)
                        .set("tier", tier)
                        .set("points", user.getPoints())
                        .set("required_points", this.api.getRequiredPoints(tier + 1, userPassId))
                        .set("total_points", finalTotalPoints.toString())
                        .set("balance", user.getCurrency().toString()))
                        .to(sender);
            });
        });
    }
}
