package com.flowyk.tasktrack.api;

import java.util.List;

public interface TrackingApi {

    List<String> getTasks();

    void start(String task);

    void stop(String task);
}
