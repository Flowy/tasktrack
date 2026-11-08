package com.flowyk.tasktrack.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

/** Immediate task edits shared with the tracking panel, through the API-backed state. */
final class TaskSettingsPanel extends JPanel {
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> tasks = new JList<>(model);
    private final JTextField name = new JTextField(20);
    private final JLabel error = new JLabel(" ");
    private final JButton remove = new JButton("Remove");

    TaskSettingsPanel(TrackingState state, Runnable onChanged, Runnable onClose) {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tasks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasks.getAccessibleContext().setAccessibleName("Tasks");
        tasks.setName("settingsTasks");
        JScrollPane scroll = new JScrollPane(tasks);
        scroll.setPreferredSize(new Dimension(320, 180));
        add(scroll, BorderLayout.CENTER);
        JPanel editor = new JPanel(new BorderLayout(8, 8));
        JLabel label = new JLabel("Task name");
        label.setLabelFor(name);
        editor.add(label, BorderLayout.WEST);
        name.setName("taskName");
        editor.add(name, BorderLayout.CENTER);
        JButton add = new JButton("Add");
        editor.add(add, BorderLayout.EAST);
        add(editor, BorderLayout.NORTH);
        JPanel footer = new JPanel(new BorderLayout());
        footer.add(error, BorderLayout.NORTH);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        actions.add(remove);
        actions.add(close);
        footer.add(actions, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);
        Runnable refresh = () -> {
            String selected = tasks.getSelectedValue();
            model.clear();
            model.addAll(state.getTasks());
            tasks.setSelectedValue(selected, true);
            remove.setEnabled(tasks.getSelectedValue() != null);
        };
        Runnable addTask = () -> {
            try {
                String task = name.getText();
                state.addTask(task);
                onChanged.run();
                refresh.run();
                tasks.setSelectedValue(task, true);
                name.setText("");
                error.setText(" ");
                name.requestFocusInWindow();
            } catch (IllegalArgumentException ex) {
                error.setText(ex.getMessage());
            }
        };
        add.addActionListener(_ -> addTask.run());
        name.addActionListener(_ -> addTask.run());
        tasks.addListSelectionListener(_ -> remove.setEnabled(tasks.getSelectedValue() != null));
        remove.addActionListener(_ -> {
            String selected = tasks.getSelectedValue();
            if (selected == null) return;
            state.removeTask(selected);
            onChanged.run();
            refresh.run();
            error.setText(" ");
        });
        close.addActionListener(_ -> onClose.run());
        refresh.run();
    }
}
