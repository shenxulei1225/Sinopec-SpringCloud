-- 协议 / 动作 / 对照 对齐审计（本地 dynamicbusiness）
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

\echo '=== C. 空对照但指令有必填 request 字段（界面必显示参数未齐）==='
SELECT m.custom_fields->>'mapped_protocol_version' AS ver,
       m.code AS map_code,
       a.code AS action_code,
       p.code AS instr_code
FROM ent_protocol_mapping_t1 m
JOIN ent_action_t1 a ON a.id = (m.custom_fields->'action_ref'->>'id')::bigint AND a.deleted = false
JOIN ent_data_protocol_t1 p ON p.id = (m.custom_fields->'protocol_instruction_ref'->>'id')::bigint AND p.deleted = false
WHERE m.deleted = false AND m.tenant_id = 1
  AND jsonb_array_length(COALESCE(m.custom_fields->'param_slot_mapping_json', '[]'::jsonb)) = 0
  AND EXISTS (
    SELECT 1 FROM jsonb_array_elements(COALESCE(p.custom_fields->'field_description_json'->'outbound', '[]'::jsonb)) item
    WHERE item->>'path' LIKE 'request.%'
      AND COALESCE((item->>'required')::boolean, true)
  )
ORDER BY 1, 3;

\echo '=== B. USER 对照槽在动作 param_slots 中无 fieldCode ==='
WITH user_slots AS (
  SELECT m.code AS map_code,
         m.custom_fields->>'mapped_protocol_version' AS ver,
         a.id AS action_id,
         a.code AS action_code,
         trim(slot->>'slot') AS slot_code
  FROM ent_protocol_mapping_t1 m
  JOIN ent_action_t1 a ON a.id = (m.custom_fields->'action_ref'->>'id')::bigint AND a.deleted = false
  CROSS JOIN LATERAL jsonb_array_elements(COALESCE(m.custom_fields->'param_slot_mapping_json', '[]'::jsonb)) slot
  WHERE m.deleted = false AND m.tenant_id = 1
    AND trim(COALESCE(slot->>'slot', '')) <> ''
    AND upper(COALESCE(slot->>'sourceKind', 'ACTION_PARAM')) IN ('USER', 'ACTION_PARAM')
)
SELECT DISTINCT ver, action_code, slot_code, map_code
FROM user_slots u
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t1 a,
       jsonb_array_elements(COALESCE(a.param_slots_json->'methods', '[]'::jsonb)) meth,
       jsonb_array_elements(COALESCE(meth->'fields', '[]'::jsonb)) f
  WHERE a.id = u.action_id AND f->>'fieldCode' = u.slot_code
)
AND NOT EXISTS (
  SELECT 1 FROM ent_action_t1 a,
       jsonb_array_elements(COALESCE(a.param_slots_json->'fields', '[]'::jsonb)) f
  WHERE a.id = u.action_id AND f->>'fieldCode' = u.slot_code
)
ORDER BY 1, 2, 3;
