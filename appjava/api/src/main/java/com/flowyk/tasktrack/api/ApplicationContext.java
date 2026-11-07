package com.flowyk.tasktrack.api;

import java.util.Objects;

public final class ApplicationContext {
    private TrackingApi trackingApi;
    private TimeProvider timeProvider;

    public TrackingApi getTrackingImplementation() {
        if (trackingApi == null) {
            throw new IllegalStateException("Tracking implementation has not been configured");
        }
        return trackingApi;
    }

    public void setTrackingImplementation(TrackingApi trackingApi) {
        this.trackingApi = Objects.requireNonNull(trackingApi, "trackingApi");
    }

    public TimeProvider getTimeProvider() {
        if (timeProvider == null) {
            throw new IllegalStateException("Time provider has not been configured");
        }
        return timeProvider;
    }

    public void setTimeProvider(TimeProvider timeProvider) {
        this.timeProvider = Objects.requireNonNull(timeProvider, "timeProvider");
    }
}
