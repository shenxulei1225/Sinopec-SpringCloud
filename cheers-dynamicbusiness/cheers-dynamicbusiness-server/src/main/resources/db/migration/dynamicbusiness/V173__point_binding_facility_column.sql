-- 点位对照是站场级目录。列表按所属场站筛选时只认专用表上的 facility_id。
-- 现有行都挂在武汉光谷管廊（设施编号 1）。分类页要能点到这些行，所以一并挂到默认分类。

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.ent_point_binding_t123
    ADD COLUMN IF NOT EXISTS facility_id bigint;

COMMENT ON COLUMN dynamicbusiness.ent_point_binding_t123.facility_id IS
    '所属场站。站场级列表按这一列筛选，不从字段索引反推。';

CREATE INDEX IF NOT EXISTS idx_ent_point_binding_t123_facility
    ON dynamicbusiness.ent_point_binding_t123 (tenant_id, facility_id)
    WHERE deleted = false;

UPDATE dynamicbusiness.ent_point_binding_t123
SET facility_id = 1
WHERE tenant_id = 123 AND deleted = false AND facility_id IS NULL;

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    description, sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable
)
SELECT
    'point_binding', 9360, 'facility_id', '所属场站', 'REF', true,
    '站场级归属设施；创建写入、编辑只读', 5, 1, 123,
    'point-table-seed', 'point-table-seed', false,
    false, true, true
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
    WHERE tenant_id = 123 AND entity_type_code = 'point_binding'
      AND field_code = 'facility_id' AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    field_source
)
SELECT
    model.id, 9360, true, false, true, true, 5,
    'point-table-seed', 'point-table-seed', false, 123,
    model.code, 'facility_id', false, 'BASE'
FROM dynamicbusiness.dynamic_model model
WHERE model.tenant_id = 123 AND model.code = 'MODEL-point-binding' AND model.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.tenant_id = 123 AND a.model_id = model.id
        AND a.field_code = 'facility_id' AND a.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t123 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT
    row.id, category_row.id, 'point_binding', 0,
    'point-table-seed', 'point-table-seed', false, 123
FROM dynamicbusiness.ent_point_binding_t123 row
JOIN dynamicbusiness.dynamic_category category_row
  ON category_row.tenant_id = 123
 AND category_row.code = 'point_binding_root'
 AND category_row.deleted = false
WHERE row.tenant_id = 123 AND row.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation_t123 rel
      WHERE rel.tenant_id = 123 AND rel.entity_id = row.id
        AND rel.category_id = category_row.id
  );
