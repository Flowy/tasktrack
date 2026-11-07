package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.TrackingApi;
import java.util.List;

public final class LocalTrackingApi implements TrackingApi {
    private final List<String> tasks = List.of("Plan the day", "Implement task selection", "Review changes");

    @Override
    public List<String> getTasks() {
        return tasks;
    }

    @Override
    public void start(String task) {
        validateTask(task);
    }

    @Override
    public void stop(String task) {
        validateTask(task);
    }

    private void validateTask(String task) {
        if (task == null || !tasks.contains(task)) {
            throw new IllegalArgumentException("Choose a task from the available tasks");
        }
    }
}
