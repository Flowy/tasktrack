package com.flowyk.tasktrack.api;

import java.util.List;

public interface TrackingApi {

    List<String> getTasks();

    /** Returns whether any task is currently being timed. */
    boolean isTracking();

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

    /** Returns accumulated time, including the current running interval.
     * @throws IllegalArgumentException if task is null, blank or unknown
     */
    java.time.Duration getElapsedTime(String task);
}
