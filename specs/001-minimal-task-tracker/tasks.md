# 001 - Tasks

- [x] Restore the close cursor override and add an attached-button regression assertion (FR-010). Debugger showed inherited cursor 13 (move) before the fix and 0 (default arrow) after attachment with the fix. All eight JUnit checks passed on Java 25.

- [x] Override the close button's inherited move cursor with the standard arrow (FR-010); Java 25 compilation and all eight existing checks passed. Cursor override verified by code inspection; actual pointer appearance remains a manual check.

- [x] Collapse the hidden manipulation panel by default and on mouse exit, preserving tracking-area position during hover changes; verify dimensions and existing interactions (FR-010). All eight JUnit checks passed on Java 25.

- [x] Add hover top panel, window dragging, and close action; verify hover across children, exit, drag displacement, close dispatch, and existing checks (FR-010, FR-008, FR-009). Java 25 compilation and all eight JUnit checks passed; real desktop interaction remains pending.

- [x] Remove the native title bar and align the button right with empty space left; verify window configuration, layout, and existing behavior (FR-001, FR-002, FR-008, FR-009). Java 25 compilation and all seven JUnit checks passed.

- [x] Remove status text and use a small Unicode play/pause button; verify symbols, size, accessible names, tooltips, and existing transitions (FR-001, FR-008, FR-009, QR-002). All seven JUnit checks passed on Java 25.

- [x] Change button labels to Start timer/Pause and replace state toggle with explicit start/pause methods; verify repeated commands and UI cycles (FR-001, FR-008, FR-009). Java 25 compilation and all seven JUnit checks passed.

- [x] Extract running state and transitions from the client into an independent state holder; preserve existing Swing checks and verify state transitions and instance isolation without Swing (FR-008, FR-009). Java 25 compilation and all seven JUnit checks passed.

- [x] Align Maven and launch documentation with the confirmed Java 25 baseline (QR-003); verify with JDK 25 if available.

- [x] Separate the starter from UI code in `com.flowyk.tasktrack.client` and run existing acceptance checks (FR-001, FR-002, FR-008, FR-009, QR-002).
- [x] Update scope and acceptance criteria from the user's Start/Stop request.
- [x] Record toolkit, Java baseline, in-memory state, build, and validation decisions.
- [x] Configure executable Java build (QR-003).
- [x] Implement compact always-on-top window (FR-001, FR-002).
- [x] Implement reversible Start/Stop state with no storage (FR-008, FR-009).
- [x] Compile and run Swing smoke checks; record evidence.
- [ ] Manually verify stacking, focus, keyboard activation, and restart behavior on the desktop.

- [x] Add and run permanent automated acceptance checks with requirement references; document execution and coverage limits.

- [x] Add static tasks and validated selected-task state (FR-011); verify defaults, valid/invalid selections, running-state independence, and instance reset.
- [x] Add accessible dropdown beside Start/Pause (FR-011, FR-012), update layout assertions and README, and run the state/window regression suite.
- [x] Record verification evidence and remaining manual dropdown checks (FR-011, FR-012).

- [x] Remove task selector arrow/border while preserving clickable text and popup (FR-013). Verify popup activation and run existing regression checks.

- [x] Remove closed-selector background and focus coloration (FR-013); compile and run existing checks.

- [x] Extract ManipulationPanel and compose it in TaskTrackWindow; compile and run existing state/window regression checks (FR-010, FR-012).

- [x] Replace hover with accessible show/hide toggle; verify pointer independence, layout, dragging, close and existing checks (FR-010).

- [x] Remove tracking content dependency from ManipulationPanel; compose panels/toggle in TaskTrackWindow and run regressions (FR-010, FR-012).

- [x] Extract WindowController with close/drag operations, inject into ManipulationPanel and run existing regression checks (FR-010).

- [x] Replace settings toggle plus/minus with double up/down arrows and verify FR-010 and regression suite.

- [x] Extract TaskTrackWindow presentation values into a typed layout holder and run existing regressions (FR-001, FR-008, FR-010, FR-013).

- [x] Convert layout accessors to instance methods and verify existing regressions (FR-001, FR-008, FR-010, FR-013).

- [x] Extract TrackingPanel into its own client class with unchanged UI and run existing regressions (FR-001, FR-008, FR-011, FR-012, FR-013). IntelliJ build succeeded; all 11 existing state/window tests passed.

- [x] Migrate all tests and Maven dependency to JUnit Jupiter; run the full suite preserving FR-001/002/008–013, QR-002 and API-001/002/003, API-QR-001 coverage.

Verification: Maven test on Corretto 25.0.2 with Jupiter 5.12.2 and Surefire 3.2.5 passed all 14 tests (8 MainWindowTest, 3 TrackingStateTest, 3 TrackingApiTest), with zero failures, errors or skips. Existing physical desktop acceptance checks remain pending.

- [x] Style timer control as plain clickable text and run existing regressions (FR-008, QR-002). Offline Maven reactor tests passed on Java 25; physical appearance remains a manual check.

- [x] Suppress timer focus outline and verify existing regressions (FR-008, QR-002). Offline Maven reactor tests passed on Java 25. Physical appearance remains unverified.
