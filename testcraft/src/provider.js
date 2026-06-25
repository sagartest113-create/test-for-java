'use strict';

/**
 * Provider adapter — the ONLY place that talks to an LLM.
 *
 * Exposes a single method: complete({ system, user }) -> Promise<string>.
 * Two wire protocols are implemented (anthropic, openai-compatible); the
 * factory picks one from config.protocol. Swapping providers = changing
 * TESTCRAFT_PROVIDER; the rest of the engine is provider-agnostic.
 */

const { log } = require('./logger');

const fetchFn = global.fetch || ((...a) => import('node-fetch').then(({ default: f }) => f(...a)));

const MAX_RETRIES = parseInt(process.env.TESTCRAFT_HTTP_RETRIES || '6', 10);
const INITIAL_BACKOFF_MS = 4000;

function sleep(ms) { return new Promise((r) => setTimeout(r, ms)); }

async function withRetry(label, doRequest) {
  for (let attempt = 1; attempt <= MAX_RETRIES; attempt++) {
    let res;
    try {
      res = await doRequest();
    } catch (e) {
      if (attempt === MAX_RETRIES) throw e;
      const wait = INITIAL_BACKOFF_MS * 2 ** (attempt - 1);
      log.warn(`${label}: network error (${e.message}). retry ${attempt}/${MAX_RETRIES} in ${wait}ms`);
      await sleep(wait);
      continue;
    }

    if (res.status === 429 || res.status >= 500) {
      if (attempt === MAX_RETRIES) {
        const text = await res.text().catch(() => '');
        throw new Error(`${label}: ${res.status} after ${MAX_RETRIES} retries. ${text.slice(0, 300)}`);
      }
      const retryAfter = res.headers.get('retry-after');
      const wait = retryAfter ? parseInt(retryAfter, 10) * 1000 : INITIAL_BACKOFF_MS * 2 ** (attempt - 1);
      log.warn(`${label}: ${res.status}. retry ${attempt}/${MAX_RETRIES} in ${wait}ms`);
      await sleep(wait);
      continue;
    }

    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(`${label}: ${res.status} ${text.slice(0, 400)}`);
    }
    return res;
  }
  throw new Error(`${label}: exhausted retries`);
}

/* ---------------- OpenAI-compatible (OpenAI / Groq / custom) -------------- */

function makeOpenAICompatible(cfg) {
  const url = `${cfg.baseUrl.replace(/\/$/, '')}/chat/completions`;
  return {
    name: `${cfg.provider} (${cfg.model})`,
    async complete({ system, user }) {
      const res = await withRetry(cfg.provider, () =>
        fetchFn(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${cfg.apiKey}`
          },
          body: JSON.stringify({
            model: cfg.model,
            temperature: cfg.temperature,
            max_tokens: cfg.maxTokens,
            messages: [
              { role: 'system', content: system },
              { role: 'user', content: user }
            ]
          })
        })
      );
      const data = await res.json();
      const content = data.choices && data.choices[0] && data.choices[0].message && data.choices[0].message.content;
      if (!content) throw new Error(`Empty response from ${cfg.provider}`);
      return content.trim();
    }
  };
}

/* ---------------- Anthropic Messages API (Claude) ------------------------- */

function makeAnthropic(cfg) {
  const url = `${cfg.baseUrl.replace(/\/$/, '')}/v1/messages`;
  return {
    name: `anthropic (${cfg.model})`,
    async complete({ system, user }) {
      const res = await withRetry('anthropic', () =>
        fetchFn(url, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'x-api-key': cfg.apiKey,
            'anthropic-version': '2023-06-01'
          },
          body: JSON.stringify({
            model: cfg.model,
            max_tokens: cfg.maxTokens,
            temperature: cfg.temperature,
            system,
            messages: [{ role: 'user', content: user }]
          })
        })
      );
      const data = await res.json();
      const block = data.content && data.content.find((b) => b.type === 'text');
      if (!block || !block.text) throw new Error('Empty response from anthropic');
      return block.text.trim();
    }
  };
}

function createProvider(cfg) {
  if (!cfg.apiKey) {
    throw new Error(
      `No API key. Set TESTCRAFT_API_KEY or ${cfg.keyEnvName || 'the provider key'} ` +
      `for provider "${cfg.provider}".`
    );
  }
  if (!cfg.baseUrl) {
    throw new Error(`Provider "${cfg.provider}" needs TESTCRAFT_BASE_URL.`);
  }
  if (!cfg.model) {
    throw new Error(`Provider "${cfg.provider}" needs TESTCRAFT_MODEL.`);
  }
  return cfg.protocol === 'anthropic' ? makeAnthropic(cfg) : makeOpenAICompatible(cfg);
}

module.exports = { createProvider };
