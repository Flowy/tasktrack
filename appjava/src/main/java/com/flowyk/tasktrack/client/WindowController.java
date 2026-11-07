package com.flowyk.tasktrack.client;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.util.Objects;
import javax.swing.JFrame;

/** Window operations shared by the close button and drag area. */
public final class WindowController {
    private final JFrame window;
    private Point dragOrigin;
    private Point windowOrigin;

    public WindowController(JFrame window) {
        this.window = Objects.requireNonNull(window);
    }

    public void close() {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    public void startDrag(Point screenPosition) {
        dragOrigin = new Point(screenPosition);
        windowOrigin = window.getLocation();
    }

    public void dragTo(Point screenPosition) {
        if (dragOrigin != null) {
            window.setLocation(windowOrigin.x + screenPosition.x - dragOrigin.x,
                    windowOrigin.y + screenPosition.y - dragOrigin.y);
        }
    }

    public void endDrag() {
        dragOrigin = null;
        windowOrigin = null;
    }
}
