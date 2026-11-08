package com.flowyk.tasktrack.server;

import com.flowyk.tasktrack.api.TrackingApi;
import com.flowyk.tasktrack.api.ApplicationContext;
import java.nio.file.Path;
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
    private final CsvTaskHistory history;
    private final JsonTaskList taskList;
    private final JsonActiveTask activeTask;
    private final Map<String, Duration> elapsed = new HashMap<>();
    private String currentTask;
    private Instant startedAt;

    public InMemoryTracking(ApplicationContext context) {
        this(context, CsvTaskHistory.defaultFile(), JsonTaskList.defaultFile(), JsonActiveTask.defaultFile());
    }

    InMemoryTracking(ApplicationContext context, Path historyFile) {
        this(context, historyFile, null);
    }

    InMemoryTracking(ApplicationContext context, Path historyFile, Path taskListFile) {
        this(context, historyFile, taskListFile, null);
    }

    InMemoryTracking(ApplicationContext context, Path historyFile, Path taskListFile, Path activeTaskFile) {
        this.context = Objects.requireNonNull(context, "context");
        this.history = new CsvTaskHistory(Objects.requireNonNull(historyFile, "historyFile"));
        this.taskList = taskListFile == null ? null : new JsonTaskList(taskListFile);
        if (taskList != null) {
            tasks = taskList.load(tasks);
        }
        activeTask = activeTaskFile == null ? null : new JsonActiveTask(activeTaskFile);
        var restored = activeTask == null ? null : activeTask.load();
        if (restored != null && tasks.contains(restored.task())) {
            currentTask = restored.task();
            startedAt = restored.startedAt();
        }
    }

    @Override
    public String getCurrentTask() { return currentTask; }

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
            if (taskList != null) taskList.save(updatedTasks);
            tasks = List.copyOf(updatedTasks);
        }
    }

    @Override
    public void removeTask(String task) {
        validateName(task);
        var updatedTasks = new ArrayList<>(tasks);
        if (updatedTasks.remove(task)) {
            if (taskList != null) taskList.save(updatedTasks);
            if (task.equals(currentTask)) {
                try {
                    saveActive(null, null);
                } catch (RuntimeException failure) {
                    if (taskList != null) {
                        try { taskList.save(tasks); } catch (RuntimeException rollback) { failure.addSuppressed(rollback); }
                    }
                    throw failure;
                }
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
        transitionActive(task, now, now);
        currentTask = task;
        startedAt = now;
    }

    @Override
    public void stop(String task) {
        validateTask(task);
        if (task.equals(currentTask)) {
            transitionActive(null, null, context.getTimeProvider().now());
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
            history.append(currentTask, startedAt, now);
            elapsed.merge(currentTask, Duration.between(startedAt, now), Duration::plus);
            currentTask = null;
            startedAt = null;
        }
    }

    private void saveActive(String task, Instant start) {
        if (activeTask != null) activeTask.save(task == null ? null : new JsonActiveTask.Active(task, start));
    }

    private void transitionActive(String next, Instant start, Instant now) {
        saveActive(next, start);
        try {
            finishCurrentTask(now);
        } catch (RuntimeException failure) {
            try { saveActive(currentTask, startedAt); }
            catch (RuntimeException rollback) { failure.addSuppressed(rollback); }
            throw failure;
        }
    }

    private void validateTask(String task) {
        if (task == null || !tasks.contains(task)) {
            throw new IllegalArgumentException("Choose a task from the available tasks");
        }
    }
}
