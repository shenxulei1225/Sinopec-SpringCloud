-- 工单中心、我的工单的接口从这一版起按页面勾选放行，与报修一致。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.3 节。
-- 一次性补齐，让已有的人继续能用：
--   租户管理员、场站管理员，以及已经有工单目录实体新建、修改或删除授权的角色，
--   都给工单中心、我的工单两页的查看和维护。
-- 超级管理员只管租户和业务开通，不办具体租户的业务，不补。已有的勾选不重复插入。
INSERT INTO dynamicbusiness.dynamic_page_role_grant
    (role_id, page_code, action, tenant_id, creator, updater)
SELECT DISTINCT h.role_id, p.page_code, p.action, h.tenant_id, 'V223', 'V223'
FROM (
    SELECT r.id AS role_id, r.tenant_id
    FROM public.system_role r
    WHERE r.deleted = FALSE
      AND r.code IN ('tenant_admin', 'station_admin')
    UNION
    SELECT g.role_id, g.tenant_id
    FROM dynamicbusiness.dynamic_catalog_role_grant g
    JOIN public.system_role r ON r.id = g.role_id AND r.deleted = FALSE
    WHERE g.deleted = FALSE
      AND g.registry_code = 'work_order'
      AND g.subject = 'entity'
      AND g.operation IN ('create', 'update', 'delete')
      AND r.code <> 'super_admin'
) h
CROSS JOIN (
    VALUES ('work-order-center', 'view'),
           ('work-order-center', 'maintain'),
           ('work-order-execute', 'view'),
           ('work-order-execute', 'maintain')
) AS p(page_code, action)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_page_role_grant x
    WHERE x.tenant_id = h.tenant_id AND x.role_id = h.role_id
      AND x.page_code = p.page_code AND x.action = p.action AND x.deleted = FALSE
);
