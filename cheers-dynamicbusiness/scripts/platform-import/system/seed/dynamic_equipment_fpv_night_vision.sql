-- 补用户手册里写明的机头夜视。规格表没写这一行，手册正文写了星光。
-- 负责：Matrice 30 / 30T、Matrice 350 RTK 机身相机的夜视默认值。
-- 不负责：俯仰能不能转。官网没有给出机头相机的俯仰角度。
-- 禁止：给 Matrice 300 RTK 写星光。手册和规格表都没有这一项。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, sort, creator, updater, deleted, tenant_id, model_code, field_code
)
SELECT model.id, field.id, false, false, false, false,
       '星光级', 120, 'seed', 'seed', false, 1, model.code, field.code
FROM dynamic_model AS model
JOIN dynamic_field AS field
  ON field.code = 'fpv_night_vision' AND field.tenant_id = 1 AND field.deleted = false
WHERE model.tenant_id = 1
  AND model.deleted = false
  AND model.code IN ('dji-m30-body-camera', 'dji-m350-rtk-body-camera')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment AS existing
      WHERE existing.model_id = model.id
        AND existing.field_id = field.id
        AND existing.deleted = false
  );

UPDATE dynamic_model_field_assignment AS assignment
SET default_value = '星光级',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model AS model
WHERE assignment.model_id = model.id
  AND assignment.field_code = 'fpv_night_vision'
  AND assignment.deleted = false
  AND model.code IN ('dji-m30-body-camera', 'dji-m350-rtk-body-camera')
  AND (assignment.default_value IS NULL OR btrim(assignment.default_value) = '');

UPDATE dynamic_model AS model
SET field_groups_config = jsonb_set(
        model.field_groups_config::jsonb,
        '{groups,0,fields}',
        (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object('fieldId', picked.field_id, 'sort', picked.ord)
                ORDER BY picked.ord
            ), '[]'::jsonb)
            FROM (
                SELECT assignment.field_id,
                       row_number() OVER (ORDER BY assignment.field_code) AS ord
                FROM dynamic_model_field_assignment AS assignment
                WHERE assignment.model_id = model.id
                  AND assignment.deleted = false
                  AND assignment.tenant_id = 1
                  AND assignment.field_code IN (
                      'fpv_resolution', 'fpv_fov', 'fpv_frame_rate', 'fpv_night_vision'
                  )
            ) AS picked
        )
    )::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE model.tenant_id = 1
  AND model.deleted = false
  AND model.code IN ('dji-m30-body-camera', 'dji-m350-rtk-body-camera')
  AND model.field_groups_config IS NOT NULL
  AND btrim(model.field_groups_config) <> '';

UPDATE ent_equipment_t1 AS equipment
SET custom_fields = COALESCE(equipment.custom_fields, '{}'::jsonb)
        || jsonb_build_object('fpv_night_vision', '星光级'),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE equipment.deleted = false
  AND equipment.code LIKE 'EQ-DJI-%-FPV'
  AND equipment.model_id IN (
      SELECT id FROM dynamic_model
      WHERE code IN ('dji-m30-body-camera', 'dji-m350-rtk-body-camera')
        AND tenant_id = 1
        AND deleted = false
  );

COMMIT;

SELECT model.code, assignment.field_code, assignment.default_value
FROM dynamic_model_field_assignment AS assignment
JOIN dynamic_model AS model ON model.id = assignment.model_id
WHERE model.code IN (
    'dji-m30-body-camera',
    'dji-m300-rtk-body-camera',
    'dji-m350-rtk-body-camera',
    'dji-m400-body-camera'
)
  AND assignment.deleted = false
  AND assignment.field_code = 'fpv_night_vision'
ORDER BY model.code;
