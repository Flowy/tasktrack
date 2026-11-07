package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.state.TrackingState;
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
        private final TrackingState state = new TrackingState();
        private final JLabel status = new JLabel("Stopped", SwingConstants.CENTER);
        private final JButton timerButton = new JButton("Start timer");

        private TrackingPanel() {
            super(new BorderLayout(0, 12));
            setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            setPreferredSize(new Dimension(240, 120));
            add(status, BorderLayout.CENTER);
            add(timerButton, BorderLayout.SOUTH);
            timerButton.addActionListener(event -> {
                if (state.isRunning()) {
                    state.pause();
                } else {
                    state.start();
                }
                renderState();
            });
            renderState();
        }

        private void renderState() {
            status.setText(state.isRunning() ? "Running" : "Stopped");
            timerButton.setText(state.isRunning() ? "Pause" : "Start timer");
        }
    }
}
