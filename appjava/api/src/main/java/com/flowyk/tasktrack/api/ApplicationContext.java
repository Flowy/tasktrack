package com.flowyk.tasktrack.api;

import java.util.Objects;

public final class ApplicationContext {
    private final TrackingApi trackingApi;

    public ApplicationContext(TrackingApi trackingApi) {
        this.trackingApi = Objects.requireNonNull(trackingApi, "trackingApi");
    }

    public TrackingApi getTrackingImplementation() {
        return trackingApi;
    }
}
