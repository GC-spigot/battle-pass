package io.github.battlepass.commands.bpdebug;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.BasicPlayerContainer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class CreatePlayerSub extends BpSubCommand<CommandSender> {
    private final DebugLogger logger;

    public CreatePlayerSub(BattlePlugin plugin) {
        super(plugin);
        this.logger = plugin.getDebugLogger();

        this.inheritPermission();
        this.addFlat("create");
        this.addArgument(String.class, "player", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        String fileName = this.logger.dump(container -> {
            return container instanceof BasicPlayerContainer && ((BasicPlayerContainer) container).getPlayerName().equalsIgnoreCase(this.parseArgument(args, 1));
        });
        this.lang.local("debug-dumped", fileName).to(sender);
    }
}
