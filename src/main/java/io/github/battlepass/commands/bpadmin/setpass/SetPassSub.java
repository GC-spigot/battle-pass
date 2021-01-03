package io.github.battlepass.commands.bpadmin.setpass;

import com.google.common.collect.Lists;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.pass.PassType;
import io.github.battlepass.objects.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Collectors;

public class SetPassSub extends BpSubCommand<CommandSender> {
    private final BattlePassApi api;
    private final PassLoader passLoader;

    public SetPassSub(BattlePlugin plugin) {
        super(plugin, true);
        this.api = plugin.getLocalApi();
        this.passLoader = plugin.getPassLoader();

        this.inheritPermission();
        this.addFlats("set", "pass");
        this.addArgument(User.class, "player", sender -> Bukkit.getOnlinePlayers()
                .parallelStream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        this.addArgument(String.class, "passId", sender -> Lists.newArrayList("free", "premium"));
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 2);
        String passId = this.parseArgument(args, 3);

        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        User user = maybeUser.get();
        PassType passType = this.passLoader.passTypeOfId(passId);
        if (passType == null) {
            this.lang.local("invalid-pass-id", passId).to(sender);
            return;
        }
        if (passType.getRequiredPermission() != null) {
            if (this.passLoader.passTypeOfId(user.getPassId()) != null) {
                this.lang.local("failed-set-pass-require-permission", user.getPassId(), passType.getRequiredPermission()).to(sender);
                return;
            }
            this.lang.local("failed-set-pass-require-permission", passId, passType.getRequiredPermission()).to(sender);
            return;
        }
        this.api.setPassId(user, passId);
        this.lang.local("successful-set-pass", args[2], passId).to(sender);
    }
}
