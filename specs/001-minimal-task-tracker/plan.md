# 001 - Technical plan

Status: Planned

## Decisions

- Keep `TaskTrackApp` as the application starter and delegate window construction to `client.TaskTrackWindow`. The `.client` boundary is a Java package, within the existing application artifact; it owns Swing layout, window configuration, and in-memory UI state. Preserve FR-001, FR-002, FR-008, FR-009 and QR-002 behavior and the existing main class.
- Use Swing/AWT from the JDK: a small native desktop application without external dependencies.
- Use Java 25, as confirmed by the user and configured in IntelliJ. Set Maven compiler release to 25 and use JDK 25 for builds and verification.
- Use a JFrame with always-on-top enabled, standard decorations, and exit-on-close.
- Use a JPanel containing a status label and a JButton. Keep one boolean running state in the window instance and update both labels together on the Swing event dispatch thread.
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
- Standard decorations preserve moving, minimizing, and closing. Minimized windows are not kept visible.

## Repeatable acceptance checks

Use JUnit 4 with Maven Surefire to run Swing checks on the event dispatch thread.
Tests require a graphical desktop and must fail with a clear message if headless.
Map tests to FR-001, FR-002 configuration, FR-008, and FR-009 fresh-window state.
Keep OS stacking/focus and process restart as manual acceptance checks.
