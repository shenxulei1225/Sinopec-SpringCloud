-- 机器人检查步骤的到达用「到达作业位置（机器人）」，不再用飞向目标点。
-- 这一步仍对照移动报文：选择位置上的点位、纬度、经度、地面高度。
-- 不负责：无人机和人工的飞向目标点；不恢复旧的 location_ref。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_action_t1
SET deleted = false,
    execution_means = '["ROBOT"]'::jsonb,
    param_slots_json = $slots${
      "version": 2,
      "methods": [
        {
          "executionMeans": "ROBOT",
          "fields": [
            {
              "fieldCode": "F-e49f76bdca3e4e1393e8e2f1cc0e7867",
              "required": true,
              "valueSource": "ACTION_PARAM",
              "defaultValue": null
            }
          ]
        }
      ]
    }$slots$::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code = 'act-robot-arrive';

UPDATE dynamic_entity_category_relation_t1
SET deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND entity_id = 10
  AND deleted = true;

UPDATE ent_action_t1
SET execution_means = (
      SELECT COALESCE(jsonb_agg(item.value ORDER BY item.ordinal), '[]'::jsonb)
      FROM jsonb_array_elements(execution_means) WITH ORDINALITY AS item(value, ordinal)
      WHERE item.value <> '"ROBOT"'::jsonb
    ),
    param_slots_json = jsonb_set(
      param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(method.value ORDER BY method.ordinal), '[]'::jsonb)
        FROM jsonb_array_elements(param_slots_json->'methods') WITH ORDINALITY AS method(value, ordinal)
        WHERE method.value->>'executionMeans' IS DISTINCT FROM 'ROBOT'
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code = 'action-065340437bcb46788b7dc261fab81c63';

UPDATE ent_protocol_mapping_t1
SET name = '到达作业位置（机器人） · 机器人对接协议',
    custom_fields = jsonb_set(custom_fields, '{action_ref,id}', '10'::jsonb, true),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code = 'map-arrive-robot-ws';

UPDATE ent_inspection_item_t1 item
SET step_tree_json = jsonb_set(
      item.step_tree_json,
      '{methods}'::text[],
      (
        SELECT jsonb_agg(rewritten.method ORDER BY rewritten.ordinal)
        FROM (
          SELECT method.ordinal,
                 CASE
                   WHEN COALESCE(method.value->>'methodKey', method.value->>'executionMeans') = 'ROBOT'
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
                                 'actionId', 'act-robot-arrive',
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
