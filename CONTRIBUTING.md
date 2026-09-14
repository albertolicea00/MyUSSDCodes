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

Use [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/): a
lowercase imperative subject, no trailing period, and at most 72 characters.

## Code of Conduct

By participating you agree to the [Code of Conduct](CODE_OF_CONDUCT.md).
