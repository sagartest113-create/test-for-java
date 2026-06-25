#!/usr/bin/env node
'use strict';

/**
 * Regression test for the "pre-existing broken test blocks compile" bug.
 *
 * Scenario: the repo already contains a test file that does NOT compile (a
 * leftover from an older generator — e.g. a stray markdown fence). The engine
 * did not author it, so it can't repair it; with --quarantine-on-fail it must
 * set the broken file aside and STILL drive the generated tests to green.
 */

const fs = require('fs');
const path = require('path');
const os = require('os');
const { resolveConfig } = require('../src/config');
const { Engine } = require('../src/engine');
const { log } = require('../src/logger');

const POC = path.resolve(__dirname, '../..');
const JDK21 = '/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home';
const MAIN = 'src/main/java/com/testcraft/demo/model/Cell.java';

const CELL_TEST = `package com.testcraft.demo.model;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;
class CellTest {
  @Test @DisplayName("ctor + equals by row/col")
  void basics(){ Cell a=new Cell(1,2,3), b=new Cell(1,2,99);
    assertThat(a.getRow()).isEqualTo(1); assertThat(a.getCol()).isEqualTo(2);
    assertThat(a).isEqualTo(b); assertThat(a.toString()).isEqualTo("(1,2)"); }
}`;

// A pre-existing test that starts with a markdown fence and is truncated —
// exactly the corruption seen on the amit-test-jun-25 branch.
const BROKEN_FOREIGN = '```java\npackage com.testcraft.demo.model;\nclass GhostTest {\n  void x(){ int y = ';

function fakeProvider() {
  return { name: 'fake', async complete() { return CELL_TEST; } };
}

(async () => {
  const dir = fs.mkdtempSync(path.join(os.tmpdir(), 'tc-foreign-'));
  fs.copyFileSync(path.join(POC, 'pom.xml'), path.join(dir, 'pom.xml'));
  const mainDest = path.join(dir, MAIN);
  fs.mkdirSync(path.dirname(mainDest), { recursive: true });
  fs.copyFileSync(path.join(POC, MAIN), mainDest);

  // drop the broken pre-existing foreign test
  const foreignRel = 'src/test/java/com/testcraft/demo/model/GhostTest.java';
  const foreignAbs = path.join(dir, foreignRel);
  fs.mkdirSync(path.dirname(foreignAbs), { recursive: true });
  fs.writeFileSync(foreignAbs, BROKEN_FOREIGN);

  log.info(`isolated project: ${dir}`);
  const cfg = resolveConfig({
    repoRoot: dir,
    javaHome: process.env.TESTCRAFT_JAVA_HOME || JDK21,
    maxFixAttempts: 2,
    quarantineOnFail: true
  });

  const engine = new Engine(cfg, fakeProvider());
  await engine.generate([MAIN]);
  const green = await engine.makeGreen();

  const quarantined = fs.existsSync(foreignAbs + '.skip') && !fs.existsSync(foreignAbs);
  console.log('');
  if (green.green === true && quarantined) {
    log.ok('FOREIGN SELFTEST PASSED — broken pre-existing test quarantined, generated tests reached green');
    log.dim(`(temp project at ${dir})`);
  } else {
    log.err(`FOREIGN SELFTEST FAILED: green=${JSON.stringify(green)} quarantined=${quarantined}`);
    process.exit(1);
  }
})().catch((e) => { log.err(e.stack || e.message); process.exit(1); });
