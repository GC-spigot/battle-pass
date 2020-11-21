package io.github.battlepass.loader;

import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.objects.pass.PassType;
import lombok.SneakyThrows;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PassLoader {
    private final Plugin plugin;
    private final Path dataFolder;
    private final Map<String, PassType> passTypes = Maps.newHashMap();
    private int maxTier = 0;

    public PassLoader(BattlePlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder().toPath().toAbsolutePath();
    }

    public Map<String, PassType> getPassTypes() {
        return this.passTypes;
    }

    public PassType passTypeOfId(String id) {
        return this.passTypes.get(id);
    }

    public int getMaxTier() {
        return this.maxTier;
    }

    @SneakyThrows
    public void load() {
        this.createDefaultFiles();
        Set<File> passTypeFiles = Files
                .walk(this.dataFolder.resolve("passes"))
                .map(Path::toFile)
                .filter(file -> !file.getName().equals("passes"))
                .collect(Collectors.toSet());
        for (File passTypeFile : passTypeFiles) {
            String id = passTypeFile.getName().replace(".yml", "");
            if (!id.equalsIgnoreCase("free") && !id.equalsIgnoreCase("premium")) {
                continue;
            }
            Config passTypeConfig = new Config(this.plugin, passTypeFile, true);
            if (!passTypeConfig.has("name")) {
                BattlePlugin.logger().warning("Failed to load the pass type with the id: ".concat(id));
                continue;
            }
            PassType passType = new PassType(id, passTypeConfig);
            this.passTypes.put(id, passType);
            if (passType.getTiers().lastKey() > this.maxTier) {
                this.maxTier = passType.getTiers().lastKey();
            }
            BattlePlugin.logger().info("Successfully loaded the pass type with the id: ".concat(id));
        }
    }

    private void createDefaultFiles() {
        Path passPath = this.plugin.getDataFolder().toPath().resolve("passes");
        if (!passPath.resolve("free.yml").toFile().exists()) {
            this.plugin.saveResource("passes/free.yml", false);
        }
        if (!passPath.resolve("premium.yml").toFile().exists()) {
            this.plugin.saveResource("passes/premium.yml", false);
        }
    }
}
