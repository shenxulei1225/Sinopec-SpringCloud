-- 租户管理员的目录权限改由授权行决定。
-- 还没有授权行的租户管理员，按当前已放行的范围写入本租户全部数据目录的四项操作。
-- 已经有授权行的不覆盖。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_catalog_role_grant (
    role_id, registry_code, operation, creator, updater, tenant_id
)
SELECT r.id, btrim(e.code), op.operation, '1', '1', r.tenant_id
FROM public.system_role r
JOIN dynamic_entity_type e
  ON e.tenant_id = r.tenant_id
 AND e.deleted = false
 AND e.code IS NOT NULL
 AND btrim(e.code) <> ''
CROSS JOIN (VALUES ('query'), ('create'), ('update'), ('delete')) AS op(operation)
WHERE r.deleted = false
  AND r.code = 'tenant_admin'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_catalog_role_grant g
      WHERE g.role_id = r.id
        AND g.deleted = false
  );
