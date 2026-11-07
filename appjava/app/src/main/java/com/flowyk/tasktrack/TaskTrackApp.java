package com.flowyk.tasktrack;

import com.flowyk.tasktrack.server.InMemoryTracking;
import com.flowyk.tasktrack.api.ApplicationContext;
import com.flowyk.tasktrack.client.MainWindow;
import javax.swing.SwingUtilities;

public final class TaskTrackApp {
    private TaskTrackApp() {
    }

    static void main() {
        ApplicationContext context = new ApplicationContext(new InMemoryTracking());
        SwingUtilities.invokeLater(() -> MainWindow.createWindow(context).setVisible(true));
    }
}
