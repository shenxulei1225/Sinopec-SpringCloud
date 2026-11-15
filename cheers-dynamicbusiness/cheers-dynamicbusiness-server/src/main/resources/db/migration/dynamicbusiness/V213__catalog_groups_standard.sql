-- 目录上只写了分组名、分组表里没有这一行时，侧栏不能改名。
-- 按创建分组的同一张表补上这些分组。
-- 车辆收成一组「车辆管理」。下面是「车辆登记」和「车辆进出记录」。
-- 不新建目录。个人车辆、公司车辆、车辆调度仍在车辆登记。车辆进出、车道仍在车辆进出记录。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_entity_type
SET group_name = '车辆进出记录',
    updater = 'catalog-group',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND btrim(group_name) = '车辆进出管理';

INSERT INTO dynamicbusiness.dynamic_group (
    group_type, code, name, sort, status, level,
    creator, updater, deleted, tenant_id
)
SELECT 'ENTITY_TYPE',
       'ETG-' || substr(md5(n.tenant_id::text || ':' || n.group_name), 1, 24),
       n.group_name,
       3000 + n.ord,
       1,
       1,
       'catalog-group',
       'catalog-group',
       false,
       n.tenant_id
FROM (
    SELECT tenant_id,
           group_name,
           row_number() OVER (PARTITION BY tenant_id ORDER BY group_name) AS ord
    FROM (
        SELECT DISTINCT tenant_id, btrim(group_name) AS group_name
        FROM dynamicbusiness.dynamic_entity_type
        WHERE deleted = false
          AND group_name IS NOT NULL
          AND btrim(group_name) <> ''
    ) names
) n
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dynamic_group g
    WHERE g.deleted = false
      AND g.group_type = 'ENTITY_TYPE'
      AND g.tenant_id = n.tenant_id
      AND g.name = n.group_name
);

INSERT INTO dynamicbusiness.dynamic_group (
    group_type, code, name, sort, status, level,
    creator, updater, deleted, tenant_id
)
SELECT DISTINCT 'ENTITY_TYPE',
       'ETG-' || substr(md5(e.tenant_id::text || ':车辆管理'), 1, 24),
       '车辆管理',
       2990,
       1,
       1,
       'catalog-group',
       'catalog-group',
       false,
       e.tenant_id
FROM dynamicbusiness.dynamic_entity_type e
WHERE e.deleted = false
  AND btrim(e.group_name) IN ('车辆登记', '车辆进出记录')
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_group g
      WHERE g.deleted = false
        AND g.group_type = 'ENTITY_TYPE'
        AND g.tenant_id = e.tenant_id
        AND g.name = '车辆管理'
  );

UPDATE dynamicbusiness.dynamic_group child
SET parent_id = parent.id,
    level = 2,
    updater = 'catalog-group',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_group parent
WHERE child.deleted = false
  AND parent.deleted = false
  AND child.group_type = 'ENTITY_TYPE'
  AND parent.group_type = 'ENTITY_TYPE'
  AND child.tenant_id = parent.tenant_id
  AND parent.name = '车辆管理'
  AND child.name IN ('车辆登记', '车辆进出记录')
  AND (child.parent_id IS NULL OR child.parent_id = 0);
