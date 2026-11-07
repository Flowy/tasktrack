# 002 - Technical plan

## Decisions and design

Use Java 25 and the existing .api package. TrackingApi exposes getTasks(), start(String task), stop(String task). Selection and running state are exclusively client state.

TrackingState wraps an injected TrackingApi, validates and stores the selected task and owns a boolean running flag, initially false, and forwards that task on start/pause/stop. ApplicationContext in com.flowyk.tasktrack.api accepts a TrackingApi dependency and has no concrete implementation reference or default constructor. TaskTrackApp constructs LocalTrackingApi and injects it into ApplicationContext, then calls MainWindow.createWindow(context). MainWindow extracts the API for TrackingPanel and TrackingState. LocalTrackingApi resides in com.flowyk.tasktrack.server, following the existing package-based module convention. TaskTrackApp imports and constructs it; the API context remains independent of the implementation. Remove client convenience constructors that create implementations. LocalTrackingApi owns only the immutable sample list and validates command arguments. TrackingPanel wraps the injected API in TrackingState. Selection defaults to the first task and changes preserve running state; each command sends the current selection, including after changing selection while running. Update the client running flag after successful start/stop; exceptions leave it unchanged. No per-task timing is introduced. Calls remain synchronous on the Swing EDT.

## Delivery and validation

Verify API-001/002 through the interface for valid/invalid task arguments, immutable list, idempotence and instance independence. Verify API-003 with a recording implementation: default selection, no command on selection, and exact tasks passed to start/stop. Run existing state/window regressions (API-QR-001). Desktop checks from feature 001 remain pending.

Verify API-003 by passing a recording API from ApplicationContext through MainWindow and exercising selection/start/stop; adapt existing tests to explicit injection.
