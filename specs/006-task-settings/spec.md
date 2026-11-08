# 006 - Task settings

Status: Implemented and automatically verified; physical desktop checks pending

## Problem and outcome

Manage tasks from the expandable window controls without enlarging the tracking row.

## Confirmed requirements

- Add a menu button to ManipulationPanel with a Task settings option.
- Open a dialog for adding and removing tasks through TrackingApi.
- Refresh selection after edits and disable the timer when no tasks remain.

## Proposed assumptions

- Apply edits immediately; Close dismisses the dialog. Preserve exact task names and existing duplicate/validation rules.
- Preserve a valid selection; otherwise choose the first remaining task. Removal of the running task resets the running indicator. Adding to an empty list selects the new task, stopped.
- Removal discards timing data, as specified in TIME-003. No persistence or undo.

## User scenarios and acceptance criteria

Expand controls, open Menu > Task settings, add a name and see it in the selector. Remove selected and unselected tasks; remove all and add again without errors. Closing the dialog keeps edits.

## Functional requirements

- **SET-001:** Accessible menu button opens a popup containing Task settings; its dialog is owned by the main window.
- **SET-002:** Dialog lists tasks, adds nonblank names, removes the selected entry, and displays validation errors without closing.
- **SET-003:** Each successful edit refreshes the tracker; preserve valid selection, recover missing selection, and safely handle empty lists with timer disabled.
- **SET-004:** Removing the running task resets client running state; unrelated removals preserve it.

## Quality requirements

- **SET-QR-001:** Preserve module boundaries and existing window, API and timing regressions.

## Out of scope

Persistence, renaming, elapsed display, and changes to existing selection/start/stop command semantics.

## Open questions

None blocking; defaults follow the accepted dialog proposal and existing API semantics.

## Validation evidence

PASS (SET-001/002/003/004): `mvn -o -f appjava/pom.xml package` with Maven 3.9.16 and Corretto 25.0.2 built all modules and passed all 26 tests (16 client, 10 server), no failures or skips. Three new tests cover owned modal dialog opening through the menu, validation, add/remove, selection reconciliation, active versus unrelated removal, empty-list timer disabling, and recovery after adding a task.

PASS (SET-QR-001 regressions): existing window, API, and server timing checks passed; client continues to depend only on the API and Java desktop modules. `git diff --check` passed.

LIMITATION (SET-QR-001 boundary script): `powershell -NoProfile -ExecutionPolicy Bypass -File appjava/check-boundaries.ps1 -Javac C:/Workspace/bin/java/corretto/jdk25.0.2_10/bin/javac.exe` compiled all legal modules and rejected the first four illegal dependency probes, then failed because the synthetic client descriptor with an added server requirement compiles. The server exports its package and does not require the client, so this probe is already permitted by the unchanged module descriptors (also checked against HEAD). Module-policy correction is outside this UI change.

Pending manual checks: physical pointer/keyboard menu and dialog use, desktop appearance, focus and stacking. Edits remain in memory and removal discards timing data under TIME-003.
