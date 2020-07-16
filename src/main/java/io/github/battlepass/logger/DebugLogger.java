package io.github.battlepass.logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DebugLogger {
    private final BattlePlugin plugin;
    private final SimpleDateFormat lineTimeFormat = new SimpleDateFormat("[hh:mm:ss]");
    private final Cache<Long, LogContainer> backlog = CacheBuilder.newBuilder().maximumSize(30000).expireAfterWrite(15, TimeUnit.MINUTES).build();
    private final List<String> startupLog = Lists.newArrayList();
    private final Path debugFolder;
    private boolean startupOccurred = false;

    public DebugLogger(BattlePlugin plugin) {
        this.plugin = plugin;
        this.debugFolder = plugin.getDataFolder().toPath().resolve("debug-files");
        this.makeBasePathIfNotExists();
    }

    public void log(String message) {
        this.backlog.put(System.currentTimeMillis(), new BasicContainer(message));
    }

    public void log(LogContainer logContainer) {
        this.backlog.put(System.currentTimeMillis(), logContainer);
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
        this.log(message);
    }

    @SneakyThrows
    public String dump() {

        ImmutablePair<String, FileWriter> filePair = this.makeDebugFile();
        FileWriter writer = filePair.getValue();
        Map<Long, LogContainer> orderedBacklog = Maps.newTreeMap(Collections.reverseOrder());
        this.backlog.asMap().putAll(orderedBacklog);

        this.writeRunningInfo(writer);
        for (String line : this.startupLog) {
            this.writeLine(writer, line);
        }
        writer.write(System.getProperty("line.separator"));
        for (Map.Entry<Long, LogContainer> entry : orderedBacklog.entrySet()) {
            this.writeLine(writer, this.lineTimeFormat.format(new Date(entry.getKey())).concat(" ").concat(entry.getValue().toString()));
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
        this.writeLine(writer, System.getProperty("line.separator"));
    }

    @SneakyThrows
    private void writeLine(Writer writer, String line) {
        writer.write(line);
        writer.write(System.getProperty("line.separator"));
    }

    @SneakyThrows
    private ImmutablePair<String, FileWriter> makeDebugFile() {
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("YYYY-MM-dd hh-mm-ss");
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
}
