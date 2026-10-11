-- 检查步骤不另存动作名称。删掉步骤上已经写下的名称。
-- 页面和生成任务都按动作库里这个动作现在的名称来显示。
-- 不负责：任务执行记录上已经生成的步骤标题。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_inspection_item_t1 item
SET step_tree_json = jsonb_set(
      item.step_tree_json,
      '{methods}'::text[],
      (
        SELECT COALESCE(jsonb_agg(rewritten.method ORDER BY rewritten.ordinal), '[]'::jsonb)
        FROM (
          SELECT method.ordinal,
                 CASE
                   WHEN jsonb_typeof(COALESCE(method.value->'stepTree', method.value->'actionTree')) = 'array'
                     THEN jsonb_set(
                       method.value,
                       CASE
                         WHEN method.value ? 'stepTree' THEN '{stepTree}'::text[]
                         ELSE '{actionTree}'::text[]
                       END,
                       (
                         SELECT COALESCE(jsonb_agg((node.value - 'stepTitle') ORDER BY node.ordinal), '[]'::jsonb)
                         FROM jsonb_array_elements(COALESCE(method.value->'stepTree', method.value->'actionTree'))
                           WITH ORDINALITY AS node(value, ordinal)
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
  AND jsonb_typeof(item.step_tree_json->'methods') = 'array'
  AND item.step_tree_json::text LIKE '%stepTitle%';
