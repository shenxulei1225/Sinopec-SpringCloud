-- 数据页里读另一本目录的栏（例如检查项页里的设备栏），从这一版起要同时有那本目录的同一项实体授权。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.4 节。
-- 一次性补齐：角色在本页有哪些实体操作，就给本页布局里另一本目录的实体栏、详情栏补同样的操作。
-- 本页自己、本页复用的底座类型不算另一本目录，不补。目录不存在的栏不补。已有的授权不重复插入。
INSERT INTO dynamicbusiness.dynamic_catalog_role_grant
    (role_id, registry_code, subject, operation, tenant_id, creator, updater)
SELECT DISTINCT g.role_id, c.col_code, 'entity', g.operation, g.tenant_id, 'V222', 'V222'
FROM (
    SELECT DISTINCT et.tenant_id, et.code AS page_code, l.column_meta ->> 'entityEntityTypeCode' AS col_code
    FROM dynamicbusiness.dynamic_entity_type et
    JOIN dynamicbusiness.dm_data_tab_layout l
      ON l.tenant_id = et.tenant_id AND l.layout_id = et.data_layout_id AND l.deleted = false
    WHERE et.deleted IS NOT TRUE
      AND l.column_kind IN ('ENTITY', 'DETAIL')
      AND COALESCE(l.column_meta ->> 'entityEntityTypeCode', '') <> ''
      AND l.column_meta ->> 'entityEntityTypeCode' <> et.code
      AND l.column_meta ->> 'entityEntityTypeCode' IS DISTINCT FROM et.base_entity_type_code
) c
JOIN dynamicbusiness.dynamic_entity_type t
  ON t.tenant_id = c.tenant_id AND t.code = c.col_code AND t.deleted IS NOT TRUE
JOIN dynamicbusiness.dynamic_catalog_role_grant g
  ON g.tenant_id = c.tenant_id AND g.registry_code = c.page_code AND g.subject = 'entity' AND g.deleted = false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_catalog_role_grant x
    WHERE x.tenant_id = g.tenant_id AND x.role_id = g.role_id AND x.registry_code = c.col_code
      AND x.subject = 'entity' AND x.operation = g.operation AND x.deleted = false
);
