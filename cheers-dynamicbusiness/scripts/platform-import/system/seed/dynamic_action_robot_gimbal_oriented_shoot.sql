-- 定点拍照的机器人这一档：相机型号「机器人云台」。
-- 参数按机器人协议拍照指令：偏航角、俯仰角、焦距、拍摄张数、是否驻留、动作耗时。
-- 这台云台不进无人机的适用型号。无人机那一档没有自己的参数时，画面会沿用大疆相机的清单。
-- 不负责：不改大疆云台相机已有参数，不改开始拍照，不写协议对照。停靠点仍在到达那一步。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_model (
    code, name, entity_type_code, description, status, sort,
    creator, updater, tenant_id, governance_status
)
SELECT
    'robot-gimbal-cam',
    '机器人云台',
    'equipment',
    '机器人主云台。定点拍照在这一型号上填偏航角、俯仰角、焦距和拍摄张数。',
    1,
    170044,
    '',
    '',
    1,
    'COMPANY'
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamic_model
    WHERE code = 'robot-gimbal-cam'
      AND tenant_id = 1
      AND deleted = false
);

INSERT INTO dynamic_model_category_relation_t1 (
    model_id, category_id, entity_type_code, sort,
    creator, updater, tenant_id, model_code, category_code
)
SELECT
    m.id,
    2104809,
    'equipment',
    8192,
    '',
    '',
    1,
    m.code,
    'EQCAT-L3-PATROL-UAV-CAMERA'
FROM dynamic_model m
WHERE m.code = 'robot-gimbal-cam'
  AND m.tenant_id = 1
  AND m.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_model_category_relation_t1 r
      WHERE r.model_id = m.id
        AND r.category_id = 2104809
        AND r.deleted = false
  );

WITH cam AS (
    SELECT id::text AS id_text
    FROM dynamic_model
    WHERE code = 'robot-gimbal-cam'
      AND tenant_id = 1
      AND deleted = false
),
src AS (
    SELECT m.elem -> 'fields' AS fields
    FROM ent_action_t1 a
    CROSS JOIN LATERAL jsonb_array_elements(a.param_slots_json -> 'methods') AS m(elem)
    WHERE a.id = 74
      AND m.elem ->> 'equipmentModelId' = '1772'
),
rebuilt AS (
    SELECT
        a.id,
        (
            SELECT jsonb_agg(x.elem ORDER BY x.ord)
            FROM (
                SELECT
                    CASE
                        WHEN elem ->> 'executionMeans' = 'UAV'
                         AND elem ->> 'equipmentModelId' IS NULL
                        THEN jsonb_set(
                            elem,
                            '{applicableModelIds}',
                            COALESCE(
                                (
                                    SELECT jsonb_agg(item ORDER BY item.ord)
                                    FROM jsonb_array_elements(elem -> 'applicableModelIds')
                                        WITH ORDINALITY AS item(value, ord)
                                    WHERE item.value #>> '{}' <> cam.id_text
                                ),
                                '[]'::jsonb
                            )
                        )
                        ELSE elem
                    END AS elem,
                    ord
                FROM jsonb_array_elements(a.param_slots_json -> 'methods')
                    WITH ORDINALITY AS kept(elem, ord)
                CROSS JOIN cam
                WHERE COALESCE(elem ->> 'executionMeans', '') <> 'ROBOT'
                  AND NOT (
                      elem ->> 'executionMeans' = 'UAV'
                      AND elem ->> 'equipmentModelId' IS NOT NULL
                      AND elem ->> 'equipmentModelId' = cam.id_text
                  )
                UNION ALL
                SELECT jsonb_build_object(
                    'fields', '[]'::jsonb,
                    'executionMeans', 'UAV',
                    'applicableModelIds',
                    (
                        SELECT COALESCE(jsonb_agg(model_id ORDER BY model_ord), '[]'::jsonb)
                        FROM (
                            SELECT kept.elem ->> 'equipmentModelId' AS model_id, kept.ord AS model_ord
                            FROM jsonb_array_elements(a.param_slots_json -> 'methods')
                                WITH ORDINALITY AS kept(elem, ord)
                            WHERE kept.elem ->> 'executionMeans' = 'UAV'
                              AND kept.elem ->> 'equipmentModelId' IS NOT NULL
                              AND kept.elem ->> 'equipmentModelId' <> cam.id_text
                        ) models
                    ),
                    'applicableCategoryIds', jsonb_build_array(2104809),
                    'paramsPerEquipmentModel', true,
                    'paramsSharedAcrossModels', false
                ), 0::bigint
                FROM cam
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM jsonb_array_elements(a.param_slots_json -> 'methods') AS existing(elem)
                    WHERE existing.elem ->> 'executionMeans' = 'UAV'
                      AND existing.elem ->> 'equipmentModelId' IS NULL
                )
                UNION ALL
                SELECT jsonb_build_object(
                    'fields', '[]'::jsonb,
                    'executionMeans', 'ROBOT',
                    'applicableModelIds', jsonb_build_array(cam.id_text),
                    'applicableCategoryIds', jsonb_build_array(2104809),
                    'paramsPerEquipmentModel', true,
                    'paramsSharedAcrossModels', false
                ), 1000::bigint
                FROM cam
                UNION ALL
                SELECT jsonb_build_object(
                    'fields', src.fields,
                    'executionMeans', 'ROBOT',
                    'equipmentModelId', cam.id_text
                ), 1001::bigint
                FROM cam
                CROSS JOIN src
            ) x
        ) AS methods
    FROM ent_action_t1 a
    WHERE a.id = 84
      AND a.code = 'orientedShoot'
)
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(a.param_slots_json, '{methods}', rebuilt.methods),
    update_time = CURRENT_TIMESTAMP
FROM rebuilt
WHERE a.id = rebuilt.id
  AND rebuilt.methods IS NOT NULL
  AND jsonb_array_length(rebuilt.methods) > 0;

-- 检查项没填的格子读型号上的自动模式。动作耗时仍留在动作上。
UPDATE dynamic_model AS model
SET auto_param_defaults = copied.defaults,
    updater = '',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT jsonb_object_agg(field.elem ->> 'fieldCode', field.elem -> 'defaultValue')::text AS defaults
    FROM ent_action_t1 AS action
    CROSS JOIN LATERAL jsonb_array_elements(action.param_slots_json -> 'methods') AS method(elem)
    CROSS JOIN LATERAL jsonb_array_elements(COALESCE(method.elem -> 'fields', '[]'::jsonb)) AS field(elem)
    WHERE action.id = 84
      AND action.code = 'orientedShoot'
      AND method.elem ->> 'equipmentModelId' = (
          SELECT id::text
          FROM dynamic_model
          WHERE code = 'robot-gimbal-cam'
            AND tenant_id = 1
            AND deleted = false
      )
      AND field.elem ->> 'fieldCode' <> 'action_duration'
      AND field.elem -> 'defaultValue' IS NOT NULL
      AND jsonb_typeof(field.elem -> 'defaultValue') <> 'null'
      AND field.elem ->> 'defaultValue' <> ''
) AS copied
WHERE model.code = 'robot-gimbal-cam'
  AND model.tenant_id = 1
  AND model.deleted = false
  AND copied.defaults IS NOT NULL;

COMMIT;
