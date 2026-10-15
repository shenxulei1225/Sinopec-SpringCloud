-- 门户名称和 Logo 属于业务租户。登录列表里的租户名不改。

ALTER TABLE system_tenant
    ADD COLUMN IF NOT EXISTS portal_name varchar(64);

ALTER TABLE system_tenant
    ADD COLUMN IF NOT EXISTS logo_url varchar(1024);

COMMENT ON COLUMN system_tenant.portal_name IS '门户名称。登录后左上角和登录页标题使用。空则仍显示租户名';
COMMENT ON COLUMN system_tenant.logo_url IS '门户 Logo 地址。空则使用默认图标';

UPDATE system_tenant
SET portal_name = '智慧场站平台',
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE id = 1
  AND deleted = false
  AND (portal_name IS NULL OR portal_name = '');

UPDATE system_tenant
SET portal_name = '智慧管廊',
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE id = 123
  AND deleted = false
  AND (portal_name IS NULL OR portal_name = '');
