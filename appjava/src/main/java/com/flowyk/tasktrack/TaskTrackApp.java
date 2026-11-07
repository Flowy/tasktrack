package com.flowyk.tasktrack;

import com.flowyk.tasktrack.client.MainWindow;
import javax.swing.SwingUtilities;

public final class TaskTrackApp {
    private TaskTrackApp() {
    }

    static void main() {
        SwingUtilities.invokeLater(() -> MainWindow.createWindow().setVisible(true));
    }
}
