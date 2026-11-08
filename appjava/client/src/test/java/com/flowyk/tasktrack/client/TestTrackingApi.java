package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.TrackingApi;
import com.flowyk.tasktrack.api.ApplicationContext;
import java.util.List;

/** API-only fixture: client tests must not depend on the server (MOD-003). */
final class TestTrackingApi implements TrackingApi {
    public boolean isTracking() { return false; }
    public java.time.Duration getElapsedTime(String task) { return java.time.Duration.ZERO; }
    static ApplicationContext context() {
        var context = new ApplicationContext();
        context.setTimeProvider(() -> java.time.Instant.EPOCH);
        context.setTrackingImplementation(new TestTrackingApi());
        return context;
    }
    public List<String> getTasks() { return List.of("First", "Second", "Third"); }
    public void start(String task) {}
    public void stop(String task) {}
    public void addTask(String task) { throw new UnsupportedOperationException(); }
    public void removeTask(String task) { throw new UnsupportedOperationException(); }
}
