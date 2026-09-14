# My USSD Codes

[![Validate catalog](https://github.com/albertolicea00/MyUSSDCodes/actions/workflows/validate.yml/badge.svg)](https://github.com/albertolicea00/MyUSSDCodes/actions/workflows/validate.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

My USSD Codes is an open-source suite for browsing, organizing, importing, and
running USSD codes on Android and iOS. It includes a shared, validated catalog
of carrier and GSM codes that both apps can use as their source of truth.

## Monorepo layout

| Directory | Purpose |
| --- | --- |
| [`android/`](android/) | Kotlin and Jetpack Compose Android app |
| [`ios/`](ios/) | Swift and SwiftUI iOS app |
| [`data/`](data/) | USSD catalog, JSON Schemas, and validation script |
| [`.github/`](.github/) | Shared issue forms, pull-request template, and CI |

## What the apps do

Both apps offer three main areas:

1. **Sections** — browse codes by category or personal group.
2. **All codes** — search every available code in one list.
3. **Settings** — import collections, manage local data, and view app details.

Users can also create custom codes with variables such as `{number}`. Codes
marked as dangerous show a warning before dialing. Android uses `ACTION_DIAL`
and iOS opens a `tel:` URL, so the user always confirms the final call.

## The shared catalog

[`data/`](data/) is the canonical catalog. It contains JSON collections,
schemas, and a dependency-free validator. When changing catalog data, validate
it from the repository root:

```bash
node data/scripts/validate.js
```

Each code needs a verifiable source. Codes that can charge money, lock a SIM,
or erase settings must be marked as dangerous and explain the risk.

## Verify a consuming app

Apps that bundle a copy of a collection can use the
[USSD catalog verification template](templates/ussd-catalog-verification/).
It provides a dependency-free Node.js script and a weekly GitHub Actions
workflow that compare the app's local dial strings with this published catalog.
This catches catalog drift while allowing app-specific names and descriptions.

## Build the apps

```bash
# Android
cd android && ./gradlew assembleDebug

# iOS
cd ios && xcodebuild -project MyUSSDCodes.xcodeproj -scheme MyUSSDCodes \
  -destination 'platform=iOS Simulator,name=iPhone 16' build
```

See the component READMEs for platform-specific details:
[Android](android/README.md), [iOS](ios/README.md), and
[catalog](data/README.md).

## Repository migration

This project used to be maintained as three separate repositories:

- `MyUSSDCodes-apk` — Android app
- `MyUSSDCodes-ios` — iOS app
- `MyUSSDCodes-collection` — shared USSD catalog

They are now one monorepo. Their original commit histories were retained under
their corresponding directories. Links, contribution guidance, issue forms, and
CI now live here; use the paths above instead of the retired repository URLs.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request. All code,
strings, and documentation are written in English and commits follow
Conventional Commits.

## License

[MIT](LICENSE) © 2026 Alberto Licea
