-- M_USER schema for user management module.
-- Target: MySQL 8.x

CREATE TABLE IF NOT EXISTS M_USER (
    ID BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
    TENANT_ID VARCHAR(36) NULL COMMENT 'Tenant identifier',
    USERNAME VARCHAR(50) NOT NULL COMMENT 'Login username',
    DISPLAY_NAME VARCHAR(100) NULL COMMENT 'Display name',
    EMAIL VARCHAR(100) NULL COMMENT 'Email',
    EMAIL_VERIFIED TINYINT NOT NULL DEFAULT 0 COMMENT 'Email verification flag: 0/1',
    PHONE VARCHAR(30) NULL COMMENT 'Phone number',
    PHONE_VERIFIED TINYINT NOT NULL DEFAULT 0 COMMENT 'Phone verification flag: 0/1',
    PASSWORD_HASH VARCHAR(100) NOT NULL COMMENT 'BCrypt password hash',
    PASSWORD_CHANGED_AT DATETIME NULL COMMENT 'Password last changed time (UTC)',
    STATUS TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 1=enabled, 0=disabled',
    ACCOUNT_LOCKED TINYINT NOT NULL DEFAULT 0 COMMENT 'Account lock flag: 0=unlocked, 1=locked',
    LOCKED_AT DATETIME NULL COMMENT 'Locked time (UTC)',
    FAILED_LOGIN_COUNT INT NOT NULL DEFAULT 0 COMMENT 'Consecutive failed login count',
    LAST_LOGIN_AT DATETIME NULL COMMENT 'Last login time (UTC)',
    LAST_LOGIN_IP VARCHAR(64) NULL COMMENT 'Last login IP',
    LOCALE VARCHAR(20) NULL COMMENT 'Locale preference',
    TIMEZONE VARCHAR(40) NULL COMMENT 'Timezone preference',
    REMARK TEXT NULL COMMENT 'Remark',
    VERSION INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    CREATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time (UTC)',
    CREATED_BY BIGINT NULL COMMENT 'Created by user id',
    UPDATED_AT DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time (UTC)',
    UPDATED_BY BIGINT NULL COMMENT 'Updated by user id',
    DELETED_AT DATETIME NULL COMMENT 'Soft delete time (UTC)',
    PRIMARY KEY (ID),
    UNIQUE KEY uk_username (USERNAME),
    UNIQUE KEY uk_email (EMAIL),
    UNIQUE KEY uk_phone (PHONE),
    KEY idx_tenant_status (TENANT_ID, STATUS),
    KEY idx_status_locked (STATUS, ACCOUNT_LOCKED),
    KEY idx_deleted (DELETED_AT),
    KEY idx_created_by (CREATED_BY),
    KEY idx_updated_by (UPDATED_BY),
    KEY idx_username_deleted (USERNAME, DELETED_AT)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='User master table';
