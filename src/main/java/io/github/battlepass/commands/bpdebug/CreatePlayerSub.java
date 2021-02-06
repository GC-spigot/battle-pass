package io.github.battlepass.commands.bpdebug;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.logger.DebugLogger;
import io.github.battlepass.logger.containers.BasicPlayerContainer;
import io.github.battlepass.logger.containers.QuestExecutionContainer;
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
        String playerName = this.parseArgument(args, 1);
        Player player = Bukkit.getPlayer(playerName);
        String fileName = this.logger.dump(container -> {
            if (container instanceof BasicPlayerContainer) {
                return ((BasicPlayerContainer) container).getPlayerName().equalsIgnoreCase(playerName);
            } else if (container instanceof QuestExecutionContainer && player != null) {
                return ((QuestExecutionContainer) container).getPlayer().equals(player);
            }
            return false;
        });
        this.lang.local("debug-dumped", fileName).to(sender);
    }
}
