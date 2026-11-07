# 001 - Minimal task tracker

Status: Implementing

## Confirmed scope

A compact Java desktop window that stays above ordinary application windows,
with a button to start and stop. State is local to the process with no persistence.
This scope replaces the earlier proposed task list and storage features.

## Functional requirements

- **FR-001:** Show a compact desktop window with a visible running/stopped status and one Start/Stop button.
- **FR-002:** Keep the visible, non-minimized window always on top without repeatedly requesting focus.
- **FR-008:** Initially show Stopped and Start. Clicking Start changes status to Running and the button to Stop; clicking Stop reverses this. Repeated cycles work.
- **FR-009:** Keep running state only in memory. Closing exits the application; reopening starts stopped. Do not load or save state.

FR-003 through FR-007 are retired: the previous task CRUD and persistence proposals are outside this iteration. IDs are not reused.

## Acceptance criteria

1. Launch: a compact window shows Stopped and Start (FR-001, FR-008).
2. Activate another ordinary desktop application: TaskTrack stays above it and allows that application to keep keyboard focus (FR-002).
3. Click Start, then Stop, then repeat: status and action labels stay consistent (FR-008).
4. Focus the button and press Space: the same transition occurs (FR-008).
5. Close while running and reopen: the app starts stopped and no state file is written (FR-009).

## Quality requirements

- **QR-001:** Manually verify window stacking on the target desktop. Secure desktop and exclusive full-screen applications are outside the guarantee.
- **QR-002:** The action button supports normal keyboard activation and has a visible focus indicator.
- **QR-003:** Use Java and document the build/toolkit decisions.

## Implementation defaults

Windows is the first verification platform, matching the current environment.
Use a small standard decorated window and a single toggle button. Starting changes
only the running state; timers, task selection, and background work are not part of this request.

## Out of scope

Persistence, task lists, timers, network services, tray integration, installers, and custom window decorations.

## Validation evidence

- PASS: compiled application with the installed Java 8 javac using UTF-8 and Java 8 source/target settings.
- PASS: temporary Swing smoke harness checked initial labels, four start/stop cycles, Space key binding, always-on-top property, exit-on-close, fixed layout, and a fresh window starting stopped after disposing a running one.
- The implementation contains no file, preferences, or network operations.
- Maven packaging was not run because Maven is not on the terminal PATH.
- Pending: manual desktop stacking/focus, actual keyboard activation, and full process restart checks. Feature remains Implementing until those acceptance checks are recorded.

- PASS: permanent TaskTrackAppTest suite, 5 tests, run with JUnitCore using cached JUnit 4.13.2 and the installed Java 8 JDK. Covers initial labels, repeated toggles, always-on-top configuration, Space binding, and fresh-window reset. Maven runner configured but not executed here (no Maven on PATH). See README for repeatable Maven/IntelliJ instructions.
- PASS: after extracting UI into `com.flowyk.tasktrack.client.TaskTrackWindow`, compiled main and test sources with Java 8 and ran all five `TaskTrackWindowTest` checks using JUnitCore (FR-001, FR-002 configuration, FR-008, FR-009 fresh-window state, QR-002 binding). Desktop manual checks remain pending.
- PASS: corrected baseline to Java 25 (QR-003). Compiled main and test sources with Corretto 25.0.2 using `--release 25`; all five `TaskTrackWindowTest` checks passed on Java 25 via JUnitCore. Earlier Java 8 entries describe historical checks only. Maven packaging remains unverified (Maven unavailable on PATH).
