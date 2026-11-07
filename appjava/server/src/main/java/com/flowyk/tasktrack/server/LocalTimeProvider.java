package com.flowyk.tasktrack.server;

import com.flowyk.tasktrack.api.TimeProvider;
import java.time.Instant;

public final class LocalTimeProvider implements TimeProvider {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
