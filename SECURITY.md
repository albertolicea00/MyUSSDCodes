# Security Policy

## Reporting a Vulnerability

For a vulnerability, open an issue or contact the maintainer privately. Do not
open public issues for critical vulnerabilities. Reports are reviewed within
48 hours.

## Scope

- Android dials through `ACTION_DIAL`; iOS uses `tel:` URLs. Neither app places
  calls without user confirmation.
- The apps do not collect, transmit, or store personal data.
- Catalog imports are validated against JSON Schema; the catalog itself contains
  JSON data, schemas, and validation tooling only.

## Supported Versions

Only the latest versions on `main` receive security updates.
