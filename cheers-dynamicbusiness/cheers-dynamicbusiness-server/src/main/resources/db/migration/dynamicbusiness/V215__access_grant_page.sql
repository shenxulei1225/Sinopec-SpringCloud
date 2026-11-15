-- 已经开通安防的租户，补上门禁管理。进出记录仍用原来的门禁页面。
-- 新租户开通时按能力包页面清单。这里不在打开页面时补页面。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',security-access-grant',
    updater = 'access-grant',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'security-video'
  AND opened_page_codes IS NOT NULL
  AND btrim(opened_page_codes) <> ''
  AND opened_page_codes NOT LIKE '%security-access-grant%'
  AND position('security-access' IN opened_page_codes) > 0;

INSERT INTO dynamicbusiness.dynamic_page_role_grant (
    role_id, page_code, action, tenant_id, creator, updater, deleted
)
SELECT src.role_id, 'security-access-grant', 'view', src.tenant_id,
       'access-grant', 'access-grant', false
FROM dynamicbusiness.dynamic_page_role_grant src
WHERE src.page_code = 'security-access'
  AND src.action = 'view'
  AND src.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_page_role_grant own
      WHERE own.role_id = src.role_id
        AND own.page_code = 'security-access-grant'
        AND own.action = 'view'
        AND own.tenant_id = src.tenant_id
        AND own.deleted = false
  );

UPDATE dynamicbusiness.tenant_portal_menu
SET title = '进出记录',
    updater = 'access-grant',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND page_code = 'security-access'
  AND title = '门禁';

INSERT INTO dynamicbusiness.tenant_portal_menu (
    tenant_id, parent_id, title, page_code, sort, creator, updater, deleted
)
SELECT src.tenant_id, src.parent_id, '门禁管理', 'security-access-grant', src.sort + 1,
       'access-grant', 'access-grant', false
FROM dynamicbusiness.tenant_portal_menu src
WHERE src.deleted = false
  AND src.page_code = 'security-access'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.tenant_portal_menu own
      WHERE own.tenant_id = src.tenant_id
        AND own.page_code = 'security-access-grant'
        AND own.deleted = false
  );
