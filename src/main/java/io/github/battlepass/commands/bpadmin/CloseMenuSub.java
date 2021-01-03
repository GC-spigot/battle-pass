package io.github.battlepass.commands.bpadmin;

import com.google.common.collect.Lists;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.actions.Action;
import io.github.battlepass.commands.BpSubCommand;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class CloseMenuSub extends BpSubCommand<CommandSender> {

    public CloseMenuSub(BattlePlugin plugin) {
        super(plugin, true);

        this.inheritPermission();
        this.addFlats("close", "menu");
        this.addArgument(Player.class, "player", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Player player = this.parseArgument(args, 2);
        if (player == null) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        this.lang.local("closed-player-menu", player.getName()).to(sender);
        Action.executeSimple(player, Lists.newArrayList(Action.parse("[menu]{close}")), this.plugin, new Replacer());
    }
}
