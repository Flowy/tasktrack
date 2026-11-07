package com.flowyk.tasktrack.client;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
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

import static com.flowyk.tasktrack.client.Layout.*;

public final class MainWindow {
    private MainWindow() {
    }

    public static JFrame createWindow() {
        Layout layout = new Layout();
        JFrame window = new JFrame(layout.text(Text.WINDOW_TITLE));
        window.setUndecorated(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setAlwaysOnTop(true);
        JPanel root = new JPanel(new BorderLayout());
        ManipulationPanel controls = new ManipulationPanel(new WindowController(window));
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
        body.add(new TrackingPanel(layout), BorderLayout.CENTER);
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

    private static final class TrackingPanel extends JPanel {
        private final Layout layout;
        private final TrackingState state = new TrackingState();
        private final JButton timerButton = new JButton();

        private TrackingPanel(Layout layout) {
            super(new BorderLayout(layout.distance(Distance.TRACKING_GAP), layout.distance(Distance.VERTICAL_GAP)));
            this.layout = layout;
            setBorder(new javax.swing.border.EmptyBorder(layout.insets(Spacing.TRACKING_PADDING)));
            setPreferredSize(layout.size(Size.TRACKING_PANEL));
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
            taskSelector.setName(layout.text(Text.SELECTOR_NAME));
            taskSelector.setToolTipText(layout.text(Text.CHOOSE_TASK));
            taskSelector.getAccessibleContext().setAccessibleName(layout.text(Text.CHOOSE_TASK));
            taskSelector.setSelectedItem(state.getSelectedTask());
            taskSelector.addActionListener(_ -> state.selectTask((String) taskSelector.getSelectedItem()));
            add(taskSelector, BorderLayout.CENTER);
            timerButton.setFont(layout.font(Typeface.TIMER_BUTTON));
            timerButton.setMargin(layout.insets(Spacing.TIMER_MARGIN));
            timerButton.setPreferredSize(layout.size(Size.TIMER_BUTTON));
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
            timerButton.setText(layout.text(state.isRunning() ? Text.PAUSE_SYMBOL : Text.START_SYMBOL));
            String action = layout.text(state.isRunning() ? Text.PAUSE : Text.START_TIMER);
            timerButton.setToolTipText(action);
            timerButton.getAccessibleContext().setAccessibleName(action);
        }
    }
}
