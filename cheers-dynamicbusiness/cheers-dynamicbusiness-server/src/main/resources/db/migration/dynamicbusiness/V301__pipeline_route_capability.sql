-- 管线走向接到目录能力开关。型号上能看到这一列。
-- 起点、终点不再作为详情里的两格。沿线场站仍是各自指向这条管线的记录。

UPDATE dynamic_field
SET name = '管线走向',
    updater = 'flyway-v301',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'route_geojson'
  AND name = '走向';

UPDATE dynamic_model_field_assignment a
SET create_visible = TRUE,
    edit_visible = TRUE,
    detail_visible = TRUE,
    updater = 'flyway-v301',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE a.deleted = false
  AND f.id = a.field_id
  AND f.tenant_id = a.tenant_id
  AND f.deleted = false
  AND f.code = 'route_geojson';

UPDATE dynamic_entity_type_base_field
SET type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
    updater = 'flyway-v301',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code = 'pipeline'
  AND field_code IN ('start_facility_id', 'end_facility_id');

INSERT INTO dynamic_entity_type_capability (
  tenant_id, entity_type_code, capability_code, enabled, deleted,
  creator, create_time, updater, update_time
)
SELECT et.tenant_id, et.code, 'pipeline-route', TRUE, FALSE,
       'flyway-v301', CURRENT_TIMESTAMP, 'flyway-v301', CURRENT_TIMESTAMP
FROM dynamic_entity_type et
WHERE et.deleted = false
  AND et.code = 'pipeline'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_type_capability existing
    WHERE existing.deleted = false
      AND existing.tenant_id = et.tenant_id
      AND existing.entity_type_code = et.code
      AND existing.capability_code = 'pipeline-route'
  );

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'pipeline';
