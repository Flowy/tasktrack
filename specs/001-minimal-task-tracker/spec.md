# 001 - Minimal task tracker

Status: Implementing

## Confirmed scope

A compact Java desktop window that stays above ordinary application windows,
with a button to start and pause. State is local to the process with no persistence.
Includes static task selection; task editing and storage remain outside scope.

## Functional requirements

- **FR-001:** Show a compact desktop window without the native title bar, with one small Unicode symbol button on the right, a task selector on the left, and no status text.
- **FR-002:** Keep the visible, non-minimized window always on top without repeatedly requesting focus.
- **FR-008:** Initially show ▶ (start timer). Clicking it starts tracking and shows ⏸ (pause); clicking pause returns to ▶. Repeated cycles work. Tooltips and accessible names identify the current action.
- **FR-009:** Keep running state only in memory. Closing exits the application; reopening starts stopped. Do not load or save state.
- **FR-010:** Initially hide the top manipulation panel without reserving vertical space. An always-visible button toggles the drag and close panel. Pointer entry/exit never changes visibility. Keep the tracking area stationary on expansion/collapse. Dragging moves the window; closing exits the app. Give the toggle action tooltips, accessible names and keyboard activation.

FR-003 through FR-007 are retired: the previous task CRUD and persistence proposals are outside this iteration. IDs are not reused.

## Acceptance criteria

1. Launch: a compact window shows a small ▶ button without status text (FR-001, FR-008).
2. Activate another ordinary desktop application: TaskTrack stays above it and allows that application to keep keyboard focus (FR-002).
3. Click ▶, then ⏸, then repeat: symbols, tooltips, and accessible names stay consistent (FR-008).
4. Focus the button and press Space: the same transition occurs (FR-008).
5. Close while running and reopen: the app starts stopped and no state file is written (FR-009).
6. Toggle the top panel with its button, check pointer entry/exit does not change visibility, drag to move, toggle to collapse, and close to exit (FR-010).
   The drag area uses a move cursor; the close button uses the standard arrow cursor (FR-010).

## Quality requirements

- **QR-001:** Manually verify window stacking on the target desktop. Secure desktop and exclusive full-screen applications are outside the guarantee.
- **QR-002:** The action button supports normal keyboard activation and has a visible focus indicator.
- **QR-003:** Use Java and document the build/toolkit decisions.

## Implementation defaults

Windows is the first verification platform, matching the current environment.
Use a small undecorated window and a single action button aligned right. Starting changes
only the running state; elapsed timers and background work remain outside scope.

## Out of scope

Persistence, task editing, elapsed timers, network services, tray integration, installers, and additional title-bar controls beyond drag and close.

## Validation evidence

- PASS: FR-010 cursor regression: debugger logpoints showed cursor type 13 (MOVE_CURSOR) after attachment without the missing override, and type 0 (DEFAULT_CURSOR) after restoring it. Added an attached-button cursor assertion; all eight JUnit checks passed with Java 25. Debugger probes cleaned up.
- PASS: collapsed hover-panel update compiled with Java 25 and all eight JUnit checks passed. FR-010 checks verify initial 48 pixel height, expansion to 72 pixels on entry with stationary tracking area, collapse on exit, and retained drag displacement. Real desktop interaction remains pending.
- PASS: hover controls compiled with Java 25 and all eight JUnit checks passed. Synthetic events verify initial hiding, entry, child transitions, exit, keeping controls visible during dragging, screen-coordinate displacement, and close-event dispatch (FR-010). Existing timer and window checks also pass. Real pointer interaction and process exit remain manual checks.
- PASS: undecorated window with right-aligned button compiled with Java 25; all seven JUnit checks passed, including no native decorations, 8 pixel right gap, empty left space, preserved button size, and existing start/pause behavior. Manual desktop checks remain pending.
- PASS: compact Unicode button update compiled with Java 25; all seven JUnit checks passed. Window tests verify no status label, actual 40-by-32 button size, font coverage for both symbols, tooltips, accessible names, repeated cycles, keyboard binding, and fresh-window reset. Centered FlowLayout preserves button size despite the Windows minimum decorated-window width. Manual desktop checks remain pending.
- PASS: Start timer/Pause button delegates to explicit `start()`/`pause()` state methods. Compiled with Java 25 and ran all seven JUnit checks, including repeated start/pause commands and UI cycles (FR-001, FR-008, FR-009). No state toggle method remains. Desktop manual checks remain pending.
- PASS: extracted `state.TrackingState` from the Swing client without changing requirements. Compiled main and test sources with Corretto 25.0.2 (`--release 25`); all five existing window checks and two independent state checks passed via JUnitCore (FR-008 repeated transitions, FR-009 instance isolation). Manual desktop checks remain pending.
- PASS: compiled application with the installed Java 8 javac using UTF-8 and Java 8 source/target settings.
- PASS: temporary Swing smoke harness checked initial labels, four start/stop cycles, Space key binding, always-on-top property, exit-on-close, fixed layout, and a fresh window starting stopped after disposing a running one.
- The implementation contains no file, preferences, or network operations.
- Maven packaging was not run because Maven is not on the terminal PATH.
- Pending: manual desktop stacking/focus, actual keyboard activation, and full process restart checks. Feature remains Implementing until those acceptance checks are recorded.

- PASS: permanent TaskTrackAppTest suite, 5 tests, run with JUnitCore using cached JUnit 4.13.2 and the installed Java 8 JDK. Covers initial labels, repeated toggles, always-on-top configuration, Space binding, and fresh-window reset. Maven runner configured but not executed here (no Maven on PATH). See README for repeatable Maven/IntelliJ instructions.
- PASS: after extracting UI into `com.flowyk.tasktrack.client.TaskTrackWindow`, compiled main and test sources with Java 8 and ran all five `TaskTrackWindowTest` checks using JUnitCore (FR-001, FR-002 configuration, FR-008, FR-009 fresh-window state, QR-002 binding). Desktop manual checks remain pending.
- PASS: corrected baseline to Java 25 (QR-003). Compiled main and test sources with Corretto 25.0.2 using `--release 25`; all five `TaskTrackWindowTest` checks passed on Java 25 via JUnitCore. Earlier Java 8 entries describe historical checks only. Maven packaging remains unverified (Maven unavailable on PATH).

## Task selection extension

Confirmed request: populate a few static tasks in state and let the user choose one.

- **FR-011:** State provides a fixed, nonempty list of sample tasks and holds the selected task in memory. The selector displays that list and updates the selected task when the user chooses another entry.
- **FR-012:** Keep the compact always-on-top window, hover controls, and Start/Pause action available alongside task selection.

Implementation defaults (non-blocking assumptions): three sample task names, first task initially selected, selection changes preserve running state, and each new window resets selection. No task CRUD, per-task timing, or persistence.

Acceptance: launch shows the first task; choose each entry by the standard dropdown; selection survives Start/Pause cycles; choosing while running preserves Pause; reopening restores the first task (FR-011, FR-012). Verify state selection and invalid selections independently, and UI selection alongside the existing regression suite. Real pointer and keyboard dropdown interaction remains a manual check.

- PASS (FR-011, FR-012): compiled main/test sources with Corretto 25.0.2 javac (`--release 25 -encoding UTF-8`), then ran TrackingStateTest and TaskTrackWindowTest via JUnitCore 4.13.2: all 10 checks passed. Covers immutable sample list, default/valid/invalid selections, selection independence from running state, fresh-state reset, dropdown entries and selection cycles, accessible name, layout, fresh-window reset, and existing start/pause, hover, drag, and close regressions. Actual pointer/keyboard dropdown interaction and OS stacking/focus remain manual checks.

- **FR-013:** Show the selected task as clickable text without dropdown arrow or surrounding selector border; clicking the name opens the existing task popup. Preserve keyboard selection and accessible naming.

- PASS (FR-013): Java 25 compilation and all 11 JUnit checks passed. New Swing check verifies no arrow button, zero border insets, and popup visibility after a synthetic left click on the task text in a visible window. Physical pointer/keyboard interaction remains a manual check.

FR-013 refinement: the closed task selector displays plain text with no background fill or focus coloration. Keep normal selection highlighting inside the task popup.

- PASS (FR-013 coloration refinement): Java 25 main/test compilation and all 11 existing JUnit checks pass, including task popup activation. Code review confirms transparent closed-value renderer and suppressed background/focus-color painting; visual appearance remains a manual check.

## Manipulation feature separation

Confirmed request: extract the manipulation panel (close button and drag zone) from TaskTrackWindow into a separate feature composed into the main window. Preserve FR-010 hover, collapse, positioning, drag, cursor, and close behavior; no new user behavior is introduced.

- PASS (FR-010, FR-012 manipulation extraction): compiled main and test sources using Corretto 25.0.2 with UTF-8 and release 25; all 11 existing JUnit checks passed. Synthetic checks cover hover expansion/collapse, stationary tracking area, child transitions, drag displacement, cursors, and close dispatch through the extracted panel. Physical desktop checks remain pending.

- PASS (FR-010 button activation): Java 25 UTF-8 compilation and all 11 JUnit checks passed. Updated regression verifies initial collapse, show/hide activation, action names, Space binding, pointer entry/exit independence, stationary tracking content, drag displacement, cursors and close dispatch. Real desktop interaction remains a manual check. Earlier hover verification entries are historical and superseded by button activation.

Confirmed structural refinement: ManipulationPanel contains only drag and close controls and takes no tracking content. TaskTrackWindow composes tracking, manipulation controls and the toggle. Preserve FR-010 and FR-012 behavior.

- PASS (FR-010, FR-012 independent manipulation panel): compiled main/tests with Java 25 and ran all 11 JUnit checks successfully after moving composition/toggle/resize into TaskTrackWindow. ManipulationPanel now accepts only JFrame and owns only drag/close controls. Physical desktop verification remains pending.

Confirmed structural refinement: a custom controller holds the window and provides close dispatch and drag support. ManipulationPanel uses that controller instead of JFrame. Preserve FR-010 behavior.

- PASS (FR-010 window controller): Java 25 main/test compilation and all 11 existing JUnit checks passed, including drag displacement and close-event dispatch through WindowController. Physical desktop interaction remains pending.

FR-010 visual refinement: use an upward double arrow to show the manipulation panel and a downward double arrow to hide it. Preserve action tooltips and accessible names.

- PASS (FR-010 double arrows): Java 25 compilation and all 11 JUnit checks passed, including up/down toggle symbol assertions before expansion, after expansion and after collapse. Visual desktop appearance remains unverified.
