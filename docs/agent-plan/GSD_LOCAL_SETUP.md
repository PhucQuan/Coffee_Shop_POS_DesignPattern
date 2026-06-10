# GSD Local Setup

GSD Core has been cloned and locally installed for this project.

## Chosen tool

Use GSD Core as the main workflow tool.

Reason:

- It is lighter than BMAD for a school project.
- It supports Codex directly.
- Its phase loop is easy to map to final-project work:
  Discuss -> Plan -> Execute -> Verify -> Ship.

BMAD-style role separation is still represented in `TASKS.md`.

## Installed paths

Cloned source:

```text
work/tools/gsd-core
```

Local Codex install inside project:

```text
outputs/coffee-shop-pos-java/.codex
```

Installed core skills:

- `gsd-help`
- `gsd-new-project`
- `gsd-phase`
- `gsd-discuss-phase`
- `gsd-plan-phase`
- `gsd-execute-phase`
- `gsd-surface`
- `gsd-update`

## Important note

Codex may need a fresh session opened from the project folder before it detects local `.codex/skills`.

Project folder:

```text
C:\Users\DELL\Documents\Codex\2026-06-05\nh-l-m-t-i-cho\outputs\coffee-shop-pos-java
```

## Suggested commands after reload

Start with:

```text
$gsd-help
```

Then for the existing Coffee Shop POS project:

```text
$gsd-phase Phase 2 - Admin CRUD and UI polish
```

or:

```text
$gsd-plan-phase 1
```

## Current manual fallback

If GSD commands are not visible in Codex yet, use these files directly:

- `CONTEXT.md`
- `STATE.md`
- `BACKLOG.md`
- `TASKS.md`
- `VERIFY.md`

Verification command:

```bat
test.bat
```
