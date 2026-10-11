-- 大疆任务级录/停/拍：补 payload_index 对照；动作库声明 payload_position_index；
-- 对照与动作 param 来源分组 USER → ACTION_PARAM（动作参数，非登录用户）。
SET search_path TO dynamicbusiness;
BEGIN;

-- 1) startRecord / stopRecord / takePhoto（UAV）声明负载索引槽（冻结时由执行设备挂载写入）
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            elem,
            '{fields}',
            COALESCE(elem->'fields', '[]'::jsonb) || (
              SELECT COALESCE(jsonb_agg(add.field), '[]'::jsonb)
              FROM (VALUES
                ('{"fieldCode":"payload_position_index","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE elem->>'executionMeans' = 'UAV'
                AND NOT EXISTS (
                  SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                  WHERE have->>'fieldCode' = 'payload_position_index'
                )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'patch_dji_payload',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN ('startRecord', 'stopRecord', 'takePhoto');

-- 2) 任务级大疆录/停/拍：空对照 → 负载索引来自 EXECUTOR_MOUNT
UPDATE ent_protocol_mapping_t1 m
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'::jsonb
    ),
    updater = 'patch_dji_payload',
    update_time = CURRENT_TIMESTAMP
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code IN (
    'map-start-record-dji-dock',
    'map-start-record-dji-pilot',
    'map-stop-record-dji-dock',
    'map-stop-record-dji-pilot',
    'map-takephoto-dji-dock',
    'map-takePhoto-dji-pilot'
  );

-- 3) 直播录/停：selected_camera 实为挂载负载编号 → EXECUTOR_MOUNT
UPDATE ent_protocol_mapping_t1 m
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'slot' = 'selected_camera' AND elem->>'path' = 'data.payload_index'
            THEN jsonb_set(elem, '{sourceKind}', '"EXECUTOR_MOUNT"')
            ELSE elem
          END
        )
        FROM jsonb_array_elements(custom_fields->'param_slot_mapping_json') elem
      )
    ),
    updater = 'patch_dji_payload',
    update_time = CURRENT_TIMESTAMP
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code IN (
    'map-live-recordStart-dji-dock',
    'map-live-recordStart-dji-pilot',
    'map-live-recordStop-dji-dock',
    'map-live-recordStop-dji-pilot'
  )
  AND custom_fields->'param_slot_mapping_json' IS NOT NULL;

-- 4) 对照 JSON：USER → ACTION_PARAM
UPDATE ent_protocol_mapping_t1 m
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN elem->>'sourceKind' = 'USER'
            THEN jsonb_set(elem, '{sourceKind}', '"ACTION_PARAM"')
            ELSE elem
          END
        ), '[]'::jsonb)
        FROM jsonb_array_elements(custom_fields->'param_slot_mapping_json') elem
      )
    ),
    updater = 'patch_dji_payload',
    update_time = CURRENT_TIMESTAMP
WHERE m.deleted = false AND m.tenant_id = 1
  AND custom_fields->'param_slot_mapping_json' @> '[{"sourceKind":"USER"}]'::jsonb;

-- 5) 动作 param_slots methods.fields valueSource USER → ACTION_PARAM
UPDATE ent_action_t1 a
SET param_slots_json = (
      SELECT jsonb_set(
        a.param_slots_json,
        '{methods}',
        (
          SELECT jsonb_agg(
            jsonb_set(
              meth,
              '{fields}',
              (
                SELECT COALESCE(jsonb_agg(
                  CASE
                    WHEN fld->>'valueSource' = 'USER'
                    THEN jsonb_set(fld, '{valueSource}', '"ACTION_PARAM"')
                    ELSE fld
                  END
                ), '[]'::jsonb)
                FROM jsonb_array_elements(COALESCE(meth->'fields', '[]'::jsonb)) fld
              )
            )
          )
          FROM jsonb_array_elements(a.param_slots_json->'methods') meth
        )
      )
    ),
    updater = 'patch_dji_payload',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND param_slots_json::text LIKE '%"valueSource":"USER"%';

COMMIT;
