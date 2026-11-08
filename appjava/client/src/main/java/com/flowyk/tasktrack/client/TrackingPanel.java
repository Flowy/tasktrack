package com.flowyk.tasktrack.client;

import com.flowyk.tasktrack.api.TrackingApi;
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
    private final TrackingState state;
    private final JButton timerButton = new JButton();
    private final JComboBox<String> taskSelector;
    private boolean refreshing;

    public TrackingPanel(Layout layout, TrackingApi api) {
        super(new BorderLayout(layout.distance(Distance.TRACKING_GAP), layout.distance(Distance.VERTICAL_GAP)));
        this.layout = layout;
        this.state = new TrackingState(api);
        setBorder(new javax.swing.border.EmptyBorder(layout.insets(Spacing.TRACKING_PADDING)));
        setPreferredSize(layout.size(Size.TRACKING_PANEL));
        taskSelector = new JComboBox<>(state.getTasks().toArray(String[]::new));
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
        taskSelector.setSelectedItem(this.state.getSelectedTask());
        taskSelector.addActionListener(_ -> {
            if (!refreshing && taskSelector.getSelectedItem() != null) {
                this.state.selectTask((String) taskSelector.getSelectedItem());
            }
        });
        add(taskSelector, BorderLayout.CENTER);
        timerButton.setFont(layout.font(Typeface.TIMER_BUTTON));
        timerButton.setMargin(layout.insets(Spacing.TIMER_MARGIN));
        timerButton.setPreferredSize(layout.size(Size.TIMER_BUTTON));
        add(timerButton, BorderLayout.EAST);
        timerButton.addActionListener(_ -> {
            if (state.isRunning()) {
                this.state.stop();
            } else {
                this.state.start();
            }
            renderState();
        });
        renderState();
    }

    private void renderState() {
        timerButton.setEnabled(state.getSelectedTask() != null);
        timerButton.setText(layout.text(state.isRunning() ? Text.PAUSE_SYMBOL : Text.START_SYMBOL));
        String action = layout.text(state.isRunning() ? Text.PAUSE : Text.START_TIMER);
        timerButton.setToolTipText(action);
        timerButton.getAccessibleContext().setAccessibleName(action);
    }

    public void showTaskSettings(java.awt.Window owner) {
        javax.swing.JDialog dialog = new javax.swing.JDialog(owner, "Task settings",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(new TaskSettingsPanel(state, this::refreshTasks, dialog::dispose));
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private void refreshTasks() {
        refreshing = true;
        try {
            taskSelector.setModel(new javax.swing.DefaultComboBoxModel<>(state.getTasks().toArray(String[]::new)));
            taskSelector.setSelectedItem(state.getSelectedTask());
            taskSelector.setEnabled(state.getSelectedTask() != null);
        } finally {
            refreshing = false;
        }
        renderState();
    }
}
