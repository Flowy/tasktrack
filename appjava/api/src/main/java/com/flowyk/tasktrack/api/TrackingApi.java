package com.flowyk.tasktrack.api;

import java.util.List;

public interface TrackingApi {

    List<String> getTasks();

    /** Adds a nonblank, case-sensitive task name; an existing name is unchanged.
     * @throws IllegalArgumentException if task is null or blank
     */
    void addTask(String task);

    /** Removes a task name; an absent name is unchanged.
     * @throws IllegalArgumentException if task is null or blank
     */
    void removeTask(String task);

    void start(String task);

    void stop(String task);
}
