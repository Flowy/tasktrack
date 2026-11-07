package com.flowyk.tasktrack.api;

import java.time.Instant;

@FunctionalInterface
public interface TimeProvider {
    Instant now();
}
