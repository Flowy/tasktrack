package com.flowyk.tasktrack.state;

import com.flowyk.tasktrack.client.TrackingState;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrackingStateTest {
    @Test
    public void fr011_selectionIsValidatedAndIndependentOfRunningState() {
        TrackingState state = new TrackingState();
        assertEquals(3, state.getTasks().size());
        assertEquals(state.getTasks().getFirst(), state.getSelectedTask());
        state.start();
        for (String task : state.getTasks()) {
            state.selectTask(task);
            assertEquals(task, state.getSelectedTask());
            assertTrue(state.isRunning());
        }
        String selected = state.getSelectedTask();
        assertThrows(IllegalArgumentException.class, () -> state.selectTask("Unknown task"));
        assertThrows(IllegalArgumentException.class, () -> state.selectTask(null));
        assertEquals(selected, state.getSelectedTask());
        assertThrows(UnsupportedOperationException.class, () -> state.getTasks().clear());
        state.pause();
        assertEquals(selected, state.getSelectedTask());
        TrackingState fresh = new TrackingState();
        assertEquals(fresh.getTasks().getFirst(), fresh.getSelectedTask());
    }

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
