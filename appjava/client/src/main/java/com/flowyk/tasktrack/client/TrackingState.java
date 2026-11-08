package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.TrackingApi;
import java.util.List;
import java.util.Objects;

public class TrackingState {
    private final TrackingApi api;
    private String selectedTask;
    private boolean running;
    private String startedTask;

    public TrackingState(TrackingApi api) {
        this.api = Objects.requireNonNull(api, "api");
        selectedTask = api.getTasks().isEmpty() ? null : api.getTasks().getFirst();
        String active = api.getCurrentTask();
        if (active != null && api.getTasks().contains(active)) {
            selectedTask = active;
            startedTask = active;
            running = true;
        }
    }

    public List<String> getTasks() {
        return api.getTasks();
    }

    public String getSelectedTask() {
        return selectedTask;
    }

    public void selectTask(String task) {
        if (task == null || !getTasks().contains(task)) {
            throw new IllegalArgumentException("Select a task from the available tasks");
        }
        selectedTask = task;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isTracking() {
        return api.isTracking();
    }

    public java.time.Duration getElapsedTime() {
        return selectedTask == null ? java.time.Duration.ZERO : api.getElapsedTime(selectedTask);
    }

    public void start() {
        if (selectedTask == null) return;
        api.start(selectedTask);
        startedTask = selectedTask;
        running = true;
    }

    public void pause() {
        stop();
    }

    public void stop() {
        if (selectedTask == null) return;
        api.stop(selectedTask);
        running = false;
        startedTask = null;
    }

    public void addTask(String task) {
        api.addTask(task);
        reconcileSelection();
    }

    public void removeTask(String task) {
        api.removeTask(task);
        if (Objects.equals(task, startedTask) && !getTasks().contains(task)) {
            running = false;
            startedTask = null;
        }
        reconcileSelection();
    }

    private void reconcileSelection() {
        List<String> tasks = getTasks();
        if (selectedTask == null || !tasks.contains(selectedTask)) {
            selectedTask = tasks.isEmpty() ? null : tasks.getFirst();
        }
        if (tasks.isEmpty()) running = false;
    }
}
