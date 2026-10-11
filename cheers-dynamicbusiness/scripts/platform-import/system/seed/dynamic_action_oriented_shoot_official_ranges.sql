-- 定点拍照的俯仰范围按大疆官网分相机改。
-- 负责：定向拍照这一页列出的自带相机保持该页的俯仰。
--       禅思、Z30、XT2、XTS 改成云台限位页上正向安装的俯仰。
--       官网两页都没写俯仰的相机，去掉从 Matrice 30 抄来的俯仰范围。
-- 不负责：改偏航。定点拍照这一格官网偏航仍是 -180 到 180。云台关节能转到 310 的，不写进这一格。
--         改海康的格子。改设备上已经填的角度。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE official_pitch (
  camera_code text PRIMARY KEY,
  pitch_min numeric,
  pitch_max numeric,
  clear_range boolean NOT NULL
) ON COMMIT DROP;

INSERT INTO official_pitch (camera_code, pitch_min, pitch_max, clear_range) VALUES
  ('dji-pl-h20', -120, 45, false),
  ('dji-pl-h20t', -120, 30, false),
  ('dji-pl-h20n', -120, 30, false),
  ('dji-pl-h30', -120, 60, false),
  ('dji-pl-h30t', -120, 60, false),
  ('dji-pl-z30', -120, 30, false),
  ('dji-pl-xts', -120, 30, false),
  ('dji-pl-xt2', -90, 30, false),
  ('dji-pl-fpv', NULL, NULL, true),
  ('dji-pl-assist', NULL, NULL, true),
  ('dji-pl-dock-cam', NULL, NULL, true);

CREATE OR REPLACE FUNCTION pg_temp.apply_pitch(fields jsonb, pitch_min numeric, pitch_max numeric, clear_range boolean)
RETURNS jsonb
LANGUAGE sql
AS $$
  SELECT COALESCE(jsonb_agg(
    CASE
      WHEN field->>'fieldCode' <> 'gimbalPitchRotateAngle' THEN field
      WHEN clear_range THEN field - 'min' - 'max'
      ELSE field || jsonb_build_object('min', pitch_min, 'max', pitch_max)
    END
    ORDER BY ord
  ), '[]'::jsonb)
  FROM jsonb_array_elements(COALESCE(fields, '[]'::jsonb)) WITH ORDINALITY AS listed(field, ord);
$$;

UPDATE ent_action_t1 action
SET param_slots_json = jsonb_set(
      action.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN spec.camera_code IS NULL THEN elem
            ELSE jsonb_set(
              elem,
              '{fields}',
              pg_temp.apply_pitch(elem->'fields', spec.pitch_min, spec.pitch_max, spec.clear_range)
            )
          END
          ORDER BY ord
        ), '[]'::jsonb)
        FROM jsonb_array_elements(action.param_slots_json->'methods') WITH ORDINALITY AS listed(elem, ord)
        LEFT JOIN dynamic_model camera
          ON camera.deleted = false
         AND camera.id::text = elem->>'equipmentModelId'
        LEFT JOIN official_pitch spec
          ON spec.camera_code = camera.code
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE action.deleted = false
  AND action.tenant_id = 1
  AND action.code = 'orientedShoot'
  AND jsonb_typeof(action.param_slots_json->'methods') = 'array';

COMMIT;
