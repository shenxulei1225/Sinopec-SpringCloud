-- 人工检查步骤的到达用「到达作业位置（人工）」，不再用飞向目标点。
-- 参数仍是选择位置，生成任务时把要去的点写进这一格。人工不发设备报文，不另做指令对照。
-- 不负责：无人机的飞向目标点；摄像机方法。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite,
  custom_fields, creator, deleted
)
SELECT
  tenant_id, entity_type_code, model_id,
  '到达作业位置（人工）',
  'act-manual-arrive',
  status,
  '["MANUAL"]'::jsonb,
  jsonb_set(param_slots_json, '{methods,0,executionMeans}', '"MANUAL"'::jsonb),
  child_action_ids_json,
  is_composite,
  custom_fields,
  'seed',
  false
FROM ent_action_t1 source
WHERE source.tenant_id = 1
  AND source.code = 'act-robot-arrive'
  AND NOT EXISTS (
    SELECT 1 FROM ent_action_t1 existing
    WHERE existing.tenant_id = 1 AND existing.code = 'act-manual-arrive'
  );

INSERT INTO dynamic_entity_category_relation_t1 (
  entity_id, category_id, entity_type_code, sort, creator, deleted, tenant_id
)
SELECT action.id, link.category_id, 'action', link.sort, 'seed', false, 1
FROM ent_action_t1 action
JOIN dynamic_entity_category_relation_t1 link
  ON link.tenant_id = 1
 AND link.deleted = false
 AND link.entity_type_code = 'action'
 AND link.entity_id = 10
WHERE action.tenant_id = 1
  AND action.code = 'act-manual-arrive'
  AND action.deleted = false
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 existing
    WHERE existing.tenant_id = 1
      AND existing.deleted = false
      AND existing.entity_type_code = 'action'
      AND existing.entity_id = action.id
      AND existing.category_id = link.category_id
  );

UPDATE ent_action_t1
SET execution_means = (
      SELECT COALESCE(jsonb_agg(item.value ORDER BY item.ordinal), '[]'::jsonb)
      FROM jsonb_array_elements(execution_means) WITH ORDINALITY AS item(value, ordinal)
      WHERE item.value <> '"MANUAL"'::jsonb
    ),
    param_slots_json = jsonb_set(
      param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(method.value ORDER BY method.ordinal), '[]'::jsonb)
        FROM jsonb_array_elements(param_slots_json->'methods') WITH ORDINALITY AS method(value, ordinal)
        WHERE method.value->>'executionMeans' IS DISTINCT FROM 'MANUAL'
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code = 'action-065340437bcb46788b7dc261fab81c63';

UPDATE ent_inspection_item_t1 item
SET step_tree_json = jsonb_set(
      item.step_tree_json,
      '{methods}'::text[],
      (
        SELECT jsonb_agg(rewritten.method ORDER BY rewritten.ordinal)
        FROM (
          SELECT method.ordinal,
                 CASE
                   WHEN COALESCE(method.value->>'methodKey', method.value->>'executionMeans') = 'MANUAL'
                     THEN jsonb_set(
                       method.value,
                       CASE
                         WHEN method.value ? 'stepTree' THEN '{stepTree}'::text[]
                         ELSE '{actionTree}'::text[]
                       END,
                       (
                         SELECT COALESCE(jsonb_agg(
                           CASE
                             WHEN node.value->>'actionId' = 'action-065340437bcb46788b7dc261fab81c63'
                               THEN node.value || jsonb_build_object(
                                 'actionId', 'act-manual-arrive',
                                 'paramSlots', '[]'::jsonb
                               )
                             ELSE node.value
                           END
                           ORDER BY node.ordinal
                         ), '[]'::jsonb)
                         FROM jsonb_array_elements(
                           COALESCE(method.value->'stepTree', method.value->'actionTree')
                         ) WITH ORDINALITY AS node(value, ordinal)
                       )
                     )
                   ELSE method.value
                 END AS method
          FROM jsonb_array_elements(item.step_tree_json->'methods') WITH ORDINALITY AS method(value, ordinal)
        ) rewritten
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE item.deleted = false
  AND item.tenant_id = 1
  AND jsonb_typeof(item.step_tree_json) = 'object'
  AND item.step_tree_json::text LIKE '%action-065340437bcb46788b7dc261fab81c63%';
