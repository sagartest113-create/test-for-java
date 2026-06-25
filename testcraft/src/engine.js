'use strict';

const fs = require('fs');
const path = require('path');
const { log } = require('./logger');
const jp = require('./javaProject');
const mvn = require('./maven');
const prompts = require('./prompts');

function writeFileSafe(abs, content) {
  fs.mkdirSync(path.dirname(abs), { recursive: true });
  fs.writeFileSync(abs, content, 'utf8');
}

function fqcnToTestPath(cfg, fqcn) {
  return path.join(cfg.srcTest, fqcn.replace(/\./g, '/') + '.java').replace(/\\/g, '/');
}

// main rel path -> jacoco fileKey "pkg/File.java"
function mainToCoverageKey(cfg, mainPath) {
  const after = mainPath.replace(/\\/g, '/').split(`${cfg.srcMain}/`)[1];
  return after; // e.g. com/testcraft/demo/service/Foo.java
}

class Engine {
  constructor(cfg, provider) {
    this.cfg = cfg;
    this.provider = provider;
    /** testPath -> state */
    this.units = new Map();
    /** test files renamed to *.skip because they couldn't be made green */
    this.quarantined = [];
  }

  /**
   * Build a machine-readable summary for the PR comment / logs.
   * @param {object} opts { greenResult, csv }
   */
  summary({ greenResult, csv } = {}) {
    const files = [];
    let testCasesAdded = 0;
    let createdFiles = 0;
    let extendedFiles = 0;
    for (const u of this.units.values()) {
      const code = u.current || '';
      const n = (code.match(/@(?:Test|ParameterizedTest|RepeatedTest)\b/g) || []).length;
      testCasesAdded += n;
      if (u.mode === 'extend') extendedFiles++; else createdFiles++;
      files.push({ test: u.testPath, source: u.mainPath, type: u.fileType, tests: n, mode: u.mode });
    }
    const green = greenResult ? greenResult.green : undefined;
    return {
      green,
      status: green === true ? 'green' : green === 'partial' ? 'partial' : 'red',
      // true only when the suite actually compiled and ran all-green
      verified: green === true,
      suiteRan: !!(greenResult && greenResult.totals),
      testFilesGenerated: files.length,
      createdFiles,
      extendedFiles,
      testCasesAdded,
      quarantined: this.quarantined,
      coverage: csv ? { line: +csv.linePct.toFixed(1), branch: +csv.branchPct.toFixed(1) } : null,
      files
    };
  }

  /* ----- Phase 0: build work units + generate initial tests ----- */

  async generate(mainPaths) {
    const cfg = this.cfg;
    const basePackage = cfg.basePackage || jp.detectBasePackage(cfg);
    log.info(`Base package (informational): ${basePackage}`);

    for (const mainPath of mainPaths) {
      const abs = path.join(cfg.repoRoot, mainPath);
      if (!fs.existsSync(abs)) { log.warn(`skip (missing): ${mainPath}`); continue; }
      const content = fs.readFileSync(abs, 'utf8');
      const fileType = jp.classifyFile(mainPath, content);
      if (jp.SKIP_TYPES.has(fileType)) { log.dim(`  skip [${fileType}] ${mainPath}`); continue; }

      const testPath = jp.mainToTestPath(cfg, mainPath);
      const testClass = jp.testClassName(cfg, mainPath);
      const dependencies = jp.resolveDependencies(cfg, content);

      // If a test file already exists, AUGMENT it (keep existing tests, add for
      // the changed code) instead of overwriting. Common on PRs to existing code.
      const existingAbs = path.join(cfg.repoRoot, testPath);
      const existingTest = fs.existsSync(existingAbs) ? fs.readFileSync(existingAbs, 'utf8') : null;

      this.units.set(testPath, {
        mainPath, content, fileType, testPath, testClass, dependencies,
        coverageKey: mainToCoverageKey(cfg, mainPath),
        existingTest,
        mode: existingTest ? 'extend' : 'create',
        current: null, lastGreen: null
      });
    }

    const created = [...this.units.values()].filter((u) => u.mode === 'create').length;
    const extended = this.units.size - created;
    log.step(`Generating ${this.units.size} test class(es) (${created} new, ${extended} extending existing)`);
    for (const u of this.units.values()) {
      if (cfg.dryRun) { log.dim(`  [dry-run] would ${u.mode} ${u.testPath}`); continue; }
      await this.generateUnit(u);
      log.ok(`${u.mode === 'extend' ? 'extended' : 'generated'} [${u.fileType}] ${u.testPath}`);
    }
  }

  /** Generate (or regenerate) one unit's test, with a local sanity check + one strict retry. */
  async generateUnit(u) {
    const cfg = this.cfg;
    const baseUser = u.mode === 'extend'
      ? prompts.extendPrompt({ ...u, existingTest: u.existingTest })
      : prompts.generatePrompt(u);
    let code = prompts.extractJava(await this.provider.complete({
      system: prompts.systemPrompt(u.fileType), user: baseUser
    }));
    const san = prompts.sanityValidJava(code);
    if (!san.ok) {
      log.warn(`  ${u.testPath}: ${san.reason}; regenerating once (strict)`);
      const strictUser = baseUser +
        '\n\nIMPORTANT: Output ONLY one complete, compilable Java class. No markdown fences (```), ' +
        'no prose. Do not truncate — finish every method and close every brace.';
      code = prompts.extractJava(await this.provider.complete({
        system: prompts.systemPrompt(u.fileType), user: strictUser
      }));
    }
    u.current = code;
    writeFileSafe(path.join(cfg.repoRoot, u.testPath), code);
    return u;
  }

  /* ----- Phase 1: drive the suite to all-green ----- */

  async makeGreen() {
    const cfg = this.cfg;
    if (cfg.dryRun) return { green: false, dryRun: true };

    for (let attempt = 1; attempt <= cfg.maxFixAttempts + 1; attempt++) {
      log.step(`Verify round ${attempt}: compile`);
      const comp = mvn.compileTests(cfg);
      if (!comp.ok) {
        const errored = Object.keys(comp.errorsByFile);
        const targets = errored.filter((f) => this.units.has(f));
        const foreign = errored.filter((f) => !this.units.has(f));
        log.err(`compile failed: ${targets.length} generated, ${foreign.length} pre-existing file(s)`);
        if (attempt > cfg.maxFixAttempts) return this.giveUp('compile', comp.errorsByFile);

        // Repair our own generated files from the compiler messages.
        for (const tf of targets) {
          await this.repair(this.units.get(tf), comp.errorsByFile[tf], null);
        }

        // Pre-existing (foreign) tests block the whole module's compile and we
        // can't author fixes for files we didn't generate. Quarantine them (only
        // with explicit opt-in) and retry, so the generated tests still get
        // compiled and run; otherwise stop with guidance.
        if (foreign.length) {
          if (cfg.quarantineOnFail) {
            this.quarantineFiles(foreign, 'pre-existing test fails to compile');
          } else if (targets.length === 0) {
            log.err(`Pre-existing tests do not compile (not generated by this run):\n${foreign.join('\n')}`);
            log.err('Fix them, or re-run with --quarantine-on-fail to set them aside.');
            return this.giveUp('compile-foreign', comp.errorsByFile);
          }
        }
        continue;
      }
      log.ok('compiles');

      log.step(`Verify round ${attempt}: run tests`);
      const res = mvn.runTests(cfg);
      const t = res.totals;
      log.info(`tests=${t.tests} failures=${t.failures} errors=${t.errors} skipped=${t.skipped}`);

      if (res.ok && t.failures === 0 && t.errors === 0) {
        for (const u of this.units.values()) u.lastGreen = u.current;
        log.ok('all tests pass');
        return { green: true, totals: t };
      }

      if (attempt > cfg.maxFixAttempts) return this.giveUp('test', res.byClass);

      const failingClasses = Object.keys(res.byClass);
      for (const fqcn of failingClasses) {
        const tf = fqcnToTestPath(cfg, fqcn);
        const u = this.units.get(tf);
        if (!u) { log.warn(`failing class not owned by this run: ${fqcn}`); continue; }
        await this.repair(u, null, res.byClass[fqcn].failures);
      }
    }
    return { green: false };
  }

  /* ----- Phase 1 (alt): take each file to green on its own ----- */
  /*
   * Better for weaker models: isolating one test file means compiler errors
   * can't mask each other, the LLM context stays small, and one bad file never
   * blocks the rest. Files that can't be greened are quarantined to *.skip.
   */
  async makeGreenSequential() {
    const cfg = this.cfg;
    if (cfg.dryRun) return { green: false, dryRun: true };
    const abs = (rel) => path.join(cfg.repoRoot, rel);
    const hold = (u) => { if (fs.existsSync(abs(u.testPath))) fs.renameSync(abs(u.testPath), abs(u.testPath) + '.hold'); };
    const restore = (u) => { if (fs.existsSync(abs(u.testPath) + '.hold')) fs.renameSync(abs(u.testPath) + '.hold', abs(u.testPath)); };

    const units = [...this.units.values()];

    // A) Hold all our generated files aside, then clear any pre-existing broken
    //    tests once (so they don't fail every per-file compile below).
    units.forEach(hold);
    for (let i = 0; i <= cfg.maxFixAttempts; i++) {
      const comp = mvn.compileTests(cfg);
      if (comp.ok) break;
      const broken = Object.keys(comp.errorsByFile);
      if (!broken.length) break;
      if (cfg.quarantineOnFail) { this.quarantineFiles(broken, 'pre-existing test fails to compile'); continue; }
      log.warn('pre-existing tests do not compile and --quarantine-on-fail is off; results may be unreliable');
      break;
    }

    // B) One file at a time.
    let greenCount = 0;
    for (const u of units) {
      log.step(`Sequential: ${u.testPath}`);
      restore(u);
      let ok = false;
      for (let attempt = 1; attempt <= cfg.maxFixAttempts + 1; attempt++) {
        const san = prompts.sanityValidJava(u.current);
        if (!san.ok) {
          if (attempt > cfg.maxFixAttempts) break;
          log.warn(`  ${san.reason}; regenerating`);
          await this.generateUnit(u);
          continue;
        }
        const comp = mvn.compileTests(cfg);
        if (!comp.ok) {
          if (comp.errorsByFile[u.testPath]) {
            if (attempt > cfg.maxFixAttempts) break;
            await this.repair(u, comp.errorsByFile[u.testPath], null);
            continue;
          }
          // a pre-existing file slipped through; quarantine and retry
          if (cfg.quarantineOnFail) { this.quarantineFiles(Object.keys(comp.errorsByFile), 'pre-existing'); continue; }
          break;
        }
        const res = mvn.runTests(cfg, { only: [u.testClass] });
        const t = res.totals;
        if (res.ok && t.failures === 0 && t.errors === 0) { ok = true; break; }
        if (attempt > cfg.maxFixAttempts) break;
        const fqcn = Object.keys(res.byClass)[0];
        await this.repair(u, null, fqcn ? res.byClass[fqcn].failures : []);
      }
      if (ok) { u.lastGreen = u.current; greenCount++; log.ok(`  green (${u.testClass})`); }
      else {
        if (fs.existsSync(abs(u.testPath))) {
          fs.renameSync(abs(u.testPath), abs(u.testPath) + '.skip');
          this.quarantined.push(u.testPath);
        }
        this.units.delete(u.testPath);
        log.warn(`  could not green ${u.testPath} — quarantined`);
      }
    }

    // C) Final confirmation across everything that survived.
    log.step('Sequential: final full verify');
    const comp = mvn.compileTests(cfg);
    const res = comp.ok ? mvn.runTests(cfg) : { ok: false, totals: { tests: 0, failures: 0, errors: 0, skipped: 0 } };
    const allPass = comp.ok && res.ok && res.totals.failures === 0 && res.totals.errors === 0;
    log.info(`greened ${greenCount}/${units.length} file(s); quarantined ${this.quarantined.length}`);
    return { green: allPass ? (this.quarantined.length ? 'partial' : true) : 'partial', totals: res.totals };
  }

  async repair(unit, compileErrors, failures) {
    const cfg = this.cfg;
    log.info(`  repairing ${unit.testPath}`);
    const code = prompts.extractJava(await this.provider.complete({
      system: prompts.systemPrompt(unit.fileType),
      user: prompts.fixPrompt({
        mainPath: unit.mainPath,
        content: unit.content,
        dependencies: unit.dependencies,
        testClass: unit.testClass,
        previousTest: unit.current,
        compileErrors,
        failures
      })
    }));
    unit.current = code;
    writeFileSafe(path.join(cfg.repoRoot, unit.testPath), code);
  }

  /** Rename failing test files to *.skip so the rest of the suite can stay green. */
  quarantineFiles(files, reason) {
    for (const tf of files) {
      const abs = path.join(this.cfg.repoRoot, tf);
      if (fs.existsSync(abs)) {
        fs.renameSync(abs, abs + '.skip');
        log.warn(`quarantined ${tf} -> ${tf}.skip${reason ? ` (${reason})` : ''}`);
        this.quarantined.push(tf);
        this.units.delete(tf);
      }
    }
  }

  giveUp(stage, info) {
    log.err(`Could not reach green at stage "${stage}" within ${this.cfg.maxFixAttempts} fix attempts.`);
    if (this.cfg.quarantineOnFail) {
      const failingFiles = stage.startsWith('compile')
        ? Object.keys(info)
        : Object.keys(info).map((fqcn) => fqcnToTestPath(this.cfg, fqcn));
      this.quarantineFiles(failingFiles, `unfixable after ${this.cfg.maxFixAttempts} attempts`);
      return { green: 'partial', quarantined: failingFiles };
    }
    return { green: false, stage, info };
  }

  /* ----- Phase 2: push coverage toward the target ----- */

  async raiseCoverage() {
    const cfg = this.cfg;
    if (cfg.dryRun) return { coverage: null };

    let last = -1;
    for (let round = 1; round <= cfg.maxCoverageRounds; round++) {
      log.step(`Coverage round ${round}: measuring`);
      const { csv, xml } = mvn.runCoverage(cfg);
      if (!csv) { log.warn('no jacoco report found'); return { coverage: null }; }
      log.info(`overall line coverage: ${csv.linePct.toFixed(1)}% (branch ${csv.branchPct.toFixed(1)}%)`);

      if (csv.linePct >= cfg.coverageTarget) {
        log.ok(`coverage target ${cfg.coverageTarget}% reached`);
        return { coverage: csv.linePct, csv };
      }
      if (csv.linePct <= last + 0.01) {
        log.warn('coverage stopped improving; stopping coverage loop');
        return { coverage: csv.linePct, csv };
      }
      last = csv.linePct;

      // Pick the units whose source is most under-covered.
      const candidates = [];
      for (const u of this.units.values()) {
        const cov = xml[u.coverageKey];
        if (cov && cov.missedLines.length && cov.linePct < cfg.coverageTarget) {
          candidates.push({ u, cov });
        }
      }
      candidates.sort((a, b) => b.cov.missedLines.length - a.cov.missedLines.length);
      const batch = candidates.slice(0, 5);
      if (!batch.length) { log.warn('no improvable units'); return { coverage: csv.linePct, csv }; }

      log.info(`augmenting ${batch.length} under-covered file(s)`);
      for (const { u, cov } of batch) {
        const augmented = prompts.extractJava(await this.provider.complete({
          system: prompts.systemPrompt(u.fileType),
          user: prompts.coveragePrompt({
            mainPath: u.mainPath,
            content: u.content,
            dependencies: u.dependencies,
            testClass: u.testClass,
            existingTest: u.lastGreen || u.current,
            missedLines: cov.missedLines
          })
        }));
        u.current = augmented;
        writeFileSafe(path.join(cfg.repoRoot, u.testPath), augmented);
      }

      // Re-establish green; revert any unit the augmentation broke beyond repair.
      const green = await this.makeGreen();
      if (green.green !== true) {
        log.warn('coverage additions broke the suite; reverting augmented files to last green');
        for (const { u } of batch) {
          if (u.lastGreen) {
            u.current = u.lastGreen;
            writeFileSafe(path.join(cfg.repoRoot, u.testPath), u.lastGreen);
          }
        }
        await this.makeGreen();
      }
    }
    const { csv } = mvn.runCoverage(cfg);
    return { coverage: csv ? csv.linePct : null, csv };
  }
}

module.exports = { Engine };
