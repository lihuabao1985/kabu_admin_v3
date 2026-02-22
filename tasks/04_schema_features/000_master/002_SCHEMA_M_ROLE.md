# 002_SCHEMA_M_ROLE.md

## 阶段
Schema + Security Master（Role）

---

## 0. 依赖（Dependencies）
- Requires: 001_SCHEMA_M_USER.md（审计字段 created_by/updated_by 可引用）

---

## 1. 目标

定义角色表 `M_ROLE`，并支持扩展：

- 角色 CRUD
- 角色启用/禁用
- 系统内置角色保护（不可删/不可改关键字段）
- 排序与分组（便于前端管理界面）
- 多租户预留

---

## 2. 数据库 Schema（MySQL）

表名：`M_ROLE`

### 2.1 字段定义（建议）

| 字段 | 类型 | NULL | 默认值 | 约束/建议 | 说明 |
|---|---|---:|---|---|---|
| ID | BIGINT | NO | AUTO_INCREMENT | PK | 主键 |
| TENANT_ID | VARCHAR(36) | YES | NULL | INDEX | 租户ID |
| ROLE_CODE | VARCHAR(50) | NO | - | UNIQUE | 角色编码（如 ROLE_ADMIN） |
| ROLE_NAME | VARCHAR(100) | NO | - |  | 角色名称 |
| DESCRIPTION | VARCHAR(255) | YES | NULL |  | 描述 |
| STATUS | TINYINT | NO | 1 | INDEX | 1=启用 0=禁用 |
| IS_SYSTEM | TINYINT | NO | 0 | INDEX | 1=系统内置（禁止删除/谨慎修改） |
| SORT_ORDER | INT | NO | 0 |  | 排序 |
| REMARK | TEXT | YES | NULL |  | 备注 |
| VERSION | INT | NO | 0 |  | 乐观锁 |
| CREATED_AT | DATETIME | NO | CURRENT_TIMESTAMP |  | 创建时间 |
| CREATED_BY | BIGINT | YES | NULL | INDEX | 创建人 |
| UPDATED_AT | DATETIME | NO | CURRENT_TIMESTAMP | on update CURRENT_TIMESTAMP | 更新时间 |
| UPDATED_BY | BIGINT | YES | NULL | INDEX | 更新人 |
| DELETED_AT | DATETIME | YES | NULL | INDEX | 软删除 |

---

## 3. 约束与索引
- `ROLE_CODE` UNIQUE
- 查询默认过滤：DELETED_AT IS NULL

索引建议：
- idx_tenant_status (TENANT_ID, STATUS)
- idx_is_system (IS_SYSTEM)
- idx_deleted (DELETED_AT)

---

## 4. OpenAPI First（建议）
- GET /api/roles（分页+条件：roleCode/roleName/status/tenantId）
- GET /api/roles/{id}
- POST /api/roles（管理员）
- PUT /api/roles/{id}（管理员）
- PATCH /api/roles/{id}/status
- DELETE /api/roles/{id}（软删除；IS_SYSTEM=1 禁止）

---

## 5. 实现要求
- 禁止删除仍被用户/权限引用的角色（或必须先解除关联）
- IS_SYSTEM=1 的角色：禁止删除；更新需额外校验（比如禁止改 ROLE_CODE）
- MyBatis：禁止 SELECT *；分页必做

---

## 6. 测试要求
- 创建角色、唯一冲突
- 系统角色删除拒绝
- 软删除过滤
- 并发更新（VERSION）

mvn clean verify