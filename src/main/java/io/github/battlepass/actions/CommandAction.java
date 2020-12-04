package io.github.battlepass.actions;

import me.hyfe.simplespigot.annotations.Nullable;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.entity.Player;

public class CommandAction extends Action {

    public CommandAction(String condition, String value) {
        super(condition, value);
    }

    public void accept(Player player, @Nullable Replacer replacer) {
        String command = this.value;
        player.performCommand(replacer == null ? command : replacer.applyTo(command));
    }
}
