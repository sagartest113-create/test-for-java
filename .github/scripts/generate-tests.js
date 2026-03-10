#!/usr/bin/env node

/**
 * TestCraft AI — Stable Test Generator
 * Generates JUnit5 tests for Java files in PR
 */

const fs = require("fs");
const path = require("path");
const { execSync } = require("child_process");
const fetch = global.fetch || require("node-fetch");

const GROQ_API_KEY = process.env.GROQ_API_KEY;
const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const GITHUB_REPOSITORY = process.env.GITHUB_REPOSITORY;
const PR_NUMBER = process.env.PR_NUMBER;
const PR_HEAD_REF = process.env.PR_HEAD_REF;

const REPO_ROOT =
  process.env.GITHUB_WORKSPACE || path.resolve(__dirname, "../..");

const GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
const MODEL = "llama-3.1-8b-instant";

const BATCH_SIZE = 2;
const DELAY_BETWEEN_BATCH = 6000;

const RESULT_PATH = path.join(__dirname, "result.json");

function log(msg) {
  console.log(`[TestCraft] ${msg}`);
}

/* ---------------------------------------------------- */
/* GitHub PR files */
/* ---------------------------------------------------- */

function getPrFiles() {
  const [owner, repo] = GITHUB_REPOSITORY.split("/");

  const url = `https://api.github.com/repos/${owner}/${repo}/pulls/${PR_NUMBER}/files?per_page=100`;

  return new Promise((resolve, reject) => {
    const https = require("https");

    https
      .get(
        url,
        {
          headers: {
            Accept: "application/vnd.github.v3+json",
            Authorization: `Bearer ${GITHUB_TOKEN}`,
            "User-Agent": "TestCraft",
          },
        },
        (resp) => {
          let data = "";

          resp.on("data", (chunk) => (data += chunk));

          resp.on("end", () => {
            try {
              resolve(JSON.parse(data));
            } catch (e) {
              reject(e);
            }
          });
        }
      )
      .on("error", reject);
  });
}

function mainJavaFiles(files) {
  return files
    .filter(
      (f) =>
        f.filename.endsWith(".java") &&
        f.filename.includes("src/main/java")
    )
    .map((f) => f.filename);
}

/* ---------------------------------------------------- */
/* Skip low-value files */
/* ---------------------------------------------------- */

function shouldSkip(file) {
  return (
    file.includes("/dto/") ||
    file.includes("/model/") ||
    file.includes("/config/") ||
    file.includes("Exception") ||
    file.endsWith("Request.java") ||
    file.endsWith("Response.java") ||
    file.endsWith("Enum.java")
  );
}

/* ---------------------------------------------------- */
/* Convert main path → test path */
/* ---------------------------------------------------- */

function mainPathToTestPath(mainPath) {
  const normalized = mainPath.replace(/\\/g, "/");

  const after = normalized.split("src/main/java/")[1];

  const base = path.basename(after, ".java");

  const dir = path.dirname(after);

  return `src/test/java/${dir}/${base}Test.java`;
}

/* ---------------------------------------------------- */
/* Groq API with retry */
/* ---------------------------------------------------- */

async function callGroq(prompt, retries = 300) {
  try {
    const body = JSON.stringify({
      model: MODEL,
      messages: [
        {
          role: "system",
          content: `
You are an expert Java testing engineer.

Generate a COMPLETE JUnit5 test class.

Rules:
- Only output valid Java code
- Use JUnit5
- Use Mockito for services
- Use @WebMvcTest for controllers
- Test success + failure cases
- Include edge cases
`,
        },
        {
          role: "user",
          content: prompt,
        },
      ],
      temperature: 0.2,
      max_tokens: 3000,
    });

    const res = await fetch(GROQ_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${GROQ_API_KEY}`,
      },
      body,
    });

    if (!res.ok) {
      const text = await res.text();

      if (text.includes("rate_limit_exceeded") && retries > 0) {
        log("Groq rate limit hit — retrying in 7s...");
        await new Promise((r) => setTimeout(r, 7000));
        return callGroq(prompt, retries - 1);
      }

      throw new Error(text);
    }

    const data = await res.json();

    return data.choices[0].message.content.trim();
  } catch (err) {
    if (retries > 0) {
      log("Retrying after error...");
      await new Promise((r) => setTimeout(r, 5000));
      return callGroq(prompt, retries - 1);
    }

    throw err;
  }
}

function extractCode(raw) {
  const match = raw.match(/```(?:java)?\s*([\s\S]*?)```/);
  return match ? match[1].trim() : raw;
}

/* ---------------------------------------------------- */
/* Generate test */
/* ---------------------------------------------------- */

async function generateTest(file) {
  const full = path.join(REPO_ROOT, file);

  if (!fs.existsSync(full)) return null;

  if (shouldSkip(file)) {
    log(`Skipping ${file}`);
    return null;
  }

  const content = fs.readFileSync(full, "utf8");

  const testPath = mainPathToTestPath(file);

  const testFull = path.join(REPO_ROOT, testPath);

  if (fs.existsSync(testFull)) {
    log(`Test already exists for ${file}`);
    return null;
  }

  const prompt = `
Generate JUnit5 test class for the following Java file.

File:
${file}

Code:
${content.slice(0, 2500)}
`;

  const raw = await callGroq(prompt);

  const code = extractCode(raw);

  fs.mkdirSync(path.dirname(testFull), { recursive: true });

  fs.writeFileSync(testFull, code);

  log(`Generated: ${testPath}`);

  return testPath;
}

/* ---------------------------------------------------- */
/* Git commit */
/* ---------------------------------------------------- */

function gitPush(files) {
  if (files.length === 0) return;

  execSync(
    'git config user.email "testcraft-ai@users.noreply.github.com"',
    { cwd: REPO_ROOT }
  );

  execSync('git config user.name "TestCraft AI"', {
    cwd: REPO_ROOT,
  });

  files.forEach((f) => {
    execSync(`git add ${f}`, { cwd: REPO_ROOT });
  });

  execSync('git commit -m "🤖 TestCraft AI generated tests"', {
    cwd: REPO_ROOT,
  });

  execSync(`git push origin ${PR_HEAD_REF}`, {
    cwd: REPO_ROOT,
  });
}

/* ---------------------------------------------------- */
/* Main */
/* ---------------------------------------------------- */

async function main() {
  if (!GROQ_API_KEY) {
    log("Missing GROQ_API_KEY");

    fs.writeFileSync(
      RESULT_PATH,
      JSON.stringify({ success: false, error: "Missing GROQ_API_KEY" })
    );

    process.exit(1);
  }

  const files = await getPrFiles();

  const javaFiles = mainJavaFiles(files);

  log(`Java files changed: ${javaFiles.length}`);

  const created = [];
  const details = {};

  for (let i = 0; i < javaFiles.length; i += BATCH_SIZE) {
    const batch = javaFiles.slice(i, i + BATCH_SIZE);

    log(`Batch ${i / BATCH_SIZE + 1}`);

    for (const file of batch) {
      try {
        const result = await generateTest(file);

        if (result) {
          created.push(result);
          details[file] = result;
        }
      } catch (err) {
        log(`Error for ${file}: ${err.message}`);
        details[file] = `ERROR: ${err.message}`;
      }
    }

    await new Promise((r) => setTimeout(r, DELAY_BETWEEN_BATCH));
  }

  gitPush(created);

  fs.writeFileSync(
    RESULT_PATH,
    JSON.stringify(
      {
        success: true,
        filesCreated: created.length,
        details,
      },
      null,
      2
    )
  );

  log(`Generated ${created.length} tests`);
}

main();