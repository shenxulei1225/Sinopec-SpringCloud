-- 机器人检查步骤里的定点拍照，对照机器人协议拍照指令 200301。
-- 字段对照与「开始拍照」相同：张数、偏航角、俯仰角、焦距、是否驻留、动作耗时。
-- 不负责：不改开始拍照已有对照，不给智能分析动作编设备指令。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO ent_protocol_mapping_t1 (
    tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
    1,
    'protocol_mapping',
    m.id,
    '定点拍照 · 机器人对接协议',
    'map-oriented-shoot-robot-ws',
    1,
    jsonb_build_object(
        'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
        'mapped_protocol_version', 'robot-ws',
        'protocol_mapping_status', 'CONFIGURED',
        'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
        'param_slot_mapping_json', '[
            {"slot":"shot_count","path":"request.number"},
            {"slot":"yaw","path":"request.angleLevel"},
            {"slot":"pitch","path":"request.angleVertical"},
            {"slot":"focal_length","path":"request.focuses"},
            {"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},
            {"slot":"action_duration","path":"request.time"}
        ]'::jsonb
    ),
    '',
    false
FROM dynamic_model m
JOIN ent_action_t1 a
  ON a.deleted = false AND a.tenant_id = 1 AND a.code = 'orientedShoot'
JOIN ent_data_protocol_t1 p
  ON p.deleted = false AND p.tenant_id = 1 AND p.code = 'proto-robot-200301-photo'
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code = 'protocol_mapping_standard'
  AND NOT EXISTS (
      SELECT 1
      FROM ent_protocol_mapping_t1 e
      WHERE e.deleted = false
        AND e.tenant_id = 1
        AND e.code = 'map-oriented-shoot-robot-ws'
  );

COMMIT;
