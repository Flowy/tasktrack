# 005 - Implementation tasks

- [x] Replace context factories with mutable dependency setters and sequential launcher setup (TIME-006). Verified replacement, missing dependencies and null rejection; reactor packaging passed all 23 tests.

- [x] Resolve time through context on each request; update launcher construction and regression fixtures (TIME-005). Verified deterministic tracking and factory construction via Maven reactor tests: all 23 pass.

- [x] Inject TimeProvider through ApplicationContext and launcher, implement LocalTimeProvider, and store Instant start times (TIME-005, TIME-QR-001). Verified with controlled Instant tests and reactor packaging: all 22 tests passed.

- [x] Implement single active task, accumulation and elapsed query (TIME-001/002/004). Verified with deterministic server tests.
- [x] Clear timing on removal and preserve instance isolation (TIME-003). Verified removal/re-add and independent instances.
- [x] Run Maven reactor checks and record results (TIME-QR-001). All 20 tests pass.
