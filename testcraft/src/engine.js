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
    if (!basePackage) log.warn('Could not detect base package; dependency context may be limited.');
    log.info(`Base package: ${basePackage}`);

    for (const mainPath of mainPaths) {
      const abs = path.join(cfg.repoRoot, mainPath);
      if (!fs.existsSync(abs)) { log.warn(`skip (missing): ${mainPath}`); continue; }
      const content = fs.readFileSync(abs, 'utf8');
      const fileType = jp.classifyFile(mainPath, content);
      if (jp.SKIP_TYPES.has(fileType)) { log.dim(`  skip [${fileType}] ${mainPath}`); continue; }

      const testPath = jp.mainToTestPath(cfg, mainPath);
      const testClass = jp.testClassName(cfg, mainPath);
      const dependencies = basePackage ? jp.resolveDependencies(cfg, content, basePackage) : [];

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
      const user = u.mode === 'extend'
        ? prompts.extendPrompt({ ...u, existingTest: u.existingTest })
        : prompts.generatePrompt(u);
      const code = prompts.extractJava(await this.provider.complete({
        system: prompts.systemPrompt(u.fileType),
        user
      }));
      u.current = code;
      writeFileSafe(path.join(cfg.repoRoot, u.testPath), code);
      log.ok(`${u.mode === 'extend' ? 'extended' : 'generated'} [${u.fileType}] ${u.testPath}`);
    }
  }

  /* ----- Phase 1: drive the suite to all-green ----- */

  async makeGreen() {
    const cfg = this.cfg;
    if (cfg.dryRun) return { green: false, dryRun: true };

    for (let attempt = 1; attempt <= cfg.maxFixAttempts + 1; attempt++) {
      log.step(`Verify round ${attempt}: compile`);
      const comp = mvn.compileTests(cfg);
      if (!comp.ok) {
        const targets = Object.keys(comp.errorsByFile).filter((f) => this.units.has(f));
        log.err(`compile failed in ${targets.length} generated file(s)`);
        if (attempt > cfg.maxFixAttempts) return this.giveUp('compile', comp.errorsByFile);
        for (const tf of targets) {
          await this.repair(this.units.get(tf), comp.errorsByFile[tf], null);
        }
        // Unknown-source compile errors (not ours) can't be fixed here.
        const foreign = Object.keys(comp.errorsByFile).filter((f) => !this.units.has(f));
        if (foreign.length && targets.length === 0) {
          log.err(`compile errors outside generated tests:\n${foreign.join('\n')}`);
          return this.giveUp('compile-foreign', comp.errorsByFile);
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

  giveUp(stage, info) {
    log.err(`Could not reach green at stage "${stage}" within ${this.cfg.maxFixAttempts} fix attempts.`);
    if (this.cfg.quarantineOnFail) {
      // Quarantine units that are still failing so the rest of the suite stays green.
      const failingFiles = stage.startsWith('compile')
        ? Object.keys(info)
        : Object.keys(info).map((fqcn) => fqcnToTestPath(this.cfg, fqcn));
      for (const tf of failingFiles) {
        const abs = path.join(this.cfg.repoRoot, tf);
        if (fs.existsSync(abs)) {
          fs.renameSync(abs, abs + '.skip');
          log.warn(`quarantined ${tf} -> ${tf}.skip`);
          this.quarantined.push(tf);
          this.units.delete(tf);
        }
      }
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
