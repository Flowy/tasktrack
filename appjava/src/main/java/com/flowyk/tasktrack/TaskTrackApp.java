package com.flowyk.tasktrack;

import com.flowyk.tasktrack.client.TaskTrackWindow;
import javax.swing.SwingUtilities;

public final class TaskTrackApp {
    private TaskTrackApp() {
    }

    static void main() {
        SwingUtilities.invokeLater(() -> TaskTrackWindow.createWindow().setVisible(true));
    }
}
