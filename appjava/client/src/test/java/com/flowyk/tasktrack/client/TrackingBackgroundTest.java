package com.flowyk.tasktrack.client;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrackingBackgroundTest {
    @Test void color002And003_backgroundFollowsApiStateIndependentlyOfSelection() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            class ActiveTasks extends TaskSettingsTest.Tasks {
                String active;
                public boolean isTracking() { return active != null; }
                public void start(String task) { active = task; }
                public void stop(String task) { if (task.equals(active)) active = null; }
            }
            ActiveTasks api = new ActiveTasks();
            Layout layout = new Layout();
            TrackingPanel panel = new TrackingPanel(layout, api);
            var stopped = panel.getBackground();
            JComboBox<?> selector = (JComboBox<?>) panel.getComponent(0);
            JButton timer = (JButton) panel.getComponent(1);
            timer.doClick(0);
            assertEquals(layout.color(Layout.Palette.TRACKING_ACTIVE), panel.getBackground());
            assertNotEquals(stopped, panel.getBackground());
            selector.setSelectedIndex(1);
            assertEquals(layout.color(Layout.Palette.TRACKING_ACTIVE), panel.getBackground());
            timer.doClick(0); // Inactive stop leaves the server tracking First.
            assertEquals(layout.color(Layout.Palette.TRACKING_ACTIVE), panel.getBackground());
            selector.setSelectedIndex(0);
            timer.doClick(0);
            timer.doClick(0);
            assertEquals(stopped, panel.getBackground());
            assertEquals(stopped, selector.getBackground());
            api.active = "Second"; // Server activity is independent of the local running flag.
            selector.setSelectedIndex(1);
            assertEquals(layout.color(Layout.Palette.TRACKING_ACTIVE), panel.getBackground());
            assertFalse(timer.isOpaque());
            api.active = null;
            api.tasks.clear();
            TrackingPanel empty = new TrackingPanel(layout, api);
            assertEquals(stopped, empty.getBackground());
        });
    }
}
