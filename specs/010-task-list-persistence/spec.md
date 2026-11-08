# 010 - Task list persistence

Status: Implemented and automatically verified; existing module boundary check failure noted below

## Problem and outcome
Restore the task list across application instances using a local file.

## Confirmed requirements
- Save the task list to a file and read it back.

## Proposed assumptions
- Use Documents/TaskTrack/task-list.json, separate from CSV interval history.
- Save successful additions/removals immediately. Missing files use existing sample tasks; an empty saved list stays empty.
- Restore names and order only; timers and elapsed totals remain session-local.

## User scenarios and acceptance criteria
After editing tasks and restarting, the same names appear in the same order, including an empty list.

## Functional requirements
- **LIST-001:** Store and load exact unique nonblank task names in insertion order as UTF-8 JSON.
- **LIST-002:** Production startup loads the saved list; missing files retain samples. Save edits before changing in-memory state; no-op edits do not write.
- **LIST-003:** Reject malformed or invalid files with a descriptive error without overwriting them. Replace files atomically; failed writes preserve previous state.

## Quality requirements
- **LIST-QR-001:** Verify using temporary directories, including special characters, restart, empty lists and failures.

## Out of scope
Timer restoration, history migration, concurrent writers, UI error dialogs.

## Open questions
None blocking; the defaults above preserve current behavior where no file exists.

## Validation evidence
PASS (LIST-001/002/003, LIST-QR-001): `mvn -o -f appjava/pom.xml package` using Maven 3.9.16 and Corretto 25.0.2 passed all 39 tests (20 client, 19 server), no failures or skips. Four new tests verify exact names/order across instances, empty-list restoration, immutable lists, invalid JSON preservation, no-op edits, and failed add/remove preserving state, active tracking and disk contents. `git diff --check` passed.

Module boundary script compiled all production modules with Gson and passed the first four missing-requires probes, then failed its extra-requires client-to-server probe because the existing server descriptor exports its package without qualification. This pre-existing export policy was not changed. No manual desktop restart or non-Windows filesystem verification was performed. Atomic replacement support is filesystem-dependent; unsupported saves report an error.
