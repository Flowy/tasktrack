# 002 - Tracking API

Status: Implemented and automatically verified

## Problem and outcome

Expose the existing TrackingState workflow through an implementable .api boundary.

## Confirmed requirements

- Provide an API for task start, task stop, and task list operations; all UI state belongs to the client.
- Running state and selection belong to the client. The client sends the selected task with each start/stop call.

## Proposed assumptions

- Follow existing package boundaries: .api is a Java package in appjava.
- Preserve selection and in-memory behavior. Stop means the existing pause operation.

## Functional requirements

- **API-001:** A public interface exposes task listing, start(String task), and stop(String task) without Swing types.
- **API-002:** LocalTrackingApi in com.flowyk.tasktrack.server provides immutable sample tasks and idempotent start/stop with validated task arguments. TrackingState holds client selection and running state and delegates commands with that selection. Running state changes only after a successful command; a thrown exception leaves client state unchanged.
- **API-003:** ApplicationContext belongs to the .api package and contains only injected API dependencies. TaskTrackApp constructs LocalTrackingApi and the context, then passes the full context to MainWindow. MainWindow obtains the tracking API from the context for TrackingPanel and TrackingState. Client components require injection and never construct a concrete API implementation; UI behavior is preserved.

## Quality requirements

- **API-QR-001:** Compile on Java 25 and preserve existing state/window regression checks.

## User scenarios and acceptance criteria

An implementer can supply the interface to TrackingPanel. Listing tasks and clicking Start/Pause use that implementation. Selection stays in the client and selection changes alone make no API commands. Repeated stop calls leave tracking stopped without clearing selection. Invalid selection leaves state unchanged.

## Out of scope

Network endpoints, persistence, task editing, elapsed time. Separate Maven/JPMS build modules were subsequently authorized in feature 003.

## Open questions

None blocking; package organization follows the current project convention.

## Validation evidence

Historical verification before client selection correction: PASS (API-001, API-002, API-003, API-QR-001): compiled all main/test Java sources with Corretto 25.0.2 (`javac --release 25 -encoding UTF-8`) into appjava/target/api-check using cached JUnit 4.13.2 and Hamcrest 1.3. Ran TrackingApiTest, TrackingStateTest and MainWindowTest via JUnitCore: OK (13 tests). New checks exercise the interface contract and an alternate injected implementation with initially running, non-default selection. Existing desktop stacking/focus and physical input checks from feature 001 remain pending; no new OS behavior is introduced. Maven packaging was not run.


PASS (selection ownership correction; API-001/002/003, API-QR-001): Java 25 compilation and all 13 JUnit checks passed. Recording API verifies selection alone makes no commands, start receives Second, changing selection while running preserves state, and stop then receives First. Existing client selection and window regression tests pass. Maven packaging and desktop manual checks were not run.

PASS (client running-state ownership; API-001/002/003, API-QR-001): compiled all Java sources on Corretto 25.0.2 with --release 25 and ran TrackingApiTest, TrackingStateTest and MainWindowTest via JUnitCore 4.13.2: all 14 tests passed. Recording implementation has no running state; button labels and command sequence verify client ownership. A shared API leaves new client state stopped; failed commands preserve the previous client running flag. Earlier entries describe superseded contracts. Maven packaging and physical desktop checks were not run.

PASS (application context; API-003, API-QR-001): compiled main/test sources with Corretto 25.0.2 (--release 25 -encoding UTF-8); all 14 JUnitCore checks passed. The recording API is provided by ApplicationContext to MainWindow and exercised through its actual tracking panel. Existing state/window regressions use explicitly supplied context dependencies. No client class constructs a concrete API. Maven packaging and physical desktop checks remain unrun.

PASS (API context boundary; API-003, API-QR-001): Java 25 compilation and all 14 JUnitCore tests passed. ApplicationContext is in .api with no LocalTrackingApi reference; TaskTrackApp constructs the implementation. The composition check passes the full context into MainWindow and verifies commands reach its supplied recording API. Earlier context-location entries are historical. Maven packaging and physical desktop checks were not run.

PASS (server package move; API-002/003, API-QR-001): moved LocalTrackingApi to com.flowyk.tasktrack.server and updated application/test imports. Compiled all main/test sources with Corretto 25.0.2 (--release 25 -encoding UTF-8); all 14 JUnitCore checks passed. Maven packaging and physical desktop checks were not run.
