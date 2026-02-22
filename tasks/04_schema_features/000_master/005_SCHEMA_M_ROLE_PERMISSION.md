# 005_SCHEMA_M_ROLE_PERMISSION.md

## 阶段
Schema + Security Master（Role ↔ Permission）

---

## 0. 依赖（Dependencies）
- Requires: 002_SCHEMA_M_ROLE.md
- Requires: 003_SCHEMA_M_PERMISSION.md

---

## 1. 目标

定义角色-权限关联表 `M_ROLE_PERMISSION`，并支持扩展：

- 给角色批量绑定权限（覆盖/追加/移除）
- 支持权限生效范围（可选：scope）
- 审计字段
- 多租户预留
- 支持动态菜单（通过 permission 的 UI_MENU_KEY/UI_ROUTE）

---

## 2. 数据库 Schema（MySQL）

表名：`M_ROLE_PERMISSION`

### 2.1 字段定义（建议）

| 字段 | 类型 | NULL | 默认值 | 约束/建议 | 说明 |
|---|---|---:|---|---|---|
| ROLE_ID | BIGINT | NO | - | PK(FK) | 角色ID |
| PERMISSION_ID | BIGINT | NO | - | PK(FK) | 权限ID |
| TENANT_ID | VARCHAR(36) | YES | NULL | INDEX | 租户ID（可选） |
| SCOPE | VARCHAR(50) | YES | NULL |  | 权限范围（可选，如 GLOBAL/OWN/DEPT） |
| CREATED_AT | DATETIME | NO | CURRENT_TIMESTAMP |  | 创建时间 |
| CREATED_BY | BIGINT | YES | NULL | INDEX | 创建人 |
| UPDATED_AT | DATETIME | YES | NULL |  | 可选 |
| UPDATED_BY | BIGINT | YES | NULL | INDEX | 可选 |

### 2.2 主键与约束
- PRIMARY KEY (ROLE_ID, PERMISSION_ID)
- （可选）若启用 TENANT_ID：建议 UNIQUE (TENANT_ID, ROLE_ID, PERMISSION_ID)

### 2.3 索引要求
| 索引名 | 字段 | 目的 |
|---|---|---|
| idx_role_id | ROLE_ID | 查角色权限 |
| idx_perm_id | PERMISSION_ID | 查权限被哪些角色引用 |
| idx_tenant_role | TENANT_ID, ROLE_ID | 多租户过滤 |

---

## 3. 业务规则（必须）
- 绑定幂等
- 禁止绑定 STATUS=0 的权限（或绑定后无效，需明确）
- 删除权限/角色时必须先清理关联或禁止删除

---

## 4. OpenAPI First（建议）
- GET  /api/roles/{roleId}/permissions
- PUT  /api/roles/{roleId}/permissions（覆盖）
- POST /api/roles/{roleId}/permissions（追加）
- DELETE /api/roles/{roleId}/permissions（移除）

（可选）
- GET /api/permissions/{permissionId}/roles

---

## 5. 安全要求（Spring Security）
- 仅管理员可修改角色权限
- 权限变更后必须有缓存刷新策略（若有缓存）
- 后端必须强制 403（前端只做隐藏）

---

## 6. 测试要求
- 覆盖/追加/移除正确性
- 幂等
- 权限控制
- 变更后权限生效（至少一个集成测试）

mvn clean verify