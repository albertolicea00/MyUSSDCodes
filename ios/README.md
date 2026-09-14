# My USSD Codes [iPhone]

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](../LICENSE)
[![Swift](https://img.shields.io/badge/Swift-5-F05138?logo=swift)](https://swift.org)
[![iOS](https://img.shields.io/badge/iOS-17%2B-blue)](https://developer.apple.com/ios)
[![SwiftUI](https://img.shields.io/badge/UI-SwiftUI-007AFF)](https://developer.apple.com/xcode/swiftui)

iOS app to browse, organize and run USSD codes. Built with Swift and SwiftUI.

> 📦 **Monorepo components:**
>
> | What | Location |
> | ---- | ---------- |
> | 📋 Code catalog (collections) | [`../data/`](../data/) |
> | 📱 Android app | [`../android/`](../android/) |

> **Migration note:** Android, iOS, and the catalog were previously separate
> repositories. They now live together in the [My USSD Codes monorepo](../).

## ✨ Features

The app is organized in three sections (bottom tab bar):

1. **Sections** — codes grouped by category and by the user's own fully-customizable groups.
2. **All codes** — a flat list of every code with instant search (name, code, category, tags).
3. **Settings** — import collections from the catalog (URL or pasted JSON), reset data, and app info.

Beyond the built-in catalog, users can **create their own codes** with a bit of logic: a code may declare **variables** (placeholders like `{number}`) and the app asks for each value right before dialing. Codes marked as **dangerous** (SIM locks, charges) show a warning first.

Dialing opens the code in the Phone app via a `tel:` URL — the final call tap is always the user's.

> **iOS caveat:** Apple blocks some USSD/MMI codes from being dialed programmatically ("Dialing not allowed"). Codes that iOS rejects must be typed manually in the Phone app; the app still works as a reference for them.

## 🛠 Tech stack

- Swift 5 · SwiftUI (iOS 17+)
- `Codable` models matching the catalog's JSON Schema
- Plain JSON file persistence in Application Support (no database) — the dataset is tiny
- No third-party dependencies

## 📁 Project structure

```
MyUSSDCodes/
├── MyUSSDCodesApp.swift        # App entry point, injects CodeStore
├── Models/Models.swift         # UssdCode, CodeVariable, CodeGroup, CodeCollection, AppData
├── Store/CodeStore.swift       # ObservableObject: persistence + seed + import
├── Dialer/UssdDialer.swift     # {placeholder} substitution + tel: URL
├── Views/
│   ├── RootView.swift          # TabView (Sections / All codes / Settings)
│   ├── SectionsView.swift      # Groups + categories, section detail
│   ├── AllCodesView.swift      # Searchable flat list
│   ├── SettingsView.swift      # Import, data, about
│   ├── CodeEditorView.swift    # Create/edit custom codes
│   └── Components.swift        # CodeRow, CodeListView, RunCodeSheet
└── Resources/gsm-standard.json # Bundled seed mirrored from ../data/
```

## 🏗 Building

Open `MyUSSDCodes.xcodeproj` in Xcode 16+ and run, or:

```bash
xcodebuild -project MyUSSDCodes.xcodeproj -scheme MyUSSDCodes \
  -destination 'platform=iOS Simulator,name=iPhone 16' build
```

## 📥 Importing collections

Settings → Import accepts a collection URL from the shared
[`../data/`](../data/) catalog, for example:

```
https://raw.githubusercontent.com/albertolicea00/MyUSSDCodes/main/data/codes/gsm-standard.json
```

The collection format and JSON Schema are documented in [`../data/`](../data/).

## 🤝 Contributing

See the root [contribution guide](../CONTRIBUTING.md). New USSD codes belong in
the shared catalog under [`../data/`](../data/), not in the app seed resource.

## ⚠️ Disclaimer

USSD codes are executed by your carrier. Codes vary by country, carrier and plan; some may be paid services. Double-check a code before running it.

## 📄 License

[MIT](../LICENSE) © 2026 Alberto Licea
