package com.flowyk.tasktrack.client;
import com.flowyk.tasktrack.api.ApplicationContext;
import com.flowyk.tasktrack.server.InMemoryTracking;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.GraphicsEnvironment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/** Specification 001 checks; desktop stacking and focus still require manual verification. */
public class MainWindowTest {
    private JFrame window;
    private JButton button;

    @Before
    public void openWindow() throws Exception {
        assertFalse("These Swing tests require a graphical desktop", GraphicsEnvironment.isHeadless());
        SwingUtilities.invokeAndWait(() -> {
            window = MainWindow.createWindow(new ApplicationContext(new InMemoryTracking()));
            button = (JButton) find(window.getContentPane(), "Start timer");
            assertNotNull("Start timer/Pause action is required", button);
        });
    }

    private Component find(Component component, String name) {
        if (name.equals(component.getName()) || (component instanceof JButton candidate
                && name.equals(candidate.getAccessibleContext().getAccessibleName()))) return component;
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                Component found = find(child, name);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void assertAction(String symbol, String action) {
        assertEquals(symbol, button.getText());
        assertEquals(action, button.getToolTipText());
        assertEquals(action, button.getAccessibleContext().getAccessibleName());
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
            assertAction("▶", "Start timer");
            assertTrue(button.isVisible());
            assertEquals(2, button.getParent().getComponentCount());
            assertEquals(new Dimension(40, 32), button.getSize());
            assertTrue(window.isUndecorated());
            assertEquals(8, window.getContentPane().getWidth() - SwingUtilities.convertPoint(button, 0, 0, window.getContentPane()).x - button.getWidth());
            Component selector = find(window.getContentPane(), "taskSelector");
            assertNotNull(selector);
            assertTrue(selector.getWidth() > 0);
            assertTrue(selector.getX() + selector.getWidth() < button.getX());
            assertTrue(button.getFont().canDisplay('▶'));
            assertTrue(button.getFont().canDisplay('⏸'));
        });
    }

    @Test
    public void fr011AndFr012_selectTasksAlongsideStartPause() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JComboBox<?> selector = (JComboBox<?>) find(window.getContentPane(), "taskSelector");
            assertEquals(3, selector.getItemCount());
            assertEquals(0, selector.getSelectedIndex());
            assertEquals("Choose task", selector.getAccessibleContext().getAccessibleName());
            button.doClick(0);
            for (int index = 0; index < selector.getItemCount(); index++) {
                selector.setSelectedIndex(index);
                assertEquals(selector.getItemAt(index), selector.getSelectedItem());
                assertAction("⏸", "Pause");
            }
            button.doClick(0);
            assertEquals(2, selector.getSelectedIndex());
            window.dispose();
        });
        openWindow();
        SwingUtilities.invokeAndWait(() -> assertEquals(0,
                ((JComboBox<?>) find(window.getContentPane(), "taskSelector")).getSelectedIndex()));
    }

    @Test
    public void fr013_taskTextOpensPopupWithoutArrow() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JComboBox<?> selector = (JComboBox<?>) find(window.getContentPane(), "taskSelector");
            for (Component child : selector.getComponents()) {
                assertFalse("Task selector has no arrow button", child instanceof JButton);
            }
            assertEquals(new java.awt.Insets(0, 0, 0, 0), selector.getInsets());
            window.setVisible(true);
            selector.dispatchEvent(new MouseEvent(selector, MouseEvent.MOUSE_PRESSED,
                    System.currentTimeMillis(), MouseEvent.BUTTON1_DOWN_MASK,
                    12, selector.getHeight() / 2, 1, false, MouseEvent.BUTTON1));
            assertTrue("Clicking the task name opens its popup", selector.isPopupVisible());
            selector.setPopupVisible(false);
        });
    }

    @Test
    public void fr010_toggleDragAndClose() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            Component root = window.getContentPane();
            Component controls = find(root, "windowControls");
            assertNotNull(controls);
            assertEquals(java.awt.Cursor.MOVE_CURSOR, controls.getCursor().getType());
            assertEquals(java.awt.Cursor.DEFAULT_CURSOR, find(root, "Close app").getCursor().getType());
            assertFalse(controls.isVisible());
            assertEquals(48, window.getHeight());
            JButton toggle = (JButton) find(root, "toggleWindowControls");
            assertTrue(toggle.isVisible());
            assertEquals("⇈", toggle.getText());
            assertEquals("Show window controls", toggle.getToolTipText());
            assertNotNull(toggle.getInputMap(JComponent.WHEN_FOCUSED).get(KeyStroke.getKeyStroke("pressed SPACE")));
            Point initialLocation = window.getLocation();
            root.dispatchEvent(new MouseEvent(root, MouseEvent.MOUSE_ENTERED, 0, 0, 10, 40, 0, false));
            assertFalse("Hover does not reveal controls", controls.isVisible());
            toggle.doClick(0);
            assertEquals("⇊", toggle.getText());
            assertEquals("Hide window controls", toggle.getAccessibleContext().getAccessibleName());
            assertTrue(controls.isVisible());
            assertEquals(72, window.getHeight());
            assertEquals(initialLocation.y - 24, window.getY());
            assertEquals(initialLocation.y + 8, window.getY() + SwingUtilities.convertPoint(button, 0, 0, root).y);
            root.dispatchEvent(new MouseEvent(root, MouseEvent.MOUSE_EXITED, 0, 0, 120, 40, 0, false));
            assertTrue("Moving to a child stays inside", controls.isVisible());
            button.dispatchEvent(new MouseEvent(button, MouseEvent.MOUSE_ENTERED, 0, 0, 2, 2, 0, false));
            assertTrue(controls.isVisible());
            Point before = window.getLocation();
            controls.dispatchEvent(new MouseEvent(controls, MouseEvent.MOUSE_PRESSED, 0, 0,
                    5, 5, 100, 100, 1, false, MouseEvent.BUTTON1));
            controls.dispatchEvent(new MouseEvent(controls, MouseEvent.MOUSE_EXITED, 0, 0, -10, -10, 0, false));
            assertTrue("Keep controls visible while dragging", controls.isVisible());
            controls.dispatchEvent(new MouseEvent(controls, MouseEvent.MOUSE_DRAGGED, 0,
                    MouseEvent.BUTTON1_DOWN_MASK, 35, 25, 130, 120, 0, false, MouseEvent.NOBUTTON));
            assertEquals(new Point(before.x + 30, before.y + 20), window.getLocation());
            controls.dispatchEvent(new MouseEvent(controls, MouseEvent.MOUSE_RELEASED, 0, 0,
                    5, 5, 130, 120, 1, false, MouseEvent.BUTTON1));
            root.dispatchEvent(new MouseEvent(root, MouseEvent.MOUSE_EXITED, 0, 0, -1, -1, 0, false));
            assertTrue("Pointer exit leaves controls open", controls.isVisible());
            toggle.doClick(0);
            assertFalse(controls.isVisible());
            assertEquals("⇈", toggle.getText());
            assertEquals("Show window controls", toggle.getAccessibleContext().getAccessibleName());
            assertEquals(48, window.getHeight());
            assertEquals(new Point(initialLocation.x + 30, initialLocation.y + 20), window.getLocation());
            root.dispatchEvent(new MouseEvent(root, MouseEvent.MOUSE_ENTERED, 0, 0, 5, 5, 0, false));
            assertFalse(controls.isVisible());
            toggle.doClick(0);
            assertTrue(controls.isVisible());
            AtomicBoolean closing = new AtomicBoolean();
            window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    closing.set(true);
                }
            });
            ((JButton) find(root, "Close app")).doClick(0);
            assertTrue("Close button uses normal app closing path", closing.get());
        });
    }

    @Test
    public void fr002_windowRequestsAlwaysOnTop() throws Exception {
        SwingUtilities.invokeAndWait(() -> assertTrue(window.isAlwaysOnTop()));
    }

    @Test
    public void fr008_startAndPauseRepeatedly() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            for (int cycle = 0; cycle < 5; cycle++) {
                button.doClick(0);
                assertAction("⏸", "Pause");
                button.doClick(0);
                assertAction("▶", "Start timer");
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
            assertEquals(JFrame.EXIT_ON_CLOSE, window.getDefaultCloseOperation());
            window.dispose();
        });
        openWindow();
        SwingUtilities.invokeAndWait(() -> assertAction("▶", "Start timer"));
    }
}
