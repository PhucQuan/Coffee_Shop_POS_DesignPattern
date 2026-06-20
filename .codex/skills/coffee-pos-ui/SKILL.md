---
name: coffee-pos-ui
description: Polish and extend the Java Swing UI for this coffee shop POS while preserving the existing warm visual language, AppTheme/AppShell conventions, and workflow-specific layouts. Use when adding or refactoring screens, dialogs, forms, lists, cards, dashboards, empty states, alerts, or renderer logic in `src/main/java/com/coffeeshop/presentation`.
---

# Coffee POS UI

## Overview

Use this skill to keep new UI work consistent with the current PurrCoffee desktop app instead of drifting into generic Swing layouts. Read the existing presentation files first, then make the smallest set of changes that improves clarity, hierarchy, and usability.

## Workflow

1. Inspect the current UI primitives before editing:
   - `src/main/java/com/coffeeshop/presentation/AppTheme.java`
   - `src/main/java/com/coffeeshop/presentation/AppShell.java`
   - the target screen in `src/main/java/com/coffeeshop/presentation/`
2. Preserve the established design language:
   - warm amber background
   - espresso text
   - copper primary actions
   - honey-gold highlights
   - rounded cards with either shadow or border, never both
3. Prefer extending existing helpers before inventing new UI abstractions:
   - `AppTheme.card(...)`
   - `AppTheme.roundedPanel(...)`
   - `AppTheme.button(...)`
   - `AppTheme.ghostButton(...)`
   - `AppTheme.muted(...)`
4. Keep screens role-oriented:
   - cashier screens should optimize speed and scanning
   - kitchen screens should optimize queue state and urgency
   - admin screens should optimize density, editability, and audit visibility
5. Verify the result visually in code terms:
   - action hierarchy is obvious
   - spacing follows the existing 8px rhythm
   - empty states are intentional
   - warnings and destructive actions are easy to distinguish
   - scrollable areas, split panes, and list renderers still feel consistent

## Rules

- Reuse `AppTheme` colors and typography unless there is a strong reason not to.
- Keep `Segoe UI` and the existing palette unless the project itself changes direction.
- Prefer `BorderLayout`, `GridBagLayout`, `GridLayout`, and `BoxLayout` combinations already used in the repo over custom layout experiments.
- When adding cards, choose one surface treatment:
  shadow card or bordered panel.
- When adding admin tooling, favor compact forms plus readable history/detail panes.
- When adding POS or kitchen interactions, favor large hit targets and immediate status feedback.
- Add short helper text where the workflow is not obvious, but avoid paragraph-heavy UI.
- If a panel manages real data, design the empty state and validation/error state, not only the success state.

## Screen Patterns

- Login:
  strong hero/title, clear primary action, compact helper copy, minimal clutter.
- POS:
  left selection/customization, center browsing, right bill/checkout summary.
- Kitchen:
  kanban-style queue, strong status color, obvious next action.
- Admin:
  list/detail or list/form split, management actions grouped at the edge, reporting blocks in cards.
- Dialogs:
  one dominant action, supporting status label, close action secondary.

## Read More

Read `references/ui-checklist.md` when you need the detailed component checklist for forms, lists, cards, states, and role-specific screens.
