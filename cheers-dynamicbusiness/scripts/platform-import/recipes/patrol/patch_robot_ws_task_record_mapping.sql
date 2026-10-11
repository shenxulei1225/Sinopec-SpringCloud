-- 现网补丁：robot-ws 任务级 startRecord/stopRecord（与 system/seed/dynamic_protocol_standard_action_mapping_rest.sql 同源）。
SET search_path TO dynamicbusiness;
BEGIN;

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
  1, 'protocol_mapping', model.id,
  v.action_name || ' · ' || v.version_name,
  v.map_code, 1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.version_code,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', v.slots::jsonb
  ),
  'patch-robot-ws-record', false
FROM (VALUES
  ('map-start-record-robot-ws', 'startRecord', '开始录像', 'robot-ws', '机器人对接协议', 'proto-uav-200201-record-on', '[{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"stream_url","path":"request.rstp"},{"slot":"record_rotate","path":"request.rotate"},{"slot":"action_duration","path":"request.time"}]'),
  ('map-stop-record-robot-ws', 'stopRecord', '结束录像', 'robot-ws', '机器人对接协议', 'proto-uav-200202-record-off', '[{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]')
) AS v(map_code, action_code, action_name, version_code, version_name, instruction_code, slots)
JOIN dynamic_model model ON model.deleted = false AND model.tenant_id = 1 AND model.code = 'protocol_mapping_standard'
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1 AND p.code = v.instruction_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.map_code
);

COMMIT;
