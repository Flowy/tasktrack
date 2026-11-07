# TaskTrack development instructions

## Specification-driven workflow

- Read `specs/constitution.md` and the relevant feature specification before changing application code.
- For new behavior, draft or update `specs/NNN-feature-name/spec.md` first, using `specs/_template/`.
- Keep user requirements separate from proposed assumptions. Resolve material open questions with the user before implementing dependent behavior; do independent work meanwhile.
- Record technical decisions in the feature's `plan.md` and actionable work in `tasks.md` before implementation.
- Keep requirement IDs stable. Reference them in tasks and meaningful tests or manual verification records.
- Update specifications when agreed behavior changes, and keep task checkboxes and verification evidence accurate.
- Do not describe proposed, unimplemented, or unverified behavior as complete.

## Scope

The product is a primarily Java desktop task tracker with a minimal, always-on-top window.
Do not introduce a web application, cloud service, account system, or unrelated features without a product requirement.
