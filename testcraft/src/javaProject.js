'use strict';

const fs = require('fs');
const path = require('path');

const FILE_TYPES = {
  CONTROLLER: 'controller',
  SERVICE: 'service',
  REPOSITORY: 'repository',
  DTO: 'dto',
  MODEL: 'model',
  ENUM: 'enum',
  CONFIG: 'config',
  UTIL: 'util',
  EXCEPTION: 'exception',
  UNKNOWN: 'unknown'
};

// File types we never generate tests for (no behaviour worth asserting, or
// excluded from coverage in the real service's pom).
const SKIP_TYPES = new Set([FILE_TYPES.CONFIG]);

function classifyFile(filePath, content) {
  const lower = filePath.toLowerCase();
  if (/\bpublic\s+enum\s+/.test(content)) return FILE_TYPES.ENUM;
  if (lower.includes('/config') || content.includes('@Configuration')) return FILE_TYPES.CONFIG;
  if (lower.includes('/controller') || content.includes('@RestController') || content.includes('@Controller')) return FILE_TYPES.CONTROLLER;
  if (lower.includes('/service') || content.includes('@Service')) return FILE_TYPES.SERVICE;
  if (lower.includes('/repositor') || content.includes('@Repository')) return FILE_TYPES.REPOSITORY;
  if (lower.includes('/dto') || /\bpublic\s+record\s+/.test(content)) return FILE_TYPES.DTO;
  if (lower.includes('/exception') || /extends\s+\w*Exception/.test(content)) return FILE_TYPES.EXCEPTION;
  if (lower.includes('/util')) return FILE_TYPES.UTIL;
  if (lower.includes('/model') || lower.includes('/entit') || lower.includes('/domain')) return FILE_TYPES.MODEL;
  return FILE_TYPES.UNKNOWN;
}

/** Auto-detect the project's base package from the first package decl under src/main/java. */
function detectBasePackage(cfg) {
  const mainRoot = path.join(cfg.repoRoot, cfg.srcMain);
  const files = walkJava(mainRoot);
  for (const f of files) {
    const m = fs.readFileSync(f, 'utf8').match(/^\s*package\s+([\w.]+)\s*;/m);
    if (m) {
      // Walk up to a sensible common prefix (e.g. com.foo.bar).
      const parts = m[1].split('.');
      return parts.slice(0, Math.min(parts.length, 4)).join('.');
    }
  }
  return null;
}

function walkJava(dir) {
  const out = [];
  if (!fs.existsSync(dir)) return out;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, entry.name);
    if (entry.isDirectory()) out.push(...walkJava(p));
    else if (entry.isFile() && entry.name.endsWith('.java')) out.push(p);
  }
  return out;
}

/** All main source files, as repo-relative paths. */
function listMainSources(cfg) {
  return walkJava(path.join(cfg.repoRoot, cfg.srcMain))
    .map((abs) => path.relative(cfg.repoRoot, abs).replace(/\\/g, '/'));
}

/** Map src/main/java/.../Foo.java -> src/test/java/.../FooTest.java */
function mainToTestPath(cfg, mainPath) {
  const norm = mainPath.replace(/\\/g, '/');
  const marker = `${cfg.srcMain}/`;
  if (!norm.includes(marker)) return null;
  const afterMain = norm.split(marker)[1];
  const base = path.basename(afterMain, '.java');
  const dir = path.dirname(afterMain);
  return path.join(cfg.srcTest, dir, `${base}${cfg.testSuffix}.java`).replace(/\\/g, '/');
}

/**
 * Resolve in-project imports of `content` to their source files (for context).
 *
 * Project files are detected by whether the import maps to an actual file under
 * src/main/java — no base-package guess needed. Library imports (java.*,
 * jakarta.*, org.springframework.*, …) have no such file and are skipped.
 */
function resolveDependencies(cfg, content, limit = 8) {
  // non-static, non-wildcard single-type imports
  const re = /^import\s+([\w.]+);/gm;
  const deps = [];
  let m;
  const seen = new Set();
  while ((m = re.exec(content)) !== null && deps.length < limit) {
    const fqcn = m[1];
    if (fqcn.endsWith('.*')) continue;
    if (seen.has(fqcn)) continue;
    seen.add(fqcn);
    const rel = path.join(cfg.srcMain, fqcn.replace(/\./g, '/') + '.java');
    const abs = path.join(cfg.repoRoot, rel);
    if (fs.existsSync(abs)) {
      deps.push({ path: rel, content: fs.readFileSync(abs, 'utf8') });
    }
  }
  return deps;
}

function testClassName(cfg, mainPath) {
  return path.basename(mainPath, '.java') + cfg.testSuffix;
}

module.exports = {
  FILE_TYPES,
  SKIP_TYPES,
  classifyFile,
  detectBasePackage,
  listMainSources,
  mainToTestPath,
  resolveDependencies,
  testClassName,
  walkJava
};
