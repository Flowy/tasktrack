package com.flowyk.tasktrack.server;

import java.time.Duration;
import java.time.Instant;
import com.flowyk.tasktrack.api.ApplicationContext;
import com.flowyk.tasktrack.api.TimeProvider;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTimeTrackingTest {
    @Test void color001_reportsAuthoritativeActivity() {
        assertFalse(tracking.isTracking());
        tracking.start(first);
        assertTrue(tracking.isTracking());
        tracking.start(second);
        tracking.stop(first);
        assertTrue(tracking.isTracking());
        tracking.removeTask(first);
        assertTrue(tracking.isTracking());
        tracking.stop(second);
        assertFalse(tracking.isTracking());
        tracking.start(second);
        tracking.removeTask(second);
        assertFalse(tracking.isTracking());
    }

    private static final class TestTimeProvider implements TimeProvider {
        private Instant instant = Instant.parse("2026-11-07T12:00:00Z");

        public Instant now() { return instant; }
        void advanceNanos(long nanos) { instant = instant.plusNanos(nanos); }
    }

    private final TestTimeProvider now = new TestTimeProvider();
    @org.junit.jupiter.api.io.TempDir
    java.nio.file.Path directory;
    private ApplicationContext context(TimeProvider provider) {
        var context = new ApplicationContext();
        context.setTimeProvider(provider);
        context.setTrackingImplementation(new InMemoryTracking(context, directory.resolve("history.csv")));
        return context;
    }

    private ApplicationContext context;
    private InMemoryTracking tracking;
    @org.junit.jupiter.api.BeforeEach void setup() {
        context = context(now);
        tracking = (InMemoryTracking) context.getTrackingImplementation();
    }
    private final String first = "Plan the day";
    private final String second = "Implement task selection";

    @Test
    void time001_time002_accumulatesOnlyRunningIntervals() {
        assertEquals(Duration.ZERO, tracking.getElapsedTime(first));
        tracking.stop(first);
        tracking.start(first);
        now.advanceNanos(10);
        tracking.start(first);
        assertEquals(Duration.ofNanos(10), tracking.getElapsedTime(first));
        now.advanceNanos(5);
        tracking.stop(first);
        now.advanceNanos(100);
        tracking.stop(first);
        assertEquals(Duration.ofNanos(15), tracking.getElapsedTime(first));
        tracking.start(first);
        now.advanceNanos(7);
        assertEquals(Duration.ofNanos(22), tracking.getElapsedTime(first));
        tracking.stop(first);
        assertEquals(Duration.ofNanos(22), tracking.getElapsedTime(first));
    }

    @Test
    void time004_switchesOneActiveTaskAndIgnoresInactiveStop() {
        tracking.start(first);
        now.advanceNanos(10);
        tracking.start(second);
        now.advanceNanos(20);
        tracking.stop(first);
        assertEquals(Duration.ofNanos(10), tracking.getElapsedTime(first));
        assertEquals(Duration.ofNanos(20), tracking.getElapsedTime(second));
        tracking.start(first);
        now.advanceNanos(5);
        assertEquals(Duration.ofNanos(15), tracking.getElapsedTime(first));
        assertEquals(Duration.ofNanos(20), tracking.getElapsedTime(second));
    }

    @Test
    void time002_invalidArgumentsLeaveActiveIntervalIntact() {
        tracking.start(first);
        now.advanceNanos(10);
        for (String invalid : new String[] {null, "", " ", "Missing"}) {
            assertThrows(IllegalArgumentException.class, () -> tracking.start(invalid));
            assertThrows(IllegalArgumentException.class, () -> tracking.stop(invalid));
            assertThrows(IllegalArgumentException.class, () -> tracking.getElapsedTime(invalid));
        }
        now.advanceNanos(5);
        assertEquals(Duration.ofNanos(15), tracking.getElapsedTime(first));
    }

    @Test
    void time003_time004_removalDiscardsTimeAndInstancesAreIsolated() {
        tracking.start(first);
        now.advanceNanos(10);
        tracking.start(second);
        tracking.removeTask(first);
        tracking.addTask(first);
        assertEquals(Duration.ZERO, tracking.getElapsedTime(first));
        tracking.removeTask(second);
        assertThrows(IllegalArgumentException.class, () -> tracking.getElapsedTime(second));
        tracking.addTask(second);
        now.advanceNanos(20);
        assertEquals(Duration.ZERO, tracking.getElapsedTime(second));
        tracking.start(second);
        tracking.addTask(second);
        now.advanceNanos(5);
        assertEquals(Duration.ofNanos(5), tracking.getElapsedTime(second));
        var other = context(now);
        assertEquals(Duration.ZERO, ((InMemoryTracking) other.getTrackingImplementation()).getElapsedTime(second));
    }

    @Test
    void time005_contextExposesTheInjectedProviderUsedByTracking() {
        assertSame(now, context.getTimeProvider());
        assertSame(tracking, context.getTrackingImplementation());
        tracking.start(first);
        now.advanceNanos(2_000_000_003L);
        assertEquals(Duration.ofSeconds(2, 3), tracking.getElapsedTime(first));
        assertThrows(NullPointerException.class, () -> new InMemoryTracking(null));
        assertThrows(NullPointerException.class, () -> context.setTimeProvider(null));
        assertThrows(NullPointerException.class, () -> context.setTrackingImplementation(null));
        assertSame(now, context.getTimeProvider());
        assertSame(tracking, context.getTrackingImplementation());
    }

    @Test
    void time006_contextCanBeInitializedThenDependenciesReplaced() {
        var created = new ApplicationContext();
        assertThrows(IllegalStateException.class, created::getTimeProvider);
        assertThrows(IllegalStateException.class, created::getTrackingImplementation);
        created.setTimeProvider(now);
        var initial = new InMemoryTracking(created, directory.resolve("history.csv"));
        created.setTrackingImplementation(initial);
        initial.start(first);
        Instant replacementTime = now.now().plusSeconds(30);
        created.setTimeProvider(() -> replacementTime);
        assertEquals(Duration.ofSeconds(30), initial.getElapsedTime(first));
        initial.stop(first);
        assertEquals(Duration.ofSeconds(30), initial.getElapsedTime(first));
        var replacement = new InMemoryTracking(created, directory.resolve("history.csv"));
        created.setTrackingImplementation(replacement);
        assertSame(replacement, created.getTrackingImplementation());
        assertEquals(Duration.ZERO, replacement.getElapsedTime(first));
    }

    @Test
    void time005_localProviderReturnsCurrentInstant() {
        Instant before = Instant.now();
        Instant actual = new LocalTimeProvider().now();
        Instant after = Instant.now();
        assertFalse(actual.isBefore(before));
        assertFalse(actual.isAfter(after));
    }
}
