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

Use JUnit Jupiter with Maven Surefire to run Swing checks on the event dispatch thread.
Tests require a graphical desktop and must fail with a clear message if headless.
Map tests to FR-001, FR-002 configuration, FR-008, and FR-009 fresh-window state.
Keep OS stacking/focus and process restart as manual acceptance checks.

## Task selection

Use an immutable List<String> of three distinct sample task titles in TrackingState and explicit getTasks/getSelectedTask/selectTask methods. Reject values outside the fixed list. Start/Pause and selection are independent in-memory state. Bind a noneditable Swing JComboBox to this state, with an accessible name and tooltip. Use BorderLayout with an 8 pixel gap and a 280-by-48 tracking area to fit the selector and existing 40-by-32 action. Keep hover controls full width. Verify FR-011/FR-012 state invariants and Swing transitions, plus existing regressions.

For FR-013, retain JComboBox state, popup, and keyboard behavior using a BasicComboBoxUI delegate without an arrow button. Remove the selector border, use the full component bounds for the current value, and use a hand cursor to indicate clickability. Verify the existing suite and popup activation from a click on the selected text.

Render the closed task value transparently and suppress the combo delegate's background/focus color painting; retain the standard list renderer for popup rows (FR-013).

## Manipulation feature separation

Extract a public final ManipulationPanel in the client package. It wraps supplied tracking content and owns the controls, drag state, recursive hover listeners, and window resizing/positioning. TaskTrackWindow composes it with TrackingPanel as the content pane. Keep existing component hierarchy and names so FR-010 regression checks exercise the extracted feature unchanged.

## Button-controlled manipulation panel

Confirmed request replaces hover with a button. Default: always-visible 24 pixel toggle left of supplied content, initially collapsed. Remove recursive hover listeners; retain drag/close and stationary-content pack adjustment. Update accessible action names and verify button toggling, pointer independence, positioning and existing regressions (FR-010).

## Independent manipulation panel

Make ManipulationPanel a standalone drag/close panel accepting only JFrame. Move toggle creation, root/body composition and stationary-content resize adjustment into TaskTrackWindow. Use the panel itself as windowControls, removing the redundant controls container. Run existing FR-010/FR-012 regressions.

## Window controller

Introduce WindowController holding JFrame and drag origins, with close(), startDrag(Point), dragTo(Point), and endDrag(). ManipulationPanel translates left-button mouse events into controller calls. TaskTrackWindow constructs and injects the controller. Retain existing layout/toggle responsibility and verify FR-010 regressions.

Use Unicode U+21C8/U+21CA double arrows for the settings toggle (up to expand above, down to collapse); retain accessible actions and verify symbols in the existing FR-010 check.

## Layout holder

Introduce TaskTrackLayout with enum handles and typed text, distance, size, insets, and font accessors. Return fresh mutable AWT values to avoid shared mutation. Move presentation values and component names from TaskTrackWindow; retain Swing layout constraints and behavior there. Verify using the existing state/window suite.

Construct one TaskTrackLayout per window and pass it to TrackingPanel and toggle rendering; expose a public constructor and non-static typed accessors.

## Tracking panel extraction

Move the existing nested TrackingPanel unchanged into a public final client class with a public Layout constructor. Keep state, rendering, selector configuration, sizing, and event handlers together; MainWindow retains window composition. Verify with the existing state/window regression suite.

## JUnit Jupiter migration

Use junit-jupiter 5.12.2 (API and engine) with existing Surefire 3.2.5. Migrate all three test classes together, use BeforeEach/AfterEach, and move assertion messages to the final argument. Preserve all existing checks and graphical desktop requirements; no product behavior changes.
