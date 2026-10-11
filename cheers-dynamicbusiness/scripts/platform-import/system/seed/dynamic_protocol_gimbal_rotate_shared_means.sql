-- 旋转云台是无人机、机器人、固定摄像机共用的动作。人到现场是目视，不转云台。
-- 检查方法里机器人、固定摄像机的「调整拍摄角度」改指向这个动作。
-- 不负责：这几档的拍照；不另造机器人云台动作；不给这几档补大疆航线报文；不把旋转云台放进人工步骤。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_action_t1 a
SET execution_means = '["UAV","ROBOT","FIXED_CAMERA"]'::jsonb,
    param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}'::text[],
      (
        SELECT jsonb_agg(piece.method ORDER BY piece.ord)
        FROM (
          SELECT existing.ordinal AS ord, existing.value AS method
          FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS existing(value, ordinal)
          UNION ALL
          SELECT 100 + extra.ord,
                 jsonb_set(uav.value, '{executionMeans}'::text[], to_jsonb(extra.means))
          FROM jsonb_array_elements(a.param_slots_json->'methods') AS uav(value)
          JOIN (VALUES ('ROBOT', 1), ('FIXED_CAMERA', 2)) AS extra(means, ord)
            ON upper(uav.value->>'executionMeans') = 'UAV'
          WHERE NOT EXISTS (
            SELECT 1
            FROM jsonb_array_elements(a.param_slots_json->'methods') present
            WHERE upper(COALESCE(present->>'executionMeans', present->>'methodKey')) = extra.means
          )
        ) piece
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'gimbalRotate';

UPDATE ent_inspection_item_t1 item
SET step_tree_json = jsonb_set(
      item.step_tree_json,
      '{methods}'::text[],
      (
        SELECT jsonb_agg(rewritten.method ORDER BY rewritten.ordinal)
        FROM (
          SELECT method.ordinal,
                 CASE
                   WHEN COALESCE(method.value->>'methodKey', method.value->>'executionMeans')
                        IN ('ROBOT', 'FIXED_CAMERA')
                     THEN jsonb_set(
                       method.value,
                       CASE
                         WHEN method.value ? 'stepTree' THEN '{stepTree}'::text[]
                         ELSE '{actionTree}'::text[]
                       END,
                       (
                         SELECT COALESCE(jsonb_agg(
                           CASE node.value->>'actionId'
                             WHEN 'act-robot-aim' THEN node.value || jsonb_build_object(
                               'actionId', 'gimbalRotate', 'paramSlots', '[]'::jsonb)
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
  AND item.step_tree_json::text ~ '"actionId": "act-robot-aim"';

-- 人工不转云台。去掉已经抄到人工档上的那一套参数。
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}'::text[],
      (
        SELECT COALESCE(jsonb_agg(method.value ORDER BY method.ordinal), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS method(value, ordinal)
        WHERE upper(COALESCE(method.value->>'executionMeans', method.value->>'methodKey', '')) <> 'MANUAL'
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = 'gimbalRotate'
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(a.param_slots_json->'methods') method
    WHERE upper(COALESCE(method->>'executionMeans', method->>'methodKey', '')) = 'MANUAL'
  );
