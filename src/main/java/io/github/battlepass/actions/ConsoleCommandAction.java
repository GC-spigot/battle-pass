package io.github.battlepass.actions;

import me.hyfe.simplespigot.text.replacer.Replacer;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;

public class ConsoleCommandAction extends Action {

    public ConsoleCommandAction(String condition, String value) {
        super(condition, value);
    }

    public void accept(@Nullable Replacer replacer) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replacer == null ? this.value : replacer.applyTo(this.value));
    }
}
