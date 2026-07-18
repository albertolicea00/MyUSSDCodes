# Contributing to My USSD Codes — Android

Thanks for your interest! Contributions of all sizes are welcome.

## Where things go

- **App bugs and features** → this repository.
- **New USSD codes / collections** → the [catalog repository](https://github.com/albertolicea00/my-ussd-codes). The app only bundles a seed copy of the GSM standard collection.
- **iOS work** → [my-ussd-codes-ios](https://github.com/albertolicea00/my-ussd-codes-ios).

## Getting started

1. Fork and clone the repository.
2. Open it in Android Studio (Ladybug or newer) or build from the CLI:

   ```bash
   ./gradlew assembleDebug
   ```

3. Create a branch: `feat/group-reordering`, `fix/dial-uri-encoding`.

## Guidelines

- Kotlin + Jetpack Compose, Material 3. Follow the existing package layout (`data/`, `ui/`, `util/`).
- All code, resources and docs in **English**.
- State changes go through `AppViewModel.update {}` so persistence stays consistent.
- Keep dialing on `ACTION_DIAL` — the user must always confirm the call themselves.
- Run `./gradlew lint` before opening a PR.
- Keep PRs focused: one logical change per PR.

## Commit style

[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/), lowercase imperative subject, ≤ 72 chars:

```
feat: add group reordering
fix: encode hash in dial uri
docs: document import format
refactor: extract run code dialog
chore: bump compose bom
```

## Code of Conduct

By participating you agree to our [Code of Conduct](CODE_OF_CONDUCT.md).
