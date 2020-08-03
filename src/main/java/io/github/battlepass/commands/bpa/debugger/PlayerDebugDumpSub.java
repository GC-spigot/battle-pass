package io.github.battlepass.commands.bpa.debugger;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.BasicPlayerContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class PlayerDebugDumpSub extends BpSubCommand<CommandSender> {
    private final DebugLogger logger;

    public PlayerDebugDumpSub(BattlePlugin plugin) {
        super(plugin, true);
        this.logger = plugin.getDebugLogger();

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
