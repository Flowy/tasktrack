# 009 - Completed interval CSV persistence

Status: Implemented and automatically verified

## Problem and outcome
Keep a local history of completed tracking intervals across application restarts.

## Confirmed requirements
- Store task name, start time and end time in CSV when a task finishes.
- Store the user-facing CSV in Documents/TaskTrack/tasks.csv (user-requested location refinement).

## Proposed assumptions
- Stop and switching tasks complete intervals. Active task removal retains existing discard semantics; shutdown does not finish an interval.
- Use tasks.csv, UTF-8, a header, and ISO-8601 UTC timestamps. History is append-only; loading task lists and elapsed totals is outside scope.

## User scenarios and acceptance criteria
Stopping or switching a running task appends its name and exact start/end timestamps. Repeated stop or start produces no duplicate intervals. Existing history survives new tracking instances.

## Functional requirements
- **CSV-001:** Append each stopped/switched interval with columns task_name,start_time,end_time; preserve existing history and create parent directories/header as needed.
- **CSV-002:** Preserve Unicode and CSV special characters using quoted fields with doubled quotes and CRLF record endings; use Instant timestamps.
- **CSV-003:** Default to user.home/Documents/TaskTrack/tasks.csv on all platforms.
- **CSV-004:** Failed writes throw a descriptive UncheckedIOException before clearing or switching the active timer; no-op operations and removal produce no records.

## Quality requirements
- **CSV-QR-001:** Verify with deterministic clocks and temporary directories without writing tests into real user data.

## Out of scope
History viewer, loading timers/tasks, shutdown persistence, concurrent application instances, crash-atomic writes.

## Open questions
None blocking; storage format and interval interpretation follow existing tracking commands.

## Validation evidence
PASS (CSV-001/002/003/004, CSV-QR-001): Offline Maven 3.9.16 reactor package with Corretto 25.0.2 passed all 35 tests (20 client, 15 server), no failures or skips. Four new tests verify exact UTF-8 escaped CSV, timestamps, switching, no-op commands, append across instances, directory rules, removal and state preservation/retry after write failure. Existing server tests use temporary directories. git diff --check passed. Physical desktop and macOS/Linux filesystem checks were not run; their path selection is unit tested.


Location refinement verification: PASS (CSV-003), offline Maven reactor package on Corretto 25.0.2 / Maven 3.9.16 passed all 35 tests with no failures or skips. Default Documents path is verified. Existing history copied to C:\Users\Lukas\Documents\TaskTrack\tasks.csv and SHA-256 equality verified; AppData original retained as backup. Supersedes historical platform-path verification above.
