package io.github.battlepass.commands.bpa;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.objects.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeleteUserSub extends BpSubCommand<CommandSender> {
    private final UserCache userCache;

    public DeleteUserSub(BattlePlugin plugin) {
        super(plugin);
        this.userCache = plugin.getUserCache();
        this.inheritPermission();
        this.addFlats("delete", "user");
        this.addArgument(User.class, "player", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 2);
        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        UUID uuid = maybeUser.get().getUuid();
        Player player = Bukkit.getPlayer(uuid);
        this.userCache.invalidate(uuid);
        this.userCache.load(uuid);
        this.lang.local("target-user-data-deleted").to(player);
        if (player != null && !sender.getName().equals(player.getName())) {
            this.lang.local("user-data-deleted", player.getName()).to(sender);
        }
    }
}
