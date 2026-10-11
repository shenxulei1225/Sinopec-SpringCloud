-- 检查方法里仍指向已删除动作的无人机步骤，改到还在的航线动作。
-- 悬停观察 → 悬停等待；调整拍摄角度 → 旋转云台；拍照 → 定向拍照。
-- 旧的到达作业位置 → 飞向目标点。只改无人机方法，以及还没按手段拆开、用的就是这几条无人机动作的旧清单。
-- 不负责：机器人、人工、摄像机方法。这几档没有对应的新拍照、转云台动作，不把无人机动作套上去。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_inspection_item_t1 item
SET step_tree_json = jsonb_set(
      item.step_tree_json,
      '{methods}'::text[],
      (
        SELECT jsonb_agg(rewritten.method ORDER BY rewritten.ordinal)
        FROM (
          SELECT method.ordinal,
                 CASE
                   WHEN COALESCE(method.value->>'methodKey', method.value->>'executionMeans') = 'UAV'
                     THEN jsonb_set(
                       method.value,
                       CASE
                         WHEN method.value ? 'stepTree' THEN '{stepTree}'::text[]
                         ELSE '{actionTree}'::text[]
                       END,
                       (
                         SELECT COALESCE(jsonb_agg(
                           CASE node.value->>'actionId'
                             WHEN 'act-hover' THEN node.value || jsonb_build_object('actionId', 'hover', 'paramSlots', '[]'::jsonb)
                             WHEN 'act-robot-aim' THEN node.value || jsonb_build_object('actionId', 'gimbalRotate', 'paramSlots', '[]'::jsonb)
                             WHEN 'act-robot-shoot' THEN node.value || jsonb_build_object('actionId', 'orientedShoot', 'paramSlots', '[]'::jsonb)
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
  AND item.step_tree_json::text ~ '"actionId": "(act-hover|act-robot-aim|act-robot-shoot)"';

UPDATE ent_inspection_item_t1 item
SET step_tree_json = (
      SELECT COALESCE(jsonb_agg(
        CASE node.value->>'actionId'
          WHEN 'act-arrive' THEN node.value || jsonb_build_object(
            'actionId', 'action-065340437bcb46788b7dc261fab81c63', 'paramSlots', '[]'::jsonb)
          WHEN 'act-aim' THEN node.value || jsonb_build_object('actionId', 'gimbalRotate', 'paramSlots', '[]'::jsonb)
          WHEN 'act-shoot' THEN node.value || jsonb_build_object('actionId', 'orientedShoot', 'paramSlots', '[]'::jsonb)
          ELSE node.value
        END
        ORDER BY node.ordinal
      ), '[]'::jsonb)
      FROM jsonb_array_elements(item.step_tree_json) WITH ORDINALITY AS node(value, ordinal)
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE item.deleted = false
  AND item.tenant_id = 1
  AND jsonb_typeof(item.step_tree_json) = 'array'
  AND item.step_tree_json::text ~ '"actionId": "(act-arrive|act-aim|act-shoot)"';
