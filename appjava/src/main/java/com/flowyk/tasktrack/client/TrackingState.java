package com.flowyk.tasktrack.client;

/** In-memory tracking state, initially stopped. */
public final class TrackingState {
    private boolean running;

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
