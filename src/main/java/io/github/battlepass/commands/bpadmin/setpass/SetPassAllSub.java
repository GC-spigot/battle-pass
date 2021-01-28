package io.github.battlepass.commands.bpadmin.setpass;

import com.google.common.collect.Lists;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.pass.PassType;
import org.bukkit.command.CommandSender;

public class SetPassAllSub extends BpSubCommand<CommandSender> {
    private final UserCache userCache;
    private final PassLoader passLoader;
    private final BattlePassApi api;

    public SetPassAllSub(BattlePlugin plugin) {
        super(plugin);
        this.userCache = plugin.getUserCache();
        this.passLoader = plugin.getPassLoader();
        this.api = plugin.getLocalApi();

        this.inheritPermission();
        this.addFlats("set", "pass", "all");
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
        this.lang.local("successful-set-pass-global", passId).to(sender);
        this.userCache.asyncModifyAll(user -> {
            this.api.setPassId(user, passId);
        });
    }
}
