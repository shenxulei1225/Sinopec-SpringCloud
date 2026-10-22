-- 现网补丁：uav-ws 开始/结束录像对照行已存在但 param_slot_mapping_json 为空时补齐。
-- 与 system/seed/dynamic_protocol_standard_action_mapping_rest.sql 中 INSERT 的 slots 同源。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"stream_url","sourceKind":"SYSTEM","path":"request.streamUrl"},{"slot":"record_rotate","path":"request.rotate"},{"slot":"action_duration","path":"request.time"}]'::jsonb
    ),
    updater = 'patch-uav-ws-record',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'map-start-record-uav-ws'
  AND (
    custom_fields->'param_slot_mapping_json' IS NULL
    OR jsonb_typeof(custom_fields->'param_slot_mapping_json') <> 'array'
    OR jsonb_array_length(custom_fields->'param_slot_mapping_json') = 0
  );

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]'::jsonb
    ),
    updater = 'patch-uav-ws-record',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'map-stop-record-uav-ws'
  AND (
    custom_fields->'param_slot_mapping_json' IS NULL
    OR jsonb_typeof(custom_fields->'param_slot_mapping_json') <> 'array'
    OR jsonb_array_length(custom_fields->'param_slot_mapping_json') = 0
  );

COMMIT;
