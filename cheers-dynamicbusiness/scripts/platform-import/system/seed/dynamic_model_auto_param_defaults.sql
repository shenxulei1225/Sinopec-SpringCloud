SET search_path TO dynamicbusiness, public;

-- 把动作里按型号写下的用户参数默认值，抄到设备型号的自动模式上。
-- 检查项没填的格子读型号。动作耗时仍留在动作上，不抄。
-- 动作上的原默认值先留着，任务生成还从动作读。已经写过自动模式的型号不覆盖。

UPDATE dynamic_model AS model
SET auto_param_defaults = copied.defaults,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT model_id, jsonb_object_agg(field_code, default_value)::text AS defaults
    FROM (
        SELECT DISTINCT ON (model_id, field_code)
            model_id,
            field_code,
            default_value
        FROM (
            SELECT (method.elem ->> 'equipmentModelId')::bigint AS model_id,
                   field.elem ->> 'fieldCode' AS field_code,
                   field.elem -> 'defaultValue' AS default_value
            FROM ent_action_t1 AS action
            CROSS JOIN LATERAL jsonb_array_elements(action.param_slots_json -> 'methods') AS method(elem)
            CROSS JOIN LATERAL jsonb_array_elements(COALESCE(method.elem -> 'fields', '[]'::jsonb)) AS field(elem)
            WHERE action.deleted = false
              AND COALESCE(method.elem ->> 'equipmentModelId', '') ~ '^[0-9]+$'
              AND COALESCE(field.elem ->> 'valueSource', 'ACTION_PARAM') IN ('USER', 'ACTION_PARAM')
              AND COALESCE(field.elem ->> 'fieldCode', '') <> ''
              AND field.elem ->> 'fieldCode' <> 'action_duration'
              AND field.elem -> 'defaultValue' IS NOT NULL
              AND jsonb_typeof(field.elem -> 'defaultValue') <> 'null'
              AND field.elem ->> 'defaultValue' <> ''
        ) AS raw
        ORDER BY model_id, field_code, default_value::text
    ) AS picked
    GROUP BY model_id
) AS copied
WHERE model.id = copied.model_id
  AND model.deleted = false
  AND COALESCE(model.auto_param_defaults, '') = '';
