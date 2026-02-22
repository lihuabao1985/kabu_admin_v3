# 003_SCHEMA_M_PERMISSION.md

## 阶段
Schema + Security Master（Permission）

---

## 0. 依赖（Dependencies）
- Requires: 002_SCHEMA_M_ROLE.md（后续角色绑定权限）
- Requires: 005_SCHEMA_M_ROLE_PERMISSION.md（若你拆分文件，可后置；这里先定义标准）

---

## 1. 目标

定义权限表 `M_PERMISSION`，支持企业级扩展：

- 权限 CRUD
- 权限分组/分类/排序（用于管理与动态菜单）
- 资源维度（API 路径 + HTTP 方法）
- 支持按钮级权限（可选：resource_type=UI）
- 多租户预留
- 软删除与乐观锁

---

## 2. 数据库 Schema（MySQL）

表名：`M_PERMISSION`

### 2.1 字段定义（建议）

| 字段 | 类型 | NULL | 默认值 | 约束/建议 | 说明 |
|---|---|---:|---|---|---|
| ID | BIGINT | NO | AUTO_INCREMENT | PK | 主键 |
| TENANT_ID | VARCHAR(36) | YES | NULL | INDEX | 租户ID |
| PERMISSION_CODE | VARCHAR(120) | NO | - | UNIQUE | 权限编码（如 STOCK:PRICE_HISTORY:READ） |
| PERMISSION_NAME | VARCHAR(120) | NO | - |  | 权限名称 |
| DESCRIPTION | VARCHAR(255) | YES | NULL |  | 描述 |
| STATUS | TINYINT | NO | 1 | INDEX | 1=启用 0=禁用 |
| RESOURCE_TYPE | VARCHAR(20) | NO | 'API' | INDEX | API/UI/DATA 等 |
| RESOURCE | VARCHAR(200) | NO | - | INDEX | 资源标识（API 路径或前端路由/菜单 key） |
| HTTP_METHOD | VARCHAR(10) | YES | NULL | INDEX | GET/POST/PUT/DELETE（RESOURCE_TYPE=API 时推荐填） |
| ACTION | VARCHAR(50) | YES | NULL | INDEX | READ/WRITE/DELETE/EXECUTE（可与 HTTP_METHOD 二选一或共存） |
| PERMISSION_GROUP | VARCHAR(60) | YES | NULL | INDEX | 分组（如 SECURITY/STOCK/EMS） |
| SORT_ORDER | INT | NO | 0 |  | 排序 |
| UI_MENU_KEY | VARCHAR(120) | YES | NULL | INDEX | 绑定菜单 key（用于动态菜单，可选） |
| UI_ROUTE | VARCHAR(200) | YES | NULL |  | 绑定前端路由（可选） |
| REMARK | TEXT | YES | NULL |  | 备注 |
| VERSION | INT | NO | 0 |  | 乐观锁 |
| CREATED_AT | DATETIME | NO | CURRENT_TIMESTAMP |  | 创建时间 |
| CREATED_BY | BIGINT | YES | NULL | INDEX | 创建人 |
| UPDATED_AT | DATETIME | NO | CURRENT_TIMESTAMP | on update CURRENT_TIMESTAMP | 更新时间 |
| UPDATED_BY | BIGINT | YES | NULL | INDEX | 更新人 |
| DELETED_AT | DATETIME | YES | NULL | INDEX | 软删除 |

---

## 3. 约束与索引（建议）
- PERMISSION_CODE UNIQUE
- 建议组合索引：
  - idx_res_method (RESOURCE, HTTP_METHOD)
  - idx_group_status (PERMISSION_GROUP, STATUS)
- 查询默认过滤：DELETED_AT IS NULL

---

## 4. OpenAPI First（建议）
- GET /api/permissions（分页+条件：group/status/resourceType/resource/code）
- GET /api/permissions/{id}
- POST /api/permissions（管理员）
- PUT /api/permissions/{id}（管理员）
- PATCH /api/permissions/{id}/status
- DELETE /api/permissions/{id}（软删）

---

## 5. 实现要求
- 权限码命名必须遵循规范（DOMAIN:RESOURCE:ACTION）
- 禁止 SELECT *
- 分页必做，排序字段白名单（防注入）
- STATUS=0 的权限不应被授权（绑定时校验）

---

## 6. 测试要求
- 创建权限、唯一冲突
- 软删过滤
- resource+method 查询正确
- 并发更新（VERSION）

mvn clean verify