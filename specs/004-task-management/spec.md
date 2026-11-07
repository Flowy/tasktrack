# 004 - Task management API

Status: Implemented and automatically verified

## Problem and outcome

Allow API callers to add and remove tasks in InMemoryTracking.

## Confirmed requirements

- Expose task addition and removal through the tracking API and in-memory implementation.
- Store tasks in an unmodifiable list and assign a new unmodifiable list when editing.

## Proposed assumptions

- Task names remain exact, case-sensitive strings; duplicate additions and missing-task removals are no-ops.
- Reject null and blank names with IllegalArgumentException. Preserve nonblank names as supplied.
- Keep the sample tasks, insertion order, and instance-local storage. No persistence or UI controls are requested.

## User scenarios and acceptance criteria

Adding a new name makes it available for listing and start/stop. Removing it excludes it from listing and makes subsequent start/stop fail validation. Previously returned lists remain immutable snapshots.

## Functional requirements

- **TASK-001:** TrackingApi exposes void addTask(String task) and void removeTask(String task).
- **TASK-002:** Add appends a unique name; remove deletes that name. Repeated operations are harmless. Null/blank arguments fail without changing tasks.
- **TASK-003:** getTasks returns an immutable snapshot; mutations affect only the receiving instance. Existing start/stop validation uses current tasks.
- **TASK-004:** The stored task list is unmodifiable from initialization; edits replace it rather than modifying it in place.

## Quality requirements

- **TASK-QR-001:** Preserve Java 25 module boundaries and existing client regressions.

## Out of scope

UI controls, selection reconciliation, persistence, concurrency guarantees, and network endpoints.

## Open questions

None blocking. Defaults above follow the existing string-based API.

## Validation evidence

PASS (TASK-004): Stored-list initialization uses List.of and each edit assigns List.copyOf of a temporary edited list; getTasks returns the stored immutable snapshot. Re-ran offline Maven packaging on Corretto 25.0.2; all 16 existing tests passed, including immutable snapshots before and after edits.

PASS (TASK-001/002/003, TASK-QR-001): offline Maven reactor packaging with Maven 3.9.16 and Corretto 25.0.2 (`mvn -o -f appjava/pom.xml package`) compiled all four Java modules and passed all 16 tests (13 client, 3 server), with no failures or skipped tests. Server checks cover mutation, duplicate/missing operations, invalid names, insertion order, immutable snapshots, independent instances, empty-list recovery and start/stop after removal. No manual desktop checks were run; UI behavior was not extended.
