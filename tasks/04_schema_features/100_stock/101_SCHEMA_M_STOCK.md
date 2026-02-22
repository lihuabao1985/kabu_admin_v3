# 101_SCHEMA_M_STOCK

## 1. 目标

基于 `M_STOCK`（銘柄管理表）建设“股票主数据管理”能力，支撑：

- 股票基础信息统一维护（代码、名称、行业、市场、财务指标、股东结构等）
- 为行情历史、公告、选股策略提供稳定主数据源
- 提供批量导入与增量更新能力，支持业务持续同步
- 通过标志位与软删除管理股票生命周期

---

## 2. 表结构业务含义

表名：`M_STOCK`

主字段分组说明：

- 主标识信息：
  - `ID`、`STOCK_CODE`、`STOCK_NAME`
- 行业与市场信息：
  - `TYPE_CODE`、`TYPE_NAME`、`MARKET`、`SECURITIES`、`BANK`
- 基础时间与上市信息：
  - `DATE`、`ESTABLISHED`、`LISTED`、`SETTLEMENT`、`SETTLEMENT_DAY`
- 交易与规模信息：
  - `STOCK_PRICE`、`TOTAL_KABU`、`TRADING_UNIT`、`TOTAL_ZIKA`
- 财务与估值信息：
  - `TOTAL_ASSETS`、`NET_WORTH`、`CAPITAL`、`EARNED_SURPLUS`、`INTEREST_BEARING_DEBT`
  - `PER`、`EPS`、`PBR`、`PSR`、`ROE`、`ROA`
  - `SALES`、`ORDINARY_PROFIT`、`OPERATING_PROFIT_MARGIN`、`ORDINARY_PROFIT_MARGIN`
  - `CAPITAL_ADEQUACY_RATIO`、`CURRENT_RATIO`
- 配当与权益信息：
  - `DIVIDEND`、`EXPECTED_DIVIDEND`、`FIXED_DIVIDEND`
  - `LASTDATE_WITH_ENTITLEMENT`、`DIVIDEND_DATE`、`VESTING_DATE`
- 信用与持股结构：
  - `CREDIT_BALANCE_BUY`、`CREDIT_BALANCE_SELL`
  - `COMPARED_PREVIOUS_WEEK_BUY`、`COMPARED_PREVIOUS_WEEK_SELL`
  - `CREDIT_MULTIPLIER`、`CREDIT_RENTAL_DIV`
  - `GAIKOKU_KABU`、`HUDOU_KABU`、`TOUSIN_KABU`、`TOKUTEI_KABU`
- 文本扩展信息：
  - `TEKUSYOKU`、`RENKETU_ZIGYO`、`SUPPLIER`、`SALES_DESTINATION`
  - `HOMEPAGE`、`NEWS_URL`、`IR_URL`
- 标志位：
  - `PREFERENTIAL_FLG`、`DIVIDEND_FLG`
  - `ICHIMOKU_UPPER_FLG`、`ICHIMOKU_DOWLOAD_FLG`
  - `DEL_FLG`

---

## 3. 功能需求

### 3.1 股票主数据查询

- 支持按 `STOCK_CODE` 精确查询
- 支持按 `STOCK_NAME` 模糊查询
- 支持按 `TYPE_CODE/TYPE_NAME`、`MARKET`、`DEL_FLG` 过滤
- 支持按标志位过滤（优待、配当、一目均衡上下穿）
- 支持分页、排序（默认 `ID desc`）
- 支持查看单条明细（按 `ID`）

### 3.2 股票主数据维护

- 支持新增单条股票资料
- 支持编辑单条股票资料
- 支持逻辑删除（写 `DEL_FLG`，不物理删除）
- 支持恢复已逻辑删除数据

### 3.3 批量导入与同步

- 支持 CSV/外部数据源批量导入
- 导入模式：
  - 全量刷新（按 `STOCK_CODE` 覆盖并同步删除标记）
  - 增量更新（仅更新传入股票）
- 支持幂等：同一 `STOCK_CODE` 重复导入不产生重复记录
- 返回导入统计：总数、成功数、更新数、新增数、失败数、失败原因

### 3.4 主数据联动能力

- 为 `M_STOCK_PRICE_HISTORY` 提供 `STOCK_CODE` 主数据校验
- 对外提供股票下拉候选接口（代码+名称）
- 支持按市场/行业输出基础字典，用于前端筛选项

---

## 4. 数据质量与业务规则

### 4.1 必填与唯一

- `STOCK_CODE` 必填，且全局唯一
- `STOCK_NAME` 建议必填

### 4.2 格式校验

- `STOCK_CODE` 仅允许字母数字及常见交易所后缀符号（按业务规则定义）
- 日期类字符串字段（如 `DATE`、`SETTLEMENT_DAY`、`DIVIDEND_DATE`）统一格式（建议 `yyyy-MM-dd`）
- URL 字段（`HOMEPAGE`、`NEWS_URL`、`IR_URL`）需满足 URL 格式

### 4.3 数值类字符串规范

- 价格、估值、财务类字段虽为 `varchar`，入库前应做可解析校验
- 允许空值；非空时需可转为数值（可带千分位时先清洗）
- 百分比字段统一不带 `%` 符号入库（展示层补充）

### 4.4 标志位规范

- 标志位字段统一取值约定：`0/1` 或 `Y/N`（需项目内统一）
- `DEL_FLG`：`0`=有效，`1`=逻辑删除（建议默认 `0`）

---

## 5. OpenAPI 需求草案

### 5.1 查询接口

- `GET /api/stocks`
  - query: `stockCode`, `stockName`, `typeCode`, `market`, `delFlg`, `page`, `size`, `sort`
- `GET /api/stocks/{id}`
- `GET /api/stocks/options`
  - query: `keyword`, `market`, `limit`

### 5.2 维护接口

- `POST /api/stocks`
- `PUT /api/stocks/{id}`
- `PATCH /api/stocks/{id}/delete-flag`
- `DELETE /api/stocks/{id}`（如采用逻辑删除，可映射为设置 `DEL_FLG=1`）

### 5.3 导入接口

- `POST /api/stocks:import`
  - body: 导入模式、数据列表

---

## 6. 前端页面需求

- 页面名：`股票主数据管理`
- 查询区：
  - 股票代码、股票名称、行业、市场、删除标记、分页条件
- 列表区：
  - 代码、名称、行业、市场、最新股价、配当标记、优待标记、删除标记
- 详情/编辑区：
  - 按字段分组展示（基础信息、财务指标、配当权益、信用信息、链接信息）
- 操作：
  - 新增、编辑、逻辑删除、恢复、批量导入
- 交互：
  - 导入结果弹窗（新增/更新/失败统计）
  - 字段校验错误提示到具体字段

---

## 7. 权限需求

建议权限码：

- `STOCK:VIEW`：查看
- `STOCK:MANAGE`：新增/编辑/删除/恢复
- `STOCK:IMPORT`：批量导入

---

## 8. 验收标准

- 支持多条件查询且分页准确
- 批量导入幂等，重复数据不生成重复记录
- `STOCK_CODE` 唯一约束有效
- 标志位、日期、URL、数值类字段校验生效
- 逻辑删除后默认查询不返回（可按条件查看）
- 权限控制生效，越权返回 403

---

## 9. 后续建议（可选）

- 在库层增加唯一索引：`uk_stock_code(STOCK_CODE)`
- 增加常用索引：`idx_market(MARKET)`、`idx_type_code(TYPE_CODE)`、`idx_del_flg(DEL_FLG)`
- 中长期建议将数值类 `varchar` 字段逐步拆分为数值类型（`decimal/bigint`），降低计算和排序风险
- 增加审计字段（创建/更新人、时间）提升可追溯性
