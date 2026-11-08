# 011 - Active task persistence

Status: Implemented and automatically verified

## Problem and outcome
Continue the active tracking interval after application restart.

## Confirmed requirements
- Store only the currently open task and its start time in another persistence file; restore and continue on startup.

## Proposed assumptions
- Use Documents/TaskTrack/active-task.json. Time while closed counts toward the interval.
- No active task is represented by JSON null. Missing files mean no active task.
- Restore UI selection and running state. Completed totals remain session-local.

## User scenarios and acceptance criteria
Start a task, close the application, and reopen: the same task runs from its original start time. Stop, switch or remove it: the next startup reflects that change.

## Functional requirements
- **ACTIVE-001:** Save task name and exact Instant start time immediately on start/switch; restore on startup.
- **ACTIVE-002:** Stop and active removal clear the saved interval; repeated commands preserve the original start time.
- **ACTIVE-003:** Restore selection and running state in the desktop client.
- **ACTIVE-004:** Reject malformed files descriptively without overwriting them; ignore an otherwise valid record whose task has been removed. Replace the file atomically and preserve memory on save failure.

## Quality requirements
- **ACTIVE-QR-001:** Verify restart, timestamps, lifecycle and failures using temporary directories and deterministic clocks.

## Out of scope
Concurrent application instances, restoring completed totals, transactions spanning CSV/list/active files.

## Open questions
None blocking.

## Validation evidence
PASS (ACTIVE-001/002/003/004, ACTIVE-QR-001): Offline Maven 3.9.16 reactor package with Corretto 25.0.2 passed all 43 tests (21 client, 22 server), with no failures or skips. New deterministic tests cover restart with original nanosecond timestamp, downtime elapsed, switch/stop/removal, repeated commands, malformed file preservation, stale removed names, failed active writes, history failure rollback, and restored client selection/running state. No manual desktop restart or cross-platform filesystem checks were performed. Multi-file crash transactions remain out of scope.
