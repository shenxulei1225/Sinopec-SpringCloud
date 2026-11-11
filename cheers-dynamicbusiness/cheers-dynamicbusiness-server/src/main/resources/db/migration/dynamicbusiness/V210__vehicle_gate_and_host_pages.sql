-- 已经开通车辆管理的租户，补上门岗车辆和我的外来车。
-- 新租户开通时按能力包页面清单。这里不在打开页面时补页面。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',security-vehicle-gate,security-my-vehicles',
    updater = 'vehicle-pass',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'security-video'
  AND opened_page_codes IS NOT NULL
  AND btrim(opened_page_codes) <> ''
  AND opened_page_codes NOT LIKE '%security-vehicle-gate%'
  AND position('security-vehicle-pass' IN opened_page_codes) > 0;

INSERT INTO dynamicbusiness.dynamic_page_role_grant (
    role_id, page_code, action, tenant_id, creator, updater, deleted
)
SELECT src.role_id, 'security-vehicle-gate', 'view', src.tenant_id,
       'vehicle-pass', 'vehicle-pass', false
FROM dynamicbusiness.dynamic_page_role_grant src
WHERE src.page_code = 'security-vehicle-pass'
  AND src.action = 'view'
  AND src.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_page_role_grant own
      WHERE own.role_id = src.role_id
        AND own.page_code = 'security-vehicle-gate'
        AND own.action = 'view'
        AND own.tenant_id = src.tenant_id
        AND own.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_page_role_grant (
    role_id, page_code, action, tenant_id, creator, updater, deleted
)
SELECT src.role_id, 'security-my-vehicles', 'view', src.tenant_id,
       'vehicle-pass', 'vehicle-pass', false
FROM dynamicbusiness.dynamic_page_role_grant src
WHERE src.page_code = 'security-my-visitors'
  AND src.action = 'view'
  AND src.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_page_role_grant own
      WHERE own.role_id = src.role_id
        AND own.page_code = 'security-my-vehicles'
        AND own.action = 'view'
        AND own.tenant_id = src.tenant_id
        AND own.deleted = false
  );
