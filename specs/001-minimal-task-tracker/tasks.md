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
