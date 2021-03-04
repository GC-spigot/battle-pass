package io.github.battlepass.commands.bpadmin.balance;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.objects.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetBalanceSub extends BpSubCommand<CommandSender> {

    public SetBalanceSub(BattlePlugin plugin) {
        super(plugin);
        this.inheritPermission();

        this.addFlats("set");
        this.addFlatWithAliases("balance", "bal", "currency");
        this.addArgument(User.class, "player", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        this.addArgument(BigInteger.class, "amount");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 2);
        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        User user = maybeUser.get();
        BigInteger amount = this.parseArgument(args, 3);
        if (amount == null) {
            this.lang.local("invalid-number-input").to(sender);
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(user.getUuid());
        if (player.getName() != null) {
            this.lang.local("set-user-balance", player.getName(), amount.toString()).to(sender);
        }
        user.updateCurrency(current -> amount);
    }
}
