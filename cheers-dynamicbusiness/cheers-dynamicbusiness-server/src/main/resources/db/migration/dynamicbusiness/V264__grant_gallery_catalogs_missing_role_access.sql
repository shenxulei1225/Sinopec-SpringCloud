-- 智慧管廊（租户 123）有几本数据目录打开后，分类、型号、实体都显示「没有该操作权限」。
-- 这些目录没有归属声明，直接分配的角色有这项操作就放行。
-- 租户管理员在同组已经授全的目录上有查看、新增、修改、删除。
-- 这里只把缺的对象和操作补到同一批角色上。已有的行不改。别的租户不写。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_catalog_role_grant (
    role_id, registry_code, subject, operation, tenant_id,
    creator, create_time, updater, update_time, deleted
)
SELECT g.role_id, v.target_code, g.subject, g.operation, g.tenant_id,
       'V264', CURRENT_TIMESTAMP, 'V264', CURRENT_TIMESTAMP, false
FROM (
    VALUES
        ('strategy_alarm', 'alarm_strategy'),
        ('event_topic_alarm', 'event_topic'),
        ('linkage_alarm', 'alarm'),
        ('linkage', 'alarm'),
        ('access_grant', 'security_access')
) AS v(target_code, template_code)
JOIN dynamic_catalog_role_grant g
    ON g.deleted = false
   AND g.tenant_id = 123
   AND g.registry_code = v.template_code
WHERE EXISTS (
    SELECT 1
    FROM dynamic_entity_type t
    WHERE t.deleted = false
      AND t.tenant_id = 123
      AND t.code = v.target_code
)
AND NOT EXISTS (
    SELECT 1
    FROM dynamic_catalog_role_grant x
    WHERE x.deleted = false
      AND x.tenant_id = g.tenant_id
      AND x.role_id = g.role_id
      AND x.registry_code = v.target_code
      AND x.subject = g.subject
      AND x.operation = g.operation
);
