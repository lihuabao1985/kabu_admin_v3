# 100_SCHEMA_M_STOCK_PRICE_HISTORY

## 1. 目标

基于 `M_STOCK_PRICE_HISTORY`（株价履历管理表）建设“股票日线历史行情管理”能力，支撑：

- 按股票和日期范围查询历史行情
- 导入/同步日线行情数据（批量）
- 对异常行情进行人工修正与备注
- 为后续图表分析、异动检测、公告联动提供标准数据源

---

## 2. 表结构业务含义

表名：`M_STOCK_PRICE_HISTORY`

主字段说明：

- `STOCK_CODE`：股票代码（关联股票主数据）
- `TRANS_DATE`：交易日期（按日粒度）
- `BEFORE_DAY_PRICE`：前日收盘价
- `OPEN_PRICE/HIGH_PRICE/LOW_PRICE/CLOSE_PRICE`：开高低收
- `ADJUSTED_CLOSE_PRICE`：复权收盘价
- `BEFORE_DAY_DIFF`：较前日涨跌额
- `BEFORE_DAY_DIFF_PERCENT`：较前日涨跌幅（%）
- `VOLUME`：成交量（股）
- `REMARK`：人工备注
- `ID`：自增主键

---

## 3. 功能需求

### 3.1 历史行情查询

- 支持按 `STOCK_CODE` 精确查询
- 支持按 `TRANS_DATE` 区间查询（开始/结束）
- 支持分页、排序（默认 `TRANS_DATE desc`）
- 支持查看单日明细（按 `ID` 或 `STOCK_CODE + TRANS_DATE`）

### 3.2 历史行情导入（批量）

- 支持批量导入（CSV/外部行情源）
- 导入模式：
  - 覆盖更新（同股票同交易日已有记录则更新）
  - 跳过重复（已有则不处理）
- 返回导入结果统计：总数、成功数、失败数、失败原因

### 3.3 行情维护（人工修正）

- 支持新增单条记录
- 支持编辑单条记录（价格、成交量、备注）
- 支持删除单条记录（仅管理员）
- 支持给异常数据追加 `REMARK`

### 3.4 数据质量校验

- 价格与成交量非负
- `HIGH_PRICE >= OPEN_PRICE/CLOSE_PRICE/LOW_PRICE`
- `LOW_PRICE <= OPEN_PRICE/CLOSE_PRICE/HIGH_PRICE`
- `BEFORE_DAY_DIFF = CLOSE_PRICE - BEFORE_DAY_PRICE`（允许空值时跳过）
- `BEFORE_DAY_DIFF_PERCENT` 与涨跌额一致（保留 2 位小数）

---

## 4. 关键业务规则

- 一只股票在一个交易日只应有一条有效行情  
  建议在库层补充唯一约束：`(STOCK_CODE, TRANS_DATE)`
- `STOCK_CODE` 必须存在于股票主数据表（如 `M_STOCK_MASTER`）
- 导入时默认按“交易日 + 股票代码”进行幂等处理
- 对历史修正必须记录操作人和时间（审计能力）

---

## 5. OpenAPI 需求草案

### 5.1 查询接口

- `GET /api/stocks/{stockCode}/price-history`
  - query: `dateFrom`, `dateTo`, `page`, `size`, `sort`
- `GET /api/stock-price-history/{id}`

### 5.2 维护接口

- `POST /api/stock-price-history`
- `PUT /api/stock-price-history/{id}`
- `DELETE /api/stock-price-history/{id}`

### 5.3 导入接口

- `POST /api/stock-price-history:import`
  - body: 导入模式、数据列表

---

## 6. 前端页面需求

- 页面名：`股票历史行情管理`
- 查询区：
  - 股票代码、交易日期范围
- 列表区：
  - 交易日、开高低收、前日价、涨跌额、涨跌幅、成交量、备注
- 操作：
  - 新增、编辑、删除、批量导入
- 交互：
  - 导入结果弹窗（成功/失败统计）
  - 校验失败行提示具体原因

---

## 7. 权限需求

建议权限码：

- `STOCK_PRICE_HISTORY:VIEW`：查看
- `STOCK_PRICE_HISTORY:MANAGE`：新增/编辑/删除
- `STOCK_PRICE_HISTORY:IMPORT`：批量导入

---

## 8. 验收标准

- 可按股票+日期范围稳定查询，分页准确
- 批量导入可幂等执行，不产生重复脏数据
- 校验规则生效，错误数据可被拦截并反馈原因
- 人工修正后数据可立即查询到
- 权限控制生效，越权访问返回 403

---

## 9. 后续建议（可选）

- 增加索引：`idx_stock_date(STOCK_CODE, TRANS_DATE)`
- 如需长期保存高频历史，后续可按年份或股票分区
- 增加“复权类型”与“数据来源”字段提升可追溯性
