package com.flowyk.tasktrack.client;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxUI;

import static com.flowyk.tasktrack.client.Layout.*;

public final class TrackingPanel extends JPanel {
    private final Layout layout;
    private final TrackingState state = new TrackingState();
    private final JButton timerButton = new JButton();

    public TrackingPanel(Layout layout) {
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
