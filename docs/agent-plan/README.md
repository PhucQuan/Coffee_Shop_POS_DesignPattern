# Coffee Shop POS - BMAD/GSD Agent Plan

This folder contains the working documents for coordinating AI agents on the Coffee Shop POS final project.

The structure combines:

- BMAD-style role separation: PM, Analyst, Architect, Developer, QA, UX/Docs.
- GSD-style phase loop: Discuss -> Plan -> Execute -> Verify -> Ship.

Use this folder as the source of truth before asking any agent to modify code.

## Recommended workflow

1. Read `CONTEXT.md` and `STATE.md`.
2. Pick one phase from `BACKLOG.md`.
3. Give the assigned agent the exact task block from `TASKS.md`.
4. Agent edits only the files listed in the task scope.
5. Run the verification command from the task.
6. Update `STATE.md` with what changed and what remains.

## Current project path

`C:\Users\DELL\Documents\Codex\2026-06-05\nh-l-m-t-i-cho\outputs\coffee-shop-pos-java`

## Current verification command

```bat
test.bat
```

For GUI smoke test:

```bat
run.bat
```
