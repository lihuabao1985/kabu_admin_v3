# Schema → 功能生成任务索引（00_SCHEMA_INDEX.md）

本目录用于管理所有“数据库 Schema → OpenAPI → 功能生成”任务。

执行原则：

- 一表一任务文件
- 明确依赖
- 分领域管理
- 严禁一次生成多个无依赖的大表
- 必须遵循 OpenAPI First

---

# 一、执行总原则

1. 每个 Schema 任务必须包含：
   - 表结构定义
   - 索引与约束说明
   - OpenAPI 接口清单
   - 后端实现范围
   - 前端实现范围
   - 测试要求
   - 性能与安全要求
   - 输出要求

2. 执行顺序必须遵循依赖关系。

3. 每个任务必须独立可回滚。

4. 所有表默认要求：
   - 禁止 SELECT *
   - 必须分页
   - 必须索引优化
   - 必须单元测试
   - 必须权限控制

---

# 二、领域划分（Domain Grouping）

## 1️⃣ 主数据（Master Data）

路径：
/tasks/04_schema_features/master/

执行顺序：

| 顺序 | 文件 | 表 | 依赖 |
|------|------|----|------|
| 001 | 001_SCHEMA_M_USER.md | 用户表 | 无 |
| 002 | 002_SCHEMA_M_ROLE.md | 角色表 | M_USER |
| 003 | 003_SCHEMA_M_PERMISSION.md | 权限表 | M_ROLE |

说明：
- Master 表通常优先生成
- 为后续业务表提供基础数据

---

## 2️⃣ 股票领域（Stock Domain）

路径：
/tasks/04_schema_features/stock/

执行顺序：

| 顺序 | 文件 | 表 | 依赖 |
|------|------|----|------|
| 100 | 101_SCHEMA_M_STOCK_MASTER.md | 股票主表 | 无 |
| 101 | 101_SCHEMA_M_STOCK_PRICE_HISTORY.md | 股价历史表 | M_STOCK_MASTER |
| 102 | 102_SCHEMA_M_TIMELY_DISCLOSURE.md | 適時開示表 | M_STOCK_MASTER |

说明：
- 必须先生成股票主表
- 历史表必须有联合索引
- 大数据量表必须考虑性能

---


说明：
- 遥测表必须考虑高并发写入
- 告警表必须考虑索引与状态字段

---

# 三、执行步骤（统一三步法）

对每个 Schema 文件，必须执行：

---

## Step 1：只读分析

指令：

- 阅读指定 Schema 任务文件
- 列出实现计划
- 列出将变更的文件
- 输出风险点
- 不修改代码

---

## Step 2：执行实现

指令：

- 按计划实现
- 只修改允许目录
- 必须更新 OpenAPI
- 必须新增 migration 脚本
- 必须写单元测试
- 必须跑构建

---

## Step 3：验收与收尾

输出必须包含：

- Diff 摘要
- 变更文件清单
- 执行过的命令
- 测试结果
- 风险说明
- 回滚步骤

---

# 四、强制构建验证

后端：

mvn clean verify

前端：

npm test
npm run build

如涉及迁移：

- migration 必须可执行
- 不得破坏历史数据

---

# 五、风险控制规则

1. 禁止一次执行超过 2 个 Schema 文件。
2. 禁止跨领域批量生成。
3. 大数据量表必须优先定义索引。
4. 涉及权限的表必须与权限模块联动。
5. 涉及金额/金融字段必须增加精度验证测试。

---

# 六、任务状态标记（可选）

建议每个表文件顶部标记：

- Status: Pending
- Status: In Progress
- Status: Completed

完成后必须更新此文件。

---

# 七、维护规则

- 新增表必须加入本索引
- 修改依赖必须更新依赖关系
- 删除任务必须标记废弃，不直接删除

---

# 八、建议执行顺序（推荐）

1. Master Domain
2. Security Related Tables
3. Business Core Tables
4. Historical / Large Volume Tables
5. Log / Audit Tables
