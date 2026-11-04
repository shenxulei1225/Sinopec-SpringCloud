-- 数据页上，选中一个防火区后，型号栏只出现这个分区里有点位的型号。
-- 状态采集、设备控制分别挂到各自有点位的防火区分类上。
-- 页面读的是本租户的型号分类关联表。不要写到不带租户后缀的公共表。
-- 不改关系图连线。分类到型号、型号到点位的筛选线已经画好。

SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
    model_code, category_code
)
SELECT DISTINCT
    model.id,
    category_row.id,
    'point_binding',
    CASE model.code WHEN 'MODEL-point-binding' THEN 10 ELSE 20 END,
    'point-table-seed',
    'point-table-seed',
    false,
    123,
    model.code,
    category_row.code
FROM dynamicbusiness.ent_point_binding_t123 point
JOIN dynamicbusiness.dynamic_model model
  ON model.id = point.model_id
 AND model.deleted = false
JOIN dynamicbusiness.dynamic_entity_category_relation_t123 entity_link
  ON entity_link.tenant_id = 123
 AND entity_link.deleted = false
 AND entity_link.entity_type_code = 'point_binding'
 AND entity_link.entity_id = point.id
JOIN dynamicbusiness.dynamic_category category_row
  ON category_row.id = entity_link.category_id
 AND category_row.tenant_id = 123
 AND category_row.deleted = false
 AND category_row.category_type_code = 'zone'
 AND category_row.level = 4
WHERE point.tenant_id = 123
  AND point.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation_t123 existing
      WHERE existing.tenant_id = 123
        AND existing.deleted = false
        AND existing.model_id = model.id
        AND existing.category_id = category_row.id
  );
