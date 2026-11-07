package com.flowyk.tasktrack;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/** Specification 001 checks; desktop stacking and focus still require manual verification. */
public class TaskTrackAppTest {
    private JFrame window;
    private JLabel status;
    private JButton button;

    @Before
    public void openWindow() throws Exception {
        assertFalse("These Swing tests require a graphical desktop", GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            window = TaskTrackApp.createWindow();
            for (Component component : window.getContentPane().getComponents()) {
                if (component instanceof JLabel) status = (JLabel) component;
                if (component instanceof JButton) button = (JButton) component;
            }
            assertNotNull("Visible status is required", status);
            assertNotNull("Start/Stop action is required", button);
        });
    }

    @After
    public void closeWindow() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (window != null) window.dispose();
        });
    }

    @Test
    public void fr001AndFr008_launchStopped() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertEquals("Stopped", status.getText());
            assertEquals("Start", button.getText());
            assertTrue(status.isVisible());
            assertTrue(button.isVisible());
        });
    }

    @Test
    public void fr002_windowRequestsAlwaysOnTop() throws Exception {
        SwingUtilities.invokeAndWait(() -> assertTrue(window.isAlwaysOnTop()));
    }

    @Test
    public void fr008_startAndStopRepeatedly() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            for (int cycle = 0; cycle < 5; cycle++) {
                button.doClick(0);
                assertEquals("Running", status.getText());
                assertEquals("Stop", button.getText());
                button.doClick(0);
                assertEquals("Stopped", status.getText());
                assertEquals("Start", button.getText());
            }
        });
    }

    @Test
    public void qr002_spaceHasAnActivationBinding() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Object binding = button.getInputMap(JComponent.WHEN_FOCUSED)
                    .get(KeyStroke.getKeyStroke("pressed SPACE"));
            assertNotNull(binding);
            assertNotNull(button.getActionMap().get(binding));
        });
    }

    @Test
    public void fr009_newWindowStartsStoppedAfterRunningWindowCloses() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            button.doClick(0);
            assertEquals("Running", status.getText());
            assertEquals(JFrame.EXIT_ON_CLOSE, window.getDefaultCloseOperation());
            window.dispose();
        });
        openWindow();
        SwingUtilities.invokeAndWait(() -> {
            assertEquals("Stopped", status.getText());
            assertEquals("Start", button.getText());
        });
    }
}
