-- 大疆 Pilot 1.16.1：补与机场对称的 flyto、框选变焦协议指令及预置对照。
-- 机场已有 proto-dji-dock-fly-to-point / proto-dji-dock-camera-frame-zoom，Pilot 侧曾漏建，对接页搜不到。
-- 不负责：改 Flyway；已有机库请本脚本或 platform-import 重跑相关 seed。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, command_direction, creator, deleted
)
SELECT
  1,
  'data_protocol',
  m.id,
  src.name,
  v.pilot_code,
  1,
  jsonb_set(src.custom_fields, '{protocol_version}', '["dji-pilot-1.16.1"]'::jsonb, true)
    || jsonb_build_object(
      'communication_protocol', 'mqtt',
      'protocol_channel', 'services',
      'mqtt_topic', 'thing/product/{gateway_sn}/services'
    ),
  'downlink',
  'seed',
  false
FROM (VALUES
  ('proto-dji-dock-fly-to-point', 'proto-dji-pilot-fly-to-point'),
  ('proto-dji-dock-camera-frame-zoom', 'proto-dji-pilot-camera-frame-zoom')
) AS v(dock_code, pilot_code)
JOIN ent_data_protocol_t1 src
  ON src.deleted = false AND src.tenant_id = 1 AND src.code = v.dock_code
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'data_protocol_dji_pilot'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_data_protocol_t1 e
  WHERE e.tenant_id = 1 AND e.deleted = false AND e.code = v.pilot_code
);

UPDATE ent_data_protocol_t1 p
SET
  name = src.name,
  model_id = m.id,
  command_direction = 'downlink',
  status = 1,
  deleted = false,
  custom_fields = jsonb_set(src.custom_fields, '{protocol_version}', '["dji-pilot-1.16.1"]'::jsonb, true)
    || jsonb_build_object(
      'communication_protocol', 'mqtt',
      'protocol_channel', 'services',
      'mqtt_topic', 'thing/product/{gateway_sn}/services'
    ),
  command_json = jsonb_set(src.custom_fields, '{protocol_version}', '["dji-pilot-1.16.1"]'::jsonb, true)
    -> 'command_json',
  field_description_json = jsonb_set(src.custom_fields, '{protocol_version}', '["dji-pilot-1.16.1"]'::jsonb, true)
    -> 'field_description_json',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('proto-dji-dock-fly-to-point', 'proto-dji-pilot-fly-to-point'),
  ('proto-dji-dock-camera-frame-zoom', 'proto-dji-pilot-camera-frame-zoom')
) AS v(dock_code, pilot_code)
JOIN ent_data_protocol_t1 src ON src.deleted = false AND src.tenant_id = 1 AND src.code = v.dock_code
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'data_protocol_dji_pilot'
WHERE p.tenant_id = 1 AND p.deleted = false AND p.code = v.pilot_code;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, cat.id, 'data_protocol', e.id, NULL, 0, 'seed', false
FROM ent_data_protocol_t1 e
JOIN dynamic_category cat ON cat.deleted = false AND cat.tenant_id = 1 AND cat.code = 'cat-protocol-dji-uav'
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN ('proto-dji-pilot-fly-to-point', 'proto-dji-pilot-camera-frame-zoom')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'data_protocol'
      AND r.entity_id = e.id AND r.category_id = cat.id
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, v.field_code, v.value_string, v.value_number, 'seed', 1, false
FROM ent_data_protocol_t1 e
JOIN (VALUES
  ('opcode', NULL::text, 0::bigint),
  ('exchange_mode', 'sync_wait_result', NULL::bigint),
  ('protocol_version', 'dji-pilot-1.16.1', NULL::bigint),
  ('communication_protocol', 'mqtt', NULL::bigint),
  ('protocol_channel', 'services', NULL::bigint),
  ('mqtt_topic', 'thing/product/{gateway_sn}/services', NULL::bigint)
) AS v(field_code, value_string, value_number) ON true
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN ('proto-dji-pilot-fly-to-point', 'proto-dji-pilot-camera-frame-zoom')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = v.field_code
  );

UPDATE ent_data_protocol_t1 p
SET custom_fields = jsonb_set(p.custom_fields, '{command_json,outbound,data}', v.data, true)
FROM (VALUES
  ('fly_to_point', '{"fly_to_id":"","max_speed":0,"points":[{"latitude":0,"longitude":0,"height":0}]}'::jsonb),
  ('camera_frame_zoom', '{"payload_index":"","camera_type":"","locked":false,"x":0,"y":0,"width":0,"height":0}'::jsonb)
) AS v(method, data)
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.code IN ('proto-dji-pilot-fly-to-point', 'proto-dji-pilot-camera-frame-zoom')
  AND p.custom_fields->'command_json'->'outbound'->>'method' = v.method;

UPDATE ent_data_protocol_t1 p
SET custom_fields = jsonb_set(
      p.custom_fields,
      '{field_description_json,outbound}',
      COALESCE(p.custom_fields->'field_description_json'->'outbound', '[]'::jsonb) || v.fields,
      true
    )
FROM (VALUES
  (
    'fly_to_point',
    '[{"code":"fly_to_id","path":"data.fly_to_id","type":"string","label":"飞向目标点编号","required":true,"description":"这一次飞向目标点的编号"},{"code":"max_speed","path":"data.max_speed","type":"integer","label":"最大速度","required":true,"description":"米每秒"},{"code":"latitude","path":"data.points.0.latitude","type":"number","label":"目标点纬度","required":true,"description":"从动作的位置取纬度"},{"code":"longitude","path":"data.points.0.longitude","type":"number","label":"目标点经度","required":true,"description":"从动作的位置取经度"},{"code":"height","path":"data.points.0.height","type":"number","label":"目标点高度","required":true,"description":"从动作的位置取高程"}]'::jsonb
  ),
  (
    'camera_frame_zoom',
    '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"红外、广角或变焦。"},{"code":"locked","path":"data.locked","type":"boolean","label":"机头锁定","required":true,"description":"锁定时云台和机身一起转。"},{"code":"x","path":"data.x","type":"number","label":"框选左上角横坐标","required":true,"description":"镜头左上角为原点。"},{"code":"y","path":"data.y","type":"number","label":"框选左上角纵坐标","required":true,"description":"镜头左上角为原点。"},{"code":"width","path":"data.width","type":"number","label":"框选宽度","required":true,"description":"相对整幅画面。"},{"code":"height","path":"data.height","type":"number","label":"框选高度","required":true,"description":"相对整幅画面。"}]'::jsonb
  )
) AS v(method, fields)
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.code IN ('proto-dji-pilot-fly-to-point', 'proto-dji-pilot-camera-frame-zoom')
  AND p.custom_fields->'command_json'->'outbound'->>'method' = v.method
  AND NOT EXISTS (
    SELECT 1 FROM jsonb_array_elements(COALESCE(p.custom_fields->'field_description_json'->'outbound', '[]'::jsonb)) item
    WHERE item->>'path' = (v.fields->0->>'path')
  );

UPDATE ent_data_protocol_t1 p
SET
  command_json = p.custom_fields->'command_json',
  field_description_json = p.custom_fields->'field_description_json'
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.code IN ('proto-dji-pilot-fly-to-point', 'proto-dji-pilot-camera-frame-zoom');

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
  'seed', false
FROM (VALUES
  (
    'map-arrive-dji-pilot',
    'action-065340437bcb46788b7dc261fab81c63',
    '飞向目标点',
    'dji-pilot-1.16.1',
    '大疆 Pilot',
    'proto-dji-pilot-fly-to-point',
    '[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.latitude","path":"data.points.0.latitude"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.longitude","path":"data.points.0.longitude"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.height","path":"data.points.0.height"},{"slot":"max_speed","path":"data.max_speed"},{"slot":"fly_to_id","path":"data.fly_to_id"}]'
  ),
  (
    'map-frame-zoom-dji-pilot',
    'frameZoom',
    '框选变焦',
    'dji-pilot-1.16.1',
    '大疆 Pilot',
    'proto-dji-pilot-camera-frame-zoom',
    '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"locked","sourceKind":"INTERACTION","path":"data.locked"},{"slot":"frame_x","sourceKind":"INTERACTION","path":"data.x"},{"slot":"frame_y","sourceKind":"INTERACTION","path":"data.y"},{"slot":"frame_width","sourceKind":"INTERACTION","path":"data.width"},{"slot":"frame_height","sourceKind":"INTERACTION","path":"data.height"}]'
  )
) AS v(map_code, action_code, action_name, version_code, version_name, instruction_code, slots)
JOIN dynamic_model model ON model.deleted = false AND model.tenant_id = 1 AND model.code = 'protocol_mapping_standard'
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1 AND p.code = v.instruction_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.tenant_id = 1 AND e.code = v.map_code AND e.deleted = false
);

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'protocol_mapping', e.id, NULL, 0, 'seed', false
FROM ent_protocol_mapping_t1 e
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'protocol_mapping_root'
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN ('map-arrive-dji-pilot', 'map-frame-zoom-dji-pilot')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
      AND r.entity_id = e.id AND r.category_id = c.id AND r.deleted = false
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'action_ref', NULL, (e.custom_fields->'action_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN ('map-arrive-dji-pilot', 'map-frame-zoom-dji-pilot')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN ('map-arrive-dji-pilot', 'map-frame-zoom-dji-pilot')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN ('map-arrive-dji-pilot', 'map-frame-zoom-dji-pilot')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
  );

COMMIT;
