# My USSD Codes — Android

Android app to browse, organize and run USSD codes. Built with Kotlin and Jetpack Compose.

> **This is not a monorepo.** Related repositories:
>
> | What | Repository |
> | ---- | ---------- |
> | Code catalog (importable collections) | [my-ussd-codes](https://github.com/albertolicea00/my-ussd-codes) |
> | iOS app | [my-ussd-codes-ios](https://github.com/albertolicea00/my-ussd-codes-ios) |

## Features

The app is organized in three sections (bottom navigation):

1. **Sections** — codes grouped by category and by the user's own fully-customizable groups.
2. **All codes** — a flat list of every code with instant search (name, code, category, tags).
3. **Settings** — import collections from the catalog (URL or pasted JSON), reset data, and app info.

Beyond the built-in catalog, users can **create their own codes** with a bit of logic: a code may declare **variables** (placeholders like `{number}`) and the app asks for each value right before dialing. Codes marked as **dangerous** (SIM locks, charges) show a warning first.

Dialing uses `ACTION_DIAL`, so the code is only pre-filled in the system dialer — the final call tap is always the user's, and no call permission is required.

## Tech stack

- Kotlin 2.0 · Jetpack Compose (Material 3) · Navigation Compose
- `kotlinx.serialization` for the collection format
- Plain JSON file persistence (no database) — the dataset is tiny
- Min SDK 26, target SDK 35

## Project structure

```
app/src/main/java/com/albertolicea00/myussdcodes/
├── MainActivity.kt
├── AppViewModel.kt              # Single source of truth (StateFlow<AppData>)
├── data/
│   ├── model/Models.kt          # UssdCode, CodeVariable, CodeGroup, CodeCollection, AppData
│   └── CodeStore.kt             # JSON persistence + bundled seed catalog
├── util/UssdDialer.kt           # {placeholder} substitution + ACTION_DIAL
└── ui/
    ├── AppRoot.kt               # Scaffold, bottom bar, NavHost
    ├── theme/Theme.kt
    ├── components/Components.kt # CodeCard, CodeList, RunCodeDialog
    └── screens/                 # Sections, AllCodes, Settings, CodeEditor
```

The seed catalog lives in `app/src/main/assets/collections/gsm-standard.json` and is a verbatim copy of the catalog repository's collection.

## Building

```bash
./gradlew assembleDebug     # build a debug APK
./gradlew installDebug      # install on a connected device
```

Or open the project in Android Studio (Ladybug or newer) and run.

## Importing collections

Settings → Import: paste a collection URL from the [catalog](https://github.com/albertolicea00/my-ussd-codes), e.g.

```
https://raw.githubusercontent.com/albertolicea00/my-ussd-codes/main/codes/gsm-standard.json
```

The collection format is documented in the catalog repository (JSON Schema included).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). New USSD codes belong in the [catalog repository](https://github.com/albertolicea00/my-ussd-codes), not here.

## Disclaimer

USSD codes are executed by your carrier. Codes vary by country, carrier and plan; some may be paid services. Double-check a code before running it.

## License

[MIT](LICENSE) © 2026 Alberto Licea
