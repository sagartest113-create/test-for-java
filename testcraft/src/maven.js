'use strict';

const fs = require('fs');
const path = require('path');
const { spawnSync } = require('child_process');
const { log } = require('./logger');

/* ------------------------------------------------------------------ */
/*  Running Maven                                                      */
/* ------------------------------------------------------------------ */

function resolveMvn(cfg) {
  const wrapper = path.join(cfg.repoRoot, 'mvnw');
  if (cfg.mvn === './mvnw' && !fs.existsSync(wrapper)) return 'mvn';
  return cfg.mvn === './mvnw' ? wrapper : cfg.mvn;
}

function mvnEnv(cfg) {
  const env = { ...process.env };
  if (cfg.javaHome) env.JAVA_HOME = cfg.javaHome;
  return env;
}

function runMaven(cfg, args, { label } = {}) {
  const mvn = resolveMvn(cfg);
  log.dim(`  $ ${path.basename(mvn)} ${args.join(' ')}${cfg.javaHome ? `   (JAVA_HOME=${cfg.javaHome})` : ''}`);
  const res = spawnSync(mvn, args, {
    cwd: cfg.repoRoot,
    env: mvnEnv(cfg),
    encoding: 'utf8',
    maxBuffer: 64 * 1024 * 1024
  });
  if (res.error) throw new Error(`Failed to run maven (${label || args.join(' ')}): ${res.error.message}`);
  return { code: res.status, stdout: res.stdout || '', stderr: res.stderr || '' };
}

/* ------------------------------------------------------------------ */
/*  Compile                                                            */
/* ------------------------------------------------------------------ */

/**
 * Compile test sources. Returns { ok, errorsByFile: { relPath: [msgs] }, raw }.
 * Parses javac diagnostics:  [ERROR] /abs/Foo.java:[12,5] message
 */
function compileTests(cfg) {
  const { code, stdout, stderr } = runMaven(cfg, ['-q', 'test-compile'], { label: 'test-compile' });
  const out = stdout + '\n' + stderr;
  if (code === 0) return { ok: true, errorsByFile: {}, raw: out };

  const errorsByFile = {};
  const re = /\[ERROR\]\s+(\/[^\s:]+\.java):\[(\d+),(\d+)\]\s+(.*)/g;
  let m;
  while ((m = re.exec(out)) !== null) {
    const rel = path.relative(cfg.repoRoot, m[1]).replace(/\\/g, '/');
    (errorsByFile[rel] = errorsByFile[rel] || []).push(`line ${m[2]}:${m[3]} — ${m[4].trim()}`);
  }
  return { ok: false, errorsByFile, raw: out };
}

/* ------------------------------------------------------------------ */
/*  Test run + surefire parsing                                        */
/* ------------------------------------------------------------------ */

function surefireDir(cfg) {
  return path.join(cfg.repoRoot, 'target', 'surefire-reports');
}

function clearSurefire(cfg) {
  const d = surefireDir(cfg);
  if (fs.existsSync(d)) {
    for (const f of fs.readdirSync(d)) {
      if (f.endsWith('.xml')) fs.rmSync(path.join(d, f));
    }
  }
}

function decode(s) {
  return s
    .replace(/&lt;/g, '<').replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"').replace(/&apos;/g, "'")
    .replace(/&amp;/g, '&');
}

/**
 * Parse target/surefire-reports/TEST-*.xml.
 * Returns { totals:{tests,failures,errors,skipped}, byClass: { fqcn: {failures:[{name,type,message,detail}]} } }
 */
function parseSurefire(cfg) {
  const dir = surefireDir(cfg);
  const totals = { tests: 0, failures: 0, errors: 0, skipped: 0 };
  const byClass = {};
  if (!fs.existsSync(dir)) return { totals, byClass };

  for (const file of fs.readdirSync(dir)) {
    if (!file.startsWith('TEST-') || !file.endsWith('.xml')) continue;
    const xml = fs.readFileSync(path.join(dir, file), 'utf8');

    const suite = xml.match(/<testsuite\b[^>]*>/);
    const attr = (name) => {
      const mm = suite && suite[0].match(new RegExp(`${name}="(\\d+)"`));
      return mm ? parseInt(mm[1], 10) : 0;
    };
    totals.tests += attr('tests');
    totals.failures += attr('failures');
    totals.errors += attr('errors');
    totals.skipped += attr('skipped');

    const classMatch = suite && suite[0].match(/name="([^"]+)"/);
    const fqcn = classMatch ? classMatch[1] : file;

    const caseRe = /<testcase\b([^>]*?)(?:\/>|>([\s\S]*?)<\/testcase>)/g;
    let cm;
    while ((cm = caseRe.exec(xml)) !== null) {
      const head = cm[1];
      const body = cm[2] || '';
      const nameM = head.match(/name="([^"]+)"/);
      const failM = body.match(/<(failure|error)\b([^>]*)>([\s\S]*?)<\/(?:failure|error)>/);
      const failSelfClose = body.match(/<(failure|error)\b([^>]*)\/>/);
      const fail = failM || failSelfClose;
      if (!fail) continue;
      const msgM = fail[2].match(/message="([\s\S]*?)"/);
      const typeM = fail[2].match(/type="([^"]*)"/);
      const cls = byClass[fqcn] || (byClass[fqcn] = { failures: [] });
      cls.failures.push({
        name: nameM ? nameM[1] : '?',
        kind: fail[1],
        type: typeM ? typeM[1] : '',
        message: msgM ? decode(msgM[1]).slice(0, 600) : '',
        detail: (failM ? decode(failM[3]) : '').trim().split('\n').slice(0, 12).join('\n')
      });
    }
  }
  return { totals, byClass };
}

/** Run the whole suite (or a filtered subset via -Dtest=...). */
function runTests(cfg, { only } = {}) {
  clearSurefire(cfg);
  const args = ['-q', 'test', '-Dsurefire.failIfNoSpecifiedTests=false'];
  if (only && only.length) args.push(`-Dtest=${only.join(',')}`);
  const { code, stdout, stderr } = runMaven(cfg, args, { label: 'test' });
  const parsed = parseSurefire(cfg);
  return { ok: code === 0, code, ...parsed, raw: (stdout + stderr).slice(-4000) };
}

/* ------------------------------------------------------------------ */
/*  Coverage (JaCoCo)                                                  */
/* ------------------------------------------------------------------ */

function num(x) { return parseInt(x, 10) || 0; }

/** Parse target/site/jacoco/jacoco.csv -> overall line %, and per-class line %. */
function parseCoverageCsv(cfg) {
  const csv = path.join(cfg.repoRoot, 'target', 'site', 'jacoco', 'jacoco.csv');
  if (!fs.existsSync(csv)) return null;
  const lines = fs.readFileSync(csv, 'utf8').trim().split('\n');
  const header = lines.shift().split(',');
  const ix = (n) => header.indexOf(n);
  let lineMissed = 0, lineCovered = 0, branchMissed = 0, branchCovered = 0;
  const classes = [];
  for (const row of lines) {
    const cols = row.split(',');
    const lm = num(cols[ix('LINE_MISSED')]);
    const lc = num(cols[ix('LINE_COVERED')]);
    const bm = num(cols[ix('BRANCH_MISSED')]);
    const bc = num(cols[ix('BRANCH_COVERED')]);
    lineMissed += lm; lineCovered += lc; branchMissed += bm; branchCovered += bc;
    const total = lm + lc;
    classes.push({
      pkg: cols[ix('PACKAGE')],
      name: cols[ix('CLASS')],
      lineMissed: lm, lineCovered: lc,
      linePct: total ? (lc / total) * 100 : 100
    });
  }
  const totalLines = lineMissed + lineCovered;
  const totalBranches = branchMissed + branchCovered;
  return {
    linePct: totalLines ? (lineCovered / totalLines) * 100 : 100,
    branchPct: totalBranches ? (branchCovered / totalBranches) * 100 : 100,
    totalLines, lineCovered, lineMissed,
    classes
  };
}

/**
 * Parse jacoco.xml for per-sourcefile missed lines.
 * Returns { 'pkg/File.java': { missedLines: [..], linePct } }
 */
function parseCoverageXml(cfg) {
  const xmlPath = path.join(cfg.repoRoot, 'target', 'site', 'jacoco', 'jacoco.xml');
  if (!fs.existsSync(xmlPath)) return {};
  const xml = fs.readFileSync(xmlPath, 'utf8');
  const out = {};
  const pkgRe = /<package\s+name="([^"]+)">([\s\S]*?)<\/package>/g;
  let pm;
  while ((pm = pkgRe.exec(xml)) !== null) {
    const pkg = pm[1];
    const sfRe = /<sourcefile\s+name="([^"]+)">([\s\S]*?)<\/sourcefile>/g;
    let sm;
    while ((sm = sfRe.exec(pm[2])) !== null) {
      const fileKey = `${pkg}/${sm[1]}`;
      const missed = [];
      let covered = 0, total = 0;
      const lineRe = /<line\s+nr="(\d+)"\s+mi="(\d+)"\s+ci="(\d+)"/g;
      let lm;
      while ((lm = lineRe.exec(sm[2])) !== null) {
        const nr = num(lm[1]); const mi = num(lm[2]); const ci = num(lm[3]);
        total++;
        if (ci > 0) covered++;
        if (ci === 0 && mi > 0) missed.push(nr);
      }
      out[fileKey] = { missedLines: missed, linePct: total ? (covered / total) * 100 : 100 };
    }
  }
  return out;
}

function runCoverage(cfg) {
  runMaven(cfg, ['-q', 'jacoco:report'], { label: 'jacoco:report' });
  const csv = parseCoverageCsv(cfg);
  const xml = parseCoverageXml(cfg);
  return { csv, xml };
}

module.exports = { runMaven, compileTests, runTests, parseSurefire, runCoverage, parseCoverageCsv, parseCoverageXml };
