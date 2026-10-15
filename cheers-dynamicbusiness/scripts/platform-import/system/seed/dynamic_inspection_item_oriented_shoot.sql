-- 检查项检查方法里的拍照步骤改为定点拍照。
-- 只改 step_tree_json 里 actionId 为 takePhoto 的步骤。人工步骤不动。
-- 不负责：不改动作库，不改已经生成的任务步骤图，不改 custom_fields 里的旧动作树。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_inspection_item_t1 AS item
SET step_tree_json = jsonb_set(
        item.step_tree_json,
        '{methods}',
        (
            SELECT jsonb_agg(rewritten.method ORDER BY rewritten.ord)
            FROM (
                SELECT
                    method.ord,
                    method.value
                        || CASE
                            WHEN method.value ? 'stepTree' THEN jsonb_build_object(
                                'stepTree',
                                (
                                    SELECT COALESCE(jsonb_agg(
                                        CASE
                                            WHEN node.value ->> 'actionId' = 'takePhoto' THEN
                                                jsonb_set(
                                                    jsonb_set(node.value, '{actionId}', '"orientedShoot"'::jsonb),
                                                    '{paramSlots}',
                                                    '[]'::jsonb
                                                )
                                            ELSE node.value
                                        END
                                        ORDER BY node.ord
                                    ), '[]'::jsonb)
                                    FROM jsonb_array_elements(method.value -> 'stepTree')
                                        WITH ORDINALITY AS node(value, ord)
                                )
                            )
                            ELSE '{}'::jsonb
                        END
                        || CASE
                            WHEN method.value ? 'actionTree' THEN jsonb_build_object(
                                'actionTree',
                                (
                                    SELECT COALESCE(jsonb_agg(
                                        CASE
                                            WHEN node.value ->> 'actionId' = 'takePhoto' THEN
                                                jsonb_set(
                                                    jsonb_set(node.value, '{actionId}', '"orientedShoot"'::jsonb),
                                                    '{paramSlots}',
                                                    '[]'::jsonb
                                                )
                                            ELSE node.value
                                        END
                                        ORDER BY node.ord
                                    ), '[]'::jsonb)
                                    FROM jsonb_array_elements(method.value -> 'actionTree')
                                        WITH ORDINALITY AS node(value, ord)
                                )
                            )
                            ELSE '{}'::jsonb
                        END AS method
                FROM jsonb_array_elements(item.step_tree_json -> 'methods')
                    WITH ORDINALITY AS method(value, ord)
            ) rewritten
        )
    ),
    update_time = CURRENT_TIMESTAMP
WHERE item.deleted = false
  AND item.step_tree_json::text LIKE '%takePhoto%';

COMMIT;
