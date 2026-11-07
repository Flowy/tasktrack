# 005 - Task time tracking

Status: Implemented and automatically verified

## Problem and outcome

InMemoryTracking currently validates start/stop commands without recording time.

## Confirmed requirements

- Add task time tracking in InMemoryTracking.
- Only one task is active, represented by the current task and its start time. Keep the implementation simple.
- Store start time as Instant. Add a time provider to ApplicationContext and implement LocalTimeProvider in the server.
- Resolve the time provider from ApplicationContext whenever time is requested.
- Initialize ApplicationContext first, then add and allow replacement of each dependency.

## Proposed assumptions

- Accumulate elapsed time across start/stop cycles in memory, including the current running interval when queried.
- Repeated start and stop remain idempotent. New tasks start at zero; removal discards their timing data.
- Expose elapsed time as java.time.Duration on the in-memory implementation; no UI or persistence changes.

## User scenarios and acceptance criteria

Starting, stopping, and resuming a task accumulates only running intervals. Querying a running task does not interrupt it.

## Functional requirements

- **TIME-001:** Record and return accumulated elapsed duration per task, including its running interval.
- **TIME-004:** Starting another task stops the previous interval. Starting the active task preserves its start time. Stopping an inactive task is a no-op. Removing the active task clears it.
- **TIME-002:** Repeated commands are harmless; invalid task arguments fail without changing timers.
- **TIME-003:** Task timing is instance-local; removal clears timing and re-adding starts at zero.
- **TIME-005:** ApplicationContext exposes an injected TimeProvider returning Instant. LocalTimeProvider supplies system time; InMemoryTracking holds ApplicationContext and resolves getTimeProvider().now() for each time request, using Instant start time for all interval calculations.
- **TIME-006:** ApplicationContext supports no-argument construction and setters for tracking implementation and time provider. Dependencies can be replaced; subsequent time requests use the current provider. Null setters fail without replacing existing values; accessing an unset dependency fails with a descriptive error.

## Quality requirements

- **TIME-QR-001:** Measure elapsed intervals using the injected Instant provider and verify deterministically without sleeps. Preserve existing regressions.

## Out of scope

Persistence, timer display, changes to client selection/running state, and network operations.

## Open questions

None blocking. User confirmed only one active task; remaining defaults above follow existing idempotent command behavior.

## Validation evidence

PASS (TIME-006, TIME-005, TIME-QR-001 mutable context refinement): offline Maven reactor packaging on Maven 3.9.16 / Corretto 25.0.2 built all modules and passed all 23 tests (13 client, 10 server), with no failures or skips. Tests verify empty context initialization, dependency replacement, existing tracking resolving the replacement provider on query and stop, and null setters preserving existing values. Launcher creates the context, sets time provider, then tracking, before scheduling the window. Supersedes factory construction in the historical records below. No manual desktop checks were run.

PASS (TIME-005 context resolution refinement, TIME-001/002/003/004, TIME-QR-001): `mvn -o -f appjava/pom.xml package` on Maven 3.9.16 / Corretto 25.0.2 built all modules and passed 23 tests (13 client, 10 server), no failures or skips. Tracking holds the context and calls getTimeProvider().now() at each time request. Context construction tests verify that the provider is available to the tracking factory and null dependencies/results are rejected. Existing deterministic interval tests and client regressions pass. This supersedes direct provider injection in the historical entry below.

PASS (TIME-001/002/003/004/005, TIME-QR-001, provider refinement): offline Maven reactor packaging with Maven 3.9.16 and Corretto 25.0.2 built all four modules and passed 22 tests (13 client, 9 server), without failures or skips. Controlled Instant tests retain interval and switching coverage; new checks verify context provider identity, injected elapsed time, null dependency rejection and LocalTimeProvider returning the current Instant. The launcher explicitly shares one provider with tracking and context. This supersedes the monotonic clock design below. No manual desktop checks were run.

PASS (TIME-001/002/003/004, TIME-QR-001): `mvn -o -f appjava/pom.xml package` with Maven 3.9.16 and Corretto 25.0.2 built all four modules and passed all 20 tests (13 client, 7 server), with no failures or skipped checks. Four new deterministic tests use an injected monotonic nanosecond supplier to verify accumulation, live queries, repeated commands, switching, inactive stops, invalid inputs, removal/re-addition and instance isolation. No UI changes or physical desktop checks.
