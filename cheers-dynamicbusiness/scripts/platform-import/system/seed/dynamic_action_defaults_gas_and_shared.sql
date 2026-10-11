-- 气体上报频率：仍带这一格的动作，空着的默认值写成 30 秒。不把这一格加回大疆飞机。
-- 还没选型号时，任务页读的是不带型号的那一行。
-- 同一动作、同一手段、同一格，各型号已经填过的默认值如果完全一样，补到不带型号的那一行。
-- 各型号数字不一样的格子保持空着，不挑一个数写上去。已有默认值不覆盖。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(
          jsonb_set(
            m.value,
            '{fields}',
            (
              SELECT COALESCE(
                jsonb_agg(
                  CASE
                    WHEN f.value->>'fieldCode' = 'gas_frequency'
                     AND COALESCE(f.value->>'defaultValue', '') = '' THEN
                      f.value || jsonb_build_object('defaultValue', '30')
                    ELSE f.value
                  END
                  ORDER BY f.ord
                ),
                '[]'::jsonb
              )
              FROM jsonb_array_elements(COALESCE(m.value->'fields', '[]'::jsonb))
                WITH ORDINALITY AS f(value, ord)
            )
          )
          ORDER BY m.ord
        ), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS m(value, ord)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.param_slots_json::text LIKE '%gas_frequency%';

CREATE TEMP TABLE shared_default ON COMMIT DROP AS
SELECT action_id, means, field_code, min(default_value) AS default_value
FROM (
  SELECT a.id AS action_id,
         COALESCE(m->>'executionMeans', '') AS means,
         f->>'fieldCode' AS field_code,
         f->>'defaultValue' AS default_value
  FROM ent_action_t1 a
  JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
  JOIN LATERAL jsonb_array_elements(COALESCE(m->'fields', '[]'::jsonb)) f ON true
  WHERE a.deleted = false
    AND a.tenant_id = 1
    AND COALESCE(m->>'equipmentModelId', '') <> ''
    AND COALESCE(f->>'defaultValue', '') <> ''
) model_rows
GROUP BY action_id, means, field_code
HAVING count(DISTINCT default_value) = 1;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(
          jsonb_set(
            m.value,
            '{fields}',
            (
              SELECT COALESCE(
                jsonb_agg(
                  CASE
                    WHEN COALESCE(m.value->>'equipmentModelId', '') = ''
                     AND COALESCE(f.value->>'defaultValue', '') = ''
                     AND shared.default_value IS NOT NULL THEN
                      f.value || jsonb_build_object('defaultValue', shared.default_value)
                    ELSE f.value
                  END
                  ORDER BY f.ord
                ),
                '[]'::jsonb
              )
              FROM jsonb_array_elements(COALESCE(m.value->'fields', '[]'::jsonb))
                WITH ORDINALITY AS f(value, ord)
              LEFT JOIN shared_default shared
                ON shared.action_id = a.id
               AND shared.means = COALESCE(m.value->>'executionMeans', '')
               AND shared.field_code = f.value->>'fieldCode'
            )
          )
          ORDER BY m.ord
        ), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS m(value, ord)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(a.param_slots_json->'methods') m
    JOIN jsonb_array_elements(COALESCE(m->'fields', '[]'::jsonb)) f ON true
    JOIN shared_default shared
      ON shared.action_id = a.id
     AND shared.means = COALESCE(m->>'executionMeans', '')
     AND shared.field_code = f->>'fieldCode'
    WHERE COALESCE(m->>'equipmentModelId', '') = ''
      AND COALESCE(f->>'defaultValue', '') = ''
  );

DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host
      ON host.deleted = false AND host.tenant_id = 1 AND host.code LIKE 'dji-m%'
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(COALESCE(m->'fields', '[]'::jsonb)) f ON true
    WHERE a.deleted = false
      AND a.tenant_id = 1
      AND m->>'equipmentModelId' = host.id::text
      AND f->>'fieldCode' = 'gas_frequency'
  ) THEN
    RAISE EXCEPTION 'gas_frequency is back on a dji aircraft row';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(COALESCE(m->'fields', '[]'::jsonb)) f ON true
    WHERE a.deleted = false
      AND a.tenant_id = 1
      AND f->>'fieldCode' = 'gas_frequency'
      AND COALESCE(f->>'defaultValue', '') <> '30'
  ) THEN
    RAISE EXCEPTION 'gas_frequency default is not 30';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(COALESCE(m->'fields', '[]'::jsonb)) f ON true
    WHERE a.deleted = false
      AND a.tenant_id = 1
      AND a.code = 'orientedShoot'
      AND COALESCE(m->>'equipmentModelId', '') = ''
      AND f->>'fieldCode' IN ('focalLength', 'orientedCameraApertue')
      AND COALESCE(f->>'defaultValue', '') <> ''
  ) THEN
    RAISE EXCEPTION 'unscoped focal or aperture was filled from mixed model values';
  END IF;
END $$;

COMMIT;
