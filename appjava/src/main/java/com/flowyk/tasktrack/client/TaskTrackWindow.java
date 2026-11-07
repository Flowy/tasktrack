package com.flowyk.tasktrack.client;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxUI;

public final class TaskTrackWindow {
    private TaskTrackWindow() {
    }

    public static JFrame createWindow() {
        JFrame window = new JFrame("TaskTrack");
        window.setUndecorated(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setAlwaysOnTop(true);
        JPanel root = new JPanel(new BorderLayout());
        ManipulationPanel controls = new ManipulationPanel(new WindowController(window));
        JButton toggle = new JButton();
        toggle.setName("toggleWindowControls");
        toggle.setPreferredSize(new Dimension(24, 24));
        toggle.setMargin(new Insets(0, 0, 0, 0));
        updateToggleAction(toggle, false);
        toggle.addActionListener(_ -> {
            int oldHeight = window.getHeight();
            Point location = window.getLocation();
            controls.setVisible(!controls.isVisible());
            updateToggleAction(toggle, controls.isVisible());
            window.pack();
            window.setLocation(location.x, location.y + oldHeight - window.getHeight());
        });
        JPanel body = new JPanel(new BorderLayout());
        body.add(toggle, BorderLayout.WEST);
        body.add(new TrackingPanel(), BorderLayout.CENTER);
        root.add(controls, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        window.setContentPane(root);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        return window;
    }

    private static void updateToggleAction(JButton toggle, boolean visible) {
        String action = visible ? "Hide window controls" : "Show window controls";
        toggle.setText(visible ? "\u21CA" : "\u21C8");
        toggle.setToolTipText(action);
        toggle.getAccessibleContext().setAccessibleName(action);
    }

    private static final class TrackingPanel extends JPanel {
        private final TrackingState state = new TrackingState();
        private final JButton timerButton = new JButton();

        private TrackingPanel() {
            super(new BorderLayout(8, 0));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            setPreferredSize(new Dimension(280, 48));
            JComboBox<String> taskSelector = new JComboBox<>(state.getTasks().toArray(String[]::new));
            taskSelector.setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    return null;
                }

                @Override
                public void paintCurrentValueBackground(Graphics graphics, Rectangle bounds, boolean hasFocus) {
                    // The task name shares the tracking panel's background.
                }

                @Override
                public void paintCurrentValue(Graphics graphics, Rectangle bounds, boolean hasFocus) {
                    super.paintCurrentValue(graphics, bounds, false);
                }

                @Override
                protected Rectangle rectangleForCurrentValue() {
                    Insets insets = comboBox.getInsets();
                    return new Rectangle(insets.left, insets.top,
                            comboBox.getWidth() - insets.left - insets.right,
                            comboBox.getHeight() - insets.top - insets.bottom);
                }
            });
            DefaultListCellRenderer taskRenderer = new DefaultListCellRenderer();
            taskSelector.setRenderer((list, value, index, selected, focused) -> {
                JComponent cell = (JComponent) taskRenderer.getListCellRendererComponent(
                        list, value, index, selected, focused);
                cell.setOpaque(index >= 0);
                return cell;
            });
            taskSelector.setOpaque(false);
            taskSelector.setBorder(BorderFactory.createEmptyBorder());
            taskSelector.setBackground(getBackground());
            taskSelector.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            taskSelector.setName("taskSelector");
            taskSelector.setToolTipText("Choose task");
            taskSelector.getAccessibleContext().setAccessibleName("Choose task");
            taskSelector.setSelectedItem(state.getSelectedTask());
            taskSelector.addActionListener(_ -> state.selectTask((String) taskSelector.getSelectedItem()));
            add(taskSelector, BorderLayout.CENTER);
            timerButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
            timerButton.setMargin(new Insets(2, 4, 2, 4));
            timerButton.setPreferredSize(new Dimension(40, 32));
            add(timerButton, BorderLayout.EAST);
            timerButton.addActionListener(_ -> {
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
            timerButton.setText(state.isRunning() ? "⏸" : "▶");
            String action = state.isRunning() ? "Pause" : "Start timer";
            timerButton.setToolTipText(action);
            timerButton.getAccessibleContext().setAccessibleName(action);
        }
    }
}
