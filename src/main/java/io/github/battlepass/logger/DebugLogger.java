package io.github.battlepass.logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.logger.containers.BasicContainer;
import io.github.battlepass.logger.containers.LogContainer;
import lombok.SneakyThrows;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DebugLogger {
    private final BattlePlugin plugin;
    private final SimpleDateFormat lineTimeFormat = new SimpleDateFormat("[hh:mm:ss]");
    private final Set<LogContainer> backlog = Sets.newConcurrentHashSet();
    private final List<String> startupLog = Lists.newArrayList();
    private final Path debugFolder;
    private boolean startupOccurred = false;
    private boolean enabled = false;

    public DebugLogger(BattlePlugin plugin) {
        this.plugin = plugin;
        this.debugFolder = plugin.getDataFolder().toPath().resolve("debug-files");
        this.makeBasePathIfNotExists();
    }

    public void log(String message) {
        this.log(new BasicContainer(message));
    }

    public void log(LogContainer logContainer) {
        if (this.enabled) {
            this.backlog.add(logContainer);
        }
    }

    public void log(Zone zone, String message) {
        if (zone == Zone.RELOAD) {
            this.log("(RELOAD) ".concat(message));
            return;
        }
        if (zone == Zone.START) {
            if (!this.startupOccurred) {
                this.startupLog.add(message);
                return;
            }
            this.log(Zone.RELOAD, message);
            return;
        }
        if (this.enabled) {
            this.log(message);
        }
    }

    @SneakyThrows
    public String dump(Predicate<LogContainer> filterFunction) {
        ImmutablePair<String, FileWriter> filePair = this.makeDebugFile();
        FileWriter writer = filePair.getValue();
        List<LogContainer> orderedBacklog = this.backlog.parallelStream()
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        this.writeRunningInfo(writer);
        for (String line : this.startupLog) {
            this.writeLine(writer, line);
        }
        writer.write(System.getProperty("line.separator"));
        if (filterFunction != null) {
            for (LogContainer logContainer : orderedBacklog) {
                if (filterFunction.test(logContainer)) {
                    this.writeLine(writer, this.lineTimeFormat.format(new Date(logContainer.getTime())).concat(" ").concat(logContainer.toString()));
                }
            }
        } else {
            for (LogContainer logContainer : orderedBacklog) {
                this.writeLine(writer, this.lineTimeFormat.format(new Date(logContainer.getTime())).concat(" ").concat(logContainer.toString()));
            }
        }
        writer.close();
        return filePair.getKey();
    }

    @SneakyThrows
    private void writeRunningInfo(Writer writer) {
        this.writeLine(writer, "MC Server Version: ".concat(Bukkit.getVersion()));
        this.writeLine(writer, "Bukkit Version: ".concat(Bukkit.getBukkitVersion()));
        this.writeLine(writer, "BattlePass version: ".concat(this.plugin.getDescription().getVersion()));
        this.writeLine(writer, "Registered Authors: ".concat(String.join(", ", this.plugin.getDescription().getAuthors())));
        this.writeLine(writer, "Registered Hooks: ".concat(String.join(", ", this.plugin.getQuestRegistry().getRegisteredHooks())));
        this.writeLine(writer, "Point Information: ".concat(this.getPassInfo()));
        this.writeLine(writer, System.getProperty("line.separator"));
    }

    @SneakyThrows
    private void writeLine(Writer writer, String line) {
        writer.write(line);
        writer.write(System.getProperty("line.separator"));
    }

    private String getPassInfo() {
        PassLoader passLoader = this.plugin.getPassLoader();
        return passLoader.passTypeOfId("free").getTotalPoints(passLoader.getMaxTier()) + " total free points and " + passLoader.passTypeOfId("premium").getTotalPoints(passLoader.getMaxTier()) + " total premium points.";
    }

    @SneakyThrows
    private ImmutablePair<String, FileWriter> makeDebugFile() {
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        String fileName = fileNameFormat.format(new Date(System.currentTimeMillis()));
        File file = this.debugFolder.resolve(fileName.concat(".txt")).toFile();

        FileWriter writer = new FileWriter(file, false);
        return ImmutablePair.of(this.plugin.getDataFolder().toPath().toAbsolutePath().toString() + "\\" + file.getName(), writer);
    }

    private void makeBasePathIfNotExists() {
        File pathFile = this.debugFolder.toFile();
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
    }

    private void cleanup() {
        long currentTime = System.currentTimeMillis();
        this.plugin.runAsync(() -> {
            this.backlog.removeIf(logContainer -> currentTime - logContainer.getTime() > 600000);
        });
    }

    public void finishedStartup(boolean startupOccurred) {
        this.startupOccurred = startupOccurred;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this::cleanup, 1200, 1200);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void clear() {
        this.backlog.clear();
    }
}
