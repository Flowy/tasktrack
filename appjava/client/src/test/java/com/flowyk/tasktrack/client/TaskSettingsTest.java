package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.TrackingApi;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskSettingsTest {
    static class Tasks implements TrackingApi {
        public boolean isTracking() { return false; }
        public java.time.Duration getElapsedTime(String task) { return java.time.Duration.ZERO; }
        final List<String> tasks = new ArrayList<>(List.of("First", "Second"));
        public List<String> getTasks() { return List.copyOf(tasks); }
        public void addTask(String task) {
            if (task == null || task.isBlank()) throw new IllegalArgumentException("Enter a task name");
            if (!tasks.contains(task)) tasks.add(task);
        }
        public void removeTask(String task) { tasks.remove(task); }
        public void start(String task) {}
        public void stop(String task) {}
    }

    static Component find(Component root, String text) {
        if (text.equals(root.getName()) || root instanceof JButton b && text.equals(b.getText())) return root;
        if (root instanceof Container c) for (Component child : c.getComponents()) {
            Component result = find(child, text);
            if (result != null) return result;
        }
        return null;
    }

    @Test void set003And004_reconcileRemovalAndRecoverFromEmpty() {
        Tasks api = new Tasks();
        TrackingState state = new TrackingState(api);
        state.start();
        state.selectTask("Second");
        state.removeTask("Second");
        assertEquals("First", state.getSelectedTask());
        assertTrue(state.isRunning());
        state.removeTask("First");
        assertNull(state.getSelectedTask());
        assertFalse(state.isRunning());
        state.start();
        state.addTask("New");
        assertEquals("New", state.getSelectedTask());
        assertFalse(state.isRunning());
        assertNull(new TrackingState(new Tasks() {{ tasks.clear(); }}).getSelectedTask());
    }

    @Test void set002_editorValidatesAddsAndRemoves() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            TrackingState state = new TrackingState(new Tasks());
            int[] changes = {0};
            boolean[] closed = {false};
            TaskSettingsPanel panel = new TaskSettingsPanel(state, () -> changes[0]++, () -> closed[0] = true);
            JTextField name = (JTextField) find(panel, "taskName");
            JButton add = (JButton) find(panel, "Add");
            JButton remove = (JButton) find(panel, "Remove");
            assertFalse(Objects.requireNonNull(remove).isEnabled());
            Objects.requireNonNull(name).setText(" ");
            Objects.requireNonNull(add).doClick();
            assertEquals(0, changes[0]);
            assertEquals(" ", name.getText());
            name.setText("New");
            add.doClick();
            assertEquals(List.of("First", "Second", "New"), state.getTasks());
            assertTrue(remove.isEnabled());
            remove.doClick();
            assertEquals(List.of("First", "Second"), state.getTasks());
            assertEquals(2, changes[0]);
            ((JButton) Objects.requireNonNull(find(panel, "Close"))).doClick();
            assertTrue(closed[0]);
        });
    }

    @Test void set001And003_menuOpensOwnedDialogAndEditsRefreshTracker() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            var context = new com.flowyk.tasktrack.api.ApplicationContext();
            context.setTrackingImplementation(new Tasks());
            JFrame window = MainWindow.createWindow(context);
            Throwable[] failure = {null};
            try {
                window.setVisible(true);
                ((JButton) Objects.requireNonNull(find(window, "toggleWindowControls"))).doClick();
                ((JButton) Objects.requireNonNull(find(window, "windowMenu"))).doClick();
                JPopupMenu popup = (JPopupMenu) javax.swing.MenuSelectionManager.defaultManager()
                        .getSelectedPath()[0];
                Timer timer = new Timer(150, _ -> {
                    JDialog dialog = null;
                    try {
                        for (Window owned : window.getOwnedWindows()) {
                            if (owned instanceof JDialog d && d.isVisible()) dialog = d;
                        }
                        assertNotNull(dialog);
                        assertEquals(window, dialog.getOwner());
                        assertEquals("Task settings", dialog.getTitle());
                        JList<?> list = (JList<?>) find(dialog, "settingsTasks");
                        JButton remove = (JButton) find(dialog, "Remove");
                        while (Objects.requireNonNull(list).getModel().getSize() > 0) {
                            list.setSelectedIndex(0);
                            Objects.requireNonNull(remove).doClick();
                        }
                        JComboBox<?> selector = (JComboBox<?>) find(window, "taskSelector");
                        assertEquals(0, Objects.requireNonNull(selector).getItemCount());
                        TrackingPanel tracker = (TrackingPanel) selector.getParent();
                        assertFalse(tracker.getComponent(1).isEnabled());
                        ((JTextField) Objects.requireNonNull(find(dialog, "taskName"))).setText("Recovered");
                        ((JButton) Objects.requireNonNull(find(dialog, "Add"))).doClick();
                        assertEquals("Recovered", selector.getSelectedItem());
                        assertTrue(tracker.getComponent(1).isEnabled());
                    } catch (Throwable ex) { failure[0] = ex; }
                    finally { if (dialog != null) dialog.dispose(); }
                });
                timer.setRepeats(false);
                timer.start();
                ((JMenuItem) popup.getComponent(0)).doClick();
                if (failure[0] != null) throw new AssertionError(failure[0]);
            } finally { window.dispose(); }
        });
    }
}
