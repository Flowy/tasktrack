package com.flowyk.tasktrack.client;

import java.util.List;

public class TrackingState {
    private final List<String> tasks = List.of("Plan the day", "Implement task selection", "Review changes");
    private String selectedTask = tasks.getFirst();
    private boolean running;

    public List<String> getTasks() {
        return tasks;
    }

    public String getSelectedTask() {
        return selectedTask;
    }

    public void selectTask(String task) {
        if (task == null || !tasks.contains(task)) {
            throw new IllegalArgumentException("Select a task from the available tasks");
        }
        selectedTask = task;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        running = true;
    }

    public void pause() {
        running = false;
    }
}
