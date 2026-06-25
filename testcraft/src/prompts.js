'use strict';

const path = require('path');
const { FILE_TYPES } = require('./javaProject');

const SHARED_RULES = `
General rules:
- JUnit 5 only (org.junit.jupiter.api.*). Do NOT use JUnit 4 (org.junit.Test).
- Output ONLY valid, complete, compilable Java. No markdown fences, no prose, no explanation.
- The FIRST line must be the package declaration matching the test's directory.
- Import every type you reference. Never invent constructors, methods, or fields that
  are not present in the file under test or its provided dependencies.
- Prefer AssertJ (org.assertj.core.api.Assertions.assertThat); JUnit assertions otherwise.
- Add a concise @DisplayName to each @Test.
- Cover the happy path, negative/edge cases, boundaries, and every branch you can reach.
- Do not write tests that depend on wall-clock time, randomness, network, or real files.
- Return ONE complete class. Never stop mid-method or mid-file, and close every brace.
  If the class would be very long, prefer fewer focused tests over leaving it truncated.`;

function systemPrompt(fileType) {
  switch (fileType) {
    case FILE_TYPES.CONTROLLER:
      return `You write tests for Spring REST controllers.
${SHARED_RULES}
Controller rules:
- Use @WebMvcTest(TheController.class) + autowired MockMvc.
- Mock collaborators with @MockBean.
- If the app uses Spring Security, add @WithMockUser (spring-security-test) and @Import the
  security config so secured endpoints return 2xx instead of 401/403.
- Assert HTTP status and JSON body with MockMvcResultMatchers / jsonPath.
- Serialize request bodies with ObjectMapper. Test valid, invalid, and not-found paths.`;

    case FILE_TYPES.SERVICE:
      return `You write tests for Spring service / business-logic classes.
${SHARED_RULES}
Service rules:
- Use @ExtendWith(MockitoExtension.class). No Spring context.
- @Mock dependencies, @InjectMocks the class under test.
- IMPORTANT: only mock interfaces or non-final classes. If a collaborator is a concrete
  final class or has no interface, construct a real instance instead of mocking it.
- Drive the core algorithm with concrete inputs and assert exact outputs.
- Verify mock interactions with Mockito.verify where it matters.`;

    case FILE_TYPES.REPOSITORY:
      return `You write tests for repositories.
${SHARED_RULES}
Repository rules:
- If it is a Spring Data JPA interface, use @DataJpaTest and the autowired repository.
- If it is a plain in-memory class, instantiate it directly with new in @BeforeEach.
- Test save/find/update/delete, generated ids, absent lookups, and multiple entities.`;

    case FILE_TYPES.DTO:
      return `You write tests for DTOs / records / value objects.
${SHARED_RULES}
DTO rules:
- Test construction, accessors, equals/hashCode/toString.
- If it has Jakarta Validation annotations, validate with a Validator from
  Validation.buildDefaultValidatorFactory(); assert violations for invalid input
  and none for valid input. Match constraint semantics EXACTLY (e.g. @Min, @NotBlank).`;

    case FILE_TYPES.MODEL:
      return `You write tests for model / entity / domain classes.
${SHARED_RULES}
Model rules:
- Test constructors, getters/setters, equals/hashCode contract (equal, unequal, null, other type).
- Test every business method with known inputs/outputs and round-trips where applicable.`;

    case FILE_TYPES.ENUM:
      return `You write tests for enums.
${SHARED_RULES}
Enum rules:
- Assert all constants and their properties. Test factory/lookup methods with valid and
  invalid input. Test boolean/helper methods for representative members.`;

    case FILE_TYPES.EXCEPTION:
      return `You write tests for exception classes.
${SHARED_RULES}
Exception rules:
- Test each constructor, message propagation, cause propagation, and inheritance.`;

    case FILE_TYPES.UTIL:
    default:
      return `You write thorough JUnit 5 tests.
${SHARED_RULES}
- For static utility methods, call them directly and assert outputs across normal,
  boundary, and invalid inputs.`;
  }
}

function depsBlock(dependencies) {
  if (!dependencies.length) return '';
  let s = `\n=== Dependency files (context only — DO NOT test these) ===\n`;
  for (const d of dependencies) s += `\n--- ${d.path} ---\n${d.content}\n`;
  return s;
}

function generatePrompt({ mainPath, content, fileType, dependencies, testClass }) {
  return (
    `Generate a complete JUnit 5 test class named ${testClass} for this file.\n\n` +
    `=== File under test: ${mainPath} ===\n${content}\n` +
    depsBlock(dependencies) +
    `\nFile type: ${fileType}\nTest class: ${testClass}\n` +
    `Maximise line and branch coverage. Output ONLY the Java test class.`
  );
}

/** Extend prompt: a test file already exists; the source changed. Augment, don't replace. */
function extendPrompt({ mainPath, content, dependencies, testClass, existingTest }) {
  return (
    `An existing test class for this file is shown below, and the source under test ` +
    `has CHANGED. Update the test class so it stays correct and well-covered:\n` +
    `- KEEP every existing test that still applies; do not delete coverage gratuitously.\n` +
    `- ADD new tests covering the new / changed behaviour in the source.\n` +
    `- FIX any existing test that no longer compiles or passes against the new source.\n` +
    `- Preserve the existing style, imports, and helper methods.\n` +
    `Return the COMPLETE updated test class (old + new), all passing.\n` +
    `\n=== File under test (current/changed): ${mainPath} ===\n${content}\n` +
    depsBlock(dependencies) +
    `\n=== Existing test: ${testClass} ===\n${existingTest}\n` +
    `\nOutput ONLY the complete Java test class.`
  );
}

/** Repair prompt: prior test + the exact compiler errors / test failures. */
function fixPrompt({ mainPath, content, dependencies, testClass, previousTest, compileErrors, failures }) {
  let feedback = '';
  if (compileErrors && compileErrors.length) {
    feedback += `\n=== COMPILER ERRORS (must all be fixed) ===\n${compileErrors.join('\n')}\n`;
  }
  if (failures && failures.length) {
    feedback += `\n=== TEST FAILURES (assertions/behaviour to correct) ===\n`;
    for (const f of failures) {
      feedback += `- ${f.name} [${f.kind}${f.type ? ': ' + f.type : ''}]\n    ${f.message}\n`;
      if (f.detail) feedback += `    ${f.detail.split('\n').slice(0, 4).join('\n    ')}\n`;
    }
  }
  return (
    `The test class below does NOT pass. Rewrite it so it COMPILES and ALL tests PASS.\n` +
    `Fix the test to match the ACTUAL behaviour of the file under test — do not assume; ` +
    `read the source. If an assertion expected the wrong value, correct the expectation. ` +
    `If a constructor/method signature is wrong, use the real one. Remove tests that assert ` +
    `behaviour the code does not have, but keep coverage high.\n` +
    feedback +
    `\n=== File under test: ${mainPath} ===\n${content}\n` +
    depsBlock(dependencies) +
    `\n=== Current (broken) test: ${testClass} ===\n${previousTest}\n` +
    `\nOutput ONLY the corrected, complete Java test class.`
  );
}

/** Coverage prompt: ask for additional tests targeting specific uncovered lines. */
function coveragePrompt({ mainPath, content, dependencies, testClass, existingTest, missedLines }) {
  const ranges = compressRanges(missedLines).join(', ');
  return (
    `This test class passes but coverage is below target. Add tests that exercise the ` +
    `UNCOVERED lines of the file under test, then return the COMPLETE updated test class ` +
    `(existing tests + new ones), still all passing.\n\n` +
    `Uncovered source line numbers: ${ranges}\n` +
    `Focus on untested branches, error paths, and conditionals on those lines.\n` +
    `\n=== File under test: ${mainPath} ===\n${content}\n` +
    depsBlock(dependencies) +
    `\n=== Current passing test: ${testClass} ===\n${existingTest}\n` +
    `\nOutput ONLY the complete Java test class.`
  );
}

function compressRanges(nums) {
  if (!nums || !nums.length) return [];
  const s = [...nums].sort((a, b) => a - b);
  const out = [];
  let start = s[0], prev = s[0];
  for (let i = 1; i < s.length; i++) {
    if (s[i] === prev + 1) { prev = s[i]; continue; }
    out.push(start === prev ? `${start}` : `${start}-${prev}`);
    start = prev = s[i];
  }
  out.push(start === prev ? `${start}` : `${start}-${prev}`);
  return out;
}

/** Remove comments and string/char literals so brace counting isn't fooled by `}` in text. */
function stripLiterals(code) {
  return code
    .replace(/\/\*[\s\S]*?\*\//g, '')
    .replace(/\/\/[^\n]*/g, '')
    .replace(/"(\\.|[^"\\])*"/g, '""')
    .replace(/'(\\.|[^'\\])*'/g, "''");
}

/**
 * Cheap local validity check on generated Java BEFORE spending a compile cycle.
 * Catches the common weak-model failures: markdown fences, truncation, no class,
 * unbalanced braces. Returns { ok, reason }.
 */
function sanityValidJava(code) {
  if (!code || !code.trim()) return { ok: false, reason: 'empty output' };
  if (code.includes('```')) return { ok: false, reason: 'contains markdown fence' };
  if (!/^\s*package\s+[\w.]+\s*;/m.test(code)) return { ok: false, reason: 'missing package declaration' };
  if (!/\b(class|interface|enum|record)\s+\w+/.test(code)) return { ok: false, reason: 'no type declaration' };
  const stripped = stripLiterals(code);
  const opens = (stripped.match(/{/g) || []).length;
  const closes = (stripped.match(/}/g) || []).length;
  if (opens !== closes) return { ok: false, reason: `unbalanced braces (${opens} open / ${closes} close)` };
  if (!code.trimEnd().endsWith('}')) return { ok: false, reason: 'truncated (does not end with })' };
  return { ok: true };
}

/** Strip markdown fences if a model adds them despite instructions. */
function extractJava(raw) {
  const m = raw.match(/```(?:java)?\s*([\s\S]*?)```/);
  let code = m ? m[1].trim() : raw.trim();
  // Drop any leading prose before the package/import/class declaration.
  const idx = code.search(/^\s*(package|import|public\s+(?:final\s+)?class|class|@)/m);
  if (idx > 0) code = code.slice(idx);
  return code;
}

module.exports = { systemPrompt, generatePrompt, extendPrompt, fixPrompt, coveragePrompt, extractJava, sanityValidJava, compressRanges };
