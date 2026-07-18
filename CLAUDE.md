# CLAUDE.md

Guidance for AI assistants (Claude Code) working in this repository.

## What this repository is

The **Android app** of My USSD Codes: browse, organize and run USSD codes. Kotlin + Jetpack Compose (Material 3), single module (`:app`).

**This is NOT a monorepo.** The catalog data lives in [MyUSSDCodes-collections](https://github.com/albertolicea00/MyUSSDCodes-collections) and the iOS app in [MyUSSDCodes-ios](https://github.com/albertolicea00/MyUSSDCodes-ios) — separate repositories. Never add USSD code data here beyond the bundled seed asset.

## Architecture

- `AppViewModel` is the single source of truth: `StateFlow<AppData>` (codes, groups, imported collections). Every mutation goes through its `update {}` helper, which persists via `CodeStore` on IO.
- `CodeStore` persists everything as one pretty-printed JSON file (`filesDir/app-data.json`). No database on purpose — the dataset is tiny. First run seeds from `assets/collections/gsm-standard.json` (verbatim copy of the catalog repo file).
- `UssdDialer` replaces `{placeholders}` with user input and fires `ACTION_DIAL` (never `ACTION_CALL`: no permission, user confirms the call). `#` is handled by `Uri.encode`.
- UI: `AppRoot` owns the Scaffold, bottom `NavigationBar` (3 tabs: sections / all / settings) and `NavHost`. Screens in `ui/screens/`, shared pieces (`CodeCard`, `CodeList`, `RunCodeDialog`) in `ui/components/`.
- Model invariants (mirror the catalog schema): `code` only contains `*#+0-9` and `{placeholders}`; every placeholder has a matching `CodeVariable`; `dangerous == true` shows a warning before dialing; user-created codes have `custom = true`.

## Commands

```bash
./gradlew assembleDebug   # build
./gradlew installDebug    # install on device
./gradlew lint            # android lint
```

## Conventions

- All code, strings and docs in **English**.
- **Conventional Commits**, lowercase imperative subject, ≤ 72 chars (`feat: add group editor`, `fix: encode hash in dial uri`).
- **Never add AI attributions, `Co-Authored-By` trailers or "Generated with" footers to commits or PRs.**
- Keep `ACTION_DIAL`; switching to `ACTION_CALL`/`CALL_PHONE` is a product decision, not a refactor.
- New USSD code data goes to the catalog repository, not to this app.
