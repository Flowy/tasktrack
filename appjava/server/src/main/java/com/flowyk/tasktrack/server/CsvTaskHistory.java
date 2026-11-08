package com.flowyk.tasktrack.server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

final class CsvTaskHistory {
    private final Path file;

    CsvTaskHistory(Path file) {
        this.file = file.toAbsolutePath();
    }

    static Path defaultFile() {
        return historyFile(Path.of(System.getProperty("user.home")));
    }

    static Path historyFile(Path home) {
        return home.resolve("Documents/TaskTrack/tasks.csv");
    }

    void append(String task, Instant start, Instant end) {
        try {
            Files.createDirectories(file.getParent());
            String header = !Files.exists(file) || Files.size(file) == 0
                    ? "task_name,start_time,end_time\r\n" : "";
            String row = "\"" + task.replace("\"", "\"\"") + "\"," + start + "," + end + "\r\n";
            Files.writeString(file, header + row, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException failure) {
            throw new UncheckedIOException("Cannot save task history to " + file, failure);
        }
    }
}
