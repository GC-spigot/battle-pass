package io.github.battlepass.logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DebugLogger {
    private final Cache<Long, String> backlog = CacheBuilder.newBuilder().maximumSize(30000).expireAfterWrite(15, TimeUnit.MINUTES).build();
    private final Path debugFolder;

    public DebugLogger(BattlePlugin plugin) {
        this.debugFolder = plugin.getDataFolder().toPath().resolve("debug-files");
        this.makeBasePathIfNotExists();
    }

    public void log(String message) {
        this.backlog.put(System.currentTimeMillis(), message);
    }

    @SneakyThrows
    public String dump() {
        SimpleDateFormat lineTimeFormat = new SimpleDateFormat("[hh:mm:ss]");
        ImmutablePair<File, FileWriter> filePair = this.makeDebugFile();
        FileWriter writer = filePair.getValue();
        Map<Long, String> orderedBacklog = Maps.newTreeMap(Collections.reverseOrder());
        this.backlog.asMap().putAll(orderedBacklog);

        for (Map.Entry<Long, String> entry : orderedBacklog.entrySet()) {
            writer.write(lineTimeFormat.format(new Date(entry.getKey())).concat(" ").concat(entry.getValue()));
        }
        writer.close();
        return filePair.getKey().getName();
    }

    @SneakyThrows
    private ImmutablePair<File, FileWriter> makeDebugFile() {
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("YYYY-MM-dd hh-mm-ss");
        String fileName = fileNameFormat.format(new Date(System.currentTimeMillis()));
        File file = this.debugFolder.resolve(fileName).toFile();
        file.mkdirs();
        FileWriter writer = new FileWriter(file);
        return ImmutablePair.of(file, writer);
    }

    private void makeBasePathIfNotExists() {
        File pathFile = this.debugFolder.toFile();
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
    }
}
