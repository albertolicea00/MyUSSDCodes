# Contributing to My USSD Codes

This monorepo contains the Android app, iOS app, and the shared USSD catalog.

## Where changes go

- `android/` — Kotlin + Jetpack Compose Android app.
- `ios/` — Swift + SwiftUI iOS app.
- `data/` — source-of-truth JSON catalog, schemas, and validator.

Keep pull requests focused on one component whenever possible. Code, strings,
and documentation are written in English.

## Android

Open `android/` in Android Studio, or run:

```bash
cd android && ./gradlew assembleDebug && ./gradlew lint
```

State changes go through `AppViewModel.update {}`. Keep dialing on
`ACTION_DIAL`; users must confirm calls themselves.

## iOS

Open `ios/MyUSSDCodes.xcodeproj` in Xcode 16 or newer. The app supports iOS
17+ and uses file-system-synchronized groups. Keep dialing through `tel:` URLs
and include screenshots with UI changes.

## Catalog data

Edit collections under `data/codes/`, follow the schemas in `data/schema/`, and
run:

```bash
node data/scripts/validate.js
```

Code and collection IDs are unique kebab-case values. Every code needs a
verifiable `source`; risky or paid operations must be marked `dangerous` with
an explanation in `notes`. Keep `data/codes/index.json` and collection versions
up to date.

## Commit style

Use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) with
this format:

```text
type(scope): imperative summary
```

Use the component directory as the scope for component changes:

| Scope | Use for |
| --- | --- |
| `android` | Changes under `android/` |
| `ios` | Changes under `ios/` |
| `data` | Catalog collections, schemas, or validation tooling under `data/` |
| `repo` | Shared root documentation, GitHub configuration, or monorepo tooling |

Common types are `feat`, `fix`, `docs`, `chore`, `refactor`, `test`, and `ci`.
Keep the summary lowercase and imperative, omit its final period, and keep the
subject to 72 characters or fewer.

```text
feat(android): add group reordering
fix(ios): encode hash in dial URL
feat(data): add cubacel balance code
fix(data): correct collection version
docs(repo): clarify monorepo migration
ci(repo): validate catalog on pull requests
```

Prefer one component per commit. If a change intentionally spans components,
split it into focused commits when practical; otherwise use the most relevant
scope and explain the cross-component impact in the commit body.

## Code of Conduct

By participating you agree to the [Code of Conduct](CODE_OF_CONDUCT.md).
