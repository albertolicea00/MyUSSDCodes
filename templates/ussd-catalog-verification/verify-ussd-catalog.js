#!/usr/bin/env node

/**
 * Verifies that local USSD dial strings match a collection published by
 * MyUSSDCodes. Requires Node.js 18+ and has no third-party dependencies.
 */
const fs = require("node:fs");
const path = require("node:path");

const configPath = process.argv[2] || "ussd-catalog.config.json";

function fail(message) {
  console.error(`USSD catalog verification failed: ${message}`);
  process.exit(1);
}

function readJson(file) {
  try {
    return JSON.parse(fs.readFileSync(file, "utf8"));
  } catch (error) {
    fail(`could not read ${file}: ${error.message}`);
  }
}

function extractCodes(value, label) {
  const codes = Array.isArray(value) ? value : value?.codes;
  if (!Array.isArray(codes)) fail(`${label} must be an array or an object with a codes array`);

  const dialStrings = codes.map((entry) => entry?.code);
  if (dialStrings.some((code) => typeof code !== "string" || code.length === 0)) {
    fail(`${label} contains an entry without a non-empty code value`);
  }
  return new Set(dialStrings);
}

function difference(left, right) {
  return [...left].filter((item) => !right.has(item)).sort();
}

async function main() {
  const config = readJson(configPath);
  if (typeof config.catalogBaseUrl !== "string" || !Array.isArray(config.checks)) {
    fail("config requires catalogBaseUrl and checks");
  }

  const baseUrl = config.catalogBaseUrl.endsWith("/")
    ? config.catalogBaseUrl
    : `${config.catalogBaseUrl}/`;
  let hasDrift = false;

  for (const check of config.checks) {
    if (typeof check.collection !== "string" || typeof check.localFile !== "string") {
      fail("every check requires collection and localFile");
    }

    const remoteUrl = new URL(`codes/${check.collection}.json`, baseUrl);
    let response;
    try {
      response = await fetch(remoteUrl);
    } catch (error) {
      fail(`could not fetch ${remoteUrl}: ${error.message}`);
    }
    if (!response.ok) fail(`${remoteUrl} returned HTTP ${response.status}`);

    const canonical = extractCodes(await response.json(), remoteUrl.href);
    const localFile = path.resolve(path.dirname(configPath), check.localFile);
    const local = extractCodes(readJson(localFile), localFile);
    const missing = difference(canonical, local);
    const extra = difference(local, canonical);

    if (missing.length === 0 && (check.allowExtra || extra.length === 0)) {
      console.log(`✓ ${check.collection}: ${canonical.size} dial strings match`);
      continue;
    }

    hasDrift = true;
    console.error(`✗ ${check.collection}: catalog drift in ${check.localFile}`);
    if (missing.length) console.error(`  missing locally: ${missing.join(", ")}`);
    if (extra.length && !check.allowExtra) console.error(`  extra locally: ${extra.join(", ")}`);
  }

  if (hasDrift) process.exit(1);
}

main().catch((error) => fail(error.message));
