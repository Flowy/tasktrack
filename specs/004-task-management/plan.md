# 004 - Technical plan

## Decisions

Retain Java 25 and the current module boundaries. Use explicit abstract API methods so implementations supply task management behavior.

## Design

Initialize InMemoryTracking with List.of and return the stored immutable list directly. For edits, copy into a temporary ArrayList, edit the copy, then assign List.copyOf to the field. Validate names before editing; check membership before adding. Removal of an absent name is harmless. Update client test doubles for the extended interface. This supersedes API-002's fixed sample-list behavior; initial samples remain unchanged.

## Delivery and validation

Server contract tests exercise TASK-001/002/003 through TrackingApi, including invalid inputs, ordering, snapshots, command validation and independent instances. Run the Maven reactor tests/package for TASK-QR-001.

## Risks and dependencies

This extends the interface, requiring implementations to implement both methods. Client selection reconciliation remains outside this API-only change.
