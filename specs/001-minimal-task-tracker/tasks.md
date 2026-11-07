# 001 - Tasks

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
