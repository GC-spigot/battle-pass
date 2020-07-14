package io.github.battlepass.actions;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.logging.Level;

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
                Bukkit.getLogger().log(Level.SEVERE, "Incorrect sound format. Must me specified as such: NAME:volume:pitch.");
            }
        }
        player.playSound(player.getLocation(), Sound.valueOf(soundName), volume, pitch);
    }
}
