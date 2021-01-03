package io.github.battlepass.actions;

import io.github.battlepass.BattlePlugin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundAction extends Action {

    public SoundAction(String condition, String value) {
        super(condition, value);
    }

    public synchronized void accept(Player player) {
        String soundName = this.value.toUpperCase();
        float volume = 1;
        float pitch = 0;
        if (soundName.contains(":")) {
            String[] splitValue = this.value.replace(" ", "").split(":");
            soundName = splitValue[0];
            try {
                volume = Float.parseFloat(splitValue[1]);
                pitch = splitValue.length == 3 ? Integer.parseInt(splitValue[2]) : 1;
            } catch (NumberFormatException ex) {
                BattlePlugin.logger().severe("Incorrect sound format. Must me specified as such: NAME:volume:pitch.");
            }
        }
        player.playSound(player.getLocation(), Sound.valueOf(soundName), volume, pitch);
    }
}
