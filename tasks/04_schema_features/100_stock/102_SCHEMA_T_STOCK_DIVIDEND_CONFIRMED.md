# 102_SCHEMA_T_STOCK_DIVIDEND_CONFIRMED

## 1. 目标

基于 `T_STOCK_DIVIDEND_CONFIRMED`（銘柄 配当確定管理表）建设“股票配当确权管理”能力，支撑：

- 按股票、基准日、确权状态查询配当信息
- 维护每股配当金额、配当利回り和关键日期（権利付き最終日/配当落ち日/基準日）
- 建立“确权前草稿 -> 确权后冻结”的业务流程
- 为选股、收益测算、公告联动和持仓配当统计提供可靠数据源

---

## 2. 表结构业务含义

表名：`T_STOCK_DIVIDEND_CONFIRMED`

主字段说明：

- `ID`：自增主键
- `STOCK_CODE`：銘柄コード（股票代码）
- `DIVIDEND_AMOUNT`：每股配当金额（円）
- `DIVIDEND_YIELD`：配当利回り（%）
- `RIGHTS_LAST_DAY`：権利付き最終日（含权最后日）
- `EX_DIVIDEND_DATE`：配当落ち日（除权日）
- `RECORD_DATE`：権利確定日（基準日，必填）
- `CONFIRMED_FLG`：確定フラグ（是否已确权）

---

## 3. 功能需求

### 3.1 配当数据查询

- 支持按 `STOCK_CODE` 精确查询
- 支持按 `RECORD_DATE` 区间查询
- 支持按 `CONFIRMED_FLG` 筛选（已确权/未确权）
- 支持分页、排序（默认 `RECORD_DATE desc, ID desc`）
- 支持查看单条明细（按 `ID`）

### 3.2 配当数据维护

- 支持新增单条配当记录
- 支持编辑未确权记录（金额、利回り、日期字段）
- 支持删除未确权记录
- 支持批量新增/更新（用于季度集中维护）

### 3.3 确权流程管理

- 支持将记录从未确权改为已确权（`CONFIRMED_FLG` 更新）
- 已确权记录默认不允许修改核心字段（`DIVIDEND_AMOUNT`、`DIVIDEND_YIELD`、三类日期）
- 若需修正已确权数据，需走“反确权 -> 修正 -> 再确权”流程（可配置是否允许）
- 确权操作需记录操作日志（操作人、时间、前后值）

### 3.4 数据导入（可选但建议）

- 支持 CSV/外部数据源批量导入
- 导入模式：
  - 覆盖更新（命中唯一业务键时更新）
  - 跳过重复（命中时忽略）
- 返回导入统计：总数、成功数、失败数、失败原因

---

## 4. 数据质量与业务规则

### 4.1 必填与取值

- `STOCK_CODE` 必填，长度不超过 10
- `DIVIDEND_AMOUNT` 必填，`>= 0`
- `RECORD_DATE` 必填
- `DIVIDEND_YIELD` 允许空；非空时应 `>= 0`
- `CONFIRMED_FLG` 建议统一为 `0/1` 或 `N/Y`（项目内统一）

### 4.2 日期逻辑校验

- 非空时建议校验：`RIGHTS_LAST_DAY <= EX_DIVIDEND_DATE <= RECORD_DATE`
- 任一日期为空时，仅校验已填字段，不阻断保存（按业务阶段允许草稿）

### 4.3 业务唯一性建议

- 建议以 `STOCK_CODE + RECORD_DATE` 作为业务唯一键（需与业务确认是否存在同日多配当场景）
- 若存在同日多配当情形，建议补充“配当类型/期别”字段后再做唯一约束

### 4.4 主数据联动

- `STOCK_CODE` 必须存在于 `M_STOCK`（股票主数据）
- 删除或停用股票主数据时，不应物理删除历史配当记录

---

## 5. OpenAPI 需求草案

### 5.1 查询接口

- `GET /api/stock-dividend-confirmed`
  - query: `stockCode`, `recordDateFrom`, `recordDateTo`, `confirmedFlg`, `page`, `size`, `sort`
- `GET /api/stock-dividend-confirmed/{id}`

### 5.2 维护接口

- `POST /api/stock-dividend-confirmed`
- `PUT /api/stock-dividend-confirmed/{id}`
- `DELETE /api/stock-dividend-confirmed/{id}`

### 5.3 确权接口

- `PATCH /api/stock-dividend-confirmed/{id}/confirmed`
- `PATCH /api/stock-dividend-confirmed/{id}/unconfirmed`（可选）
- `POST /api/stock-dividend-confirmed:batch-confirmed`（可选，批量确权）

### 5.4 导入接口（可选）

- `POST /api/stock-dividend-confirmed:import`
  - body: 导入模式、数据列表

---

## 6. 前端页面需求

- 页面名：`股票配当确权管理`
- 查询区：
  - 股票代码、基准日范围、确权状态
- 列表区：
  - 股票代码、每股配当、配当利回り、含权最后日、除权日、基准日、确权状态
- 操作：
  - 新增、编辑、删除、确权、反确权（可选）、批量导入（可选）
- 交互：
  - 已确权记录展示锁定样式，编辑按钮置灰或二次确认
  - 批量确权弹窗显示影响条数
  - 校验失败提示具体字段与原因

---

## 7. 权限需求

建议权限码：

- `STOCK_DIVIDEND_CONFIRMED:VIEW`：查看
- `STOCK_DIVIDEND_CONFIRMED:MANAGE`：新增/编辑/删除
- `STOCK_DIVIDEND_CONFIRMED:CONFIRM`：确权/反确权
- `STOCK_DIVIDEND_CONFIRMED:IMPORT`：批量导入

---

## 8. 验收标准

- 支持按股票、日期区间、确权状态稳定查询，分页准确
- 核心字段校验生效（金额、利回り、日期逻辑、主数据关联）
- 已确权数据默认不可直接编辑，确权流程可追溯
- 批量导入/批量确权结果可反馈成功失败明细
- 权限控制生效，越权访问返回 403

---

## 9. 后续建议（可选）

- 增加索引：`idx_stock_record_date(STOCK_CODE, RECORD_DATE)`
- 增加索引：`idx_confirmed_flg(CONFIRMED_FLG)`
- 根据业务确认后补充唯一约束：`uk_stock_record(STOCK_CODE, RECORD_DATE)`
- 增加审计字段：`CREATED_AT/CREATED_BY/UPDATED_AT/UPDATED_BY`
