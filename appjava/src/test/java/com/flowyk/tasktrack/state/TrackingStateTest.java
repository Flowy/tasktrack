package com.flowyk.tasktrack.state;

import org.junit.Test;

import static org.junit.Assert.*;

public class TrackingStateTest {
    @Test
    public void fr008_initiallyStoppedAndStartsAndPausesRepeatedly() {
        TrackingState state = new TrackingState();
        assertFalse(state.isRunning());
        for (int cycle = 0; cycle < 5; cycle++) {
            state.start();
            state.start();
            assertTrue(state.isRunning());
            state.pause();
            state.pause();
            assertFalse(state.isRunning());
        }
    }

    @Test
    public void fr009_newStateStartsStoppedIndependently() {
        TrackingState first = new TrackingState();
        first.start();
        TrackingState second = new TrackingState();
        assertFalse(second.isRunning());
        assertTrue(first.isRunning());
    }
}
