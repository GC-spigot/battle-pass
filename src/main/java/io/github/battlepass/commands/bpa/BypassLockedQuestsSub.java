package io.github.battlepass.commands.bpa;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.objects.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BypassLockedQuestsSub extends BpSubCommand<CommandSender> {
    private final Lang lang;

    public BypassLockedQuestsSub(BattlePlugin plugin) {
        super(plugin);
        this.lang = plugin.getLang();
        this.inheritPermission();
        this.addFlats("bypass", "locked", "quests");
        this.addArgument(User.class, "player");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 3);
        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        User user = maybeUser.get();
        Player player = user.getPlayer();
        boolean newStatus = user.toggleBypassLockedWeeks();
        this.lang.local("target-toggle-lock-bypass-".concat(newStatus ? "on" : "off")).to(player);
        if (player != null && !sender.getName().equals(player.getName())) {
            this.lang.local("toggle-lock-bypass-".concat(newStatus ? "on" : "off"), player.getName()).to(sender);
        }
    }
}
