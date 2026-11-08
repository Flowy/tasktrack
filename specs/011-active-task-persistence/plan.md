# 011 - Technical plan

Status: Implemented and automatically verified

## Decisions
Use existing Java 25 and Gson with strict JSON parsing. Store timestamp as ISO-8601 text. Follow task-list atomic replacement and temporary-file cleanup.

## Design
Add JsonActiveTask and a path-injected tracking constructor. Load after the task list. Expose the active name through TrackingApi for client initialization. Save the next active state before completing history; roll back the active file if history fails. Task removal saves the list first, so a stale active record for a removed name is ignored on restart.

## Delivery and validation
Deterministic server restart tests cover ACTIVE-001/002/004; client initialization test covers ACTIVE-003. Run Maven reactor tests.

## Risks and dependencies
Multiple files are not a single transaction: process failure between writes can leave history and active state inconsistent. Existing single-writer assumption and filesystem atomic-move requirement apply.
