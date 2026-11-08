# 010 - Technical plan

Status: Implemented; validation recorded in spec.md

## Decisions
Use JSON with Gson 2.13.1 rather than implementing a parser. Store a JSON array of strings. Keep interval CSV separate. Gson adds one runtime module; document its module-path requirement.

## Design
JsonTaskList owns validation and UTF-8 encoding, reads only on startup, and saves through a temporary sibling and atomic replacement. Fail explicitly if atomic replacement is unsupported. Production InMemoryTracking uses both stores. Retain the existing history-only test constructor; add an explicit two-path constructor for persistence tests. Save before committing mutations, including active-task removal.

## Delivery and validation
LIST-001/002/003: round-trip Unicode, quotes, newlines, order, empty list, invalid data and write failures. LIST-QR-001: temporary directories and reactor tests.

## Risks and dependencies
Synchronous file access may block briefly. No locking across processes. Gson must be on the module path when launching manually.
