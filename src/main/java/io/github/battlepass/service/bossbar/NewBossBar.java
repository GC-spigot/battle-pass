package io.github.battlepass.service.bossbar;

import io.github.battlepass.BattlePlugin;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class NewBossBar implements BossBar {
    private final org.bukkit.boss.BossBar bossBar;
    private final BattlePlugin plugin;
    private BukkitTask bukkitTask;

    public NewBossBar(BattlePlugin plugin, Player player, String title) {
        this.plugin = plugin;

        Config settings = plugin.getConfig("settings");
        this.bossBar = Bukkit.createBossBar(title, BarColor.valueOf(settings.string("boss-bar.color")), BarStyle.valueOf(settings.string("boss-bar.style")));
        this.bossBar.setVisible(false);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void show() {
        this.bossBar.setVisible(true);
    }

    @Override
    public void hide() {
        this.bossBar.setVisible(false);
    }

    @Override
    public void setTitle(String title) {
        this.bossBar.setTitle(title);
    }

    @Override
    public void setProgress(double progress) {
        this.bossBar.setProgress(progress / 100);
    }

    @Override
    public void schedule(int displayTime) {
        this.endDisplay();
        this.show();
        this.bukkitTask = Bukkit.getScheduler().runTaskLater(this.plugin, this::endDisplay, displayTime * 20);
    }

    @Override
    public void endDisplay() {
        if (this.bukkitTask == null) {
            return;
        }
        this.bukkitTask.cancel();
        this.bukkitTask = null;
        this.hide();
    }
}
