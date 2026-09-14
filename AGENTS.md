# My USSD Codes monorepo guide

## Scope

This repository contains three components:

- `android/` — Kotlin + Jetpack Compose Android app.
- `ios/` — Swift + SwiftUI iOS app.
- `data/` — canonical USSD catalog, JSON Schemas, and validator.

Keep changes scoped to the relevant component. The catalog source of truth is
`data/`; app seed files mirror it and are not the place to add catalog data.
Shared GitHub configuration and community documents live at the repository root.

## Repository-wide rules

- Write code, strings, documentation, and commit messages in English.
- Use Conventional Commits: lowercase imperative subject, no trailing period,
  maximum 72 characters.
- Do not add AI attributions, `Co-Authored-By` trailers, or generated-by
  footers to commits or pull requests.
- Do not change, add, remove, fetch, or push Git remotes unless explicitly
  requested.
- Reuse the shared root `README.md`, `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`,
  `SECURITY.md`, `LICENSE`, and `.github/`; do not recreate copies inside a
  component.

## Component guides

Before working inside a component, read its local guide. It contains the
component architecture, invariants, and commands that supplement this file:

- [`android/AGENTS.md`](android/AGENTS.md) — Android app.
- [`ios/AGENTS.md`](ios/AGENTS.md) — iOS app.
- [`data/AGENTS.md`](data/AGENTS.md) — shared catalog.

## Verification

Run the checks required by the component guide for every area you change. For
cross-component catalog changes, validate the catalog from the repository root:

```bash
node data/scripts/validate.js
```
