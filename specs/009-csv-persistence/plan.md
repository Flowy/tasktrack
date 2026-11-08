# 009 - Technical plan

Status: Implemented and automatically verified

## Decisions
Use Java 25 NIO and UTF-8 without extra dependencies. A server CsvTaskHistory class owns file encoding and OS directory selection. InMemoryTracking uses the default history path in production and a package-private Path constructor for isolated tests.

## Design
finishCurrentTask writes before changing elapsed/current state. Stop and task switching already converge here. Existing active removal remains discard-only. IOException is wrapped with the file path in UncheckedIOException. No UI changes.

## Delivery and validation
CSV-001/002: deterministic stop/switch tests, exact escaped bytes, append through a new instance and header uniqueness. CSV-003: pure directory selection tests for all platform rules. CSV-004: invalid destination test verifies active timer and elapsed remain intact. CSV-QR-001: adapt existing tracking tests to temporary paths and run offline Maven reactor package.

## Risks and dependencies
Synchronous small writes may briefly block the caller. Disk failures propagate through existing command handling; no new dialog. Partial writes after an I/O failure are not transactionally recoverable. Multiple writers are outside scope.


## Documents location refinement
Use user.home/Documents/TaskTrack for the user-facing CSV, replacing OS application data selection. Copy this user's existing history without overwriting an existing destination; retain the source as backup. No automatic migration in the application. Verify default path and rerun reactor checks.
