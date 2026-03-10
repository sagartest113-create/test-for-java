#!/usr/bin/env node
/**
 * TestCraft AI — Core Test Generation Script (Enhanced)
 * Stack: Java + JUnit 5 + Maven + Groq API (free)
 *
 * Generates well-structured tests by:
 *  - Classifying files (controller / service / repository / dto / model / enum)
 *  - Injecting project-dependency context into the prompt
 *  - Using file-type-specific test instructions
 *  - Handling Spring Security (@WithMockUser) for controller tests
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const fetch = global.fetch || require('node-fetch');

const GROQ_API_KEY = process.env.GROQ_API_KEY;
const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const GITHUB_REPOSITORY = process.env.GITHUB_REPOSITORY;
const PR_NUMBER = process.env.PR_NUMBER;
const PR_HEAD_REF = process.env.PR_HEAD_REF;

const REPO_ROOT = process.env.GITHUB_WORKSPACE || path.resolve(__dirname, '../..');
const GROQ_URL = 'https://api.groq.com/openai/v1/chat/completions';
const GROQ_MODEL = 'llama-3.1-8b-instant';
const BATCH_SIZE = 3;
const RESULT_PATH = path.join(__dirname, 'result.json');
const SRC_MAIN = 'src/main/java';

/* ------------------------------------------------------------------ */
/*  Logging / result                                                   */
/* ------------------------------------------------------------------ */

function log(msg) {
  console.log(`[TestCraft] ${msg}`);
}

function writeResult(success, data = {}) {
  fs.writeFileSync(RESULT_PATH, JSON.stringify({ success, ...data }, null, 2));
}

/* ------------------------------------------------------------------ */
/*  GitHub helpers                                                     */
/* ------------------------------------------------------------------ */

function getPrFiles() {
  const [owner, repo] = GITHUB_REPOSITORY.split('/');
  const url = `https://api.github.com/repos/${owner}/${repo}/pulls/${PR_NUMBER}/files?per_page=100`;
  log(`Fetching PR #${PR_NUMBER} files...`);
  return new Promise((resolve, reject) => {
    const https = require('https');
    https.get(url, {
      headers: {
        Accept: 'application/vnd.github.v3+json',
        Authorization: `Bearer ${GITHUB_TOKEN}`,
        'User-Agent': 'TestCraft-AI'
      }
    }, (resp) => {
      let data = '';
      resp.on('data', (chunk) => (data += chunk));
      resp.on('end', () => {
        if (resp.statusCode !== 200) {
          reject(new Error(`GitHub API ${resp.statusCode}: ${data}`));
          return;
        }
        try { resolve(JSON.parse(data)); } catch (e) { reject(e); }
      });
    }).on('error', reject);
  });
}

function mainJavaFiles(files) {
  return files
    .filter((f) => f.filename.endsWith('.java') && f.filename.includes(SRC_MAIN))
    .map((f) => f.filename);
}

function mainPathToTestPath(mainPath) {
  const normalized = mainPath.replace(/\\/g, '/');
  if (!normalized.includes(`${SRC_MAIN}/`)) return null;
  const afterMain = normalized.split(`${SRC_MAIN}/`)[1];
  const base = path.basename(afterMain, '.java');
  const dir = path.dirname(afterMain);
  return path.join('src/test/java', dir, `${base}Test.java`).replace(/\\/g, '/');
}

/* ------------------------------------------------------------------ */
/*  File classification                                                */
/* ------------------------------------------------------------------ */

const FILE_TYPES = {
  CONTROLLER: 'controller',
  SERVICE: 'service',
  REPOSITORY: 'repository',
  DTO: 'dto',
  MODEL: 'model',
  ENUM: 'enum',
  CONFIG: 'config',
  UNKNOWN: 'unknown'
};

function classifyFile(filePath, content) {
  const lower = filePath.toLowerCase();
  if (lower.includes('/config/')) return FILE_TYPES.CONFIG;
  if (lower.includes('/controller/')) return FILE_TYPES.CONTROLLER;
  if (lower.includes('/service/')) return FILE_TYPES.SERVICE;
  if (lower.includes('/repository/')) return FILE_TYPES.REPOSITORY;
  if (lower.includes('/dto/')) return FILE_TYPES.DTO;
  if (lower.includes('/model/') || lower.includes('/entity/')) {
    if (/\bpublic\s+enum\s+/.test(content)) return FILE_TYPES.ENUM;
    return FILE_TYPES.MODEL;
  }
  if (/\bpublic\s+enum\s+/.test(content)) return FILE_TYPES.ENUM;
  if (content.includes('@RestController') || content.includes('@Controller')) return FILE_TYPES.CONTROLLER;
  if (content.includes('@Service')) return FILE_TYPES.SERVICE;
  if (content.includes('@Repository')) return FILE_TYPES.REPOSITORY;
  return FILE_TYPES.UNKNOWN;
}

/* ------------------------------------------------------------------ */
/*  Dependency resolution                                              */
/* ------------------------------------------------------------------ */

function findProjectImports(content) {
  const regex = /^import\s+(com\.testcraft\.demo\.[^;]+);/gm;
  const imports = [];
  let m;
  while ((m = regex.exec(content)) !== null) {
    imports.push(m[1]);
  }
  return imports;
}

function fqcnToRelativePath(fqcn) {
  return path.join(SRC_MAIN, fqcn.replace(/\./g, '/') + '.java');
}

function readDependencyFiles(content, workspace) {
  const imports = findProjectImports(content);
  const deps = [];
  for (const fqcn of imports) {
    const relPath = fqcnToRelativePath(fqcn);
    const absPath = path.join(workspace, relPath);
    if (fs.existsSync(absPath)) {
      const depContent = fs.readFileSync(absPath, 'utf8');
      deps.push({ path: relPath, content: depContent });
    }
  }
  return deps;
}

/* ------------------------------------------------------------------ */
/*  Type-specific prompts                                              */
/* ------------------------------------------------------------------ */

const SHARED_RULES = `
General rules:
- JUnit 5 only (org.junit.jupiter.api.*).
- Output ONLY valid Java code. No markdown fences, no explanations.
- Package declaration must match the test source directory.
- Use descriptive @DisplayName annotations on each test method.
- Include positive tests, negative / edge-case tests, and boundary tests.
- Use AssertJ (org.assertj.core.api.Assertions.assertThat) where possible; otherwise JUnit assertions.
`;

function systemPrompt(fileType) {
  switch (fileType) {
    case FILE_TYPES.CONTROLLER:
      return `You are a Java test writer specializing in Spring REST controllers.
${SHARED_RULES}
Controller-specific rules:
- Use @WebMvcTest(ControllerClass.class).
- Import and use @AutoConfigureMockMvc.
- Add @WithMockUser on each test method or at the class level (spring-security-test) because all /api/** endpoints require authentication.
- Autowire MockMvc; mock service dependencies with @MockBean.
- Test every endpoint: valid input, invalid input, not-found, and edge cases.
- For POST endpoints verify status 201 / 200 and JSON body using jsonPath.
- For GET endpoints verify status 200 and 404.
- Use ObjectMapper to serialize request bodies.
- Add @Import of com.testcraft.demo.config.SecurityConfig if needed.`;

    case FILE_TYPES.SERVICE:
      return `You are a Java test writer specializing in Spring services.
${SHARED_RULES}
Service-specific rules:
- Use @ExtendWith(MockitoExtension.class) — no Spring context needed.
- Mock all dependencies with @Mock; inject into the service with @InjectMocks.
- Test the core algorithm / business logic thoroughly with known inputs and expected outputs.
- For search / pathfinding / game-engine services: test with concrete board/array data and verify the result step by step.
- Test edge cases: empty input, null, boundary values, unreachable/impossible scenarios.
- Verify interactions with mocks using Mockito.verify where appropriate.`;

    case FILE_TYPES.REPOSITORY:
      return `You are a Java test writer specializing in Spring repositories.
${SHARED_RULES}
Repository-specific rules:
- These are in-memory repositories (NOT JPA). Do NOT use @DataJpaTest.
- Instantiate the repository directly with new RepositoryClass() in @BeforeEach.
- Test CRUD operations: save returns entity with id, findById present/absent, findAll, delete.
- Verify auto-generated IDs increment correctly.
- Test with multiple saved entities.`;

    case FILE_TYPES.DTO:
      return `You are a Java test writer specializing in Java DTOs / records.
${SHARED_RULES}
DTO-specific rules:
- Test record construction, accessor methods, equals, hashCode, toString.
- If the record uses Jakarta Validation annotations (@NotNull, @Size, etc.), validate them using a jakarta.validation.Validator obtained from Validation.buildDefaultValidatorFactory().
- Test valid and invalid inputs against each constraint.`;

    case FILE_TYPES.MODEL:
      return `You are a Java test writer specializing in Java model / entity classes.
${SHARED_RULES}
Model-specific rules:
- Test constructors (default + parameterized), getters, setters.
- Test equals / hashCode contract: equal objects, unequal objects, null, different class.
- Test any business logic methods in the class.
- For board/grid models: test makeMove/undoMove round-trips, isInCheck, isSquareAttacked with known positions.`;

    case FILE_TYPES.ENUM:
      return `You are a Java test writer specializing in Java enums.
${SHARED_RULES}
Enum-specific rules:
- Test all enum constants exist and have correct properties (symbol, value, etc.).
- Test utility/factory methods like fromSymbol with valid and invalid inputs.
- Test boolean helpers (isEmpty, isWhite, isBlack) for representative members.`;

    default:
      return `You are a Java test writer.
${SHARED_RULES}
Write comprehensive JUnit 5 tests for the given file.`;
  }
}

function buildUserPrompt(mainPath, content, fileType, dependencies) {
  const testClassName = path.basename(mainPath, '.java') + 'Test';

  let prompt = `Generate a complete JUnit 5 test class named ${testClassName} for the following Java file.\n\n`;
  prompt += `=== File under test: ${mainPath} ===\n${content}\n\n`;

  if (dependencies.length > 0) {
    prompt += `=== Dependency files (for context — do NOT generate tests for these) ===\n`;
    for (const dep of dependencies) {
      prompt += `\n--- ${dep.path} ---\n${dep.content}\n`;
    }
    prompt += '\n';
  }

  prompt += `File type: ${fileType}\n`;
  prompt += `Test class name: ${testClassName}\n`;
  prompt += `Generate ONLY the test class Java code. No markdown. No explanations.\n`;
  return prompt;
}

/* ------------------------------------------------------------------ */
/*  Groq API call                                                      */
/* ------------------------------------------------------------------ */

async function callGroq(systemMsg, userContent) {
  const body = JSON.stringify({
    model: GROQ_MODEL,
    messages: [
      { role: 'system', content: systemMsg },
      { role: 'user', content: userContent }
    ],
    temperature: 0.2,
    max_tokens: 6144
  });

  const res = await fetch(GROQ_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${GROQ_API_KEY}`
    },
    body
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`Groq API ${res.status}: ${text.slice(0, 500)}`);
  }

  const data = await res.json();
  const choice = data.choices && data.choices[0];
  if (!choice || !choice.message || !choice.message.content) {
    throw new Error('Empty response from Groq');
  }
  return choice.message.content.trim();
}

function extractJavaCode(raw) {
  const mdMatch = raw.match(/```(?:java)?\s*([\s\S]*?)```/);
  return mdMatch ? mdMatch[1].trim() : raw;
}

/* ------------------------------------------------------------------ */
/*  Core: generate test for a single file                              */
/* ------------------------------------------------------------------ */

async function generateTestForFile(mainPath) {
  const workspace = process.env.GITHUB_WORKSPACE || REPO_ROOT;
  const fullPath = path.join(workspace, mainPath);

  if (!fs.existsSync(fullPath)) {
    log(`Skip (not found): ${mainPath}`);
    return null;
  }

  const content = fs.readFileSync(fullPath, 'utf8');
  const testPath = mainPathToTestPath(mainPath);
  if (!testPath) {
    log(`Could not map test path for ${mainPath}`);
    return null;
  }

  const fileType = classifyFile(mainPath, content);
  log(`Processing [${fileType}]: ${mainPath}`);

  if (fileType === FILE_TYPES.CONFIG) {
    log(`Skipping config file: ${mainPath}`);
    return null;
  }

  const dependencies = readDependencyFiles(content, workspace);
  log(`  → ${dependencies.length} dependency file(s) included as context`);

  const sysProm = systemPrompt(fileType);
  const userProm = buildUserPrompt(mainPath, content, fileType, dependencies);

  const raw = await callGroq(sysProm, userProm);
  const code = extractJavaCode(raw);

  const testFullPath = path.join(workspace, testPath);
  fs.mkdirSync(path.dirname(testFullPath), { recursive: true });
  fs.writeFileSync(testFullPath, code, 'utf8');
  log(`Generated: ${testPath}`);
  return testPath;
}

/* ------------------------------------------------------------------ */
/*  Git commit & push                                                  */
/* ------------------------------------------------------------------ */

function gitCommitAndPush(filesCreated) {
  if (filesCreated.length === 0) return;
  execSync('git config user.email "testcraft-ai[bot]@users.noreply.github.com"', { cwd: REPO_ROOT });
  execSync('git config user.name "TestCraft AI Bot"', { cwd: REPO_ROOT });
  for (const f of filesCreated) {
    execSync(`git add "${f}"`, { cwd: REPO_ROOT });
  }
  try {
    execSync('git commit -m "🤖 TestCraft AI: add generated JUnit 5 tests"', { cwd: REPO_ROOT });
    execSync(`git push origin ${PR_HEAD_REF}`, {
      cwd: REPO_ROOT,
      env: { ...process.env, GIT_TERMINAL_PROMPT: '0' }
    });
    log('Pushed test files to PR branch.');
  } catch (e) {
    if (e.message && e.message.includes('nothing to commit')) {
      log('No changes to commit (tests may already exist).');
    } else {
      throw e;
    }
  }
}

/* ------------------------------------------------------------------ */
/*  Main                                                               */
/* ------------------------------------------------------------------ */

async function main() {
  if (!GROQ_API_KEY) {
    log('GROQ_API_KEY is not set. Add it in repo Settings → Secrets → Actions.');
    writeResult(false, { error: 'GROQ_API_KEY not set' });
    process.exit(1);
  }
  if (!GITHUB_TOKEN || !GITHUB_REPOSITORY || !PR_NUMBER || !PR_HEAD_REF) {
    log('Missing GITHUB_TOKEN, GITHUB_REPOSITORY, PR_NUMBER, or PR_HEAD_REF.');
    writeResult(false, { error: 'Missing GitHub env vars' });
    process.exit(1);
  }

  try {
    const files = await getPrFiles();
    const toProcess = mainJavaFiles(files);
    log(`Files in PR: ${files.map(f => f.filename).join(', ')}`);
    log(`Java source files detected: ${toProcess.length}`);

    const summary = {};
    const created = [];

    for (let i = 0; i < toProcess.length; i += BATCH_SIZE) {
      const batch = toProcess.slice(i, i + BATCH_SIZE);
      const batchNum = Math.floor(i / BATCH_SIZE) + 1;
      log(`\n── Batch ${batchNum} (${batch.length} file(s)) ──`);

      for (const file of batch) {
        try {
          const testFile = await generateTestForFile(file);
          if (testFile) {
            created.push(testFile);
            summary[file] = testFile;
          }
        } catch (err) {
          log(`Error processing ${file}: ${err.message}`);
          summary[file] = `ERROR: ${err.message}`;
        }
      }
    }

    gitCommitAndPush(created);
    writeResult(true, { filesCreated: created.length, details: summary });
    log(`\nDone. Generated ${created.length} test file(s) from ${toProcess.length} source file(s).`);
  } catch (err) {
    log(`Error: ${err.message}`);
    writeResult(false, { error: err.message });
    process.exit(1);
  }
}

main();
