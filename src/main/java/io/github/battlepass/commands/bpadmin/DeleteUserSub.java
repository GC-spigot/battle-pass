package io.github.battlepass.commands.bpadmin;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.user.UserLoadEvent;
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
        User targetUser = maybeUser.get();
        UUID uuid = targetUser.getUuid();
        Player player = targetUser.getPlayer();
        this.userCache.invalidate(uuid);
        Bukkit.getPluginManager().callEvent(new UserLoadEvent(this.userCache.set(uuid, new User(uuid)), true));
        if (player == null) {
            this.lang.local("user-data-deleted-null-player").to(sender);
            return;
        }
        this.lang.local("target-user-data-deleted").to(player);
        if (!player.equals(sender)) {
            this.lang.local("user-data-deleted", player.getName()).to(sender);
        }
    }
}
