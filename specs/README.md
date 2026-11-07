# Specification workflow

Specifications describe observable behavior; technical plans describe how to deliver it.
Markdown in this repository is the source of truth. No external specification tool is required.

## Lifecycle

1. **Draft:** copy `specs/_template/` into the next `NNN-feature-name/` directory. Describe the problem, scenarios, numbered requirements, acceptance criteria, exclusions, and questions.
2. **Ready:** review the proposal with the user, resolve blocking questions, and record the agreed scope in the spec. A document's existence does not constitute approval.
3. **Planned:** fill in `plan.md` with architecture, decisions, risks, and validation; break the work into traceable items in `tasks.md`.
4. **Implementing:** implement one coherent slice at a time. Verify behavior against acceptance criteria, with automated tests where useful and manual desktop checks where needed.
5. **Complete:** record validation commands, results, manual checks, and limitations; finish only when all in-scope acceptance criteria pass.

Use these exact status labels in feature specs. Update specs before implementing an agreed behavior change.
Use stable identifiers such as `FR-001` within a feature; qualify references with the feature number outside its folder (for example `001/FR-001`).

## Review checklist

- Are user requirements distinguished from proposed defaults?
- Can each requirement be observed and verified?
- Are important failure cases and scope boundaries explicit?
- Are blocking questions resolved before dependent implementation?
- Does each implementation task refer to requirements and a verification method?
- Does completion include actual evidence rather than only checked boxes?
