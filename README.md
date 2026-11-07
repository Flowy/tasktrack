# TaskTrack

A minimal Java desktop window that stays above ordinary application windows.
The window has no title bar, with a task dropdown on the left and Start/Pause on the right.
On Windows, focus the window and press Alt+F4 to close it.
Click **Ã¢â€¡Ë†** to reveal the top panel; click **Ã¢â€¡Å ** to hide it. Drag its empty area to move the
window, or click **Ãƒâ€”** to close the app. Pointer entry and exit do not change panel visibility.
Use the small **Ã¢â€“Â¶** button to start and **Ã¢ÂÂ¸** to pause. Hover for the action name.
Choose from three sample tasks in the dropdown. The first task is selected initially; switching tasks preserves Start/Pause state. Task selection and running state live only in memory and reset when the application closes. There is no timer or persistence.

## Run

In IntelliJ IDEA, import `appjava/pom.xml` and run
`com.flowyk.tasktrack.TaskTrackApp.main()`.

With JDK 25 and Maven installed:

```powershell
mvn -f appjava/pom.xml clean package
$modulePath = (@('api','client','server','app') | ForEach-Object {
    "appjava/$_/target/tasktrack-$_-1.0-SNAPSHOT.jar"
}) -join [IO.Path]::PathSeparator
java --module-path $modulePath --module tasktrack.app/com.flowyk.tasktrack.TaskTrackApp
```

Module boundaries can be checked after building (JDK 25 on PATH):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File appjava/check-boundaries.ps1
```

The app uses Swing from the JDK and has no external runtime dependencies.
Always-on-top applies while the window is visible, above ordinary desktop windows.

## Specification-driven development

Start with [the specification guide](specs/README.md). Product principles live in
[the constitution](specs/constitution.md). The current scope and verification
record are in [001 - Minimal task tracker](specs/001-minimal-task-tracker/spec.md),
with technical decisions in its [plan](specs/001-minimal-task-tracker/plan.md).

## Project layout

- `appjava/`: Maven parent with four named Java modules.
- `appjava/api/`: contracts and injected application context; no application dependencies.
- `appjava/client/`: Swing UI; depends on API and java.desktop.
- `appjava/server/`: in-memory implementation; depends on API.
- `appjava/app/`: launcher that constructs and wires client and server.
- `specs/`: product principles, feature specifications, plans, and tasks.

The `com.flowyk.tasktrack.api.TrackingApi` interface exposes `getTasks()`,
`start(String task)`, and `stop(String task)`.
`ApplicationContext` in `.api` holds injected API dependencies; `TaskTrackApp` constructs the local implementation. `TrackingState` holds client selection and running state
and sends the selected task with each start/stop call. To supply another implementation,
supply it to `new ApplicationContext(api)`. `TaskTrackApp` passes the full context through
`MainWindow.createWindow(context)` to the tracking client. Calls are synchronous on the Swing event
dispatch thread. Stop preserves the selected task and matches the Pause button.
See [002 - Tracking API](specs/002-tracking-api/spec.md).

## Automated specification checks

Reload the Maven project in IntelliJ, then right-click
`appjava/client/src/test/java/com/flowyk/tasktrack/client/MainWindowTest.java` and select
**Run 'MainWindowTest'**. Alternatively, use the Maven tool window to run
**Lifecycle > test** for `appjava`.

With Maven on PATH, run from the repository root:

```powershell
mvn -f appjava/pom.xml test
```

Maven writes test results to `appjava/{client,server}/target/surefire-reports/` and returns a
nonzero exit code on failure. Tests need a graphical desktop; they do not show
windows or send real keyboard/mouse input.

The window checks cover button-controlled window controls, dragging, close-event dispatch,
the compact symbol button, repeated start/pause transitions,
always-on-top configuration, Space activation binding, and fresh-window state.
They reference requirement IDs in their names. Markdown requirements are not
executed directly: these tests encode the observable checks from the spec.
Desktop stacking/focus, real keyboard input, full process restart, and absence of
state-file writes remain manual/review checks; these tests do not prove them.

Client and server packages are exported only to tasktrack.app. Client unit tests use
API doubles; server contract checks run in the server module. Use the module path
for launch so JPMS boundaries apply.
See [003 - Java module boundaries](specs/003-module-boundaries/spec.md).
