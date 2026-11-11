-- 已经能操作车辆进出的角色，补上同一页里的名单和车道。
-- 新角色仍在权限页勾选。这里不在打开页面时补授权。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.dynamic_catalog_role_grant (
    role_id, registry_code, subject, operation, tenant_id, creator, updater, deleted
)
SELECT src.role_id, target.code, src.subject, src.operation, src.tenant_id,
       'vehicle-pass', 'vehicle-pass', false
FROM dynamicbusiness.dynamic_catalog_role_grant src
CROSS JOIN (VALUES ('vehicle_list'), ('vehicle_lane')) AS target(code)
WHERE src.registry_code = 'vehicle_pass'
  AND src.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_catalog_role_grant own
      WHERE own.role_id = src.role_id
        AND own.registry_code = target.code
        AND own.subject = src.subject
        AND own.operation = src.operation
        AND own.tenant_id = src.tenant_id
        AND own.deleted = false
  );
