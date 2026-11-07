# 003 - Technical plan

Status: Implemented and verified

## Decisions

Use appjava as Maven parent/aggregator with api, client, server and app children. Retain Java 25 and JUnit Jupiter. Each child has module-info.java. Use qualified exports from client/server to tasktrack.app. java.base remains implicit; Swing modules require java.desktop.

## Design

Move sources without changing packages. Move TaskTrackApp to app and keep constructor injection. Client tests are patched into the client module by Maven; move state tests into the client package and API/client tests out of the API package to avoid split packages. Extract implementation contract test into server. Introduce an API-only client fixture.

## Delivery and validation

Compile all modules and run existing JUnit tests (MOD-001/002/003, MOD-QR-001). Add a repeatable compiler check for forbidden edges using javac on the module path, including attempted requires bypass of qualified exports. Run Maven if available; record any tooling limitations accurately.

## Risks and dependencies

Desktop tests require a graphical session. Existing uncommitted user changes must remain intact. Launch must use the module path to enforce boundaries.
