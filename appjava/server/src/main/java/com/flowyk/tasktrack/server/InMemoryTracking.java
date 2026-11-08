package com.flowyk.tasktrack.server;

import com.flowyk.tasktrack.api.TrackingApi;
import com.flowyk.tasktrack.api.ApplicationContext;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class InMemoryTracking implements TrackingApi {
    private List<String> tasks = List.of("Plan the day", "Implement task selection", "Review changes");
    private final ApplicationContext context;
    private final Map<String, Duration> elapsed = new HashMap<>();
    private String currentTask;
    private Instant startedAt;

    public InMemoryTracking(ApplicationContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    public List<String> getTasks() {
        return tasks;
    }

    @Override
    public boolean isTracking() {
        return currentTask != null;
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
            if (task.equals(currentTask)) {
                currentTask = null;
                startedAt = null;
            }
            elapsed.remove(task);
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
        if (task.equals(currentTask)) {
            return;
        }
        Instant now = context.getTimeProvider().now();
        finishCurrentTask(now);
        currentTask = task;
        startedAt = now;
    }

    @Override
    public void stop(String task) {
        validateTask(task);
        if (task.equals(currentTask)) {
            finishCurrentTask(context.getTimeProvider().now());
        }
    }

    @Override
    public Duration getElapsedTime(String task) {
        validateTask(task);
        Duration total = elapsed.getOrDefault(task, Duration.ZERO);
        return task.equals(currentTask)
                ? total.plus(Duration.between(startedAt, context.getTimeProvider().now()))
                : total;
    }

    private void finishCurrentTask(Instant now) {
        if (currentTask != null) {
            elapsed.merge(currentTask, Duration.between(startedAt, now), Duration::plus);
            currentTask = null;
            startedAt = null;
        }
    }

    private void validateTask(String task) {
        if (task == null || !tasks.contains(task)) {
            throw new IllegalArgumentException("Choose a task from the available tasks");
        }
    }
}
