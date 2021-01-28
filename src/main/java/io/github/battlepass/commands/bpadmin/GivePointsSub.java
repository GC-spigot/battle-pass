package io.github.battlepass.commands.bpadmin;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.objects.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Collectors;

public class GivePointsSub extends BpSubCommand<CommandSender> {
    private final BattlePassApi api;

    public GivePointsSub(BattlePlugin plugin) {
        super(plugin, true);
        this.api = plugin.getLocalApi();

        this.inheritPermission();
        this.addFlats("give", "points");
        this.addArgument(User.class, "player", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        this.addArgument(Integer.class, "points");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 2);
        int points = this.parseArgument(args, 3);

        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        this.api.givePoints(maybeUser.get(), points);
        this.lang.local("successful-give-points", args[2], points).to(sender);
    }
}
