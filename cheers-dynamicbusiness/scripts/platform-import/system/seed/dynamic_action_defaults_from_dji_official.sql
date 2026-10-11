-- 按大疆官网已公布的规格和快门档，补还空着的动作默认值。
-- 禅思 H20T（Matrice 300 RTK）：变焦等效焦距近端 31.7 毫米，常规光圈 f/2.8 写成 280，拍照感光度下限 100。
--   来源：https://enterprise.dji.com/zenmuse-h20-series/specs
-- 禅思 H30T（Matrice 350 RTK、Matrice 400）：变焦等效焦距近端 33.4 毫米，光圈 f/1.6 写成 160，单拍感光度下限 100。
--   来源：https://enterprise.dji.com/zenmuse-h30-series/specs
-- 焦距用等效毫米，和 Matrice 30 已写入的 113 同一套口径。不写热成像焦距。
-- 快门：航线字段单位是秒。飞机属性快门档 12 对应 1/1000 秒，写成 0.001。档位字段写成 12。不写自动档。
--   来源：https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html
-- 感光度档：属性枚举 3 对应 100。不把 100 写进档位字段。
-- 不写：环境亮度（航线元素页没有样例数字）、探照灯亮度（只公布 0 到 100）、喊话文件、气体上报频率。
-- 不写机场、东古、机器人。已有默认值不覆盖。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE official_default (
  model_id text NOT NULL,
  field_code text NOT NULL,
  default_value text NOT NULL,
  PRIMARY KEY (model_id, field_code)
) ON COMMIT DROP;

INSERT INTO official_default (model_id, field_code, default_value)
SELECT host.id::text, spec.field_code, spec.default_value
FROM (
  VALUES
    ('dji-m300-rtk', 'focalLength', '31.7'),
    ('dji-m300-rtk', 'orientedCameraApertue', '280'),
    ('dji-m300-rtk', 'aperture_value', '280'),
    ('dji-m300-rtk', 'orientedCameraISO', '100'),
    ('dji-m350-rtk', 'focalLength', '33.4'),
    ('dji-m350-rtk', 'orientedCameraApertue', '160'),
    ('dji-m350-rtk', 'aperture_value', '160'),
    ('dji-m350-rtk', 'orientedCameraISO', '100'),
    ('dji-m400', 'focalLength', '33.4'),
    ('dji-m400', 'orientedCameraApertue', '160'),
    ('dji-m400', 'aperture_value', '160'),
    ('dji-m400', 'orientedCameraISO', '100')
) AS spec(model_code, field_code, default_value)
JOIN dynamic_model host
  ON host.deleted = false
 AND host.tenant_id = 1
 AND host.code = spec.model_code;

INSERT INTO official_default (model_id, field_code, default_value)
SELECT host.id::text, spec.field_code, spec.default_value
FROM dynamic_model host
CROSS JOIN (
  VALUES
    ('orientedCameraShutterTime', '0.001'),
    ('shutter_value', '12'),
    ('iso_value', '3')
) AS spec(field_code, default_value)
WHERE host.deleted = false
  AND host.tenant_id = 1
  AND host.code LIKE 'dji-m%'
ON CONFLICT (model_id, field_code) DO NOTHING;

CREATE OR REPLACE FUNCTION pg_temp.apply_official_default(model_id text, field jsonb)
RETURNS jsonb
LANGUAGE plpgsql AS $$
DECLARE
  code text := field->>'fieldCode';
  cur text := COALESCE(field->>'defaultValue', '');
  nxt text;
BEGIN
  IF cur <> '' THEN
    RETURN field;
  END IF;
  SELECT d.default_value INTO nxt
  FROM official_default d
  WHERE d.model_id = apply_official_default.model_id
    AND d.field_code = code;
  IF nxt IS NULL THEN
    RETURN field;
  END IF;
  RETURN field || jsonb_build_object('defaultValue', nxt);
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
                      pg_temp.apply_official_default(m.value->>'equipmentModelId', f.value)
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
  host_count int;
BEGIN
  SELECT count(DISTINCT model_id) INTO host_count
  FROM official_default
  WHERE field_code = 'focalLength';
  IF host_count <> 3 THEN
    RAISE EXCEPTION '禅思挂载的飞机没有配齐：%', host_count;
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code = 'dji-m30' AND host.deleted = false AND host.tenant_id = 1
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'orientedShoot'
      AND m->>'equipmentModelId' = host.id::text
      AND (
        (f->>'fieldCode' = 'focalLength' AND f->>'defaultValue' IS DISTINCT FROM '113')
        OR (f->>'fieldCode' = 'orientedCameraApertue' AND f->>'defaultValue' IS DISTINCT FROM '280')
        OR (f->>'fieldCode' = 'orientedCameraISO' AND f->>'defaultValue' IS DISTINCT FROM '100')
        OR (f->>'fieldCode' = 'orientedCameraShutterTime' AND f->>'defaultValue' IS DISTINCT FROM '0.001')
        OR (f->>'fieldCode' = 'orientedCameraLuminance' AND f->>'defaultValue' IS DISTINCT FROM '16')
      )
  ) THEN
    RAISE EXCEPTION 'Matrice 30 定点拍照默认值被改错';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code = 'dji-m300-rtk' AND host.deleted = false AND host.tenant_id = 1
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'orientedShoot'
      AND m->>'equipmentModelId' = host.id::text
      AND (
        (f->>'fieldCode' = 'focalLength' AND f->>'defaultValue' IS DISTINCT FROM '31.7')
        OR (f->>'fieldCode' = 'orientedCameraApertue' AND f->>'defaultValue' IS DISTINCT FROM '280')
        OR (f->>'fieldCode' = 'orientedCameraISO' AND f->>'defaultValue' IS DISTINCT FROM '100')
      )
  ) THEN
    RAISE EXCEPTION 'Matrice 300 RTK 定点拍照没有按 H20T 规格写入';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code IN ('dji-m350-rtk', 'dji-m400') AND host.deleted = false AND host.tenant_id = 1
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'orientedShoot'
      AND m->>'equipmentModelId' = host.id::text
      AND (
        (f->>'fieldCode' = 'focalLength' AND f->>'defaultValue' IS DISTINCT FROM '33.4')
        OR (f->>'fieldCode' = 'orientedCameraApertue' AND f->>'defaultValue' IS DISTINCT FROM '160')
        OR (f->>'fieldCode' = 'orientedCameraISO' AND f->>'defaultValue' IS DISTINCT FROM '100')
      )
  ) THEN
    RAISE EXCEPTION 'Matrice 350 RTK 或 Matrice 400 定点拍照没有按 H30T 规格写入';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code IN ('donggu-uav-small', 'donggu-uav-large') AND host.deleted = false
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    WHERE a.code = 'hover'
      AND m->>'equipmentModelId' = host.id::text
  ) THEN
    RAISE EXCEPTION '东古被写进了悬停';
  END IF;

END $$;

COMMIT;
