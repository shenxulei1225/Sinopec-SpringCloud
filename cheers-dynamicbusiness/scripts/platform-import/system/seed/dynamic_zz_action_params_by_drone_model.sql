-- 已停用：这张脚本按无人机型号写入定点拍照参数名单。不要再执行。
-- 定点拍照的参数名单改按相机类型存放，见 dynamic_action_oriented_shoot_by_camera.sql。
-- 定点拍照按无人机型号写入参数名单。
-- 负责：官网定向拍照支持的飞机型号各一份元素名单，俯仰角范围按官网；东古小型无人机和东古大型无人机写入东古拍照要用户填的偏航、俯仰、焦距、张数、驻停、时长。
-- 不负责：机场、M300、M350、M400、M3TA（不把未声明支持的型号写成已经支持）；给 M4 编相机类型编号；给环境亮度写一个数；把东古字段抄进大疆机型。
-- 重跑时先去掉本动作上已按型号拆开的无人机行，再按当前未分型号的那一份重写。未分型号的那一行留下。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE OR REPLACE FUNCTION pg_temp.oriented_fields(src jsonb, pitch_min numeric, pitch_max numeric, include_lens boolean)
RETURNS jsonb
LANGUAGE sql
AS $$
  SELECT COALESCE(jsonb_agg(patched ORDER BY ord), '[]'::jsonb)
  FROM (
    SELECT
      CASE
        WHEN f->>'fieldCode' = 'gimbalPitchRotateAngle' AND $2 IS NOT NULL
          THEN f || jsonb_build_object('min', $2, 'max', $3)
        WHEN f->>'fieldCode' = 'gimbalYawRotateAngle'
          THEN f || jsonb_build_object('min', -180, 'max', 180)
        WHEN f->>'fieldCode' = 'aircraftHeading'
          THEN f || jsonb_build_object('min', -180, 'max', 180)
        WHEN f->>'fieldCode' = 'focusX'
          THEN f || jsonb_build_object('min', 1, 'max', 959)
        WHEN f->>'fieldCode' = 'focusY'
          THEN f || jsonb_build_object('min', 1, 'max', 719)
        WHEN f->>'fieldCode' = 'focusRegionWidth'
          THEN f || jsonb_build_object('min', 1, 'max', 959)
        WHEN f->>'fieldCode' = 'focusRegionHeight'
          THEN f || jsonb_build_object('min', 1, 'max', 719)
        WHEN f->>'fieldCode' = 'targetAngle'
          THEN f || jsonb_build_object('min', 0, 'max', 360)
        ELSE f
      END AS patched,
      ord
    FROM jsonb_array_elements($1) WITH ORDINALITY AS t(f, ord)
    WHERE $4 OR f->>'fieldCode' IS DISTINCT FROM 'payloadLensIndex'
  ) s;
$$;

CREATE TEMP TABLE oriented_base ON COMMIT DROP AS
SELECT m->'fields' AS fields
FROM ent_action_t1 a
JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'orientedShoot'
  AND m->>'executionMeans' = 'UAV'
  AND NOT (m ? 'equipmentModelId');

CREATE TEMP TABLE donggu_photo_fields ON COMMIT DROP AS
SELECT COALESCE(jsonb_agg(
         CASE
           WHEN COALESCE(f->>'valueSource', '') = '' THEN f || jsonb_build_object('valueSource', 'ACTION_PARAM')
           ELSE f
         END
         ORDER BY ord
       ), '[]'::jsonb) AS fields
FROM ent_action_t1 a
JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m
  ON m->>'executionMeans' = 'ROBOT'
JOIN LATERAL jsonb_array_elements(m->'fields') WITH ORDINALITY AS t(f, ord) ON true
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'act-robot-shoot';

-- 俯仰范围只写官网给出数字的机型。include_lens 为 false 的是官网支持列没有「拍摄照片存储类型」的 M3E/M3T。
CREATE TEMP TABLE model_spec (
  model_code text PRIMARY KEY,
  pitch_min numeric,
  pitch_max numeric,
  include_lens boolean,
  kind text
) ON COMMIT DROP;

INSERT INTO model_spec (model_code, pitch_min, pitch_max, include_lens, kind) VALUES
  ('dji-m30', -120, 45, true, 'dji'),
  ('dji-m30t', -120, 45, true, 'dji'),
  ('dji-m3e', -90, 35, false, 'dji'),
  ('dji-m3t', -90, 35, false, 'dji'),
  ('dji-m3d', -90, 30, true, 'dji'),
  ('dji-m3td', -90, 30, true, 'dji'),
  ('dji-m4d', -90, 30, true, 'dji'),
  ('dji-m4td', -90, 30, true, 'dji'),
  ('dji-m4e', -90, 30, true, 'dji'),
  ('dji-m4t', -90, 30, true, 'dji'),
  ('donggu-uav-large', NULL, NULL, true, 'donggu'),
  ('donggu-uav-small', NULL, NULL, true, 'donggu');

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(elem), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
        WHERE NOT (
          elem->>'executionMeans' = 'UAV'
          AND elem ? 'equipmentModelId'
        )
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'orientedShoot'
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (a.param_slots_json->'methods') || built.rows
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT jsonb_agg(row_obj ORDER BY model_code) AS rows
  FROM (
    SELECT
      s.model_code,
      jsonb_build_object(
        'executionMeans', 'UAV',
        'equipmentModelId', m.id,
        'fields',
          CASE
            WHEN s.kind = 'donggu' THEN (SELECT fields FROM donggu_photo_fields)
            ELSE pg_temp.oriented_fields(
              (SELECT fields FROM oriented_base),
              s.pitch_min,
              s.pitch_max,
              s.include_lens
            )
          END
      ) AS row_obj
    FROM model_spec s
    JOIN dynamic_model m
      ON m.deleted = false
     AND m.tenant_id = 1
     AND m.entity_type_code = 'equipment'
     AND m.code = s.model_code
  ) spec_rows
) built
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'orientedShoot'
  AND built.rows IS NOT NULL;

COMMIT;
