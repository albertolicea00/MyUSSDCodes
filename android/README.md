# My USSD Codes  [Android]

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](../LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/Compose-Material%203-4285F4)](https://developer.android.com/jetpack/compose)

Android app to browse, organize and run USSD codes. Built with Kotlin and Jetpack Compose.

> 📦 **Monorepo components:**
>
> | What | Location |
> | ---- | ---------- |
> | 📋 Code catalog (collections) | [`../data/`](../data/) |
> | 📱 iOS app | [`../ios/`](../ios/) |

> **Migration note:** Android, iOS, and the catalog were previously separate
> repositories. They now live together in the [My USSD Codes monorepo](../).

## ✨ Features

The app is organized in three sections (bottom navigation):

1. **Sections** — codes grouped by category and by the user's own fully-customizable groups.
2. **All codes** — a flat list of every code with instant search (name, code, category, tags).
3. **Settings** — import collections from the catalog (URL or pasted JSON), reset data, and app info.

Beyond the built-in catalog, users can **create their own codes** with a bit of logic: a code may declare **variables** (placeholders like `{number}`) and the app asks for each value right before dialing. Codes marked as **dangerous** (SIM locks, charges) show a warning first.

Dialing uses `ACTION_DIAL`, so the code is only pre-filled in the system dialer — the final call tap is always the user's, and no call permission is required.

## 🛠 Tech stack

- Kotlin 2.0 · Jetpack Compose (Material 3) · Navigation Compose
- `kotlinx.serialization` for the collection format
- Plain JSON file persistence (no database) — the dataset is tiny
- Min SDK 26, target SDK 35

## 📁 Project structure

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

The seed catalog lives in `app/src/main/assets/collections/gsm-standard.json`
and mirrors [`../data/codes/gsm-standard.json`](../data/codes/gsm-standard.json).

## 🏗 Building

```bash
./gradlew assembleDebug     # build a debug APK
./gradlew installDebug      # install on a connected device
```

Or open the project in Android Studio (Ladybug or newer) and run.

## 📥 Importing collections

Settings → Import accepts a collection URL from the shared
[`../data/`](../data/) catalog, for example:

```
https://raw.githubusercontent.com/albertolicea00/MyUSSDCodes/main/data/codes/gsm-standard.json
```

The collection format and JSON Schema are documented in [`../data/`](../data/).

## 🤝 Contributing

See the root [contribution guide](../CONTRIBUTING.md). New USSD codes belong in
the shared catalog under [`../data/`](../data/), not in the app seed asset.

## ⚠️ Disclaimer

USSD codes are executed by your carrier. Codes vary by country, carrier and plan; some may be paid services. Double-check a code before running it.

## 📄 License

[MIT](../LICENSE) © 2026 Alberto Licea
