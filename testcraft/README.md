# TestCraft Engine

Self-correcting JUnit test generation for Java/Maven projects.

Unlike a one-shot "ask an LLM for a test" script, this engine runs a **closed
feedback loop**: it generates a test, compiles it, runs it, reads the actual
compiler errors and test failures, feeds them back to the model to repair, and
repeats until the suite is **green** — then measures JaCoCo coverage and asks
for more tests targeting the **specific uncovered lines** until it hits the
target (default 95%).

It is **provider-pluggable**. When your paid API key arrives you change one
environment variable; nothing in the engine changes.

## Why this exists

The previous POC used a weak model with no feedback loop. Result: 52% of tests
passed, 44% coverage, 14 files that didn't even compile. The two root causes:

1. **No verification loop** — tests were written once and never run, so wrong
   assertions and hallucinated constructors shipped as-is.
2. **Environment drift** — Maven ran on JDK 25 where Mockito can't mock; the
   project targets JDK 21.

This engine fixes both: it **pins the JDK** and **iterates until green**.

## Requirements

- Node.js ≥ 18
- A JDK matching the project (this repo: **Java 21**) and Maven (or `./mvnw`)
- An LLM API key (Anthropic / OpenAI / Groq, or any OpenAI-compatible "custom" endpoint)

## Quick start (local)

```bash
# 1. Point at a provider + key (swap freely — this is the ONLY thing that changes)
export TESTCRAFT_PROVIDER=anthropic            # anthropic | openai | groq | custom
export TESTCRAFT_API_KEY=sk-...                # or ANTHROPIC_API_KEY / OPENAI_API_KEY / GROQ_API_KEY

# 2. Run against changed files (vs origin/main), pinning the JDK
node testcraft/src/cli.js \
  --changed \
  --java-home "/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home" \
  --coverage-target 95

# Other selections:
node testcraft/src/cli.js --all                         # every src/main file
node testcraft/src/cli.js --files src/main/java/.../Foo.java
node testcraft/src/cli.js --dry-run --all               # plan only, no LLM, no key
node testcraft/src/cli.js --print-config                # show resolved settings
```

## Switching AI provider

| Provider | `TESTCRAFT_PROVIDER` | Key env | Default model | Notes |
|---|---|---|---|---|
| Anthropic Claude | `anthropic` | `ANTHROPIC_API_KEY` | `claude-sonnet-4-6` | strongest for compile-clean tests |
| OpenAI | `openai` | `OPENAI_API_KEY` | `gpt-4o` | |
| Groq | `groq` | `GROQ_API_KEY` | `llama-3.3-70b-versatile` | fast/cheap; 70B (not the old 8B) |
| Custom / self-hosted | `custom` | `TESTCRAFT_API_KEY` | — set `TESTCRAFT_MODEL` | needs OpenAI-compatible `/chat/completions`; set `TESTCRAFT_BASE_URL` |

`TESTCRAFT_API_KEY` is always accepted as a generic override. `TESTCRAFT_MODEL`
overrides the default model for any provider.

## New file vs. existing file (extend mode)

For each selected source file the engine checks whether a test file already
exists at the mapped path:

- **No test yet** → generates a fresh test class (`create` mode).
- **Test already exists** → runs in `extend` mode: the existing test is sent to
  the model with the changed source and instructions to **keep the existing
  tests, add cases for the new/changed code, and fix anything that no longer
  passes** — then the same green + coverage loop runs. Existing tests are
  augmented, not blindly overwritten.

This is the common PR case: you changed existing code that already has a test,
and the engine adds coverage for the new code while preserving what's there.

## How the loop works

```
select files ─▶ generate test per file
                      │
                      ▼
        ┌──── compile (mvn test-compile) ──── errors? ──▶ repair w/ compiler msgs ─┐
        │                                                                          │
        ▼                                                                          │
   run tests (mvn test, parse surefire) ── failures? ──▶ repair w/ failure msgs ──┘
        │                                            (up to --max-fix-attempts)
        ▼  all green
   jacoco:report ── below target? ──▶ add tests for uncovered lines ──▶ re-verify green
        │                                          (up to --max-coverage-rounds)
        ▼  target met / no further gain
      done
```

Safety: if coverage-driven additions break the suite and can't be repaired, the
affected file is reverted to its last green version. With `--quarantine-on-fail`
(used in CI), any file that still can't be made green is renamed to `*.skip` so
the committed suite is always green.

## Key options

| Flag | Env | Default | Meaning |
|---|---|---|---|
| `--coverage-target N` | `TESTCRAFT_COVERAGE_TARGET` | 95 | target line coverage % |
| `--max-fix-attempts N` | `TESTCRAFT_MAX_FIX_ATTEMPTS` | 4 | repair iterations to reach green |
| `--max-coverage-rounds N` | `TESTCRAFT_MAX_COVERAGE_ROUNDS` | 3 | coverage-fill iterations |
| `--java-home DIR` | `TESTCRAFT_JAVA_HOME` | `$JAVA_HOME` | JDK pinned for Maven |
| `--base-package PKG` | `TESTCRAFT_BASE_PACKAGE` | auto-detected | used for dependency context |
| `--no-coverage` | — | off | stop after green, skip coverage loop |
| `--quarantine-on-fail` | `TESTCRAFT_QUARANTINE=1` | off | isolate stubborn files to `*.skip` |

For a different project (e.g. a real microservice), adjust via env:
`TESTCRAFT_TEST_SUFFIX=Tests` (the real services name tests `FooTests.java`),
`TESTCRAFT_MVN=./mvnw`, and `--repo <dir>`.

## CI

`.github/workflows/testcraft.yml` runs the same engine when someone comments
`/generate-tests` on a PR. Configure once in repo settings:

- Secret `TESTCRAFT_API_KEY`
- Variables `TESTCRAFT_PROVIDER` (and optionally `TESTCRAFT_MODEL`, `TESTCRAFT_BASE_URL`)

## Self-test (no key needed)

`node testcraft/selftest/validate.js` builds an isolated Maven project and runs
the full pipeline with a **fake provider** (the LLM is stubbed with known tests,
one deliberately broken). It proves generate → compile → run → **repair** →
green → coverage parsing all work against real Maven/Java. Use it to verify the
engine after changes without spending tokens.
```bash
node testcraft/selftest/validate.js
```
