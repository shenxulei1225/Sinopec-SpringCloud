-- 数据目录「策略」（alarm_strategy）在告警分组里，但没有角色授权。
-- 打开列表、型号和分类树都会被拒绝。
-- 按同一租户里「告警记录」已有的角色、对象和操作补上。没有这本目录的租户不写。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_catalog_role_grant (
    role_id, registry_code, subject, operation, tenant_id,
    creator, create_time, updater, update_time, deleted
)
SELECT g.role_id, 'alarm_strategy', g.subject, g.operation, g.tenant_id,
       'V250', CURRENT_TIMESTAMP, 'V250', CURRENT_TIMESTAMP, false
FROM dynamic_catalog_role_grant g
WHERE g.deleted = false
  AND g.registry_code = 'alarm'
  AND EXISTS (
      SELECT 1
      FROM dynamic_entity_type t
      WHERE t.deleted = false
        AND t.code = 'alarm_strategy'
        AND t.tenant_id = g.tenant_id
  )
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_catalog_role_grant x
      WHERE x.deleted = false
        AND x.tenant_id = g.tenant_id
        AND x.role_id = g.role_id
        AND x.registry_code = 'alarm_strategy'
        AND x.subject = g.subject
        AND x.operation = g.operation
  );
