-- 点位目录仍是一个。两个型号只区分这一行是状态采集还是设备控制。
-- 读和 Real 留在状态采集。写归设备控制。不拆设备型号，不改分区，不改点位编码。

SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_model
SET name = '状态采集',
    description = '设备报上来的状态。读，以及点表里写成 Real 的测量。',
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND code = 'MODEL-point-binding'
  AND deleted = false;

INSERT INTO dynamicbusiness.dynamic_model (
    code, name, entity_type_code, description, status, sort, creator, updater,
    deleted, tenant_id, governance_status
)
SELECT
    'MODEL-point-binding-control', '设备控制', 'point_binding',
    '下给设备的控制。点表里的写。', 1, 20,
    'point-table-seed', 'point-table-seed', false, 123, 'COMPANY'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_model
    WHERE tenant_id = 123 AND code = 'MODEL-point-binding-control' AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    control.id, source.field_id, source.required, source.is_searchable, source.is_filterable,
    source.is_sortable, source.sort,
    'point-table-seed', 'point-table-seed', false, 123,
    control.code, source.field_code, source.collection_point,
    source.create_visible, source.edit_visible, source.detail_visible, source.field_source
FROM dynamicbusiness.dynamic_model control
JOIN dynamicbusiness.dynamic_model_field_assignment source
  ON source.tenant_id = 123
 AND source.deleted = false
 AND source.model_code = 'MODEL-point-binding'
WHERE control.tenant_id = 123
  AND control.code = 'MODEL-point-binding-control'
  AND control.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment existing
      WHERE existing.tenant_id = 123
        AND existing.model_id = control.id
        AND existing.field_code = source.field_code
        AND existing.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
    model_code, category_code
)
SELECT
    control.id, link.category_id, 'point_binding', 20,
    'point-table-seed', 'point-table-seed', false, 123,
    control.code, link.category_code
FROM dynamicbusiness.dynamic_model control
JOIN dynamicbusiness.dynamic_model_category_relation_t123 link
  ON link.tenant_id = 123
 AND link.deleted = false
 AND link.model_code = 'MODEL-point-binding'
WHERE control.tenant_id = 123
  AND control.code = 'MODEL-point-binding-control'
  AND control.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation_t123 existing
      WHERE existing.tenant_id = 123
        AND existing.model_id = control.id
        AND existing.category_id = link.category_id
        AND existing.deleted = false
  );

UPDATE dynamicbusiness.ent_point_binding_t123 point
SET model_id = control.id,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_model control
WHERE control.tenant_id = 123
  AND control.code = 'MODEL-point-binding-control'
  AND control.deleted = false
  AND point.tenant_id = 123
  AND point.deleted = false
  AND point.read_write = 'Write'
  AND point.model_id IS DISTINCT FROM control.id;
