-- 无人机、机器人的动作参数只留在带型号的那一行。
-- 没有型号的那一行删掉。人工、固定摄像机仍是这一巡检方式自己的一行，不动。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(
          jsonb_agg(m.value ORDER BY m.ord),
          '[]'::jsonb
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS m(value, ord)
        WHERE NOT (
          COALESCE(m.value->>'executionMeans', '') IN ('UAV', 'ROBOT')
          AND COALESCE(m.value->>'equipmentModelId', '') = ''
        )
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
    WHERE COALESCE(m->>'executionMeans', '') IN ('UAV', 'ROBOT')
      AND COALESCE(m->>'equipmentModelId', '') = ''
  );

DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    WHERE a.deleted = false
      AND a.tenant_id = 1
      AND COALESCE(m->>'executionMeans', '') IN ('UAV', 'ROBOT')
      AND COALESCE(m->>'equipmentModelId', '') = ''
  ) THEN
    RAISE EXCEPTION 'UAV or ROBOT row without a model is still present';
  END IF;
END $$;

COMMIT;
