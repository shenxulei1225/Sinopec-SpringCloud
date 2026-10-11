-- 悬停时长不单独占一格。动作耗时就是悬停时长。
-- 若无人机这一档里还有 hoverTime，删掉这一格。不改适用型号，不替换整份参数。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN meth->>'executionMeans' = 'UAV'
            THEN jsonb_set(
              meth,
              '{fields}',
              COALESCE((
                SELECT jsonb_agg(f)
                FROM jsonb_array_elements(COALESCE(meth->'fields', '[]'::jsonb)) f
                WHERE f->>'fieldCode' <> 'hoverTime'
              ), '[]'::jsonb)
            )
            ELSE meth
          END
          ORDER BY ord
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS rows(meth, ord)
      )
    ),
    updater = 'patch-hover-param-slots',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.code = 'hover'
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(a.param_slots_json->'methods') meth,
         jsonb_array_elements(COALESCE(meth->'fields', '[]'::jsonb)) f
    WHERE meth->>'executionMeans' = 'UAV'
      AND f->>'fieldCode' = 'hoverTime'
  );

COMMIT;
