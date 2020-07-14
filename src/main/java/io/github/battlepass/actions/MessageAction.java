package io.github.battlepass.actions;

import me.hyfe.simplespigot.text.Replacer;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class MessageAction extends Action {

    public MessageAction(String condition, String value) {
        super(condition, value);
    }

    public synchronized void accept(Player player, @Nullable Replacer replacer) {
        Text.sendMessage(player, replacer == null ? this.value : replacer.applyTo(this.value));
    }
}