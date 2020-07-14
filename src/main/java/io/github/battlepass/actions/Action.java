package io.github.battlepass.actions;

import me.hyfe.simplespigot.text.Replacer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public abstract class Action {
    protected final String condition;
    protected final String value;

    public Action(String condition, String value) {
        this.condition = condition;
        this.value = value;
    }

    public static Action parse(String string) {
        String condition = string.contains("(") && string.contains(")") ? string.substring(string.indexOf("(") + 1, string.indexOf(")")).toLowerCase() : "";
        String value = string.contains("{") && string.contains("}") ? string.substring(string.indexOf("{") + 1, string.indexOf("}")) : "";
        switch (string.contains("[") && string.contains("]") ? string.substring(string.indexOf("[") + 1, string.indexOf("]")).toLowerCase() : "") {
            case "menu":
                return new MenuAction(condition, value);
            case "message":
                return new MessageAction(condition, value);
            case "sound":
                return new SoundAction(condition, value);
            default:
                return null;
        }
    }

    public static void executeSimple(Player player, Iterable<Action> actions, @Nullable Replacer replacer) {
        for (Action action : actions) {
            if (action instanceof MessageAction) {
                ((MessageAction) action).accept(player, replacer);
            } else if (action instanceof SoundAction) {
                ((SoundAction) action).accept(player);
            }
        }
    }
}