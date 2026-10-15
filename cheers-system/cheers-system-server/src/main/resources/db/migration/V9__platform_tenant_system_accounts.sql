-- 平台租户只放系统账号。登录列表不展示它。
-- 智慧巡检仍是业务租户。super 和超级管理员角色从智慧巡检挪到平台，不复制到每个租户。

ALTER TABLE system_tenant
    ADD COLUMN IF NOT EXISTS scope varchar(32) NOT NULL DEFAULT 'BUSINESS';

COMMENT ON COLUMN system_tenant.scope IS 'BUSINESS 业务租户；PLATFORM 平台，只放系统账号';

INSERT INTO system_tenant (
    id, name, contact_name, status, package_id, expire_time, account_count, scope,
    creator, create_time, updater, update_time, deleted
)
SELECT
    (SELECT COALESCE(MAX(id), 0) + 1 FROM system_tenant),
    '平台',
    '平台',
    0,
    0,
    TIMESTAMP '2099-12-31 23:59:59',
    9999,
    'PLATFORM',
    '1',
    CURRENT_TIMESTAMP,
    '1',
    CURRENT_TIMESTAMP,
    false
WHERE NOT EXISTS (
    SELECT 1 FROM system_tenant WHERE scope = 'PLATFORM' AND deleted = false
);

SELECT setval('system_tenant_seq', (SELECT MAX(id) FROM system_tenant));

UPDATE system_users
SET username = 'super',
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE id = 1
  AND username = 'admin'
  AND deleted = false;

UPDATE system_users
SET tenant_id = (
        SELECT id FROM system_tenant
        WHERE scope = 'PLATFORM' AND deleted = false
        ORDER BY id
        LIMIT 1
    ),
    dept_id = NULL,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE id = 1
  AND username = 'super'
  AND deleted = false;

UPDATE system_role
SET tenant_id = (
        SELECT id FROM system_tenant
        WHERE scope = 'PLATFORM' AND deleted = false
        ORDER BY id
        LIMIT 1
    ),
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'super_admin'
  AND tenant_id = 1
  AND deleted = false;

UPDATE system_user_role
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE user_id = 1
  AND role_id = 2
  AND deleted = false;
