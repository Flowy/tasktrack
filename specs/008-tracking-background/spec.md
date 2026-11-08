# 008 - Tracking background

Status: Implemented and automatically verified; physical appearance pending

## Problem and outcome

Make tracking activity visible through background color.

## Confirmed requirements

- Add background color based on time tracking state in InMemoryTracking.

## Proposed assumptions

- Color the tracking row soft green while any task is active; restore the normal theme background when stopped or empty.
- Use authoritative server state independently of client selection. Keep existing command and elapsed-display semantics.
- Refresh color immediately after UI actions and during the existing five-minute refresh.

## User scenarios and acceptance criteria

Starting a task turns the row green. Selecting another task keeps it green while the original task runs. Stopping the active task or removing it restores the normal background.

## Functional requirements

- **COLOR-001:** TrackingApi exposes whether any task is active, derived from InMemoryTracking currentTask. Inactive stops do not change this result; active removal clears it.
- **COLOR-002:** TrackingPanel background uses active state, with transparent task text and elapsed control sharing the background. Stopped and empty states use the theme background.
- **COLOR-003:** Re-render after commands, selection and task edits and on existing periodic refresh; preserve minutes display and command behavior.

## Quality requirements

- **COLOR-QR-001:** Keep colors in the client layout holder and state in the server; verify state transitions and UI mapping without a client dependency on server.

## Out of scope

New command semantics, persistence, notifications and changes to elapsed refresh cadence.

## Open questions

None blocking; palette and row scope are implementation defaults.

## Validation evidence

PASS (COLOR-001/002/003, COLOR-QR-001): offline Maven reactor package on Maven 3.9.16 / Corretto 25.0.2 passed all 31 tests (20 client, 11 server), no failures or skips. Server transitions cover switching, inactive stop, active stop and removals. API-only Swing test verifies active/stopped backgrounds, selection independence, authoritative activity despite local running flag, shared selector background and empty state. Existing elapsed, command, settings and window regressions pass. git diff --check passed. Physical desktop appearance remains unverified.
