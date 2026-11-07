# 003 - Java module boundaries

Status: Implemented and automatically verified

## Problem and outcome

Enforce application dependency boundaries using JPMS rather than package conventions.

## Confirmed requirements

- Client and server may depend only on API among application modules; API depends on neither.
- A separate launcher wires client and server together.

## Proposed assumptions

- Retain Java 25, Maven and Swing; use four Maven artifacts with matching named Java modules.

## User scenarios and acceptance criteria

Legal application imports compile on the module path. Direct client/server imports and API imports of implementation packages fail compilation.

## Functional requirements

- **MOD-001:** API has no application dependencies. Client and server each require only API among application modules.
- **MOD-002:** Client and server export their packages only to the launcher, which constructs and injects dependencies.
- **MOD-003:** Client tests use API doubles; server contract checks reside with server.

## Quality requirements

- **MOD-QR-001:** Preserve existing behavior and regression checks on Java 25. Document modular build and launch commands and verify prohibited imports fail.

## Out of scope

Network services, persistence, new UI behavior, and protection against explicit JVM access overrides.

## Open questions

None blocking.

## Validation evidence

PASS (MOD-001/002/003, MOD-QR-001): `mvn -o -f appjava/pom.xml package` with Maven 3.9.16 and Corretto 25.0.2 produced all four modular JARs. All 14 Jupiter tests passed (13 client, 1 server), with no failures or skipped tests. Client tests have no server dependency.

PASS (MOD-001/002): `powershell -NoProfile -ExecutionPolicy Bypass -File appjava/check-boundaries.ps1 -Javac C:/Workspace/bin/java/corretto/jdk25.0.2_10/bin/javac.exe` compiled all legal production modules and rejected eight forbidden probes. These cover client/server cross-imports and API imports of either implementation, both with the production descriptor and with an attempted additional requires directive. API back-dependencies create cycles; qualified exports reject client/server access even with requires added.

PASS (MOD-QR-001): `java --dry-run --module-path <four generated JARs> --module tasktrack.app/com.flowyk.tasktrack.TaskTrackApp` resolved the modular launcher successfully. Full launch/OS stacking checks were not repeated. Existing Swing regression tests ran in the graphical session. `git diff --check` passed.

The first offline packaging attempt selected Maven's default jar plugin 3.5.0 and failed because dependencies were not cached. Restoring the original project's explicit jar plugin version 3.4.2 resolved this; the final offline build passed. Qualified exports may produce a module-not-found warning while client/server are built ahead of the launcher; final module resolution passed.
