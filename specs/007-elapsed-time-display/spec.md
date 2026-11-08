# 007 - Elapsed time display

Status: Minutes-only display implemented and automatically verified; physical desktop appearance pending

## Problem and outcome

Show tracked time in the minimal tracking row.

## Confirmed requirements

- Replace the play/pause symbol in TrackingPanel with a time tracking display.
- Display only total whole minutes, without seconds or conversion to hours. This supersedes the most-significant-unit display.

## Proposed assumptions

- Use an m suffix, truncate partial minutes and clamp negative elapsed time to zero. Keep precise underlying Duration accumulation to preserve partial minutes across sessions.
- Keep the display clickable with existing start/pause commands, tooltips and keyboard activation.
- Refresh at five-minute elapsed boundaries while attached, and immediately after selection or commands. Show disabled 0m when no tasks remain.

## User scenarios and acceptance criteria

Select a task to see its accumulated time; start tracking to see it advance, pause to retain elapsed time, and resume to continue accumulating.

## Functional requirements

- **DISPLAY-001:** Display elapsed time from TrackingApi for the selected task, including its current interval, using total whole minutes and an m suffix (0m, 65m, 120m).
- **DISPLAY-002:** Preserve existing start/pause activation and selection semantics; refresh immediately on user changes and at five-minute elapsed boundaries.
- **DISPLAY-003:** Empty task lists show disabled zero time; disposal stops periodic updates.

## Quality requirements

- **DISPLAY-QR-001:** Preserve API module boundaries and existing regressions; verify formatting and refresh deterministically.

## Out of scope

Persistence and changes to task switching or start/stop semantics.

## Open questions

None blocking; the minutes-only request resolves the prior hours presentation question.

## Validation evidence

PASS (DISPLAY-001/002/003, DISPLAY-QR-001): offline Maven reactor package on Maven 3.9.16 / Corretto 25.0.2 passed all 28 tests (18 client, 10 server), with no failures or skips. Deterministic tests cover formatting, unbounded hours, API-backed selection, refresh callbacks, start/pause action names, empty selection and timer attachment/disposal. Existing window, settings, command and server timing checks pass. git diff --check passed. Physical desktop appearance and actual wall-clock refresh remain manual checks.


PASS (DISPLAY-001/003 compact refinement): offline Maven reactor package on Maven 3.9.16 / Corretto 25.0.2 passed all 28 tests, no failures or skips. Verified second/minute/hour boundaries, truncation, zero and negative durations, unbounded hours, selection and periodic refresh; window regression verifies the narrower 56-pixel control. Supersedes the previous HH:mm:ss presentation. Physical desktop appearance remains unverified.

## Historical five-unit tick refinement (superseded)

Confirmed: tick every five seconds in the seconds range, and every five minutes in the minutes and hours ranges (DISPLAY-002). Hours formatting is awaiting user clarification. Keep immediate refresh after selection/start/pause and stop refresh on disposal.


PASS (DISPLAY-002/003 adaptive scheduling): offline Maven reactor package passed all 29 tests (19 client, 10 server), no failures or skips. Deterministic checks cover second/minute/hour scheduling boundaries and subsecond alignment; lifecycle regressions pass. Hours text remains unchanged pending the requested presentation choice. Physical timing remains unverified.

PASS (DISPLAY-001/002/003, DISPLAY-QR-001 minutes-only refinement): offline Maven reactor package on Maven 3.9.16 / Corretto 25.0.2 passed all 29 tests (19 client, 10 server), no failures or skips. Checks cover sub-minute zero, whole-minute truncation, total minutes above an hour, negative durations, five-minute scheduling throughout, selection, commands and disposal. git diff --check passed. Supersedes earlier seconds/hours presentation and scheduling. Physical desktop appearance remains unverified.
