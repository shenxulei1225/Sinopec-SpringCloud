-- 指令报文 id 的显示名，以及东古大型无人机不该带上的机器人协议。
-- 负责：lineId 这一格的名称写成「指令报文id」。东古大型无人机只保留无人机对接协议。
-- 不负责：改报文键名 lineId。不负责给机场或 Pilot 编序列号。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE ent_data_protocol_t1
SET custom_fields = replace(custom_fields::text, '"label": "线路id"', '"label": "指令报文id"')::jsonb,
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND custom_fields::text LIKE '%"label": "线路id"%';

UPDATE ent_data_protocol_t1
SET field_description_json = replace(field_description_json::text, '"label": "线路id"', '"label": "指令报文id"')::jsonb,
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND field_description_json::text LIKE '%"label": "线路id"%';

UPDATE ent_equipment_t1
SET custom_fields = jsonb_set(custom_fields, '{protocol_versions}', '["uav-ws"]'::jsonb, true),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'de6e497903c847d0b2a6803aeaa1136c'
  AND custom_fields->>'platform_protocol' = 'uav-ws'
  AND custom_fields->'protocol_versions' ? 'robot-ws';

UPDATE ent_task_excution_record_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{process_entries}',
      COALESCE((
        SELECT jsonb_agg(entry)
        FROM jsonb_array_elements(COALESCE(custom_fields->'process_entries', '[]'::jsonb)) entry
        WHERE NOT (
          entry->>'direction' = 'inbound'
          AND entry->>'messageKind' = 'flighttask_progress'
          AND entry->>'payloadJson' = '{"method":"flighttask_progress","data":{"flight_id":"923079"}}'
        )
      ), '[]'::jsonb),
      true)
WHERE id = 923079
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(COALESCE(custom_fields->'process_entries', '[]'::jsonb)) entry
    WHERE entry->>'payloadJson' = '{"method":"flighttask_progress","data":{"flight_id":"923079"}}'
  );

UPDATE ent_task_excution_record_t1
SET custom_fields = custom_fields - 'replay_pose'
WHERE id = 923079
  AND jsonb_typeof(custom_fields->'replay_pose') = 'array'
  AND jsonb_array_length(custom_fields->'replay_pose') = 1
  AND custom_fields->'replay_pose'->0->>'x' = '121.5'
  AND custom_fields->'replay_pose'->0->>'y' = '31.2'
  AND custom_fields->'replay_pose'->0->>'frameRef' = 'wgs84';

COMMIT;
