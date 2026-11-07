package com.flowyk.tasktrack.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
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
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboBoxUI;

public final class TaskTrackWindow {
    private TaskTrackWindow() {
    }

    public static JFrame createWindow() {
        JFrame window = new JFrame("TaskTrack");
        window.setUndecorated(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setAlwaysOnTop(true);
        window.setContentPane(new WindowPanel(window));
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        return window;
    }

    private static final class WindowPanel extends JPanel {
        private final JPanel controls = new JPanel(new BorderLayout());
        private final JFrame window;
        private Point dragOrigin;
        private Point windowOrigin;

        private WindowPanel(JFrame window) {
            super(new BorderLayout());
            this.window = window;
            controls.setPreferredSize(new Dimension(280, 24));
            controls.setName("windowControls");
            controls.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            JButton close = new JButton("×");
            close.setToolTipText("Close app");
            close.setCursor(Cursor.getDefaultCursor());
            close.getAccessibleContext().setAccessibleName("Close app");
            close.setMargin(new Insets(0, 4, 0, 4));
            close.setPreferredSize(new Dimension(24, 24));
            close.addActionListener(_ -> window.dispatchEvent(
                    new WindowEvent(window, WindowEvent.WINDOW_CLOSING)));
            controls.add(close, BorderLayout.EAST);
            add(controls, BorderLayout.NORTH);
            add(new TrackingPanel(), BorderLayout.CENTER);

            MouseAdapter drag = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    if (SwingUtilities.isLeftMouseButton(event)) {
                        dragOrigin = event.getLocationOnScreen();
                        windowOrigin = window.getLocation();
                    }
                }

                @Override
                public void mouseDragged(MouseEvent event) {
                    if (dragOrigin != null) {
                        window.setLocation(windowOrigin.x + event.getXOnScreen() - dragOrigin.x,
                                windowOrigin.y + event.getYOnScreen() - dragOrigin.y);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent event) {
                    dragOrigin = null;
                    updateHover(event);
                }
            };
            controls.addMouseListener(drag);
            controls.addMouseMotionListener(drag);
            installHover(this, new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    setControlsVisible(true);
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    updateHover(event);
                }
            });
            controls.setVisible(false);
        }

        private void updateHover(MouseEvent event) {
            Point point = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), this);
            setControlsVisible(dragOrigin != null || contains(point));
        }

        private void setControlsVisible(boolean visible) {
            if (controls.isVisible() == visible) return;
            int oldHeight = window.getHeight();
            Point location = window.getLocation();
            controls.setVisible(visible);
            window.pack();
            window.setLocation(location.x, location.y + oldHeight - window.getHeight());
        }

        private static void installHover(Component component, MouseAdapter listener) {
            component.addMouseListener(listener);
            if (component instanceof Container container) {
                for (Component child : container.getComponents()) {
                    installHover(child, listener);
                }
            }
        }
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
