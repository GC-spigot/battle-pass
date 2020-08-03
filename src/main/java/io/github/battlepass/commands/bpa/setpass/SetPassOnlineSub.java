package io.github.battlepass.commands.bpa.setpass;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.api.events.user.UserPassChangeEvent;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.pass.PassType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class SetPassOnlineSub extends BpSubCommand<CommandSender> {
    private final UserCache userCache;
    private final PassLoader passLoader;
    private final BattlePassApi api;

    public SetPassOnlineSub(BattlePlugin plugin) {
        super(plugin);
        this.userCache = plugin.getUserCache();
        this.passLoader = plugin.getPassLoader();
        this.api = plugin.getLocalApi();

        this.addFlats("set", "pass", "online");
        this.addArgument(String.class, "passId", sender -> Lists.newArrayList("free", "premium"));
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        String passId = this.parseArgument(args, 3);
        PassType passType = this.passLoader.passTypeOfId(passId);
        if (passType == null) {
            this.lang.local("invalid-pass-id", passId).to(sender);
            return;
        }
        if (passType.getRequiredPermission() != null) {
            this.lang.local("failed-set-pass-require-permission", passId, passType.getRequiredPermission()).to(sender);
            return;
        }
        Set<UUID> onlineUuids = Sets.newHashSet();
        for (Player player : Bukkit.getOnlinePlayers()) {
            onlineUuids.add(player.getUniqueId());
        }
        this.userCache.asyncModifyMultiple(user -> {
            UserPassChangeEvent event = new UserPassChangeEvent(user, passId);
            Bukkit.getPluginManager().callEvent(event);
            event.ifNotCancelled(consumerEvent -> {
                this.api.setPassId(user, consumerEvent.getNewPassId());
                this.lang.local("successful-set-pass", args[2], passId).to(sender);
            });
        }, onlineUuids);
    }
}
