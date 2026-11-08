# 008 - Technical plan

Status: Implemented and automatically verified

## Decisions

Keep Java 25 and Swing. Add TrackingApi.isTracking() returning whether a task is active, with no clock read. Expose it through TrackingState separately from its existing local running flag to preserve command semantics. Keep palette in Layout.

## Design

InMemoryTracking returns currentTask != null. TrackingPanel captures its normal theme background at construction and selects the active palette color during renderState, also updating the selector background. Existing transparent renderer and timer control show the row color. No new polling timer.

## Delivery and validation

Test server transitions including switching, inactive stop and removals (COLOR-001); API-only UI fixture tests selection independence and authoritative state, start/stop colors and empty state (COLOR-002/003). Run offline Maven reactor package (COLOR-QR-001).

## Risks and dependencies

External state changes are shown on the existing periodic refresh or next UI action. Physical appearance requires manual verification.
