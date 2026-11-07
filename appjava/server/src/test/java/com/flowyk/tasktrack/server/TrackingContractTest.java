package com.flowyk.tasktrack.server;
import com.flowyk.tasktrack.api.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrackingContractTest {
    private static ApplicationContext context() {
        var context = new ApplicationContext();
        context.setTimeProvider(new LocalTimeProvider());
        context.setTrackingImplementation(new InMemoryTracking(context));
        return context;
    }
    @Test
    public void task001_task002_task003_addRemoveAndSnapshots() {
        TrackingApi api = context().getTrackingImplementation();
        TrackingApi other = context().getTrackingImplementation();
        var original = api.getTasks();
        api.addTask("New task");
        api.addTask("New task");
        var added = api.getTasks();
        assertEquals(original.size() + 1, added.size());
        assertEquals("New task", added.getLast());
        assertFalse(original.contains("New task"));
        assertEquals(original, other.getTasks());
        assertThrows(UnsupportedOperationException.class, () -> added.add("Injected"));
        api.start("New task");
        api.stop("New task");
        api.removeTask("New task");
        api.removeTask("New task");
        assertEquals(original, api.getTasks());
        assertTrue(added.contains("New task"));
        assertThrows(IllegalArgumentException.class, () -> api.start("New task"));
        assertThrows(IllegalArgumentException.class, () -> api.stop("New task"));
        for (String task : original) api.removeTask(task);
        assertTrue(api.getTasks().isEmpty());
        api.addTask("Restored");
        assertEquals(java.util.List.of("Restored"), api.getTasks());
    }

    @Test
    public void task002_invalidNamesDoNotMutateTasks() {
        TrackingApi api = context().getTrackingImplementation();
        var original = api.getTasks();
        for (String invalid : new String[] { null, "", " ", "\t\n" }) {
            assertThrows(IllegalArgumentException.class, () -> api.addTask(invalid));
            assertThrows(IllegalArgumentException.class, () -> api.removeTask(invalid));
            assertEquals(original, api.getTasks());
        }
        api.addTask(" Task ");
        api.addTask(" task ");
        assertEquals(original.size() + 2, api.getTasks().size());
        api.start(" Task ");
        api.start(" task ");
    }

    @Test
    public void api001_api002_commandContract() {
        TrackingApi api = context().getTrackingImplementation();
        assertFalse(api.getTasks().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> api.getTasks().clear());
        for (String task : api.getTasks()) {
            api.stop(task);
            api.start(task);
            api.start(task);
            assertThrows(IllegalArgumentException.class, () -> api.start(null));
            assertThrows(IllegalArgumentException.class, () -> api.stop("Unknown task"));
            api.stop(task);
            api.stop(task);
            assertThrows(IllegalArgumentException.class, () -> api.stop(null));
            assertThrows(IllegalArgumentException.class, () -> api.start("Unknown task"));
        }
        api.start(api.getTasks().getFirst());

    }
}
