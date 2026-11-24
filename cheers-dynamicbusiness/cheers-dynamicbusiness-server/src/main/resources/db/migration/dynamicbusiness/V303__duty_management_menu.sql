-- 系统配置里增加「职务管理」。已保存的顶栏按角色与权限后面插入。
-- 已单独勾过页面的管理包补上这一页。能看角色页的角色同时能看职务页。
SET search_path TO dynamicbusiness;

UPDATE tenant_portal_menu
SET sort = sort + 1,
    updater = 'V303',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND parent_id = 556
  AND sort >= 3
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu existing
      WHERE existing.tenant_id = 1
        AND existing.page_code = 'duty-management'
        AND existing.deleted = false
  );

INSERT INTO tenant_portal_menu (tenant_id, parent_id, title, page_code, sort, creator, updater)
SELECT 1, 556, '职务管理', 'duty-management', 3, 'V303', 'V303'
WHERE NOT EXISTS (
    SELECT 1 FROM tenant_portal_menu
    WHERE tenant_id = 1 AND page_code = 'duty-management' AND deleted = false
);

UPDATE tenant_portal_menu
SET sort = sort + 1,
    updater = 'V303',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 123
  AND parent_id = 502
  AND sort >= 2
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu existing
      WHERE existing.tenant_id = 123
        AND existing.page_code = 'duty-management'
        AND existing.deleted = false
  );

INSERT INTO tenant_portal_menu (tenant_id, parent_id, title, page_code, sort, creator, updater)
SELECT 123, 502, '职务管理', 'duty-management', 2, 'V303', 'V303'
WHERE NOT EXISTS (
    SELECT 1 FROM tenant_portal_menu
    WHERE tenant_id = 123 AND page_code = 'duty-management' AND deleted = false
);

UPDATE tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',duty-management',
    updater = 'V303',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'tenant-foundation'
  AND opened_page_codes IS NOT NULL
  AND opened_page_codes <> ''
  AND position('duty-management' IN opened_page_codes) = 0;

INSERT INTO dynamic_page_role_grant (role_id, page_code, action, creator, updater, tenant_id)
SELECT source.role_id, 'duty-management', 'view', 'V303', 'V303', source.tenant_id
FROM dynamic_page_role_grant source
WHERE source.deleted = false
  AND source.page_code = 'role-permission'
  AND source.action = 'view'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_page_role_grant existing
      WHERE existing.role_id = source.role_id
        AND existing.page_code = 'duty-management'
        AND existing.action = 'view'
        AND existing.tenant_id = source.tenant_id
        AND existing.deleted = false
  );
