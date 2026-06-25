#!/usr/bin/env node
'use strict';

const path = require('path');
const { execSync } = require('child_process');
const { resolveConfig, PROVIDERS } = require('./config');
const { createProvider } = require('./provider');
const { Engine } = require('./engine');
const jp = require('./javaProject');
const { log } = require('./logger');

function parseArgs(argv) {
  const o = { files: [] };
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    const next = () => argv[++i];
    switch (a) {
      case '-h': case '--help': o.help = true; break;
      case '--all': o.all = true; break;
      case '--changed': o.changed = true; if (argv[i + 1] && !argv[i + 1].startsWith('-')) o.base = next(); break;
      case '--files': while (argv[i + 1] && !argv[i + 1].startsWith('-')) o.files.push(next()); break;
      case '--provider': o.provider = next(); break;
      case '--model': o.model = next(); break;
      case '--base-url': o.baseUrl = next(); break;
      case '--repo': o.repoRoot = next(); break;
      case '--java-home': o.javaHome = next(); break;
      case '--base-package': o.basePackage = next(); break;
      case '--coverage-target': o.coverageTarget = parseInt(next(), 10); break;
      case '--max-fix-attempts': o.maxFixAttempts = parseInt(next(), 10); break;
      case '--max-coverage-rounds': o.maxCoverageRounds = parseInt(next(), 10); break;
      case '--quarantine-on-fail': o.quarantineOnFail = true; break;
      case '--no-coverage': o.noCoverage = true; break;
      case '--dry-run': o.dryRun = true; break;
      case '--print-config': o.printConfig = true; break;
      default:
        if (a.startsWith('-')) { log.warn(`unknown flag ${a}`); }
        else o.files.push(a);
    }
  }
  return o;
}

const HELP = `
TestCraft — self-correcting JUnit test generation for Java/Maven

Usage:
  testcraft [files...] [options]
  testcraft --changed [base-ref]      generate for src/main files changed vs base (default origin/main)
  testcraft --all                     generate for every main source file

Selection:
  --files <a.java> <b.java>   explicit source files (repo-relative)
  --changed [base]            files changed vs base ref (default origin/main)
  --all                       all src/main/java files

Provider (swap when your key arrives):
  --provider <name>           ${Object.keys(PROVIDERS).join(' | ')}   (env: TESTCRAFT_PROVIDER)
  --model <id>                override model            (env: TESTCRAFT_MODEL)
  --base-url <url>            for 'custom' provider     (env: TESTCRAFT_BASE_URL)
  key via TESTCRAFT_API_KEY or ANTHROPIC_API_KEY / OPENAI_API_KEY / GROQ_API_KEY

Loop:
  --coverage-target <n>       target line coverage %% (default 95, env TESTCRAFT_COVERAGE_TARGET)
  --max-fix-attempts <n>      repair iterations to reach green (default 4)
  --max-coverage-rounds <n>   coverage-fill iterations (default 3)
  --no-coverage               stop after all-green; skip coverage loop
  --quarantine-on-fail        rename still-failing tests to *.skip instead of failing the run

Env / project:
  --repo <dir>                project root (default cwd)
  --java-home <dir>           pin JDK for maven (env: TESTCRAFT_JAVA_HOME)
  --base-package <pkg>        override auto-detected base package
  --dry-run                   plan only; no LLM calls, no writes of generated code
  --print-config              show resolved configuration and exit
`;

function selectFiles(cfg, o) {
  if (o.files.length) return o.files.map((f) => f.replace(/\\/g, '/'));
  if (o.all) return jp.listMainSources(cfg);
  // default: changed
  const base = o.base || 'origin/main';
  let diff = '';
  try {
    diff = execSync(`git diff --name-only ${base}...HEAD`, { cwd: cfg.repoRoot, encoding: 'utf8' });
  } catch (e) {
    log.warn(`git diff vs ${base} failed (${e.message.split('\n')[0]}); falling back to --all`);
    return jp.listMainSources(cfg);
  }
  const changed = diff.split('\n')
    .map((s) => s.trim())
    .filter((s) => s.endsWith('.java') && s.includes(`${cfg.srcMain}/`));
  return changed;
}

async function main() {
  const o = parseArgs(process.argv.slice(2));
  if (o.help) { console.log(HELP); return; }

  const cfg = resolveConfig(o);

  if (o.printConfig) {
    const redacted = { ...cfg, apiKey: cfg.apiKey ? `set (${cfg.apiKey.slice(0, 4)}…)` : 'MISSING' };
    console.log(JSON.stringify(redacted, null, 2));
    return;
  }

  log.info(`provider=${cfg.provider} model=${cfg.model || '(unset)'} target=${cfg.coverageTarget}% repo=${path.relative(process.cwd(), cfg.repoRoot) || '.'}`);

  const files = selectFiles(cfg, o);
  if (!files.length) { log.warn('no source files selected — nothing to do.'); return; }
  log.info(`selected ${files.length} source file(s)`);
  files.forEach((f) => log.dim(`  • ${f}`));

  let provider = null;
  if (!cfg.dryRun) provider = createProvider(cfg);

  const engine = new Engine(cfg, provider);
  await engine.generate(files);

  if (cfg.dryRun) { log.ok('dry-run complete (no LLM calls made).'); return; }

  const green = await engine.makeGreen();
  const suiteRed = green.green === false;
  if (suiteRed) {
    log.err('Suite is not green. Re-run with --quarantine-on-fail to isolate stubborn files, or raise --max-fix-attempts.');
  }

  let cov = { csv: null };
  if (!suiteRed && !o.noCoverage) {
    cov = await engine.raiseCoverage();
    if (cov.coverage != null) {
      const meets = cov.coverage >= cfg.coverageTarget;
      (meets ? log.ok : log.warn)(`final line coverage: ${cov.coverage.toFixed(1)}% (target ${cfg.coverageTarget}%)`);
    }
  }

  // Emit a machine-readable summary for CI to turn into a PR comment.
  const summary = engine.summary({ greenResult: green, csv: cov.csv });
  const resultPath = process.env.TESTCRAFT_RESULT || path.join(cfg.repoRoot, 'testcraft-result.json');
  require('fs').writeFileSync(resultPath, JSON.stringify(summary, null, 2));

  log.step('Summary');
  log.info(`status=${summary.status}  test files=${summary.testFilesGenerated}  test cases=${summary.testCasesAdded}` +
    (summary.coverage ? `  line coverage=${summary.coverage.line}% (branch ${summary.coverage.branch}%)` : ''));
  if (summary.quarantined.length) log.warn(`quarantined (could not green): ${summary.quarantined.join(', ')}`);
  for (const f of summary.files) log.dim(`  • ${f.test}  (${f.tests} test${f.tests === 1 ? '' : 's'}, ${f.type})`);
  log.dim(`  summary written to ${path.relative(cfg.repoRoot, resultPath)}`);

  if (suiteRed) process.exitCode = 1;
  else log.ok('Done.');
}

main().catch((e) => {
  log.err(process.env.TESTCRAFT_DEBUG ? (e.stack || e.message) : e.message);
  process.exitCode = 1;
});
