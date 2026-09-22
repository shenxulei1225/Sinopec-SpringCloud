-- ============================================================================
-- action · 16 动作耗时参数（字段库 + 现网动作槽）
-- 权威：动作库 param_slots_json.action_duration 默认值（分钟）是排程动作耗时。
-- 不负责：写进已保存路线；把 dwell_duration（停留秒）当成排程耗时。
-- 幂等：已有槽不重复追加；已填默认值不覆盖。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  v.code, v.name, v.type, v.unit, v.description, 'LIBRARY', 1, v.max_relations,
  'NONE', NULL, NULL, v.semantic_type, 1, 'seed'
FROM (
  VALUES
    (
      'action_duration', '动作耗时', 'NUMBER', 'min', 1,
      '排程用的动作耗时（分钟）。不是停留时长秒。', NULL
    )
) AS v(code, name, type, unit, max_relations, description, semantic_type)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  semantic_type = EXCLUDED.semantic_type,
  source = 'LIBRARY',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_action_t1 a
SET
  param_slots_json = '{"version":1,"fields":[]}'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND (a.param_slots_json IS NULL OR jsonb_typeof(a.param_slots_json) <> 'object');

UPDATE ent_action_t1 a
SET
  param_slots_json = jsonb_set(
    a.param_slots_json,
    '{fields}',
    CASE
      WHEN jsonb_typeof(a.param_slots_json->'fields') = 'array' THEN a.param_slots_json->'fields'
      ELSE '[]'::jsonb
    END
      || '[{"fieldCode":"action_duration","required":false,"defaultValue":null}]'::jsonb
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json) = 'object'
  AND (
    jsonb_typeof(a.param_slots_json->'fields') IS DISTINCT FROM 'array'
    OR NOT EXISTS (
      SELECT 1
      FROM jsonb_array_elements(a.param_slots_json->'fields') e
      WHERE e->>'fieldCode' = 'action_duration'
    )
  );

UPDATE ent_action_t1 a
SET
  param_slots_json = jsonb_set(
    a.param_slots_json,
    '{methods}',
    (
      SELECT COALESCE(jsonb_agg(
        CASE
          WHEN EXISTS (
            SELECT 1
            FROM jsonb_array_elements(COALESCE(m->'fields', '[]'::jsonb)) e
            WHERE e->>'fieldCode' = 'action_duration'
          ) THEN m
          ELSE jsonb_set(
            COALESCE(m, '{}'::jsonb),
            '{fields}',
            COALESCE(m->'fields', '[]'::jsonb)
              || '[{"fieldCode":"action_duration","required":false,"defaultValue":null}]'::jsonb
          )
        END
      ), '[]'::jsonb)
      FROM jsonb_array_elements(a.param_slots_json->'methods') m
    )
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND jsonb_array_length(a.param_slots_json->'methods') > 0;

CREATE TEMP TABLE action_duration_seed (
  code text PRIMARY KEY,
  minutes int NOT NULL
);

INSERT INTO action_duration_seed (code, minutes) VALUES
  ('act-arrive', 1),
  ('act-hover', 2),
  ('act-aim', 1),
  ('act-shoot', 1),
  ('act-ground-patrol', 5),
  ('act-gas-detect', 2),
  ('act-robot-arrive', 1),
  ('act-robot-hold', 2),
  ('act-robot-aim', 1),
  ('act-robot-shoot', 1),
  ('act-robot-self-check', 1),
  ('act-robot-battery-check', 1),
  ('act-robot-return-charge', 2),
  ('act-uav-self-check', 1),
  ('act-uav-battery-check', 1),
  ('act-uav-takeoff', 2),
  ('act-uav-land', 2),
  ('act-uav-return-charge', 2);

UPDATE ent_action_t1 a
SET
  param_slots_json = jsonb_set(
    a.param_slots_json,
    '{fields}',
    (
      SELECT jsonb_agg(
        CASE
          WHEN e->>'fieldCode' = 'action_duration'
               AND (e->'defaultValue' IS NULL OR jsonb_typeof(e->'defaultValue') = 'null')
          THEN jsonb_set(e, '{defaultValue}', to_jsonb(s.minutes))
          ELSE e
        END
      )
      FROM jsonb_array_elements(COALESCE(a.param_slots_json->'fields', '[]'::jsonb)) e
    )
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM action_duration_seed s
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = s.code
  AND jsonb_typeof(a.param_slots_json) = 'object'
  AND jsonb_typeof(a.param_slots_json->'fields') = 'array';
