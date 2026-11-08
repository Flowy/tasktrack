# 007 - Technical plan

Status: Implemented and automatically verified

## Decisions

Keep Java 25 and Swing. Expose the existing server Duration query through TrackingApi, without a client dependency on server. Retain the borderless JButton for keyboard and action support, replacing its symbols with elapsed text. Use a compact 56-pixel control and a monospaced font. Format total whole minutes with an m suffix; no seconds or hours. Keep backend Duration accumulation unchanged to avoid discarding partial minutes.

## Design

TrackingState delegates selected-task duration queries to the API, returning zero for empty selection. TrackingPanel formats whole minutes and refreshes using a Swing Timer on the event thread. Start/stop the refresh timer in addNotify/removeNotify. Selection and task-settings refresh update immediately. Preserve existing command semantics.

## Delivery and validation

Update API test doubles and existing window expectations. Add deterministic display checks for elapsed query, selection, formatting above 24 hours, empty selection and timer lifecycle (DISPLAY-001/002/003). Run offline Maven reactor package (DISPLAY-QR-001).

## Risks and dependencies

Existing wall-clock adjustments and selection/running-state semantics remain as specified in 005. Physical desktop appearance requires manual verification. Earlier timer-display exclusions in 005/006 are superseded for this feature.

## Tick scheduling refinement

Use a Swing timer with a recalculated initial delay after each render, aligned to elapsed-time boundaries: five minutes for all durations. Include Duration nanoseconds when computing the delay. Immediate action renders reschedule the timer; attach starts it and detach stops it. The minutes-only request resolves hours formatting.

