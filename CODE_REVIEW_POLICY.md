# CODE_REVIEW_POLICY.md

All pull requests must pass structured review.

Reviewers must verify correctness, safety, and maintainability.

---

# 1. Review Levels

## Level 1 – Correctness
- Does the change do what it claims?
- Are edge cases handled?
- Are failure states considered?

## Level 2 – Architecture
- Does it respect layering?
- Is responsibility clearly separated?
- Any cross-layer leakage?

## Level 3 – Security
- Any injection risks?
- Any exposure of sensitive data?
- Input validation present?

## Level 4 – Performance
- Any N+1 queries?
- Any blocking calls?
- Any unnecessary loops?

## Level 5 – Maintainability
- Is code readable?
- Are method names descriptive?
- Are complex blocks extracted?

---

# 2. Mandatory Checklist

Before approving PR:

- Tests included?
- CI passed?
- SQL readable?
- No SELECT *?
- No hidden breaking change?

---

# 3. Risk Classification

Low:
- Internal refactor
- Minor UI change

Medium:
- Business logic change
- Query modification

High:
- Authentication
- Financial logic
- Database schema

High risk changes require two reviewers.

---

# 4. Reject Conditions

Reject PR if:

- No tests
- Violates layering
- Reduces readability
- Adds unnecessary complexity
- Bypasses validation
- Introduces global mutable state