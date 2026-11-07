package com.flowyk.tasktrack.server;
import com.flowyk.tasktrack.api.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrackingContractTest {
    @Test
    public void api001_api002_commandContract() {
        TrackingApi api = new ApplicationContext(new InMemoryTracking()).getTrackingImplementation();
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
