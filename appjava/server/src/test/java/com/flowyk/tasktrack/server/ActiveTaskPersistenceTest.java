package com.flowyk.tasktrack.server;

import com.flowyk.tasktrack.api.ApplicationContext;
import java.nio.file.*;
import java.time.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ActiveTaskPersistenceTest {
    @TempDir Path directory;
    Instant now = Instant.parse("2026-11-08T12:00:00.123456789Z");

    InMemoryTracking open() {
        var context = new ApplicationContext();
        context.setTimeProvider(() -> now);
        return new InMemoryTracking(context, directory.resolve("history.csv"),
                directory.resolve("list.json"), directory.resolve("active.json"));
    }

    @Test void active001_002_restoresOriginalStartSwitchesAndStops() throws Exception {
        var tracking = open();
        String first = tracking.getTasks().getFirst();
        String second = tracking.getTasks().get(1);
        tracking.start(first);
        String saved = Files.readString(directory.resolve("active.json"));
        now = now.plusSeconds(60);
        tracking = open();
        assertEquals(first, tracking.getCurrentTask());
        assertEquals(Duration.ofSeconds(60), tracking.getElapsedTime(first));
        tracking.start(first);
        tracking.stop(second);
        assertEquals(saved, Files.readString(directory.resolve("active.json")));
        tracking.start(second);
        now = now.plusSeconds(30);
        tracking = open();
        assertEquals(second, tracking.getCurrentTask());
        assertEquals(Duration.ofSeconds(30), tracking.getElapsedTime(second));
        tracking.stop(second);
        assertFalse(open().isTracking());
        assertTrue(Files.readString(directory.resolve("history.csv")).contains("2026-11-08T12:00:00.123456789Z"));
        tracking.start(second);
        tracking.removeTask(second);
        assertFalse(open().isTracking());
    }

    @Test void active004_rejectsInvalidFileAndIgnoresRemovedTask() throws Exception {
        Path file = directory.resolve("active.json");
        for (String invalid : new String[]{"", "{}", "[]", "{\"task\":12,\"startedAt\":\"bad\"}"}) {
            Files.writeString(file, invalid);
            assertThrows(IllegalArgumentException.class, this::open);
            assertEquals(invalid, Files.readString(file));
        }
        new JsonActiveTask(file).save(new JsonActiveTask.Active("removed", now));
        assertFalse(open().isTracking());
    }

    @Test void active004_failedSavePreservesTimerAndHistoryFailureRestoresFile() throws Exception {
        var tracking = open();
        String task = tracking.getTasks().getFirst();
        tracking.start(task);
        String saved = Files.readString(directory.resolve("active.json"));
        Files.createDirectory(directory.resolve("history.csv"));
        assertThrows(java.io.UncheckedIOException.class, () -> tracking.stop(task));
        assertEquals(task, tracking.getCurrentTask());
        assertEquals(saved, Files.readString(directory.resolve("active.json")));
        Files.delete(directory.resolve("active.json"));
        Files.createDirectory(directory.resolve("active.json"));
        Files.writeString(directory.resolve("active.json/blocker"), "keep");
        assertThrows(java.io.UncheckedIOException.class, () -> tracking.stop(task));
        assertEquals(task, tracking.getCurrentTask());
    }
}
