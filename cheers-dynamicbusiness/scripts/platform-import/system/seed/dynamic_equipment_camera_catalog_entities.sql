SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO ent_equipment_t1 (
    entity_type_code, model_id, name, code, status, facility_id,
    custom_fields, creator, updater, deleted, tenant_id
)
SELECT
    'equipment',
    model.id,
    '相机 · ' || model.name,
    'equipment-cam-spec-' || model.code,
    1,
    44,
    COALESCE(specs.fields, '{}'::jsonb),
    'seed',
    'seed',
    false,
    1
FROM dynamic_model AS model
LEFT JOIN LATERAL (
    SELECT jsonb_object_agg(
        assignment.field_code,
        CASE
            WHEN btrim(assignment.default_value) ~ '^-?\d+(?:\.\d+)?$'
                THEN to_jsonb(btrim(assignment.default_value)::numeric)
            WHEN left(btrim(assignment.default_value), 1) = '{'
                THEN assignment.default_value::jsonb
            ELSE to_jsonb(btrim(assignment.default_value))
        END
    ) AS fields
    FROM dynamic_model_field_assignment AS assignment
    WHERE assignment.model_id = model.id
      AND assignment.deleted = false
      AND COALESCE(btrim(assignment.default_value), '') <> ''
      AND assignment.field_code IN (
          'wide_sensor', 'wide_focal_length', 'wide_aperture', 'wide_photo_size',
          'zoom_focal_length', 'zoom_aperture', 'zoom_photo_size',
          'mid_tele_focal_length', 'mid_tele_aperture', 'mid_tele_photo_size',
          'tele_focal_length', 'tele_aperture', 'tele_photo_size',
          'thermal_resolution', 'thermal_focal_length',
          'iso_normal', 'shutter_speed', 'gimbal_pitch_min', 'gimbal_pitch_max',
          'fpv_resolution', 'fpv_night_vision'
      )
) AS specs ON true
WHERE model.deleted = false
  AND model.entity_type_code = 'equipment'
  AND (model.code LIKE 'dji-pl-%' OR model.code = 'dji-m400-body-camera')
  AND NOT EXISTS (
      SELECT 1
      FROM ent_equipment_t1 AS existing
      WHERE existing.code = 'equipment-cam-spec-' || model.code
        AND existing.deleted = false
  );

UPDATE ent_equipment_t1
SET tree_path = '/' || id::text || '/',
    update_time = CURRENT_TIMESTAMP
WHERE code LIKE 'equipment-cam-spec-%'
  AND deleted = false
  AND (tree_path IS NULL OR tree_path = '');

INSERT INTO dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
)
SELECT equipment.id, 2104809, 'equipment', 0, 'seed', 'seed', false, 1
FROM ent_equipment_t1 AS equipment
WHERE equipment.code LIKE 'equipment-cam-spec-%'
  AND equipment.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_entity_category_relation_t1 AS relation
      WHERE relation.entity_id = equipment.id
        AND relation.category_id = 2104809
        AND relation.deleted = false
  );

COMMIT;
