# 001_SCHEMA_M_USER.md

## 阶段
Schema + Security Master（User）

---

## 0. 依赖（Dependencies）
- Requires: 01_bootstrap/01_BOOTSTRAP.md
- Requires (optional): 02_security/03_PERMISSION_MODULE.md（若要做权限端到端）

---

## 1. 目标

定义用户表 `M_USER`，并基于该表生成：

- 用户 CRUD（管理员）
- 用户分页/条件检索
- 用户启用/禁用、锁定/解锁
- 登录标识（username/email/phone 可扩展）
- Spring Security 用户加载（UserDetailsService）
- 为后续审计、软删除、乐观锁、多租户预留字段

OpenAPI First：先定义 OpenAPI 再实现代码。

---

## 2. 数据库 Schema（MySQL）

表名：`M_USER`  
Engine: InnoDB  
Charset/Collation: utf8mb4 / utf8mb4_0900_ai_ci（建议统一到 utf8mb4）

### 2.1 字段定义（建议）

| 字段 | 类型 | NULL | 默认值 | 约束/建议 | 说明 |
|---|---|---:|---|---|---|
| ID | BIGINT | NO | AUTO_INCREMENT | PK | 主键 |
| TENANT_ID | VARCHAR(36) | YES | NULL | INDEX | 租户ID（单租户可为空） |
| USERNAME | VARCHAR(50) | NO | - | UNIQUE | 登录名（唯一） |
| DISPLAY_NAME | VARCHAR(100) | YES | NULL |  | 显示名/昵称 |
| EMAIL | VARCHAR(100) | YES | NULL | UNIQUE(可选) | 邮箱（可作为登录标识） |
| EMAIL_VERIFIED | TINYINT | NO | 0 |  | 邮箱是否已验证（0/1） |
| PHONE | VARCHAR(30) | YES | NULL | UNIQUE(可选) | 手机号（可作为登录标识） |
| PHONE_VERIFIED | TINYINT | NO | 0 |  | 手机是否已验证（0/1） |
| PASSWORD_HASH | VARCHAR(100) | NO | - |  | BCrypt 密码哈希 |
| PASSWORD_CHANGED_AT | DATETIME | YES | NULL |  | 密码最近修改时间（用于强制改密策略） |
| STATUS | TINYINT | NO | 1 | INDEX | 1=启用 0=禁用 |
| ACCOUNT_LOCKED | TINYINT | NO | 0 | INDEX | 0=未锁 1=锁定 |
| LOCKED_AT | DATETIME | YES | NULL |  | 锁定时间 |
| FAILED_LOGIN_COUNT | INT | NO | 0 |  | 连续失败次数 |
| LAST_LOGIN_AT | DATETIME | YES | NULL |  | 最近登录时间 |
| LAST_LOGIN_IP | VARCHAR(64) | YES | NULL |  | 最近登录IP（可选） |
| LOCALE | VARCHAR(20) | YES | NULL |  | 语言偏好（如 zh-CN/ja-JP） |
| TIMEZONE | VARCHAR(40) | YES | NULL |  | 时区（如 Asia/Tokyo） |
| REMARK | TEXT | YES | NULL |  | 备注 |
| VERSION | INT | NO | 0 |  | 乐观锁版本号 |
| CREATED_AT | DATETIME | NO | CURRENT_TIMESTAMP |  | 创建时间（UTC） |
| CREATED_BY | BIGINT | YES | NULL | INDEX | 创建人（用户ID） |
| UPDATED_AT | DATETIME | NO | CURRENT_TIMESTAMP | on update CURRENT_TIMESTAMP | 更新时间（UTC） |
| UPDATED_BY | BIGINT | YES | NULL | INDEX | 更新人（用户ID） |
| DELETED_AT | DATETIME | YES | NULL | INDEX | 软删除时间（为空表示未删除） |

> 说明：  
> - 禁止明文密码：只保存 `PASSWORD_HASH`。  
> - `TENANT_ID` 可为空：将来做多租户时无需大改表结构。  
> - `VERSION` 为并发更新预留（尤其后台管理系统很常见）。

---

## 3. 约束与索引（必须）

### 3.1 唯一约束（建议至少一个）
- `USERNAME` UNIQUE
- （可选）`EMAIL` UNIQUE（若邮箱可登录）
- （可选）`PHONE` UNIQUE（若手机可登录）

### 3.2 索引（建议）
| 索引名 | 字段 | 目的 |
|---|---|---|
| uk_username | USERNAME | 登录查询 |
| idx_tenant_status | TENANT_ID, STATUS | 租户+状态过滤 |
| idx_status_locked | STATUS, ACCOUNT_LOCKED | 管理列表过滤 |
| idx_deleted | DELETED_AT | 软删过滤 |
| idx_created_by | CREATED_BY | 审计 |
| idx_updated_by | UPDATED_BY | 审计 |

---

## 4. OpenAPI First 设计

### 4.1 API（建议）
- GET /api/users（分页+条件：username/email/status/locked/tenantId）
- GET /api/users/{id}
- POST /api/users（管理员）
- PUT /api/users/{id}（管理员）
- PATCH /api/users/{id}/status（启用/禁用）
- PATCH /api/users/{id}/lock（锁定/解锁）
- DELETE /api/users/{id}（软删除）

### 4.2 DTO 规则
- 响应 DTO 不得包含：PASSWORD_HASH
- 请求 DTO 中密码字段仅用于创建/重置，落库必须哈希

---

## 5. 后端实现要求
- 严格分层：Controller → Service → Repository(MyBatis)
- MyBatis：禁止 SELECT *，字段显式列出
- 查询默认过滤：DELETED_AT IS NULL
- 状态/锁定/失败次数策略明确（失败次数阈值可配置）

---

## 6. 安全要求（Spring Security）
- 用户加载优先用 USERNAME（可扩展到 email/phone）
- PasswordEncoder = BCrypt
- 被禁用/被锁定用户不可登录（返回统一错误码，不泄露细节）

---

## 7. 测试要求（JUnit5 + Mockito + SpringBootTest）
必须覆盖：
- 创建用户（密码哈希）
- 用户唯一性冲突（username/email/phone）
- 禁用/锁定用户登录拒绝
- 软删除过滤生效
- 并发更新（VERSION）策略（至少一个乐观锁冲突用例）

构建：
- mvn clean verify

---

## 8. 输出要求
- migration 脚本（建表/索引/约束）
- Diff 概要
- 测试结果
- 风险点与回滚方案