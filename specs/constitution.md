# Product and engineering principles

## Confirmed product direction

1. TaskTrack tracks tasks in a desktop application.
2. Its window is minimalistic and stays above other ordinary application windows.
3. The implementation is primarily Java.
4. Development starts from specifications before application code.

## Working principles

- Keep the core task workflow small and easy to understand.
- Express requirements as observable outcomes, without prematurely committing to a toolkit.
- Separate task behavior from UI and storage details so it can be verified independently.
- Treat persistence, error handling, and platform limitations explicitly when they enter scope.
- Keep dependencies justified and technical decisions recorded in feature plans.
- Preserve user work when making changes to the repository.

## Changes

Change confirmed product direction only through an explicit user decision.
Update affected feature specs and plans when a principle changes.
