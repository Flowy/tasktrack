package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.TrackingApi;
import java.util.List;

/** API-only fixture: client tests must not depend on the server (MOD-003). */
final class TestTrackingApi implements TrackingApi {
    public List<String> getTasks() { return List.of("First", "Second", "Third"); }
    public void start(String task) {}
    public void stop(String task) {}
    public void addTask(String task) { throw new UnsupportedOperationException(); }
    public void removeTask(String task) { throw new UnsupportedOperationException(); }
}
