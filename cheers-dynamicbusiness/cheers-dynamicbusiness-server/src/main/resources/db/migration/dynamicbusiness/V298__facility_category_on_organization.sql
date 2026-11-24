-- 场站数据页左侧分类已改成组织机构。
-- 存量挂靠还指着已删除的运营区域分类，点组织机构时场站不出现。
-- 按每座站当前的所属组织，改挂到同名的组织机构分类。没有挂过的站也补上。
-- 不改所属组织这一列，不改「场站 → 管线段 → 作业区」这条权限链。

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort,
    creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT f.id, c.id, 'facility', 0,
       'V298', CURRENT_TIMESTAMP, 'V298', CURRENT_TIMESTAMP, false, 1
FROM ent_facility_t1 f
JOIN ent_organization_t1 o
  ON o.id = f.organization_id
 AND o.deleted = false
 AND o.tenant_id = 1
JOIN dynamic_category c
  ON c.deleted = false
 AND c.tenant_id = 1
 AND c.category_type_code = 'organization'
 AND c.name = o.name
WHERE f.deleted = false
  AND f.tenant_id = 1
ON CONFLICT (tenant_id, entity_type_code, entity_id, category_id)
DO UPDATE SET deleted = false,
              updater = 'V298',
              update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true,
    updater = 'V298',
    update_time = CURRENT_TIMESTAMP
WHERE r.deleted = false
  AND r.tenant_id = 1
  AND r.entity_type_code = 'facility'
  AND EXISTS (
      SELECT 1
      FROM ent_facility_t1 f
      JOIN ent_organization_t1 o
        ON o.id = f.organization_id
       AND o.deleted = false
       AND o.tenant_id = 1
      JOIN dynamic_category c
        ON c.deleted = false
       AND c.tenant_id = 1
       AND c.category_type_code = 'organization'
       AND c.name = o.name
      WHERE f.deleted = false
        AND f.tenant_id = 1
        AND f.id = r.entity_id
        AND c.id <> r.category_id
  );
