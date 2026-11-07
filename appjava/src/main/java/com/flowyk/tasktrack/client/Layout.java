package com.flowyk.tasktrack.client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

public final class Layout {
    public enum Text {
        WINDOW_TITLE, TOGGLE_NAME, SHOW_CONTROLS, HIDE_CONTROLS,
        SHOW_SYMBOL, HIDE_SYMBOL, SELECTOR_NAME, CHOOSE_TASK,
        START_SYMBOL, PAUSE_SYMBOL, START_TIMER, PAUSE
    }

    public enum Distance { TRACKING_GAP, VERTICAL_GAP }
    public enum Size { TOGGLE, TRACKING_PANEL, TIMER_BUTTON }
    public enum Spacing { TOGGLE_MARGIN, TRACKING_PADDING, TIMER_MARGIN }
    public enum Typeface { TIMER_BUTTON }

    public Layout() {
    }

    public String text(Text handle) {
        return switch (handle) {
            case WINDOW_TITLE -> "TaskTrack";
            case TOGGLE_NAME -> "toggleWindowControls";
            case SHOW_CONTROLS -> "Show window controls";
            case HIDE_CONTROLS -> "Hide window controls";
            case SHOW_SYMBOL -> "\u21C8";
            case HIDE_SYMBOL -> "\u21CA";
            case SELECTOR_NAME -> "taskSelector";
            case CHOOSE_TASK -> "Choose task";
            case START_SYMBOL -> "\u25B6";
            case PAUSE_SYMBOL -> "\u23F8";
            case START_TIMER -> "Start timer";
            case PAUSE -> "Pause";
        };
    }

    public int distance(Distance handle) {
        return switch (handle) {
            case TRACKING_GAP -> 8;
            case VERTICAL_GAP -> 0;
        };
    }

    public Dimension size(Size handle) {
        return switch (handle) {
            case TOGGLE -> new Dimension(24, 24);
            case TRACKING_PANEL -> new Dimension(280, 48);
            case TIMER_BUTTON -> new Dimension(40, 32);
        };
    }

    public Insets insets(Spacing handle) {
        return switch (handle) {
            case TOGGLE_MARGIN -> new Insets(0, 0, 0, 0);
            case TRACKING_PADDING -> new Insets(8, 8, 8, 8);
            case TIMER_MARGIN -> new Insets(2, 4, 2, 4);
        };
    }

    public Font font(Typeface handle) {
        return switch (handle) {
            case TIMER_BUTTON -> new Font("Segoe UI Symbol", Font.PLAIN, 16);
        };
    }
}
