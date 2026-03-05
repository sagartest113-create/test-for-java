#!/usr/bin/env node
/**
 * TestCraft AI — Core Test Generation Script
 * Stack: Java + JUnit 5 + Maven + Groq API (free)
 * Run from repo root or .github/scripts; expects GROQ_API_KEY, GITHUB_TOKEN, etc.
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

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

function log(msg) {
  console.log(`[TestCraft] ${msg}`);
}

function writeResult(success, data = {}) {
  fs.writeFileSync(RESULT_PATH, JSON.stringify({ success, ...data }, null, 2));
}

function getPrFiles() {
  const [owner, repo] = GITHUB_REPOSITORY.split('/');
  const url = `https://api.github.com/repos/${owner}/${repo}/pulls/${PR_NUMBER}/files`;
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
        try {
          resolve(JSON.parse(data));
        } catch (e) {
          reject(e);
        }
      });
    }).on('error', reject);
  });
}

function mainJavaFiles(files) {
  return files
    .filter((f) => f.filename.endsWith('.java') && f.filename.includes('src/main/java'))
    .map((f) => f.filename);
}

function mainPathToTestPath(mainPath) {
  const normalized = mainPath.replace(/\\/g, '/');
  if (!normalized.includes('/src/main/java/')) {
    return null;
  }
  const afterMain = normalized.split('/src/main/java/')[1];
  const base = path.basename(afterMain, '.java');
  const dir = path.dirname(afterMain);
  return path.join('src/test/java', dir, `${base}Test.java`).replace(/\\/g, '/');
}

async function callGroq(userContent) {
  const body = JSON.stringify({
    model: GROQ_MODEL,
    messages: [
      {
        role: 'system',
        content: `You are a Java test writer. Generate only the JUnit 5 test class code, no explanations.
Rules:
- Use JUnit 5 (org.junit.jupiter.api.*).
- Use Spring Boot test slices: @WebMvcTest for controllers, @DataJpaTest for repositories, or @SpringBootTest for services. Mock dependencies with @MockBean where needed.
- For REST controllers use MockMvc (org.springframework.test.web.servlet.MockMvc) and MockMvcRequestBuilders / MockMvcResultMatchers.
- For services use @InjectMocks and @Mock (Mockito).
- Output ONLY valid Java code: package declaration, imports, class with @SpringBootTest or @WebMvcTest or @DataJpaTest, then test methods. No markdown, no \`\`\`java wrapper.`
      },
      { role: 'user', content: userContent }
    ],
    temperature: 0.2,
    max_tokens: 4096
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
  let code = raw;
  const mdMatch = raw.match(/```(?:java)?\s*([\s\S]*?)```/);
  if (mdMatch) {
    code = mdMatch[1].trim();
  }
  return code;
}

async function generateTestForFile(mainPath) {
  const fullPath = path.join(REPO_ROOT, mainPath);
  if (!fs.existsSync(fullPath)) {
    log(`Skip (not in checkout): ${mainPath}`);
    return null;
  }
  const content = fs.readFileSync(fullPath, 'utf8');
  const testPath = mainPathToTestPath(mainPath);
  if (!testPath) return null;

  const prompt = `Generate a JUnit 5 test class for this Java file. Put the test class in the same package but under src/test/java, and name the class ${path.basename(testPath, '.java')}.\n\nFile: ${mainPath}\n\n\`\`\`java\n${content}\n\`\`\``;
  const raw = await callGroq(prompt);
  const code = extractJavaCode(raw);
  const testFullPath = path.join(REPO_ROOT, testPath);
  fs.mkdirSync(path.dirname(testFullPath), { recursive: true });
  fs.writeFileSync(testFullPath, code, 'utf8');
  log(`Generated: ${testPath}`);
  return testPath;
}

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
    log(`Diff analyzed — ${toProcess.length} file(s) to process.`);

    const created = [];
    for (let i = 0; i < toProcess.length; i += BATCH_SIZE) {
      const batch = toProcess.slice(i, i + BATCH_SIZE);
      log(`Batch ${Math.floor(i / BATCH_SIZE) + 1} — ${batch.length} file(s)...`);
      for (const file of batch) {
        try {
          const testFile = await generateTestForFile(file);
          if (testFile) created.push(testFile);
        } catch (err) {
          log(`Error processing ${file}: ${err.message}`);
        }
      }
    }

    gitCommitAndPush(created);
    writeResult(true, { filesCreated: created.length });
    log(`Done. Generated ${created.length} test file(s).`);
  } catch (err) {
    log(`Error: ${err.message}`);
    writeResult(false, { error: err.message });
    process.exit(1);
  }
}

main();
