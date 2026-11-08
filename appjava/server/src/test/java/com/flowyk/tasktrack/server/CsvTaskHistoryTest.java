package com.flowyk.tasktrack.server;

import com.flowyk.tasktrack.api.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CsvTaskHistoryTest {
    @TempDir Path directory;
    private Instant now = Instant.parse("2026-11-08T10:00:00Z");

    private InMemoryTracking tracking(Path file) {
        var context = new ApplicationContext();
        context.setTimeProvider(() -> now);
        return new InMemoryTracking(context, file);
    }

    @Test void csv001_csv002_appendsEscapedIntervalsAcrossInstances() throws Exception {
        Path file = directory.resolve("data/tasks.csv");
        var tracking = tracking(file);
        String task = "Task, \"quoted\"\r\n\u017d";
        tracking.addTask(task);
        tracking.start(task);
        now = now.plusSeconds(5);
        tracking.start(task);
        tracking.stop("Plan the day");
        assertFalse(Files.exists(file));
        tracking.start("Plan the day");
        now = now.plusSeconds(7);
        tracking.stop("Plan the day");
        tracking.stop("Plan the day");
        var other = tracking(file);
        other.start("Plan the day");
        other.stop("Plan the day");
        assertEquals("task_name,start_time,end_time\r\n"
                + "\"Task, \"\"quoted\"\"\r\n\u017d\",2026-11-08T10:00:00Z,2026-11-08T10:00:05Z\r\n"
                + "\"Plan the day\",2026-11-08T10:00:05Z,2026-11-08T10:00:12Z\r\n"
                + "\"Plan the day\",2026-11-08T10:00:12Z,2026-11-08T10:00:12Z\r\n", Files.readString(file));
    }

    @Test void csv004_failurePreservesActiveIntervalForStopAndSwitch() throws Exception {
        Path parent = directory.resolve("blocked");
        Files.writeString(parent, "not a directory");
        Path file = parent.resolve("tasks.csv");
        var tracking = tracking(file);
        tracking.start("Plan the day");
        now = now.plusSeconds(10);
        assertThrows(UncheckedIOException.class, () -> tracking.stop("Plan the day"));
        assertThrows(UncheckedIOException.class, () -> tracking.start("Review changes"));
        assertTrue(tracking.isTracking());
        assertEquals(Duration.ofSeconds(10), tracking.getElapsedTime("Plan the day"));
        assertEquals(Duration.ZERO, tracking.getElapsedTime("Review changes"));
        Files.delete(parent);
        tracking.stop("Plan the day");
        assertFalse(tracking.isTracking());
        assertEquals(2, Files.readAllLines(file).size());
    }

    @Test void csv004_removalDoesNotWriteHistory() {
        Path file = directory.resolve("tasks.csv");
        var tracking = tracking(file);
        tracking.start("Plan the day");
        tracking.removeTask("Plan the day");
        assertFalse(Files.exists(file));
    }

    @Test void csv003_historyIsInUserDocuments() {
        Path home = directory.resolve("home");
        assertEquals(home.resolve("Documents/TaskTrack/tasks.csv"), CsvTaskHistory.historyFile(home));
        assertEquals(Path.of(System.getProperty("user.home"), "Documents", "TaskTrack", "tasks.csv"),
                CsvTaskHistory.defaultFile());
    }
}
