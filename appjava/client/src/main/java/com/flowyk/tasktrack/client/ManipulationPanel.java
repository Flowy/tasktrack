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
