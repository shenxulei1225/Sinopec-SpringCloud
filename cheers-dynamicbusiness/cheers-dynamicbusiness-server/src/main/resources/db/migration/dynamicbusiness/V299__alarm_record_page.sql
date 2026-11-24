-- 告警页：能看安防、设备或巡检页面的人，都能打开这一页，并查询告警目录。
-- 告警目录已经有所属场站字段、还没声明按场站管理的，补上这一条。打开页面不新建分类。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_catalog_ownership (
    registry_code, field_code, target_registry_code, required, multi_valued,
    creator, updater, deleted, tenant_id)
SELECT 'alarm', 'facility_id', 'facility', true, false,
       'V299', 'V299', false, f.tenant_id
FROM dynamic_entity_type_base_field f
WHERE f.deleted = false
  AND f.entity_type_code = 'alarm'
  AND f.field_code = 'facility_id'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_catalog_ownership own
      WHERE own.tenant_id = f.tenant_id
        AND own.deleted = false
        AND own.registry_code = 'alarm'
        AND own.field_code = 'facility_id');

INSERT INTO dynamic_page_role_grant (
    role_id, page_code, action, creator, updater, deleted, tenant_id)
SELECT DISTINCT g.role_id, 'alarm-records', 'view', 'V299', 'V299', false, g.tenant_id
FROM dynamic_page_role_grant g
WHERE g.deleted = false
  AND g.action = 'view'
  AND g.page_code IN (
      'security-alarm', 'security-video', 'security-perimeter', 'security-intrusion',
      'security-access', 'security-access-grant', 'security-broadcast', 'security-person',
      'security-vehicle', 'security-behavior', 'security-phone', 'security-radio',
      'security-vehicle-gate', 'security-vehicle-pass',
      'equipment-ledger', 'spare-parts',
      'inspection-task', 'inspection-schedule',
      'analysis-capability', 'analysis-job', 'analysis-provider')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_page_role_grant x
      WHERE x.tenant_id = g.tenant_id
        AND x.role_id = g.role_id
        AND x.deleted = false
        AND x.page_code = 'alarm-records'
        AND x.action = 'view');

INSERT INTO dynamic_catalog_role_grant (
    role_id, registry_code, subject, operation, creator, updater, deleted, tenant_id)
SELECT DISTINCT p.role_id, 'alarm', s.subject, 'query', 'V299', 'V299', false, p.tenant_id
FROM dynamic_page_role_grant p
CROSS JOIN (VALUES ('entity'), ('category'), ('detail'), ('model')) AS s(subject)
WHERE p.deleted = false
  AND p.page_code = 'alarm-records'
  AND p.action = 'view'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_catalog_role_grant c
      WHERE c.tenant_id = p.tenant_id
        AND c.role_id = p.role_id
        AND c.deleted = false
        AND c.registry_code = 'alarm'
        AND c.subject = s.subject
        AND c.operation = 'query');

INSERT INTO tenant_portal_menu (
    tenant_id, parent_id, title, page_code, sort, creator, updater, deleted)
SELECT t.tenant_id, NULL, '告警', 'alarm-records',
       COALESCE((
           SELECT MAX(m.sort) FROM tenant_portal_menu m
           WHERE m.tenant_id = t.tenant_id AND m.parent_id IS NULL AND m.deleted = false
       ), 0) + 1,
       'V299', 'V299', false
FROM (SELECT DISTINCT tenant_id FROM tenant_portal_menu WHERE deleted = false) t
WHERE NOT EXISTS (
    SELECT 1 FROM tenant_portal_menu m
    WHERE m.tenant_id = t.tenant_id AND m.deleted = false AND m.page_code = 'alarm-records');

UPDATE tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',alarm-records',
    updater = 'V299',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'tenant-foundation'
  AND opened_page_codes IS NOT NULL
  AND opened_page_codes <> ''
  AND position('alarm-records' IN opened_page_codes) = 0;
