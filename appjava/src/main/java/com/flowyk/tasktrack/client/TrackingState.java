package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.TrackingApi;
import java.util.List;
import java.util.Objects;

public class TrackingState {
    private final TrackingApi api;
    private String selectedTask;
    private boolean running;

    public TrackingState(TrackingApi api) {
        this.api = Objects.requireNonNull(api, "api");
        selectedTask = api.getTasks().getFirst();
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

    public void start() {
        api.start(selectedTask);
        running = true;
    }

    public void pause() {
        stop();
    }

    public void stop() {
        api.stop(selectedTask);
        running = false;
    }
}
