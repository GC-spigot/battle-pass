package io.github.battlepass.commands.bpadmin;

import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.commands.BpSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

public class NewSeasonSub extends BpSubCommand<CommandSender> {
    private final Map<UUID, Long> confirmations = Maps.newHashMap();
    private final UserCache userCache;

    public NewSeasonSub(BattlePlugin plugin) {
        super(plugin, true);
        this.userCache = plugin.getUserCache();

        this.inheritPermission();
        this.addFlats("new", "season");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        UUID uuid = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        if (uuid == null || (this.confirmations.containsKey(uuid) && System.currentTimeMillis() - this.confirmations.get(uuid) < 30000)) {
            this.userCache.asyncModifyAll(user -> {
                user.getPendingTiers().clear();
                user.getQuestStore().clear();
                user.updatePoints(current -> BigInteger.ZERO);
                user.updateTier(current -> 1);
            });
            this.lang.local("new-season-reset").to(sender);
            return;
        }
        this.confirmations.put(uuid, System.currentTimeMillis());
        this.lang.local("confirm-new-season").to(sender);
    }
}
