package com.flowyk.tasktrack.client;

import java.awt.BorderLayout;
import java.time.Duration;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ElapsedDisplayTest {
    @Test void display002_schedulesFiveMinuteBoundaries() {
        assertEquals(300000, TrackingPanel.nextRefreshDelay(Duration.ZERO));
        assertEquals(296000, TrackingPanel.nextRefreshDelay(Duration.ofSeconds(4)));
        assertEquals(245000, TrackingPanel.nextRefreshDelay(Duration.ofSeconds(55)));
        assertEquals(240000, TrackingPanel.nextRefreshDelay(Duration.ofMinutes(1)));
        assertEquals(300000, TrackingPanel.nextRefreshDelay(Duration.ofMinutes(5)));
        assertEquals(1000, TrackingPanel.nextRefreshDelay(Duration.ofSeconds(3599)));
        assertEquals(300000, TrackingPanel.nextRefreshDelay(Duration.ofHours(1)));
        assertEquals(177500, TrackingPanel.nextRefreshDelay(Duration.ofSeconds(3722, 500000000)));
    }

    @Test void display001_formatsTotalWholeMinutes() {
        assertEquals("0m", TrackingPanel.formatElapsedTime(Duration.ZERO));
        assertEquals("0m", TrackingPanel.formatElapsedTime(Duration.ofSeconds(5)));
        assertEquals("0m", TrackingPanel.formatElapsedTime(Duration.ofSeconds(59, 999999999)));
        assertEquals("1m", TrackingPanel.formatElapsedTime(Duration.ofSeconds(60)));
        assertEquals("12m", TrackingPanel.formatElapsedTime(Duration.ofSeconds(779)));
        assertEquals("59m", TrackingPanel.formatElapsedTime(Duration.ofSeconds(3599)));
        assertEquals("60m", TrackingPanel.formatElapsedTime(Duration.ofSeconds(3600)));
        assertEquals("62m", TrackingPanel.formatElapsedTime(Duration.ofSeconds(3723, 999)));
        assertEquals("6000m", TrackingPanel.formatElapsedTime(Duration.ofHours(100)));
        assertEquals("0m", TrackingPanel.formatElapsedTime(Duration.ofSeconds(-1)));
    }

    @Test void display001To003_refreshSelectionCommandsEmptyAndLifecycle() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            class TimedTasks extends TaskSettingsTest.Tasks {
                Duration elapsed = Duration.ofSeconds(65);
                public Duration getElapsedTime(String task) {
                    assertTrue(tasks.contains(task));
                    return task.equals("First") ? elapsed : Duration.ofHours(2);
                }
            }
            TimedTasks api = new TimedTasks();
            TrackingPanel panel = new TrackingPanel(new Layout(), api);
            JButton display = (JButton) ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.EAST);
            JComboBox<?> selector = (JComboBox<?>) panel.getComponent(0);
            assertEquals("1m", display.getText());
            selector.setSelectedIndex(1);
            assertEquals("120m", display.getText());
            selector.setSelectedIndex(0);
            display.doClick(0);
            assertEquals("Pause", display.getToolTipText());
            try {
                var field = TrackingPanel.class.getDeclaredField("refreshTimer");
                field.setAccessible(true);
                Timer refresh = (Timer) field.get(panel);
                assertFalse(refresh.isRunning());
                panel.addNotify();
                try {
                    assertTrue(refresh.isRunning());
                    api.elapsed = Duration.ofSeconds(120);
                    for (var listener : refresh.getActionListeners()) listener.actionPerformed(null);
                    assertEquals("2m", display.getText());
                    display.doClick(0);
                    assertEquals("Start timer", display.getToolTipText());
                } finally {
                    panel.removeNotify();
                }
                assertFalse(refresh.isRunning());
            } catch (ReflectiveOperationException exception) {
                throw new AssertionError(exception);
            }
            api.tasks.clear();
            TrackingPanel empty = new TrackingPanel(new Layout(), api);
            JButton emptyDisplay = (JButton) empty.getComponent(1);
            assertEquals("0m", emptyDisplay.getText());
            assertFalse(emptyDisplay.isEnabled());
        });
    }
}
