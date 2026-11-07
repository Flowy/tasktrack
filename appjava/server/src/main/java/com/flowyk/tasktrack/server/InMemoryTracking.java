package com.flowyk.tasktrack.server;

import com.flowyk.tasktrack.api.TrackingApi;
import java.util.List;
import java.util.ArrayList;

public final class InMemoryTracking implements TrackingApi {
    private List<String> tasks = List.of("Plan the day", "Implement task selection", "Review changes");

    @Override
    public List<String> getTasks() {
        return tasks;
    }

    @Override
    public void addTask(String task) {
        validateName(task);
        if (!tasks.contains(task)) {
            var updatedTasks = new ArrayList<>(tasks);
            updatedTasks.add(task);
            tasks = List.copyOf(updatedTasks);
        }
    }

    @Override
    public void removeTask(String task) {
        validateName(task);
        var updatedTasks = new ArrayList<>(tasks);
        if (updatedTasks.remove(task)) {
            tasks = List.copyOf(updatedTasks);
        }
    }

    private void validateName(String task) {
        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException("Task name must not be null or blank");
        }
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
