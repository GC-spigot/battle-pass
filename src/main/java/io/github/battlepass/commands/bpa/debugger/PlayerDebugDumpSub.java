package io.github.battlepass.commands.bpa.debugger;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.BasicPlayerContainer;
import me.hyfe.simplespigot.command.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class PlayerDebugDumpSub extends SubCommand<CommandSender> {
    private final DebugLogger logger;
    private final Lang lang;

    public PlayerDebugDumpSub(BattlePlugin plugin) {
        super(plugin, true);
        this.logger = plugin.getDebugLogger();
        this.lang = plugin.getLang();

        this.inheritPermission();
        this.addFlats("debug", "dump");
        this.addArgument(String.class, "playerName", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        String fileName = this.logger.dump(container -> {
            return container instanceof BasicPlayerContainer && ((BasicPlayerContainer) container).getPlayerName().equalsIgnoreCase(this.parseArgument(args, 2));
        });
        this.lang.local("debug-dumped", fileName).to(sender);
    }
}
