package io.github.battlepass.commands.bpa;

import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.commands.BpSubCommand;
import org.bukkit.command.CommandSender;

import java.math.BigInteger;
import java.util.Map;

public class NewSeasonSub extends BpSubCommand<CommandSender> {
    private final Map<String, Long> confirmations = Maps.newHashMap();
    private final UserCache userCache;

    public NewSeasonSub(BattlePlugin plugin) {
        super(plugin, true);
        this.userCache = plugin.getUserCache();
        this.inheritPermission();

        this.addFlats("new", "season");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        String name = sender.getName();
        if (this.confirmations.containsKey(name) && System.currentTimeMillis() - this.confirmations.get(name) < 30000) {
            this.userCache.asyncModifyAll(user -> {
                user.getPendingTiers().clear();
                user.getQuestStore().clear();
                user.updatePoints(current -> new BigInteger("0"));
                user.updateTier(current -> 0);
            });
            this.lang.local("new-season-reset").to(sender);
            return;
        }
        this.confirmations.put(name, System.currentTimeMillis());
        this.lang.local("confirm-new-season").to(sender);
    }
}
