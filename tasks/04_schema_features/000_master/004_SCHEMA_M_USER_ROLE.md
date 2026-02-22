# 004_SCHEMA_M_USER_ROLE.md

## 阶段
Schema + Security Master（User ↔ Role）

---

## 0. 依赖（Dependencies）
- Requires: 001_SCHEMA_M_USER.md
- Requires: 002_SCHEMA_M_ROLE.md

---

## 1. 目标

定义用户-角色关联表 `M_USER_ROLE`，并支持扩展：

- 批量分配/覆盖/移除用户角色
- 支持“主角色/默认角色”（可选）
- 支持多租户预留
- 审计字段（谁在什么时候分配的）

---

## 2. 数据库 Schema（MySQL）

表名：`M_USER_ROLE`

### 2.1 字段定义（建议）

| 字段 | 类型 | NULL | 默认值 | 约束/建议 | 说明 |
|---|---|---:|---|---|---|
| USER_ID | BIGINT | NO | - | PK(FK) | 用户ID |
| ROLE_ID | BIGINT | NO | - | PK(FK) | 角色ID |
| TENANT_ID | VARCHAR(36) | YES | NULL | INDEX | 租户ID（可选） |
| IS_PRIMARY | TINYINT | NO | 0 |  | 是否主角色（可选） |
| CREATED_AT | DATETIME | NO | CURRENT_TIMESTAMP |  | 创建时间 |
| CREATED_BY | BIGINT | YES | NULL | INDEX | 创建人 |
| UPDATED_AT | DATETIME | YES | NULL |  | 可选：最近更新时间 |
| UPDATED_BY | BIGINT | YES | NULL | INDEX | 可选：最近更新人 |

> 主键策略：默认建议联合主键 (USER_ID, ROLE_ID)。  
> 如果你更想“每条关联有独立ID便于审计/变更记录”，也可以新增 `ID BIGINT AUTO_INCREMENT` 并改主键，但联合唯一约束仍必须保留。

### 2.2 主键与约束
- PRIMARY KEY (USER_ID, ROLE_ID)
- （可选）若启用 TENANT_ID：建议 UNIQUE (TENANT_ID, USER_ID, ROLE_ID)

### 2.3 索引要求
| 索引名 | 字段 | 目的 |
|---|---|---|
| idx_user_id | USER_ID | 查用户角色 |
| idx_role_id | ROLE_ID | 查角色用户 |
| idx_tenant_user | TENANT_ID, USER_ID | 多租户过滤 |

---

## 3. 业务规则（必须）
- 绑定幂等：重复绑定不报错/不重复插入（由主键保证）
- 解绑策略：允许无角色 / 或强制默认角色（需要在任务里明确）
- 删除用户/角色：必须先清理关联或禁止删除

---

## 4. OpenAPI First（建议）
- GET  /api/users/{userId}/roles
- PUT  /api/users/{userId}/roles（覆盖）
- POST /api/users/{userId}/roles（追加）
- DELETE /api/users/{userId}/roles（移除）

---

## 5. 测试要求
- 覆盖/追加/移除的正确性
- 幂等
- 权限控制（管理员才能改）

mvn clean verify