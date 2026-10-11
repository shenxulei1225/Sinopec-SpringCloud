-- ============================================================================
-- 定向拍照：补上人要配、对接要填的两项
-- 云台偏航角（gimbalYawRotateAngle）与共用字段「偏航角」分开，只改这一条动作的参数槽。
-- 拍照模式（orientedPhotoMode）：普通拍照 / 智能低光拍照。不设默认值。
-- 飞机拍摄当时写入的文件、曝光、对焦位置不进动作参数。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES (
  'gimbal_yaw_rotate_angle',
  '云台偏航角',
  'NUMBER',
  'deg',
  '定向拍照：云台相对地理北的偏航角（度）。-180 到 180。与飞行器目标偏航角不是同一个参数。',
  'LIBRARY',
  1,
  NULL,
  NULL,
  NULL,
  NULL,
  'gimbal_yaw_rotate_angle',
  1,
  'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false DO UPDATE
SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES (
  'oriented_photo_mode',
  '拍照模式',
  'ENUM',
  NULL,
  '定向拍照：普通拍照，或智能低光拍照。',
  'LIBRARY',
  1,
  NULL,
  NULL,
  '[{"label":"普通拍照","value":"normalPhoto"},{"label":"智能低光拍照","value":"lowLightSmartShooting"}]',
  NULL,
  'oriented_photo_mode',
  1,
  'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false DO UPDATE
SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  options = EXCLUDED.options,
  description = EXCLUDED.description,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 只改 code = orientedShoot 的参数槽。共用的「偏航角」仍留给调整拍摄角度。
UPDATE ent_action_t1
SET
  param_slots_json = (
    WITH src AS (
      SELECT param_slots_json AS doc
    ),
    fields AS (
      SELECT COALESCE(
        jsonb_agg(
          CASE
            WHEN slot->>'fieldCode' = 'yaw' THEN jsonb_build_object(
              'required', true,
              'fieldCode', 'gimbal_yaw_rotate_angle',
              'defaultValue', NULL
            )
            ELSE slot
          END
          ORDER BY ord
        ),
        '[]'::jsonb
      ) AS slots
      FROM src,
      LATERAL jsonb_array_elements(doc->'methods'->0->'fields') WITH ORDINALITY AS t(slot, ord)
    )
    SELECT jsonb_set(
      doc,
      '{methods,0,fields}',
      CASE
        WHEN slots @> '[{"fieldCode":"oriented_photo_mode"}]'::jsonb THEN slots
        ELSE (
          SELECT jsonb_agg(item ORDER BY ord)
          FROM (
            SELECT slot AS item, ord
            FROM jsonb_array_elements(slots) WITH ORDINALITY AS t(slot, ord)
            WHERE slot->>'fieldCode' <> 'action_duration'
            UNION ALL
            SELECT jsonb_build_object(
              'required', true,
              'fieldCode', 'oriented_photo_mode',
              'defaultValue', NULL
            ), 1000
            UNION ALL
            SELECT slot, 1001
            FROM jsonb_array_elements(slots) AS t(slot)
            WHERE slot->>'fieldCode' = 'action_duration'
          ) ordered
        )
      END
    )
    FROM src, fields
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE code = 'orientedShoot'
  AND deleted = false
  AND tenant_id = 1;
