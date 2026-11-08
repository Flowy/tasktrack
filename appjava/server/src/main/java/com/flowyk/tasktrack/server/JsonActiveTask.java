package com.flowyk.tasktrack.server;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Strictness;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.Instant;

final class JsonActiveTask {
    record Active(String task, Instant startedAt) {}
    private final Path file;

    JsonActiveTask(Path file) { this.file = file.toAbsolutePath(); }

    static Path defaultFile() {
        return Path.of(System.getProperty("user.home"), "Documents", "TaskTrack", "active-task.json");
    }

    Active load() {
        String json;
        try {
            json = Files.readString(file);
        } catch (NoSuchFileException missing) {
            return null;
        } catch (IOException failure) {
            throw new UncheckedIOException("Cannot read active task from " + file, failure);
        }
        try {
            JsonElement root = new GsonBuilder().setStrictness(Strictness.STRICT).create()
                    .fromJson(json, JsonElement.class);
            if (root == null) throw new IllegalArgumentException("Empty active task file");
            if (root.isJsonNull()) return null;
            JsonObject value = root.getAsJsonObject();
            String task = string(value, "task");
            if (task.isBlank()) throw new IllegalArgumentException("Blank task name");
            return new Active(task, Instant.parse(string(value, "startedAt")));
        } catch (RuntimeException failure) {
            throw new IllegalArgumentException("Invalid active task in " + file, failure);
        }
    }

    private static String string(JsonObject value, String key) {
        JsonElement field = value.get(key);
        if (field == null || !field.isJsonPrimitive() || !field.getAsJsonPrimitive().isString()) {
            throw new IllegalArgumentException("Expected string " + key);
        }
        return field.getAsString();
    }

    void save(Active active) {
        Path temporary = null;
        try {
            Files.createDirectories(file.getParent());
            temporary = Files.createTempFile(file.getParent(), "active-task-", ".tmp");
            JsonObject value = null;
            if (active != null) {
                value = new JsonObject();
                value.addProperty("task", active.task());
                value.addProperty("startedAt", active.startedAt().toString());
            }
            Files.writeString(temporary, new GsonBuilder().setPrettyPrinting().create().toJson(value) + "\n");
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException failure) {
            throw new UncheckedIOException("Cannot save active task to " + file, failure);
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); } catch (IOException ignored) { /* Best effort. */ }
            }
        }
    }
}
