package io.github.battlepass.actions;

import me.hyfe.simplespigot.annotations.Nullable;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.entity.Player;

public class MessageAction extends Action {

    public MessageAction(String condition, String value) {
        super(condition, value);
    }

    public synchronized void accept(Player player, @Nullable Replacer replacer) {
        Text.sendMessage(player, replacer == null ? this.value : replacer.applyTo(this.value));
    }
}