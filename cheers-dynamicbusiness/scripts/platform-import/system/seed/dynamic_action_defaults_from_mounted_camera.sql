-- 按飞机上挂载的主相机规格，给动作补默认值。
-- 变焦焦距：有变焦范围用近端，否则用中长焦，再否则用长焦。单位毫米。
-- 光圈：同一只镜头的光圈，实际值乘 100。f/2.8 写成 280。
-- 感光度：规格范围的下限。
-- 不负责：快门、环境亮度、气体上报频率、喊话文件、探照灯亮度。挂载设备上没有这些默认数，不另编。
-- 不负责：禅思 H20T、H30T。这两款相机的焦距和光圈规格是空的。
-- 可重复执行：这一格已经有默认值的不覆盖。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE mount_cam (
  host_code text PRIMARY KEY,
  cam_code text NOT NULL
) ON COMMIT DROP;

INSERT INTO mount_cam (host_code, cam_code) VALUES
  ('dji-m30', 'dji-pl-m30-cam'),
  ('dji-m30t', 'dji-pl-m30t-cam'),
  ('dji-m3e', 'dji-pl-m3e-cam'),
  ('dji-m3t', 'dji-pl-m3t-cam'),
  ('dji-m3ta', 'dji-pl-m3ta-cam'),
  ('dji-m3d', 'dji-pl-m3d-cam'),
  ('dji-m3td', 'dji-pl-m3td-cam'),
  ('dji-m4e', 'dji-pl-m4e-cam'),
  ('dji-m4t', 'dji-pl-m4t-cam'),
  ('dji-m4d', 'dji-pl-m4d-cam'),
  ('dji-m4td', 'dji-pl-m4td-cam');

CREATE TEMP TABLE cam_num ON COMMIT DROP AS
SELECT m.code AS cam_code,
       a.field_code,
       (regexp_match(a.default_value, '([0-9]+(?:\.[0-9]+)?)'))[1]::numeric AS num
FROM dynamic_model m
JOIN dynamic_model_field_assignment a
  ON a.model_id = m.id AND a.deleted = false AND COALESCE(a.default_value, '') <> ''
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code IN (SELECT cam_code FROM mount_cam);

CREATE TEMP TABLE host_default ON COMMIT DROP AS
SELECT host.id::text AS model_id,
       focal.num::int AS focal_mm,
       round(aper.num * 100)::int AS aperture_x100,
       iso.num::int AS iso_base
FROM mount_cam map
JOIN dynamic_model host
  ON host.deleted = false AND host.tenant_id = 1 AND host.code = map.host_code
JOIN LATERAL (
  SELECT num
  FROM cam_num
  WHERE cam_code = map.cam_code
    AND field_code IN ('zoom_focal_length', 'mid_tele_focal_length', 'tele_focal_length')
  ORDER BY CASE field_code
    WHEN 'zoom_focal_length' THEN 1
    WHEN 'mid_tele_focal_length' THEN 2
    ELSE 3
  END
  LIMIT 1
) focal ON true
JOIN LATERAL (
  SELECT num
  FROM cam_num
  WHERE cam_code = map.cam_code
    AND field_code IN ('zoom_aperture', 'mid_tele_aperture', 'tele_aperture')
  ORDER BY CASE field_code
    WHEN 'zoom_aperture' THEN 1
    WHEN 'mid_tele_aperture' THEN 2
    ELSE 3
  END
  LIMIT 1
) aper ON true
JOIN LATERAL (
  SELECT num
  FROM cam_num
  WHERE cam_code = map.cam_code AND field_code = 'iso_normal'
  LIMIT 1
) iso ON true;

CREATE OR REPLACE FUNCTION pg_temp.apply_cam_default(model_id text, field jsonb)
RETURNS jsonb
LANGUAGE plpgsql AS $$
DECLARE
  code text := field->>'fieldCode';
  cur text := COALESCE(field->>'defaultValue', '');
  spec host_default%ROWTYPE;
BEGIN
  IF cur <> '' THEN
    RETURN field;
  END IF;
  SELECT * INTO spec FROM host_default h WHERE h.model_id = apply_cam_default.model_id;
  IF NOT FOUND THEN
    RETURN field;
  END IF;
  IF code = 'focalLength' THEN
    RETURN field || jsonb_build_object('defaultValue', spec.focal_mm::text);
  ELSIF code IN ('orientedCameraApertue', 'aperture_value') THEN
    RETURN field || jsonb_build_object('defaultValue', spec.aperture_x100::text);
  ELSIF code = 'orientedCameraISO' THEN
    RETURN field || jsonb_build_object('defaultValue', spec.iso_base::text);
  END IF;
  RETURN field;
END $$;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN m.value->>'executionMeans' = 'UAV'
             AND COALESCE(m.value->>'equipmentModelId', '') <> '' THEN
              jsonb_set(
                m.value,
                '{fields}',
                (
                  SELECT COALESCE(
                    jsonb_agg(
                      pg_temp.apply_cam_default(m.value->>'equipmentModelId', f.value)
                      ORDER BY f.ord
                    ),
                    '[]'::jsonb
                  )
                  FROM jsonb_array_elements(COALESCE(m.value->'fields', '[]'::jsonb))
                    WITH ORDINALITY AS f(value, ord)
                )
              )
            ELSE m.value
          END
          ORDER BY m.ord
        ), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS m(value, ord)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array';

DO $$
DECLARE
  bad int;
BEGIN
  SELECT count(*) INTO bad FROM host_default;
  IF bad <> 11 THEN
    RAISE EXCEPTION '挂载相机规格没有配齐：%', bad;
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code = 'dji-m30' AND host.deleted = false
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'orientedShoot'
      AND m->>'equipmentModelId' = host.id::text
      AND f->>'fieldCode' = 'focalLength'
      AND f->>'defaultValue' IS DISTINCT FROM '113'
  ) THEN
    RAISE EXCEPTION 'Matrice 30 变焦焦距默认值不是 113';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code = 'dji-m3d' AND host.deleted = false
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'orientedShoot'
      AND m->>'equipmentModelId' = host.id::text
      AND f->>'fieldCode' = 'orientedCameraApertue'
      AND f->>'defaultValue' IS DISTINCT FROM '440'
  ) THEN
    RAISE EXCEPTION 'Matrice 3D 光圈默认值不是 440';
  END IF;

END $$;

COMMIT;
