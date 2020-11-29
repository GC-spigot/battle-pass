package io.github.battlepass.service.bossbar;

import io.github.battlepass.BattlePlugin;
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.entity.Player;

public interface BossBar {

    void show();

    void hide();

    void setTitle(String title);

    void setProgress(double progress);

    void schedule(int displayTime);

    void endDisplay();

    class Builder {
        public static BossBar create(BattlePlugin plugin, Player player, String title) {
            if (ServerVersion.getVersion().getVersionId() > 183) {
                return new NewBossBar(plugin, player, title);
            }
            return new OldBossBar(plugin, player, title);
        }
    }
}
