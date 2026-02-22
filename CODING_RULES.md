# CODING_RULES.md

This repository follows strict engineering standards inspired by modern AI-first software development practices.

Clarity, correctness, safety, and testability are mandatory.

---

# 1. Core Engineering Principles

1. Code must be:
   - Readable
   - Deterministic
   - Testable
   - Observable
   - Reversible

2. Prefer explicit over implicit behavior.

3. Prefer simplicity over abstraction.

4. Every change must be verifiable via automated tests.

5. No change is complete without passing build + test.

---

# 2. Architecture Rules

## 2.1 Separation of Concerns

Backend layering is mandatory:

- Controller → I/O only
- Service → Business logic
- Repository → Data access
- Domain → Business models
- DTO → Transport objects

No layer may bypass another.

Controllers must never:
- Contain business logic
- Access database directly

Services must never:
- Perform HTTP concerns
- Leak persistence models

---

# 3. Java / Backend Standards

## 3.1 Language

- Java 21 compatible
- No deprecated APIs
- Constructor injection only
- No field injection
- No mutable static state

## 3.2 Style

- Explicit return types
- Avoid magic numbers
- Avoid deeply nested logic (>3 levels)
- Extract complex logic into methods

## 3.3 Logging

- Structured logging only
- No console prints
- Log at appropriate level:
  - INFO for business events
  - WARN for recoverable issues
  - ERROR for failures

---

# 4. MyBatis & SQL Rules

1. NEVER use SELECT *
2. Explicitly list columns
3. Use parameter binding only
4. Use <where> and <if> for dynamic conditions
5. Use <foreach> for IN clauses
6. Handle empty collections explicitly
7. Queries must be readable and formatted
8. Complex queries must include intent comments

---

# 5. API Design

All APIs must return consistent envelope:

{
  "code": "SUCCESS | ERROR_CODE",
  "message": "human readable",
  "data": {}
}

No raw exceptions may reach client.

---

# 6. Error Handling

- Use centralized exception handling
- Do not swallow exceptions
- Do not catch Exception broadly unless rethrowing

---

# 7. Testing Standards

Minimum requirements:

- Unit tests for service layer
- Regression test for every bug fix
- Edge case coverage
- No reliance on external systems
- Deterministic tests only

Coverage target:
- Service layer ≥ 80%

---

# 8. Frontend Standards (React)

- Functional components only
- Hooks-based state management
- No business logic inside UI
- API calls centralized
- Avoid unnecessary re-renders
- Explicit error states

---

# 9. Security Requirements

- No secrets in code
- Validate all external input
- No dynamic SQL string concatenation
- Principle of least privilege

---

# 10. Performance

- No N+1 queries
- Avoid blocking calls in controllers
- Measure before optimizing
- No premature caching

---

# 11. Git Policy

- No direct push to main
- All changes via PR
- Every PR must pass CI
- Every PR must be reviewable

---

# 12. Code Quality Gate

A change is invalid if:

- It reduces readability
- It breaks layering
- It lacks tests
- It bypasses validation
- It weakens security