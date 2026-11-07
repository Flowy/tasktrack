# 005 - Technical plan

Status: Implemented and verified

## Decisions

Keep Java 25 and existing module boundaries. Store one current task and Instant start time, plus a map of completed durations. API TimeProvider.now() returns Instant, exposed by ApplicationContext.getTimeProvider(). LocalTimeProvider in server delegates to Instant.now(). Inject ApplicationContext into InMemoryTracking and resolve the provider when time is requested. The launcher initializes the context first, then assigns each dependency with setters. Use Duration.between for intervals. Expose getElapsedTime(String) returning Duration on InMemoryTracking only.

## Design

InMemoryTracking receives and stores ApplicationContext rather than TimeProvider. Each time request calls context.getTimeProvider().now(). ApplicationContext is a mutable dependency holder with no-argument construction, setTimeProvider and setTrackingImplementation. Remove factory construction. The launcher creates the context first, then sets LocalTimeProvider and new InMemoryTracking(context), then publishes it to Swing. Setters reject null; getters fail with IllegalStateException if unset. Client tests explicitly set their API doubles. Provider replacements affect existing tracking instances on the next time request.

Validate commands before modifying state. Start is idempotent for the active task; switching settles the previous interval at the same timestamp used to start the next. Stop only settles the named active task. Removal clears active state and stored duration. Queries add the current interval without mutation. Existing client selection semantics remain unchanged.

## Delivery and validation

Test accumulation, switching, repeated commands, inactive stops, invalid inputs, removal and isolation (TIME-001/002/003/004). Use controlled Instants without sleeps (TIME-QR-001). Verify provider injection through context and local provider implementation (TIME-005). Run the offline Maven reactor package with JDK 25.

## Risks and dependencies

Timing remains process-local and unsynchronized, matching existing synchronous usage. Client selection alone does not send a start command; changing selection does not switch the server timer until start is called.
Instant uses wall-clock time; system clock adjustments are reflected in Duration.between, including negative intervals if the clock moves backward. No additional clock-correction policy is introduced.
