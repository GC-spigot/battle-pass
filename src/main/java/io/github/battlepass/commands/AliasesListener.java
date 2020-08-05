package io.github.battlepass.commands;

import io.github.battlepass.BattlePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AliasesListener implements Listener {
    private final List<String> bpAliases;

    public AliasesListener(BattlePlugin plugin) {
        this.bpAliases = plugin.getConfig("settings").list("battle-pass-aliases");
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String strippedCommand = event.getMessage().replace("/", "");
        String[] arguments = strippedCommand.split(" ");
        for (String alias : this.bpAliases) {
            if (arguments[0].equalsIgnoreCase(alias)) {
                event.setCancelled(true);
                String preparedArguments = arguments.length > 1 ? Arrays
                        .stream(arguments, 1, arguments.length)
                        .collect(Collectors.joining(" ")) : "";
                player.performCommand("battlepass ".concat(preparedArguments));
                break;
            }
        }
    }
}
