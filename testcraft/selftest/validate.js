#!/usr/bin/env node
'use strict';

/**
 * Self-test for the TestCraft engine — runs the FULL pipeline (generate →
 * compile → run → repair → coverage) against a real, isolated Maven project,
 * with a FAKE provider standing in for the LLM. No API key required.
 *
 * The fake provider deliberately returns a BROKEN BinarySearchService test on
 * first generation, so we prove the repair loop turns red into green.
 */

const fs = require('fs');
const path = require('path');
const os = require('os');
const { resolveConfig } = require('../src/config');
const { Engine } = require('../src/engine');
const mvn = require('../src/maven');
const { log } = require('../src/logger');

const POC = path.resolve(__dirname, '../..');
const JDK21 = '/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home';

const MAIN_FILES = [
  'src/main/java/com/testcraft/demo/model/Cell.java',
  'src/main/java/com/testcraft/demo/model/SearchResult.java',
  'src/main/java/com/testcraft/demo/repository/SearchResultRepository.java',
  'src/main/java/com/testcraft/demo/service/BinarySearchService.java'
];

/* ---------------- fixtures: what a good LLM would return ---------------- */

const CELL = `package com.testcraft.demo.model;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;
class CellTest {
  @Test @DisplayName("two-arg ctor sets row/col, weight defaults 0")
  void twoArg(){ Cell c=new Cell(2,3); assertThat(c.getRow()).isEqualTo(2); assertThat(c.getCol()).isEqualTo(3); assertThat(c.getWeight()).isZero(); }
  @Test @DisplayName("three-arg ctor sets weight")
  void threeArg(){ assertThat(new Cell(1,1,5).getWeight()).isEqualTo(5); }
  @Test @DisplayName("setters update fields")
  void setters(){ Cell c=new Cell(); c.setRow(7); c.setCol(8); c.setWeight(9); assertThat(c.getRow()).isEqualTo(7); assertThat(c.getCol()).isEqualTo(8); assertThat(c.getWeight()).isEqualTo(9); }
  @Test @DisplayName("equals/hashCode use row+col only")
  void equality(){ Cell a=new Cell(1,2,3), b=new Cell(1,2,99); assertThat(a).isEqualTo(b).hasSameHashCodeAs(b); assertThat(a).isEqualTo(a); assertThat(a).isNotEqualTo(new Cell(1,3)); assertThat(a).isNotEqualTo(null); assertThat(a).isNotEqualTo("x"); }
  @Test @DisplayName("toString is (row,col)")
  void str(){ assertThat(new Cell(4,5).toString()).isEqualTo("(4,5)"); }
}`;

const SEARCH_RESULT = `package com.testcraft.demo.model;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
class SearchResultTest {
  @Test @DisplayName("full ctor sets all fields + timestamp")
  void ctor(){ SearchResult r=new SearchResult(1L,new int[]{1,2},5,true,3,2,List.of("a"));
    assertThat(r.getId()).isEqualTo(1L); assertThat(r.getSortedArray()).containsExactly(1,2);
    assertThat(r.getTarget()).isEqualTo(5); assertThat(r.isFound()).isTrue();
    assertThat(r.getIndex()).isEqualTo(3); assertThat(r.getComparisons()).isEqualTo(2);
    assertThat(r.getSteps()).containsExactly("a"); assertThat(r.getSearchedAt()).isNotNull(); }
  @Test @DisplayName("setters update fields")
  void setters(){ SearchResult r=new SearchResult(); r.setId(2L); r.setTarget(7); r.setFound(false);
    r.setIndex(-1); r.setComparisons(4); r.setSortedArray(new int[]{9}); r.setSteps(List.of("x"));
    java.time.LocalDateTime now=java.time.LocalDateTime.now(); r.setSearchedAt(now);
    assertThat(r.getId()).isEqualTo(2L); assertThat(r.getTarget()).isEqualTo(7); assertThat(r.isFound()).isFalse();
    assertThat(r.getIndex()).isEqualTo(-1); assertThat(r.getComparisons()).isEqualTo(4);
    assertThat(r.getSortedArray()).containsExactly(9); assertThat(r.getSteps()).containsExactly("x");
    assertThat(r.getSearchedAt()).isEqualTo(now); }
  @Test @DisplayName("equals/hashCode by id")
  void eq(){ SearchResult a=new SearchResult(); a.setId(1L); SearchResult b=new SearchResult(); b.setId(1L);
    SearchResult c=new SearchResult(); c.setId(2L);
    assertThat(a).isEqualTo(b).hasSameHashCodeAs(b); assertThat(a).isNotEqualTo(c);
    assertThat(a).isEqualTo(a); assertThat(a).isNotEqualTo(null); assertThat(a).isNotEqualTo("s"); }
}`;

const REPO = `package com.testcraft.demo.repository;
import com.testcraft.demo.model.SearchResult;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
class SearchResultRepositoryTest {
  private SearchResultRepository repo;
  @BeforeEach void setup(){ repo=new SearchResultRepository(); }
  private SearchResult make(){ return new SearchResult(null,new int[]{1},1,true,0,1,List.of()); }
  @Test @DisplayName("save assigns incrementing ids from 1")
  void save(){ assertThat(repo.save(make()).getId()).isEqualTo(1L); assertThat(repo.save(make()).getId()).isEqualTo(2L); }
  @Test @DisplayName("findById returns saved entity")
  void present(){ SearchResult a=repo.save(make()); assertThat(repo.findById(1L)).contains(a); }
  @Test @DisplayName("findById empty when absent")
  void absent(){ assertThat(repo.findById(99L)).isEmpty(); }
  @Test @DisplayName("findAll returns all saved")
  void all(){ repo.save(make()); repo.save(make()); assertThat(repo.findAll()).hasSize(2); }
  @Test @DisplayName("findAll returns a copy")
  void copy(){ repo.save(make()); List<SearchResult> l=repo.findAll(); l.clear(); assertThat(repo.findAll()).hasSize(1); }
}`;

const SERVICE_CORRECT = `package com.testcraft.demo.service;
import com.testcraft.demo.model.SearchResult;
import com.testcraft.demo.repository.SearchResultRepository;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;
class BinarySearchServiceTest {
  private BinarySearchService service;
  @BeforeEach void setup(){ service=new BinarySearchService(new SearchResultRepository()); }
  @Test @DisplayName("search finds target, records comparisons, persists")
  void found(){ SearchResult r=service.search(new int[]{5,3,8,1,9},8);
    assertThat(r.isFound()).isTrue(); assertThat(r.getIndex()).isEqualTo(3);
    assertThat(r.getComparisons()).isEqualTo(2); assertThat(r.getId()).isEqualTo(1L); assertThat(r.getSteps()).isNotEmpty(); }
  @Test @DisplayName("search reports not found")
  void notFound(){ SearchResult r=service.search(new int[]{5,3,8,1,9},7);
    assertThat(r.isFound()).isFalse(); assertThat(r.getIndex()).isEqualTo(-1); }
  @Test @DisplayName("search-left branch when mid > target")
  void left(){ SearchResult r=service.search(new int[]{1,2,3,4,5},1);
    assertThat(r.isFound()).isTrue(); assertThat(r.getIndex()).isEqualTo(0); }
  @Test @DisplayName("getResult delegates to repository")
  void getResult(){ SearchResult s=service.search(new int[]{1},1);
    assertThat(service.getResult(s.getId())).contains(s); assertThat(service.getResult(999L)).isEmpty(); }
  @Test @DisplayName("getAllResults returns saved results")
  void getAll(){ service.search(new int[]{1},1); service.search(new int[]{2},2); assertThat(service.getAllResults()).hasSize(2); }
}`;

// Deliberately WRONG: asserts index 999 (actual is 3) -> forces a real test failure
// so we exercise the generate -> fail -> repair -> green path.
const SERVICE_BROKEN = SERVICE_CORRECT.replace('.getIndex()).isEqualTo(3)', '.getIndex()).isEqualTo(999)');
if (SERVICE_BROKEN === SERVICE_CORRECT) throw new Error('selftest mis-wired: broken fixture is identical to correct');

const CORRECT = {
  CellTest: CELL,
  SearchResultTest: SEARCH_RESULT,
  SearchResultRepositoryTest: REPO,
  BinarySearchServiceTest: SERVICE_CORRECT
};

/* ---------------- fake provider ---------------- */

function fakeProvider() {
  let serviceGenerated = false;
  return {
    name: 'fake (selftest)',
    async complete({ user }) {
      const isFix = /does NOT pass\. Rewrite it/.test(user);
      const isExtend = /An existing test class for this file/.test(user);
      let cls = null;
      let m = user.match(/named (\w+) for this file/);
      if (m) cls = m[1];
      m = user.match(/Current \(broken\) test: (\w+)/);
      if (m) cls = m[1];
      m = user.match(/Existing test: (\w+)/);
      if (m) cls = m[1];
      if (!cls) throw new Error('fake provider could not determine test class from prompt');

      if (isExtend) { log.dim(`    [fake] extending ${cls}`); return CORRECT[cls]; }

      if (cls === 'BinarySearchServiceTest' && !isFix && !serviceGenerated) {
        serviceGenerated = true;
        log.dim(`    [fake] generating intentionally-broken ${cls}`);
        return SERVICE_BROKEN;
      }
      log.dim(`    [fake] ${isFix ? 'repairing' : 'generating'} ${cls}`);
      return CORRECT[cls];
    }
  };
}

/* ---------------- build isolated temp maven project ---------------- */

function buildTempProject() {
  const dir = fs.mkdtempSync(path.join(os.tmpdir(), 'testcraft-selftest-'));
  fs.copyFileSync(path.join(POC, 'pom.xml'), path.join(dir, 'pom.xml'));
  for (const rel of MAIN_FILES) {
    const dest = path.join(dir, rel);
    fs.mkdirSync(path.dirname(dest), { recursive: true });
    fs.copyFileSync(path.join(POC, rel), dest);
  }
  return dir;
}

/* ---------------- run ---------------- */

(async () => {
  const repoRoot = buildTempProject();
  log.info(`isolated project: ${repoRoot}`);
  const cfg = resolveConfig({
    repoRoot,
    javaHome: process.env.TESTCRAFT_JAVA_HOME || JDK21,
    coverageTarget: 85,
    maxFixAttempts: 3,
    maxCoverageRounds: 1
  });

  const engine = new Engine(cfg, fakeProvider());
  await engine.generate(MAIN_FILES);
  const green = await engine.makeGreen();

  console.log('');
  if (green.green === true) {
    log.ok(`SELFTEST: suite reached GREEN — ${green.totals.tests} tests, 0 failures, 0 errors`);
  } else {
    log.err(`SELFTEST FAILED: suite not green (${JSON.stringify(green)})`);
    process.exit(1);
  }

  const { csv, xml } = mvn.runCoverage(cfg);
  log.info(`overall line coverage (4 classes under test): ${csv.linePct.toFixed(1)}%`);
  for (const u of engine.units.values()) {
    const c = xml[u.coverageKey];
    if (c) log.info(`  ${u.coverageKey}: ${c.linePct.toFixed(1)}%  (missed lines: ${c.missedLines.length ? c.missedLines.join(',') : 'none'})`);
  }

  // Verify the PR-comment summary the engine emits.
  const summary = engine.summary({ greenResult: green, csv });
  log.info(`summary: status=${summary.status} files=${summary.testFilesGenerated} testCases=${summary.testCasesAdded} coverage=${summary.coverage.line}%`);
  if (summary.status !== 'green' || summary.testCasesAdded !== 18 || summary.coverage.line !== 100) {
    log.err(`SELFTEST FAILED: summary mismatch ${JSON.stringify(summary)}`);
    process.exit(1);
  }
  log.ok('SELFTEST: summary output verified (18 cases, green, 100% line coverage)');

  // Second pass: test files now exist -> engine should EXTEND (not overwrite) and stay green.
  log.step('Second pass: re-running with existing test files (extend mode)');
  const engine2 = new Engine(cfg, fakeProvider());
  await engine2.generate(MAIN_FILES);
  const extended = [...engine2.units.values()].filter((u) => u.mode === 'extend').length;
  const green2 = await engine2.makeGreen();
  if (extended !== 4 || green2.green !== true) {
    log.err(`SELFTEST FAILED: extend pass wrong (extended=${extended}, green=${JSON.stringify(green2)})`);
    process.exit(1);
  }
  log.ok(`SELFTEST: extend mode verified (4 files detected as existing, augmented, still green)`);

  log.ok('SELFTEST PASSED — generate/extend→compile→repair→green→coverage all verified.');
  log.dim(`(temp project left at ${repoRoot})`);
})().catch((e) => { log.err(e.stack || e.message); process.exit(1); });
