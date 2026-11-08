package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.ApplicationContext;
import java.awt.BorderLayout;
import java.awt.Point;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import static com.flowyk.tasktrack.client.Layout.*;

public final class MainWindow {
    private MainWindow() {
    }

    public static JFrame createWindow(ApplicationContext context) {
        Layout layout = new Layout();
        JFrame window = new JFrame(layout.text(Text.WINDOW_TITLE));
        window.setUndecorated(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setAlwaysOnTop(true);
        JPanel root = new JPanel(new BorderLayout());
        TrackingPanel tracking = new TrackingPanel(layout, context.getTrackingImplementation());
        ManipulationPanel controls = new ManipulationPanel(new WindowController(window),
                () -> tracking.showTaskSettings(window));
        JButton toggle = new JButton();
        toggle.setName(layout.text(Text.TOGGLE_NAME));
        toggle.setPreferredSize(layout.size(Size.TOGGLE));
        toggle.setMargin(layout.insets(Spacing.TOGGLE_MARGIN));
        updateToggleAction(layout, toggle, false);
        toggle.addActionListener(_ -> {
            int oldHeight = window.getHeight();
            Point location = window.getLocation();
            controls.setVisible(!controls.isVisible());
            updateToggleAction(layout, toggle, controls.isVisible());
            window.pack();
            window.setLocation(location.x, location.y + oldHeight - window.getHeight());
        });
        JPanel body = new JPanel(new BorderLayout());
        body.add(toggle, BorderLayout.WEST);
        body.add(tracking, BorderLayout.CENTER);
        root.add(controls, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        window.setContentPane(root);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        return window;
    }

    private static void updateToggleAction(Layout layout, JButton toggle, boolean visible) {
        String action = layout.text(visible ? Text.HIDE_CONTROLS : Text.SHOW_CONTROLS);
        toggle.setText(layout.text(visible ? Text.HIDE_SYMBOL : Text.SHOW_SYMBOL));
        toggle.setToolTipText(action);
        toggle.getAccessibleContext().setAccessibleName(action);
    }
}
