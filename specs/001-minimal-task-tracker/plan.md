# 001 - Technical plan

Status: Planned

## Decisions

- Set the close button's cursor explicitly to the default arrow so it does not inherit the manipulation panel's move cursor (FR-010).

- Add a 24 pixel top panel above the existing 160-by-48 tracking area. Hide the entire panel initially and collapse its space. On visibility changes, pack and adjust the window's top edge by the height difference so the tracking area remains stationary. Show on mouse entry anywhere in the window; check exit coordinates against the whole content pane so child transitions do not hide it. Keep it visible through dragging. Use screen-coordinate deltas for left-button dragging and dispatch WINDOW_CLOSING from the accessible Unicode close button, preserving EXIT_ON_CLOSE. Verify with synthetic mouse events and a close-event listener; real desktop interaction remains a manual check.

- Use a 40-by-32 pixel Unicode ▶/⏸ button in a 160-by-48 panel with 8 pixel padding and right-aligned FlowLayout, leaving empty space on the left. Pack an undecorated JFrame around the panel. Use the Windows Segoe UI Symbol font for glyph coverage, and preserve keyboard focus, tooltips, and accessible action names. State commands remain start/pause; only presentation changes.

- Keep `TaskTrackApp` as the application starter and delegate window construction to `client.TaskTrackWindow`. The `.client` boundary owns Swing layout, window configuration, and presentation. Preserve FR-001, FR-002, FR-008, FR-009 and QR-002 behavior and the existing main class.
- Use Swing/AWT from the JDK: a small native desktop application without external dependencies.
- Use Java 25, as confirmed by the user and configured in IntelliJ. Set Maven compiler release to 25 and use JDK 25 for builds and verification.
- Use a JFrame with always-on-top enabled, no native decorations, and exit-on-close.
- Use a JPanel containing a single JButton. Keep the boolean running state in `TrackingState` with explicit, idempotent `start()` and `pause()` methods and no toggle method. The client chooses the action from `isRunning()`. Each new window owns a fresh holder; the panel renders the action on the Swing event dispatch thread. This change does not add elapsed-time measurement or display.
- No storage, preferences, network access, or shutdown persistence.
- Configure Maven compilation and an executable JAR with com.flowyk.tasktrack.TaskTrackApp as its main class.

## Delivery

1. Replace the earlier draft scope with the accepted Start/Stop window requirements.
2. Configure the Maven build and implement the compact window.
3. Compile with the available JDK; verify toggle transitions and window configuration with a temporary Swing smoke harness.
4. Document launch instructions and outstanding manual window stacking checks.

## Validation and risks

- Compile using javac locally; Maven is not available on the terminal PATH.
- Exercise initial state, repeated toggles, keyboard action wiring, and always-on-top configuration.
- Manually check stacking and focus while working in another desktop application; an always-on-top property assertion alone cannot prove OS behavior.
- Native title-bar moving, minimizing, and close controls are removed as requested. Windows Alt+F4 remains available to close the focused window. Minimized windows are not kept visible.

## Repeatable acceptance checks

Use JUnit 4 with Maven Surefire to run Swing checks on the event dispatch thread.
Tests require a graphical desktop and must fail with a clear message if headless.
Map tests to FR-001, FR-002 configuration, FR-008, and FR-009 fresh-window state.
Keep OS stacking/focus and process restart as manual acceptance checks.
