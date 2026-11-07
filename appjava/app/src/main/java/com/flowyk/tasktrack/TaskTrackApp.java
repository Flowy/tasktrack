package com.flowyk.tasktrack;

import com.flowyk.tasktrack.server.InMemoryTracking;
import com.flowyk.tasktrack.server.LocalTimeProvider;
import com.flowyk.tasktrack.api.ApplicationContext;
import com.flowyk.tasktrack.client.MainWindow;
import javax.swing.SwingUtilities;

public final class TaskTrackApp {
    private TaskTrackApp() {
    }

    static void main() {
        ApplicationContext context = new ApplicationContext();
        context.setTimeProvider(new LocalTimeProvider());
        context.setTrackingImplementation(new InMemoryTracking(context));
        SwingUtilities.invokeLater(() -> MainWindow.createWindow(context).setVisible(true));
    }
}
