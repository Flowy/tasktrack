package com.flowyk.tasktrack.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class TaskTrackWindow {
    private TaskTrackWindow() {
    }

    public static JFrame createWindow() {
        JFrame window = new JFrame("TaskTrack");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setAlwaysOnTop(true);
        window.setContentPane(new TrackingPanel());
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        return window;
    }

    private static final class TrackingPanel extends JPanel {
        private boolean running;
        private final JLabel status = new JLabel("Stopped", SwingConstants.CENTER);
        private final JButton toggle = new JButton("Start");

        private TrackingPanel() {
            super(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            setPreferredSize(new Dimension(240, 120));
            add(status, BorderLayout.CENTER);
            add(toggle, BorderLayout.SOUTH);
            toggle.addActionListener(event -> {
                running = !running;
                status.setText(running ? "Running" : "Stopped");
                toggle.setText(running ? "Stop" : "Start");
            });
        }
    }
}
