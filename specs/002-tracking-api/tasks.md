# 002 - Implementation tasks

- [x] Remove selection from API and add task arguments to start/stop (API-001).
- [x] Keep selection in TrackingState and delegate task-specific calls to a local/injected implementation (API-002, API-003).
- [x] Verify exact task arguments, selection independence, and existing regressions; record evidence (API-QR-001).
- [x] Remove API running-state access, keep it in the client and verify command success/failure behavior (API-001, API-002, API-003, API-QR-001).
- [x] Supply the API from ApplicationContext beside TaskTrackApp; require injection through the client and verify window composition plus regressions (API-003, API-QR-001).
- [x] Move context to .api, construct concrete dependencies in TaskTrackApp, pass full context to window and verify composition/regressions (API-003, API-QR-001).
- [x] Move LocalTrackingApi into .server, update application/test imports and compile/run regressions (API-002, API-003, API-QR-001).
