package com.flowyk.tasktrack.client;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class ManipulationPanel extends JPanel {
    public ManipulationPanel(WindowController controller) {
        this(controller, () -> {});
    }

    public ManipulationPanel(WindowController controller, Runnable openTaskSettings) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(280, 24));
        setName("windowControls");
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        JButton close = new JButton("×");
        close.setToolTipText("Close app");
        close.setCursor(Cursor.getDefaultCursor());
        close.getAccessibleContext().setAccessibleName("Close app");
        close.setMargin(new Insets(0, 4, 0, 4));
        close.setPreferredSize(new Dimension(24, 24));
        close.addActionListener(_ -> controller.close());
        add(close, BorderLayout.EAST);
        JButton menu = new JButton("\u2630");
        menu.setName("windowMenu");
        menu.setToolTipText("Menu");
        menu.getAccessibleContext().setAccessibleName("Menu");
        menu.setCursor(Cursor.getDefaultCursor());
        menu.setMargin(new Insets(0, 4, 0, 4));
        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem settings = new javax.swing.JMenuItem("Task settings\u2026");
        settings.addActionListener(_ -> openTaskSettings.run());
        popup.add(settings);
        menu.addActionListener(_ -> popup.show(menu, 0, menu.getHeight()));
        add(menu, BorderLayout.WEST);
        MouseAdapter drag = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    controller.startDrag(event.getLocationOnScreen());
                }
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                controller.dragTo(event.getLocationOnScreen());
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                controller.endDrag();
            }
        };
        addMouseListener(drag);
        addMouseMotionListener(drag);
        setVisible(false);
    }

}
