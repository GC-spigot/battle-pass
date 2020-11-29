package io.github.battlepass.service.bossbar;

import io.github.battlepass.BattlePlugin;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class OldBossBar implements BossBar {
    private final BattlePlugin plugin;
    private final Player player;
    private final EntityWither wither;
    private String title;
    private BukkitTask bukkitTask;
    private boolean isShown;

    public OldBossBar(BattlePlugin plugin, Player player, String title) {
        this.plugin = plugin;
        this.player = player;
        this.title = title;
        this.wither = this.create();
    }

    private EntityWither create() {
        EntityWither wither = new EntityWither(((CraftWorld) this.player.getWorld()).getHandle());
        Location witherLocation = this.getWitherLocation(this.player.getLocation());
        wither.setCustomName(this.title);
        wither.setInvisible(true);
        wither.setLocation(witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), 0, 0);
        return wither;
    }

    public void show() {
        this.isShown = true;
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(this.wither);
        ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
    }

    public void hide() {
        this.isShown = false;
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.wither.getId());
        ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
    }

    public void setTitle(String title) {
        this.title = title;
        this.wither.setCustomName(title);
        if (this.isShown) {
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.wither.getId(), this.wither.getDataWatcher(), true);
            ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void setProgress(double progress) {
        this.wither.setHealth((float) ((progress / 100) * this.wither.getMaxHealth()));
        if (this.isShown) {
            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(this.wither.getId(), this.wither.getDataWatcher(), true);
            ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private Location getWitherLocation(Location location) {
        return location.add(location.getDirection().multiply(60));
    }

    /**
     * @param displayTime The display time in seconds
     */
    public void schedule(int displayTime) {
        this.endDisplay();
        this.show();
        this.bukkitTask = Bukkit.getScheduler().runTaskLater(this.plugin, this::endDisplay, displayTime * 20);
    }

    public void endDisplay() {
        if (this.bukkitTask == null) {
            return;
        }
        this.bukkitTask.cancel();
        this.bukkitTask = null;
        this.hide();
    }
}
