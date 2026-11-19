-- 场站部门按所属场站归属。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md（浏览时右上角的站在可见范围里收窄）。
--
-- 部门目录以前没有归属声明，有查看权限就能看到全部场站的部门，
-- 左侧分类树把各站的「运行班」等铺成一长串同名节点。
-- 专用列 facility_id 已经有值。本迁移只补归属声明，不改部门记录。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_catalog_ownership (registry_code, field_code, target_registry_code, required,
                                       multi_valued, creator, updater, tenant_id)
SELECT 'department', 'facility_id', 'facility', true, false, 'V242', 'V242', t.tenant_id
FROM (
    SELECT DISTINCT tenant_id
    FROM dynamic_entity_type
    WHERE deleted = false AND code = 'department'
) t
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_catalog_ownership o
    WHERE o.deleted = false AND o.tenant_id = t.tenant_id
      AND o.registry_code = 'department' AND o.field_code = 'facility_id'
);
