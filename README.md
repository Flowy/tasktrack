# TaskTrack

A minimal Java desktop window that stays above ordinary application windows.
Click **Start** to switch to Running; click **Stop** to return to Stopped.
State lives only in memory and resets when the application closes. There is no timer or persistence.

## Run

In IntelliJ IDEA, import `appjava/pom.xml` and run
`com.flowyk.tasktrack.TaskTrackApp.main()`.

With a JDK (8 or newer) and Maven installed:

```powershell
mvn -f appjava/pom.xml clean package
java -jar appjava/target/appjava-1.0-SNAPSHOT.jar
```

Without Maven, from the repository root:

```powershell
New-Item -ItemType Directory -Force appjava/target/classes | Out-Null
javac -encoding UTF-8 -d appjava/target/classes appjava/src/main/java/com/flowyk/tasktrack/TaskTrackApp.java
java -cp appjava/target/classes com.flowyk.tasktrack.TaskTrackApp
```

The app uses Swing from the JDK and has no external runtime dependencies.
Always-on-top applies while the window is visible, above ordinary desktop windows.

## Specification-driven development

Start with [the specification guide](specs/README.md). Product principles live in
[the constitution](specs/constitution.md). The current scope and verification
record are in [001 - Minimal task tracker](specs/001-minimal-task-tracker/spec.md),
with technical decisions in its [plan](specs/001-minimal-task-tracker/plan.md).

## Project layout

- `appjava/`: Maven Java application module.
- `specs/`: product principles, feature specifications, plans, and tasks.

## Automated specification checks

Reload the Maven project in IntelliJ, then right-click
`appjava/src/test/java/com/flowyk/tasktrack/TaskTrackAppTest.java` and select
**Run 'TaskTrackAppTest'**. Alternatively, use the Maven tool window to run
**Lifecycle > test** for `appjava`.

With Maven on PATH, run from the repository root:

```powershell
mvn -f appjava/pom.xml test
```

Maven writes test results to `appjava/target/surefire-reports/` and returns a
nonzero exit code on failure. Tests need a graphical desktop; they do not show
windows or send real keyboard/mouse input.

The five checks cover initial status, repeated Start/Stop transitions,
always-on-top configuration, Space activation binding, and fresh-window state.
They reference requirement IDs in their names. Markdown requirements are not
executed directly: these tests encode the observable checks from the spec.
Desktop stacking/focus, real keyboard input, full process restart, and absence of
state-file writes remain manual/review checks; these tests do not prove them.
