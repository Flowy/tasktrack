package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.ApplicationContext;
import com.flowyk.tasktrack.api.TrackingApi;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrackingApiTest {
    @Test
    public void api002_clientOwnsRunningStateAndPreservesItOnFailure() {
        class Commands implements TrackingApi {
            boolean fail;
            public List<String> getTasks() { return List.of("Task"); }
            public void start(String task) { if (fail) throw new IllegalStateException(); }
            public void stop(String task) { if (fail) throw new IllegalStateException(); }
        }
        Commands api = new Commands();
        TrackingState state = new TrackingState(api);
        assertFalse(state.isRunning());
        api.fail = true;
        assertThrows(IllegalStateException.class, state::start);
        assertFalse(state.isRunning());
        api.fail = false;
        state.start();
        assertTrue(state.isRunning());
        assertFalse(new TrackingState(api).isRunning());
        api.fail = true;
        assertThrows(IllegalStateException.class, state::stop);
        assertTrue(state.isRunning());
        api.fail = false;
        state.stop();
        assertFalse(state.isRunning());
    }



    @Test
    public void api003_clientSelectionIsSentOnlyWithCommands() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            class AlternateApi implements TrackingApi {
                final List<String> commands = new ArrayList<>();
                public List<String> getTasks() { return List.of("First", "Second"); }
                public void start(String task) { commands.add("start:" + task); }
                public void stop(String task) { commands.add("stop:" + task); }
            }
            AlternateApi api = new AlternateApi();
            ApplicationContext context = new ApplicationContext(api);
            assertSame(api, context.getTrackingImplementation());
            JFrame window = MainWindow.createWindow(context);
            try {
                JPanel body = (JPanel) ((BorderLayout) window.getContentPane().getLayout())
                        .getLayoutComponent(BorderLayout.CENTER);
                TrackingPanel panel = (TrackingPanel) ((BorderLayout) body.getLayout())
                        .getLayoutComponent(BorderLayout.CENTER);
                JComboBox<?> selector = (JComboBox<?>) panel.getComponent(0);
                JButton button = (JButton) panel.getComponent(1);
                assertEquals(2, selector.getItemCount());
                assertEquals("First", selector.getSelectedItem());
                selector.setSelectedIndex(1);
                assertTrue(api.commands.isEmpty());
                button.doClick();
                assertEquals(List.of("start:Second"), api.commands);
                assertEquals("Pause", button.getToolTipText());
                selector.setSelectedIndex(0);
                assertEquals(1, api.commands.size());
                assertEquals("Pause", button.getToolTipText());
                button.doClick();
                assertEquals(List.of("start:Second", "stop:First"), api.commands);
                assertEquals("Start timer", button.getToolTipText());
                button.doClick();
                assertEquals(List.of("start:Second", "stop:First", "start:First"), api.commands);
                assertEquals("Pause", button.getToolTipText());
                } finally {
                window.dispose();
            }
        });
    }
}
