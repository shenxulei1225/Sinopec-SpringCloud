-- 点位对照的数据页按管廊分区筛选。
-- 每一行挂到它所属防火区的分类上。不再挂在「点位对照」这一个总分类下。

SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t123 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT
    point.id,
    link.category_id,
    'point_binding',
    0,
    'point-table-seed',
    'point-table-seed',
    false,
    123
FROM dynamicbusiness.ent_point_binding_t123 point
JOIN dynamicbusiness.dynamic_category_entity_link_t123 link
  ON link.tenant_id = 123
 AND link.deleted = false
 AND link.entity_type_code = 'zone'
 AND link.entity_id = point.zone_id
JOIN dynamicbusiness.dynamic_category category_row
  ON category_row.id = link.category_id
 AND category_row.tenant_id = 123
 AND category_row.deleted = false
 AND category_row.category_type_code = 'zone'
 AND category_row.level = 4
WHERE point.tenant_id = 123
  AND point.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_category_relation_t123 existing
      WHERE existing.tenant_id = 123
        AND existing.entity_id = point.id
        AND existing.category_id = link.category_id
        AND existing.entity_type_code = 'point_binding'
  );

UPDATE dynamicbusiness.dynamic_entity_category_relation_t123 relation
SET deleted = true,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_category category_row
WHERE relation.tenant_id = 123
  AND relation.deleted = false
  AND relation.entity_type_code = 'point_binding'
  AND relation.category_id = category_row.id
  AND category_row.tenant_id = 123
  AND category_row.code = 'point_binding_root'
  AND category_row.deleted = false;
