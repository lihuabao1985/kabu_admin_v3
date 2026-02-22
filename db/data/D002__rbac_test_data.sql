-- RBAC 相关表测试数据（基于 000_master 文档）。
-- 依赖: D001__m_user_test_data.sql
-- 预置账号:
--   用户名: admin
--   密码: 123456

-- 先清理旧的绑定关系。
DELETE FROM M_ROLE_PERMISSION
WHERE ROLE_ID IN (
    SELECT ID FROM M_ROLE WHERE ROLE_CODE IN ('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SECURITY_MANAGER')
)
   OR PERMISSION_ID IN (
    SELECT ID FROM M_PERMISSION WHERE PERMISSION_CODE IN (
        'USER:MANAGE',
        'USER:VIEW',
        'ROLE:MANAGE',
        'PERMISSION:MANAGE',
        'RBAC:ASSIGN_ROLE',
        'RBAC:ASSIGN_PERMISSION',
        'STOCK:VIEW',
        'STOCK:MANAGE',
        'STOCK:IMPORT',
        'STOCK_PRICE_HISTORY:VIEW',
        'STOCK_PRICE_HISTORY:MANAGE',
        'STOCK_PRICE_HISTORY:IMPORT',
        'STOCK_DIVIDEND_CONFIRMED:VIEW',
        'STOCK_DIVIDEND_CONFIRMED:MANAGE',
        'STOCK_DIVIDEND_CONFIRMED:CONFIRM',
        'STOCK_DIVIDEND_CONFIRMED:IMPORT'
    )
);

DELETE FROM M_USER_ROLE
WHERE USER_ID IN (
    SELECT ID FROM M_USER WHERE USERNAME IN ('admin', 'operator_enabled')
)
   OR ROLE_ID IN (
    SELECT ID FROM M_ROLE WHERE ROLE_CODE IN ('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SECURITY_MANAGER')
);

-- 清理旧的主数据。
DELETE FROM M_PERMISSION
WHERE PERMISSION_CODE IN (
    'USER:MANAGE',
    'USER:VIEW',
    'ROLE:MANAGE',
    'PERMISSION:MANAGE',
    'RBAC:ASSIGN_ROLE',
    'RBAC:ASSIGN_PERMISSION',
    'STOCK:VIEW',
    'STOCK:MANAGE',
    'STOCK:IMPORT',
    'STOCK_PRICE_HISTORY:VIEW',
    'STOCK_PRICE_HISTORY:MANAGE',
    'STOCK_PRICE_HISTORY:IMPORT',
    'STOCK_DIVIDEND_CONFIRMED:VIEW',
    'STOCK_DIVIDEND_CONFIRMED:MANAGE',
    'STOCK_DIVIDEND_CONFIRMED:CONFIRM',
    'STOCK_DIVIDEND_CONFIRMED:IMPORT'
);

DELETE FROM M_ROLE
WHERE ROLE_CODE IN ('ROLE_ADMIN', 'ROLE_OPERATOR', 'ROLE_SECURITY_MANAGER');

-- 初始化角色数据。
INSERT INTO M_ROLE (
    TENANT_ID,
    ROLE_CODE,
    ROLE_NAME,
    DESCRIPTION,
    STATUS,
    IS_SYSTEM,
    SORT_ORDER,
    REMARK,
    VERSION,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY,
    DELETED_AT
) VALUES
(
    NULL,
    'ROLE_ADMIN',
    '管理员',
    '全局管理员角色',
    1,
    1,
    10,
    '系统内置管理员角色',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    'tenant-a',
    'ROLE_SECURITY_MANAGER',
    '安全管理员',
    '负责角色与权限管理',
    1,
    0,
    20,
    'RBAC 管理角色',
    0,
    UTC_TIMESTAMP(),
    1,
    UTC_TIMESTAMP(),
    1,
    NULL
),
(
    'tenant-a',
    'ROLE_OPERATOR',
    '操作员',
    '只读操作角色',
    1,
    0,
    30,
    '基础操作角色',
    0,
    UTC_TIMESTAMP(),
    1,
    UTC_TIMESTAMP(),
    1,
    NULL
);

-- 初始化权限数据。
INSERT INTO M_PERMISSION (
    TENANT_ID,
    PERMISSION_CODE,
    PERMISSION_NAME,
    DESCRIPTION,
    STATUS,
    RESOURCE_TYPE,
    RESOURCE,
    HTTP_METHOD,
    ACTION,
    PERMISSION_GROUP,
    SORT_ORDER,
    UI_MENU_KEY,
    UI_ROUTE,
    REMARK,
    VERSION,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY,
    DELETED_AT
) VALUES
(
    NULL,
    'USER:MANAGE',
    '用户管理',
    '创建、更新、删除用户',
    1,
    'API',
    '/api/users',
    'GET',
    'WRITE',
    'SECURITY',
    10,
    'users',
    '/users',
    '用户管理权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'USER:VIEW',
    '用户查看',
    '查看用户数据',
    1,
    'API',
    '/api/users',
    'GET',
    'READ',
    'SECURITY',
    20,
    'users',
    '/users',
    '用户查看权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'ROLE:MANAGE',
    '角色管理',
    '创建、更新、删除角色',
    1,
    'API',
    '/api/roles',
    'GET',
    'WRITE',
    'SECURITY',
    30,
    'roles',
    '/roles',
    '角色管理权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'PERMISSION:MANAGE',
    '权限管理',
    '创建、更新、删除权限',
    1,
    'API',
    '/api/permissions',
    'GET',
    'WRITE',
    'SECURITY',
    40,
    'permissions',
    '/permissions',
    '权限管理权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'RBAC:ASSIGN_ROLE',
    '分配用户角色',
    '维护用户-角色绑定关系',
    1,
    'API',
    '/api/users/{userId}/roles',
    'PUT',
    'WRITE',
    'SECURITY',
    50,
    'userRoles',
    '/user-roles',
    '用户角色绑定权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'RBAC:ASSIGN_PERMISSION',
    '分配角色权限',
    '维护角色-权限绑定关系',
    1,
    'API',
    '/api/roles/{roleId}/permissions',
    'PUT',
    'WRITE',
    'SECURITY',
    60,
    'rolePermissions',
    '/role-permissions',
    '角色权限绑定权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK:VIEW',
    '股票查看',
    '查看股票主数据',
    1,
    'API',
    '/api/stocks',
    'GET',
    'READ',
    'STOCK',
    70,
    'stocks',
    '/stocks',
    '股票查看权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK:MANAGE',
    '股票维护',
    '新增、编辑、删除、恢复股票主数据',
    1,
    'API',
    '/api/stocks',
    'POST',
    'WRITE',
    'STOCK',
    80,
    'stocks',
    '/stocks',
    '股票维护权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK:IMPORT',
    '股票导入',
    '批量导入股票主数据',
    1,
    'API',
    '/api/stocks:import',
    'POST',
    'WRITE',
    'STOCK',
    90,
    'stocks',
    '/stocks',
    '股票导入权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK_PRICE_HISTORY:VIEW',
    '股票历史行情查看',
    '查看股票历史行情数据',
    1,
    'API',
    '/api/stocks/{stockCode}/price-history',
    'GET',
    'READ',
    'STOCK',
    100,
    'stockPriceHistory',
    '/stock-price-history',
    '股票历史行情查看权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK_PRICE_HISTORY:MANAGE',
    '股票历史行情维护',
    '新增、编辑、删除股票历史行情',
    1,
    'API',
    '/api/stock-price-history',
    'POST',
    'WRITE',
    'STOCK',
    110,
    'stockPriceHistory',
    '/stock-price-history',
    '股票历史行情维护权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK_PRICE_HISTORY:IMPORT',
    '股票历史行情导入',
    '批量导入股票历史行情',
    1,
    'API',
    '/api/stock-price-history:import',
    'POST',
    'WRITE',
    'STOCK',
    120,
    'stockPriceHistory',
    '/stock-price-history',
    '股票历史行情导入权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK_DIVIDEND_CONFIRMED:VIEW',
    '股票配当确权查看',
    '查看股票配当确权数据',
    1,
    'API',
    '/api/stock-dividend-confirmed',
    'GET',
    'READ',
    'STOCK',
    130,
    'stockDividendConfirmed',
    '/stock-dividend-confirmed',
    '股票配当确权查看权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK_DIVIDEND_CONFIRMED:MANAGE',
    '股票配当确权维护',
    '新增、编辑、删除股票配当确权记录',
    1,
    'API',
    '/api/stock-dividend-confirmed',
    'POST',
    'WRITE',
    'STOCK',
    140,
    'stockDividendConfirmed',
    '/stock-dividend-confirmed',
    '股票配当确权维护权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK_DIVIDEND_CONFIRMED:CONFIRM',
    '股票配当确权操作',
    '执行确权与反确权操作',
    1,
    'API',
    '/api/stock-dividend-confirmed/{id}/confirmed',
    'PATCH',
    'WRITE',
    'STOCK',
    150,
    'stockDividendConfirmed',
    '/stock-dividend-confirmed',
    '股票配当确权操作权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
),
(
    NULL,
    'STOCK_DIVIDEND_CONFIRMED:IMPORT',
    '股票配当确权导入',
    '批量导入股票配当确权数据',
    1,
    'API',
    '/api/stock-dividend-confirmed:import',
    'POST',
    'WRITE',
    'STOCK',
    160,
    'stockDividendConfirmed',
    '/stock-dividend-confirmed',
    '股票配当确权导入权限',
    0,
    UTC_TIMESTAMP(),
    NULL,
    UTC_TIMESTAMP(),
    NULL,
    NULL
);

-- 绑定用户与角色。
INSERT INTO M_USER_ROLE (
    USER_ID,
    ROLE_ID,
    TENANT_ID,
    IS_PRIMARY,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY
)
SELECT
    u.ID,
    r.ID,
    COALESCE(u.TENANT_ID, NULL),
    1,
    UTC_TIMESTAMP(),
    1,
    UTC_TIMESTAMP(),
    1
FROM M_USER u
INNER JOIN M_ROLE r ON r.ROLE_CODE = 'ROLE_ADMIN'
WHERE u.USERNAME = 'admin';

INSERT INTO M_USER_ROLE (
    USER_ID,
    ROLE_ID,
    TENANT_ID,
    IS_PRIMARY,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY
)
SELECT
    u.ID,
    r.ID,
    u.TENANT_ID,
    1,
    UTC_TIMESTAMP(),
    1,
    UTC_TIMESTAMP(),
    1
FROM M_USER u
INNER JOIN M_ROLE r ON r.ROLE_CODE = 'ROLE_OPERATOR'
WHERE u.USERNAME = 'operator_enabled';

-- 绑定角色与权限。
INSERT INTO M_ROLE_PERMISSION (
    ROLE_ID,
    PERMISSION_ID,
    TENANT_ID,
    SCOPE,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY
)
SELECT
    r.ID,
    p.ID,
    r.TENANT_ID,
    'GLOBAL',
    UTC_TIMESTAMP(),
    1,
    UTC_TIMESTAMP(),
    1
FROM M_ROLE r
INNER JOIN M_PERMISSION p
    ON p.PERMISSION_CODE IN (
        'USER:MANAGE',
        'USER:VIEW',
        'ROLE:MANAGE',
        'PERMISSION:MANAGE',
        'RBAC:ASSIGN_ROLE',
        'RBAC:ASSIGN_PERMISSION',
        'STOCK:VIEW',
        'STOCK:MANAGE',
        'STOCK:IMPORT',
        'STOCK_PRICE_HISTORY:VIEW',
        'STOCK_PRICE_HISTORY:MANAGE',
        'STOCK_PRICE_HISTORY:IMPORT',
        'STOCK_DIVIDEND_CONFIRMED:VIEW',
        'STOCK_DIVIDEND_CONFIRMED:MANAGE',
        'STOCK_DIVIDEND_CONFIRMED:CONFIRM',
        'STOCK_DIVIDEND_CONFIRMED:IMPORT'
    )
WHERE r.ROLE_CODE = 'ROLE_ADMIN';

INSERT INTO M_ROLE_PERMISSION (
    ROLE_ID,
    PERMISSION_ID,
    TENANT_ID,
    SCOPE,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY
)
SELECT
    r.ID,
    p.ID,
    r.TENANT_ID,
    'GLOBAL',
    UTC_TIMESTAMP(),
    1,
    UTC_TIMESTAMP(),
    1
FROM M_ROLE r
INNER JOIN M_PERMISSION p
    ON p.PERMISSION_CODE IN (
        'ROLE:MANAGE',
        'PERMISSION:MANAGE',
        'RBAC:ASSIGN_ROLE',
        'RBAC:ASSIGN_PERMISSION'
    )
WHERE r.ROLE_CODE = 'ROLE_SECURITY_MANAGER';

INSERT INTO M_ROLE_PERMISSION (
    ROLE_ID,
    PERMISSION_ID,
    TENANT_ID,
    SCOPE,
    CREATED_AT,
    CREATED_BY,
    UPDATED_AT,
    UPDATED_BY
)
SELECT
    r.ID,
    p.ID,
    r.TENANT_ID,
    'GLOBAL',
    UTC_TIMESTAMP(),
    1,
    UTC_TIMESTAMP(),
    1
FROM M_ROLE r
INNER JOIN M_PERMISSION p
    ON p.PERMISSION_CODE IN ('USER:VIEW', 'STOCK:VIEW', 'STOCK_PRICE_HISTORY:VIEW', 'STOCK_DIVIDEND_CONFIRMED:VIEW')
WHERE r.ROLE_CODE = 'ROLE_OPERATOR';
