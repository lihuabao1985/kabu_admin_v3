# tasks 索引（00_INDEX.md）

本目录用于管理项目的分阶段任务（Bootstrap → 用户 → 权限 → 业务 → Schema→功能生成）。
所有任务均遵循仓库根目录的工程规范：

- CODING_RULES.md
- CODEX_TASK_GUARDRAILS.md
- CODE_REVIEW_POLICY.md

---

## 1. 目录结构约定

推荐结构：

/tasks
- 00_INDEX.md（本文件：任务导航与执行规则）
- 01_bootstrap/（框架阶段）
- 02_security/（用户/权限阶段）
- 03_business/（业务阶段）
- 04_schema_features/（表 Schema → 功能生成阶段，按领域拆分）

---

## 2. 执行总原则（必须遵守）

1) 所有功能必须 OpenAPI First：先改 OpenAPI，再写实现。  
2) 分阶段执行：一次只执行一个任务文件。  
3) 严格范围控制：只改任务允许的目录；禁止项不可触碰。  
4) 强制测试：任务完成必须跑测试与构建命令。  
5) 输出必须包含：Diff 概要、命令记录、风险与回滚方案。  
6) 任何不确定内容（例如业务规则、字段含义）必须在“计划阶段”先提出。

---

## 3. 推荐执行方式（给 Codex / 人类都适用）

对每一个任务文件，统一使用三步法：

### Step 1：只读分析（Plan-only）
指令示例：
- 阅读本任务文件，列出实现计划（涉及文件、接口、DB 变更、测试计划），不要修改任何文件。

输出要求：
- 计划清单
- 风险点
- 预计需要改动的文件列表

### Step 2：按计划执行（Implement）
指令示例：
- 按计划执行，只改允许目录，必须更新 OpenAPI，必须跑测试。

输出要求：
- Diff 概要
- 变更文件清单
- 执行过的命令与摘要

### Step 3：验收与收尾（Verify）
指令示例：
- 汇总测试结果，列出风险与回滚步骤。

---

## 4. 阶段任务导航与顺序（主线）

### Phase 1：基础框架（Bootstrap）
目标：生成可运行框架，不做业务逻辑。

- tasks/01_bootstrap/01_BOOTSTRAP.md

产出：
- Spring Boot + Maven + MyBatis + Security 基础配置
- OpenAPI 根结构
- React + TypeScript 初始化（并确定 UI 框架策略）
- 基础测试与构建可通过

---

### Phase 2：用户模块（User）
目标：实现用户管理 CRUD + 列表查询。

- tasks/02_security/02_USER_MODULE.md

依赖：
- Phase 1 完成

产出：
- 用户 API（OpenAPI + 实现）
- 用户页面（列表/新增/编辑/删除）
- 基本测试覆盖

---

### Phase 3：权限模块（RBAC / Security）
目标：实现角色与权限体系，并对 API 进行保护。

- tasks/02_security/03_PERMISSION_MODULE.md

依赖：
- Phase 2 完成

产出：
- 角色/权限模型与接口
- Spring Security RBAC 规则落地
- 权限测试（未登录/无权限/正常）

---

### Phase 4：业务模块（Business）
目标：实现核心业务域功能（按你的项目实际定义）。

- tasks/03_business/04_BUSINESS_MODULE.md

依赖：
- Phase 1~3 完成

产出：
- 业务 API（OpenAPI + 实现）
- 业务页面
- 事务/并发/性能考虑
- 完整测试与构建通过

---

## 5. Schema → 功能生成（多表任务的组织方式）

Schema 任务集中在：

- tasks/04_schema_features/00_SCHEMA_INDEX.md（Schema 专用索引）
- tasks/04_schema_features/<domain>/*.md（每表一个任务文件）

执行原则：

- 默认“一表一任务文件”，避免 diff 过大和风险扩散。
- 每个 Schema 文件必须声明 Dependencies（依赖任务/依赖表）。
- 每个 Schema 文件必须包含：
  - 表结构定义（字段/索引/约束）
  - OpenAPI 接口清单
  - 后端实现范围
  - 前端实现范围
  - 测试要求
  - 性能/安全要求
  - 输出要求

---

## 6. 强制验收命令（默认）

后端（Maven）：
- mvn clean verify

前端（npm）：
- npm test
- npm run build

说明：
- 若项目实际命令不同，请在各任务文件中覆盖写明，但必须同等强度（至少包含 test + build/verify）。

---

## 7. 变更控制与回滚要求

每个任务完成必须给出：

- 变更文件列表
- Diff 概要
- 测试结果摘要
- 风险点（含安全/性能/数据一致性）
- 回滚方案（如何撤回迁移、如何回退代码）

---

## 8. 当任务冲突或信息不足时

- 若任务约束与仓库安全规则冲突：以安全规则为准。
- 若业务规则不清晰：停止实现，先输出疑问清单与建议选项。

---

## 9. 维护规则（保持 tasks 可读）

- 文件命名采用编号前缀，保证顺序明确。
- 一个任务文件只解决一个明确目标。
- 每次新增任务，都要同步更新本索引或 Schema 索引。
