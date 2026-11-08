# 006 - Technical plan

Status: Planned

## Decisions

Use existing Java 25 Swing client and TrackingApi. No server changes required. MainWindow supplies the menu callback; ManipulationPanel owns presentation, not task storage.

## Design

TaskSettingsPanel holds the list, name field and actions inside an owned modal JDialog. TrackingState delegates mutations, reconciles selection and remembers the last successfully started task to recognize its removal. TrackingPanel refreshes its combo model under a listener guard and renders empty/running state. Preserve existing selection command behavior.

## Delivery and validation

Automated Swing/state tests cover SET-001 through SET-004. Run Maven reactor package and module boundary check for SET-QR-001. Physical pointer interaction and desktop appearance require manual verification.

## Risks and dependencies

Task removal destroys elapsed history under existing API rules. Combo model rebuilds emit selection events and must be guarded.
