'use strict';

/**
 * Central configuration for the TestCraft engine.
 *
 * Everything that changes when you "add a paid API key of any AI" lives here.
 * To switch provider you only set TESTCRAFT_PROVIDER (+ the matching API key);
 * nothing else in the engine changes.
 */

const path = require('path');

/* ------------------------------------------------------------------ */
/*  Provider registry                                                  */
/* ------------------------------------------------------------------ */
/*
 * Two wire protocols cover every provider we care about:
 *   - 'anthropic'        → Claude Messages API
 *   - 'openai-compatible'→ OpenAI /chat/completions (OpenAI, Groq, most
 *                          self-hosted / "custom" LLMs speak this)
 *
 * `keyEnv` is the *preferred* env var for that provider's key, but
 * TESTCRAFT_API_KEY always works as a generic override.
 */
const PROVIDERS = {
  anthropic: {
    protocol: 'anthropic',
    baseUrl: 'https://api.anthropic.com',
    defaultModel: 'claude-sonnet-4-6',
    keyEnv: 'ANTHROPIC_API_KEY'
  },
  openai: {
    protocol: 'openai-compatible',
    baseUrl: 'https://api.openai.com/v1',
    defaultModel: 'gpt-4o',
    keyEnv: 'OPENAI_API_KEY'
  },
  groq: {
    protocol: 'openai-compatible',
    baseUrl: 'https://api.groq.com/openai/v1',
    // 70B, not the 8B the old POC used — model strength is the #1 quality lever.
    defaultModel: 'llama-3.3-70b-versatile',
    keyEnv: 'GROQ_API_KEY'
  },
  // Self-hosted / "our own custom LLM". Must expose an OpenAI-compatible
  // /chat/completions endpoint. Set TESTCRAFT_BASE_URL + TESTCRAFT_MODEL.
  custom: {
    protocol: 'openai-compatible',
    baseUrl: null,
    defaultModel: null,
    keyEnv: 'TESTCRAFT_API_KEY'
  }
};

function pickKey(providerName, def) {
  return (
    process.env.TESTCRAFT_API_KEY ||
    (def.keyEnv ? process.env[def.keyEnv] : null) ||
    null
  );
}

function intEnv(name, fallback) {
  const v = process.env[name];
  if (v === undefined || v === '') return fallback;
  const n = parseInt(v, 10);
  return Number.isFinite(n) ? n : fallback;
}

/**
 * Resolve a fully-populated config object from env + CLI overrides.
 * @param {object} overrides values parsed from CLI flags (win over env)
 */
function resolveConfig(overrides = {}) {
  const providerName = (overrides.provider || process.env.TESTCRAFT_PROVIDER || 'anthropic').toLowerCase();
  const def = PROVIDERS[providerName];
  if (!def) {
    throw new Error(
      `Unknown provider "${providerName}". Valid: ${Object.keys(PROVIDERS).join(', ')}`
    );
  }

  const baseUrl = overrides.baseUrl || process.env.TESTCRAFT_BASE_URL || def.baseUrl;
  const model = overrides.model || process.env.TESTCRAFT_MODEL || def.defaultModel;
  const apiKey = pickKey(providerName, def);

  const repoRoot = path.resolve(overrides.repoRoot || process.env.GITHUB_WORKSPACE || process.cwd());

  return {
    repoRoot,

    // --- provider / model ---
    provider: providerName,
    protocol: def.protocol,
    baseUrl,
    model,
    apiKey,
    keyEnvName: def.keyEnv,
    temperature: parseFloat(process.env.TESTCRAFT_TEMPERATURE || '0.1'),
    maxTokens: intEnv('TESTCRAFT_MAX_TOKENS', 8192),

    // --- java / maven ---
    javaHome: overrides.javaHome || process.env.TESTCRAFT_JAVA_HOME || process.env.JAVA_HOME || null,
    mvn: process.env.TESTCRAFT_MVN || './mvnw',
    srcMain: 'src/main/java',
    srcTest: 'src/test/java',
    // base package auto-detected at runtime if left null (see javaProject.detectBasePackage)
    basePackage: overrides.basePackage || process.env.TESTCRAFT_BASE_PACKAGE || null,
    testSuffix: process.env.TESTCRAFT_TEST_SUFFIX || 'Test', // real svc uses "Tests"

    // --- the loop ---
    coverageTarget: overrides.coverageTarget != null ? overrides.coverageTarget : intEnv('TESTCRAFT_COVERAGE_TARGET', 95),
    maxFixAttempts: overrides.maxFixAttempts != null ? overrides.maxFixAttempts : intEnv('TESTCRAFT_MAX_FIX_ATTEMPTS', 4),
    maxCoverageRounds: overrides.maxCoverageRounds != null ? overrides.maxCoverageRounds : intEnv('TESTCRAFT_MAX_COVERAGE_ROUNDS', 3),
    // If a test file still fails after maxFixAttempts, quarantine it to *.skip
    // instead of leaving the whole suite red. Off by default — we want green.
    quarantineOnFail: overrides.quarantineOnFail != null ? overrides.quarantineOnFail : (process.env.TESTCRAFT_QUARANTINE === '1'),

    // --- selection ---
    dryRun: !!overrides.dryRun
  };
}

module.exports = { PROVIDERS, resolveConfig };
