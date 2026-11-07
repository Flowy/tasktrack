# 002 - Technical plan

## Decisions and design

Use Java 25 and the existing .api package. TrackingApi exposes getTasks(), start(String task), stop(String task). Selection and running state are exclusively client state.

TrackingState wraps an injected TrackingApi, validates and stores the selected task and owns a boolean running flag, initially false, and forwards that task on start/pause/stop. Its default constructor supplies LocalTrackingApi, which owns only the immutable sample list and validates command arguments. TrackingPanel wraps the injected API in TrackingState. Selection defaults to the first task and changes preserve running state; each command sends the current selection, including after changing selection while running. Update the client running flag after successful start/stop; exceptions leave it unchanged. No per-task timing is introduced. Calls remain synchronous on the Swing EDT.

## Delivery and validation

Verify API-001/002 through the interface for valid/invalid task arguments, immutable list, idempotence and instance independence. Verify API-003 with a recording implementation: default selection, no command on selection, and exact tasks passed to start/stop. Run existing state/window regressions (API-QR-001). Desktop checks from feature 001 remain pending.
