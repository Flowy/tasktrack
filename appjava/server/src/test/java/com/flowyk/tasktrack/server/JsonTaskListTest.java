package com.flowyk.tasktrack.server;

import com.flowyk.tasktrack.api.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonTaskListTest {
    @TempDir Path directory;

    private InMemoryTracking tracking(Path file) {
        var context = new ApplicationContext();
        context.setTimeProvider(() -> Instant.EPOCH);
        return new InMemoryTracking(context, directory.resolve("history.csv"), file);
    }

    @Test void list001_list002_roundTripAndEmptyRestart() {
        Path file = directory.resolve("data/task-list.json");
        var first = tracking(file);
        var samples = first.getTasks();
        String name = "Žluťoučký, \"task\"\nsecond line\\";
        first.addTask(name);
        first.removeTask(samples.getFirst());
        var restored = tracking(file);
        assertEquals(first.getTasks(), restored.getTasks());
        assertEquals(name, restored.getTasks().getLast());
        assertThrows(UnsupportedOperationException.class, () -> restored.getTasks().add("x"));
        for (String task : restored.getTasks()) restored.removeTask(task);
        assertEquals(List.of(), tracking(file).getTasks());
    }

    @Test void list003_rejectsInvalidFilesWithoutOverwriting() throws Exception {
        Path file = directory.resolve("tasks.json");
        for (String invalid : List.of("", "null", "{}", "[1]", "[null]", "[\" \" ]",
                "[\"a\",\"a\"]", "[\"a\",]", "[\"a\"] garbage")) {
            Files.writeString(file, invalid);
            assertThrows(IllegalArgumentException.class, () -> tracking(file), invalid);
            assertEquals(invalid, Files.readString(file));
        }
    }

    @Test void list002_noOpsDoNotWrite() {
        Path file = directory.resolve("tasks.json");
        var tracking = tracking(file);
        tracking.addTask(tracking.getTasks().getFirst());
        tracking.removeTask("absent");
        assertFalse(Files.exists(file));
    }

    @Test void list003_failedSavePreservesListAndActiveTaskAndDisk() throws Exception {
        Path file = directory.resolve("tasks.json");
        var tracking = tracking(file);
        String task = tracking.getTasks().getFirst();
        tracking.start(task);
        var before = tracking.getTasks();
        Files.createDirectory(file);
        Files.writeString(file.resolve("keep"), "original");
        assertThrows(UncheckedIOException.class, () -> tracking.removeTask(task));
        assertThrows(UncheckedIOException.class, () -> tracking.addTask("new"));
        assertEquals(before, tracking.getTasks());
        assertTrue(tracking.isTracking());
        assertEquals("original", Files.readString(file.resolve("keep")));
        try (var files = Files.list(directory)) {
            assertEquals(List.of(file), files.toList());
        }
    }
}
