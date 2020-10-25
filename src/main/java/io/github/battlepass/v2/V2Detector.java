package io.github.battlepass.v2;

import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.logger.Zone;
import me.hyfe.simplespigot.text.Text;

import java.nio.file.Path;
import java.util.Set;
import java.util.logging.Level;

public class V2Detector {
    private final BattlePlugin plugin;

    public V2Detector(BattlePlugin plugin) {
        this.plugin = plugin;
    }

    public boolean runV2Operations() {
        this.plugin.log(Zone.START, "Starting v2 operations.");
        if (this.isV2()) {
            BattlePlugin.logger().log(Level.SEVERE, " ");
            BattlePlugin.logger().log(Level.SEVERE, " ");
            BattlePlugin.logger().log(Level.SEVERE, " ");
            BattlePlugin.logger().log(Level.SEVERE, " ");
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4----- [BattlePass v3] -----"));
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4 "));
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4BattlePass v3 could not load. You have v2 configurations present."));
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4 "));
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4Please read https://github.com/Hyfe-JavaDebug/battlepass-wiki-v3/wiki/Moving-from-v2--to--v3 for information."));
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4As BattlePass v3 is not a drop-in replacement for v2. Join our Discord https://discord.gg/hsedh6n for more help."));
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4You can still download v2 from the Version History on Spigot if you're not ready to update yet."));
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4 "));
            BattlePlugin.logger().log(Level.SEVERE, Text.modify("&4----- [BattlePass v3] -----"));
            BattlePlugin.logger().log(Level.SEVERE, " ");
            BattlePlugin.logger().log(Level.SEVERE, " ");
            BattlePlugin.logger().log(Level.SEVERE, " ");
            BattlePlugin.logger().log(Level.SEVERE, " ");
            return true;
        }
        return false;
    }

    public boolean isV2() {
        this.plugin.log(Zone.START, "Checking if v2.");
        Path dataPath = this.plugin.getDataFolder().toPath();
        Set<Path> checkPaths = Sets.newHashSet(dataPath.resolve("config.yml"), dataPath.resolve("do-not-touch.yml"), dataPath.resolve("menus").resolve("dailymissions-menu.yml"));
        for (Path path : checkPaths) {
            this.plugin.log(Zone.START, "Checking path ".concat(path.toAbsolutePath().toString()));
            if (path.toFile().exists()) {
                this.plugin.log(Zone.START, "v2 path ".concat(path.toAbsolutePath().toString()).concat(" is present."));
                return true;
            }
            this.plugin.log(Zone.START, "Path not present.");
        }
        this.plugin.log(Zone.START, "Concluded, plugin has a low chance of v2 files.");
        return false;
    }
}