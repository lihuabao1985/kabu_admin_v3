# CODEX_TASK_GUARDRAILS.md

Codex operates under strict modification constraints.

Safety, traceability, and minimal impact are mandatory.

---

# 1. Scope Limitation

Codex may modify ONLY the directories explicitly allowed in the task.

If not explicitly allowed, DO NOT modify:

- application*.yml
- build files
- lock files
- CI configs
- Docker files
- database migrations
- root configs
- security configs

---

# 2. Change Minimization

- Modify the smallest number of files possible.
- Do not refactor unrelated code.
- Do not reformat entire files.
- Do not rename classes unless required.

---

# 3. Dependency Policy

- No new dependencies unless explicitly approved.
- No version upgrades unless requested.

---

# 4. Schema Safety

- No DB schema changes unless explicitly requested.
- No column renaming.
- No data migration logic unless task requires.

---

# 5. Mandatory Validation Before Completion

Codex must:

1. Run backend tests:
   mvn -q test

2. If frontend changed:
   npm test
   npm run build

3. Confirm build success.

4. Provide:
   - Diff summary
   - List of modified files
   - Commands executed
   - Risk assessment
   - Rollback guidance

If any test fails → Task is incomplete.

---

# 6. Testing Enforcement

Bug fix:
- Must add regression test.

New feature:
- Must add positive test case.

Refactor:
- Must ensure existing tests pass.

Performance change:
- Must document before/after reasoning.

---

# 7. SQL Enforcement

- No SELECT *
- No string concatenation SQL
- Must use parameter binding
- Must handle empty collections safely

---

# 8. Security Enforcement

- No exposure of internal stack traces
- No unsafe deserialization
- No bypassing validation

---

# 9. Conflict Resolution

If task instructions conflict with safety rules:

- Safety rules override.
- If uncertain, stop and request clarification.

---

# 10. Output Format

Completion report must include:

1. Summary
2. Diff overview
3. Test results
4. Risk assessment
5. Rollback steps