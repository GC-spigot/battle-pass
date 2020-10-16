package io.github.battlepass.registry;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.command.CommandBase;
import me.hyfe.simplespigot.registry.Registry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Optional;

public class ArgumentRegistry implements Registry {
    private final CommandBase commandBase;
    private final UserCache userCache;

    public ArgumentRegistry(BattlePlugin plugin) {
        this.commandBase = plugin.getCommandBase();
        this.userCache = plugin.getUserCache();
    }

    @Override
    public void register() {
        this.commandBase
                .registerArgumentType(User.class, string -> {
                    Player player = Bukkit.getPlayerExact(string);
                    return player == null ? Optional.empty() : this.userCache.getSync(player.getUniqueId());
                })
                .registerArgumentType(BigInteger.class, string -> {
                    try {
                        return new BigInteger(string);
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                });
    }
}