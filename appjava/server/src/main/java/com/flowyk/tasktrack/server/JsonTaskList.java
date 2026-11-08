package com.flowyk.tasktrack.server;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

final class JsonTaskList {
    private final Path file;

    JsonTaskList(Path file) {
        this.file = file.toAbsolutePath();
    }

    static Path defaultFile() {
        return Path.of(System.getProperty("user.home"), "Documents", "TaskTrack", "task-list.json");
    }

    List<String> load(List<String> defaults) {
        String json;
        try {
            json = Files.readString(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException missing) {
            return List.copyOf(defaults);
        } catch (IOException failure) {
            throw new UncheckedIOException("Cannot read task list from " + file, failure);
        }
        try {
            // Gson's typed List<String> conversion coerces numbers; inspect each value instead.
            var root = new com.google.gson.GsonBuilder().setStrictness(com.google.gson.Strictness.STRICT)
                    .create().fromJson(json, com.google.gson.JsonElement.class);
            if (root == null || !root.isJsonArray()) {
                throw new IllegalArgumentException("Expected an array of task names");
            }
            var names = new ArrayList<String>();
            var seen = new HashSet<String>();
            for (var value : root.getAsJsonArray()) {
                if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
                    throw new IllegalArgumentException("Expected a string task name");
                }
                String name = value.getAsString();
                if (name.isBlank() || !seen.add(name)) {
                    throw new IllegalArgumentException("Blank or duplicate task name");
                }
                names.add(name);
            }
            return List.copyOf(names);
        } catch (RuntimeException failure) {
            throw new IllegalArgumentException("Invalid task list in " + file, failure);
        }
    }

    void save(List<String> tasks) {
        Path temporary = null;
        try {
            Files.createDirectories(file.getParent());
            temporary = Files.createTempFile(file.getParent(), "task-list-", ".tmp");
            Files.writeString(temporary, new GsonBuilder().setPrettyPrinting().create().toJson(tasks) + "\n",
                    StandardCharsets.UTF_8);
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException failure) {
            throw new UncheckedIOException("Cannot save task list to " + file, failure);
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); } catch (IOException ignored) { /* Best-effort cleanup. */ }
            }
        }
    }
}
