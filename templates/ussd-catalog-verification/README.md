# USSD catalog verification template

Use this template in any app that ships a local copy of a My USSD Codes
collection. It compares the set of local dial strings against the canonical
collection hosted by this repository and exits with a failure when they drift.

## Install

Copy the files into the consumer repository:

```text
scripts/verify-ussd-catalog.js
ussd-catalog.config.json
.github/workflows/verify-ussd-catalog.yml
```

Update `ussd-catalog.config.json` so each `localFile` is relative to the
configuration file. A local file may be a collection object with a `codes`
array, or a JSON array of code objects. Add one entry per collection:

```json
{
  "catalogBaseUrl": "https://raw.githubusercontent.com/albertolicea00/MyUSSDCodes/main/data/",
  "checks": [
    {
      "collection": "gsm-standard",
      "localFile": "app/src/main/assets/collections/gsm-standard.json"
    }
  ]
}
```

For a consumer that intentionally ships additional local codes, set
`"allowExtra": true` on that check. Missing canonical dial strings always fail.

## Run locally

Node.js 18 or newer is required:

```bash
node scripts/verify-ussd-catalog.js
```

## GitHub Actions

Copy `verify-ussd-catalog.yml` to `.github/workflows/`. It runs on pull
requests that modify the configured Android asset path, on pushes to `main`,
manually, and every Monday. Adjust the `paths` filter for iOS or any other
local-file layout.

The template compares only `code` values. Names, descriptions, categories, and
other presentation metadata can remain app-specific.
