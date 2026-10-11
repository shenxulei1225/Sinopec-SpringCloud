-- 实时画面补上的负载操作：拍摄模式、快门、录像、云台重置、分屏、曝光、存储、红外测温。
-- 负责：这些操作收成界面操作，并对照到机场和 Pilot 的上云指令。变焦、点对焦的对照改成画面上实际带的字段。
-- 不负责：把这些写进航线文件；下发时回读设备或猜一个编号、倍数、对焦值。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

INSERT INTO dynamic_field (code, name, type, description, source, status, tenant_id, creator)
VALUES
  ('reset_mode', '云台重置', 'NUMBER', '0 回中，1 向下，2 偏航回中，3 俯仰向下。', 'LIBRARY', 1, 1, 'seed'),
  ('split_enabled', '分屏', 'STRING', '打开或关闭红外与可见光分屏。', 'LIBRARY', 1, 1, 'seed'),
  ('exposure_mode', '曝光模式', 'NUMBER', '1 自动，2 快门优先，3 光圈优先，4 手动。', 'LIBRARY', 1, 1, 'seed'),
  ('exposure_value', '曝光补偿', 'NUMBER', '相机给出的曝光档。1 到 31 对应 -5.0EV 到 +5.0EV，255 为固定。', 'LIBRARY', 1, 1, 'seed'),
  ('photo_storage_settings', '照片存储镜头', 'STRING', 'current、wide、zoom、ir，可多选。', 'LIBRARY', 1, 1, 'seed'),
  ('video_storage_settings', '视频存储镜头', 'STRING', 'current、wide、zoom、ir，可多选。', 'LIBRARY', 1, 1, 'seed'),
  ('meter_mode', '红外测温模式', 'NUMBER', '0 关闭，1 点测温，2 区域测温。', 'LIBRARY', 1, 1, 'seed'),
  ('meter_x', '测温横坐标', 'NUMBER', '红外画面上测温点或区域左上角的水平位置。', 'LIBRARY', 1, 1, 'seed'),
  ('meter_y', '测温纵坐标', 'NUMBER', '红外画面上测温点或区域左上角的竖直位置。', 'LIBRARY', 1, 1, 'seed'),
  ('meter_width', '测温区域宽度', 'NUMBER', '红外测温区域宽度，相对整幅画面。', 'LIBRARY', 1, 1, 'seed'),
  ('meter_height', '测温区域高度', 'NUMBER', '红外测温区域高度，相对整幅画面。', 'LIBRARY', 1, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET name = EXCLUDED.name, description = EXCLUDED.description, status = 1, deleted = false,
  updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite, creator, deleted
)
SELECT 1, 'action', m.id, v.name, v.code, 1, jsonb_build_array('UAV'),
  jsonb_build_object('version', 2, 'methods', jsonb_build_array(v.method)),
  '[]'::jsonb, false, 'seed', false
FROM (VALUES
  ('cameraMode', '切换拍摄模式', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_mode","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('photoTake', '拍一张', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null}]}'::jsonb),
  ('photoStop', '停止拍照', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null}]}'::jsonb),
  ('recordStart', '开始录像', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null}]}'::jsonb),
  ('recordStop', '停止录像', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null}]}'::jsonb),
  ('gimbalReset', '重置云台', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"reset_mode","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('screenSplit', '分屏', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"split_enabled","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('exposureMode', '曝光模式', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"exposure_mode","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('exposureSet', '曝光补偿', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"camera_type","required":true,"valueSource":"ACTION_PARAM","defaultValue":null},{"fieldCode":"exposure_value","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('photoStorage', '照片存储', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"photo_storage_settings","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('videoStorage', '视频存储', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"video_storage_settings","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('irMeteringMode', '红外测温模式', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"meter_mode","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('irMeteringPoint', '红外测温点', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"meter_x","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"meter_y","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb),
  ('irMeteringArea', '红外测温区域', '{"executionMeans":"UAV","fields":[{"fieldCode":"selected_camera","required":true,"valueSource":"EXECUTOR_MOUNT","defaultValue":null},{"fieldCode":"meter_x","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"meter_y","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"meter_width","required":true,"valueSource":"INTERACTION","defaultValue":null},{"fieldCode":"meter_height","required":true,"valueSource":"INTERACTION","defaultValue":null}]}'::jsonb)
) AS v(code, name, method)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'action'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t1 a WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = v.code
);

INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, command_direction, creator, deleted
)
SELECT 1, 'data_protocol', model.id, spec.action_name,
  'proto-' || version.book_code || '-live-' || spec.action_code,
  1,
  jsonb_build_object(
    'opcode', 0,
    'command_direction', 'downlink',
    'protocol_version', jsonb_build_array(version.version_code),
    'communication_protocol', 'mqtt',
    'command_json', jsonb_build_object(
      'outbound', jsonb_build_object('method', spec.method_name, 'data', spec.data)
    ),
    'field_description_json', jsonb_build_object('outbound', spec.fields)
  ),
  'downlink', 'seed', false
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', 'data_protocol_dji_dock'),
  ('dji-pilot', 'dji-pilot-1.16.1', 'data_protocol_dji_pilot')
) AS version(book_code, version_code, model_code)
JOIN dynamic_model model ON model.deleted = false AND model.tenant_id = 1 AND model.code = version.model_code
CROSS JOIN (VALUES
  ('cameraMode', '切换拍摄模式', 'camera_mode_switch', '{"payload_index":"","camera_mode":0}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_mode","path":"data.camera_mode","type":"integer","label":"拍摄模式","required":true,"description":"0 拍照，1 录像，2 智能低光，3 全景拍照。"}]'::jsonb),
  ('photoTake', '拍一张', 'camera_photo_take', '{"payload_index":""}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"}]'::jsonb),
  ('photoStop', '停止拍照', 'camera_photo_stop', '{"payload_index":""}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"}]'::jsonb),
  ('recordStart', '开始录像', 'camera_recording_start', '{"payload_index":""}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"}]'::jsonb),
  ('recordStop', '停止录像', 'camera_recording_stop', '{"payload_index":""}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"}]'::jsonb),
  ('gimbalReset', '重置云台', 'gimbal_reset', '{"payload_index":"","reset_mode":0}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"reset_mode","path":"data.reset_mode","type":"integer","label":"云台重置","required":true,"description":"0 回中，1 向下，2 偏航回中，3 俯仰向下。"}]'::jsonb),
  ('screenSplit', '分屏', 'camera_screen_split', '{"payload_index":"","enable":false}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"enable","path":"data.enable","type":"boolean","label":"分屏","required":true,"description":"打开或关闭分屏。"}]'::jsonb),
  ('exposureMode', '曝光模式', 'camera_exposure_mode_set', '{"payload_index":"","camera_type":"","exposure_mode":1}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"exposure_mode","path":"data.exposure_mode","type":"integer","label":"曝光模式","required":true,"description":"1 自动，2 快门优先，3 光圈优先，4 手动。"}]'::jsonb),
  ('exposureSet', '曝光补偿', 'camera_exposure_set', '{"payload_index":"","camera_type":"","exposure_value":16}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦。"},{"code":"exposure_value","path":"data.exposure_value","type":"integer","label":"曝光补偿","required":true,"description":"1 到 31，或 255 固定。"}]'::jsonb),
  ('photoStorage', '照片存储', 'photo_storage_set', '{"payload_index":"","photo_storage_settings":[]}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"photo_storage_settings","path":"data.photo_storage_settings","type":"array","label":"照片存储镜头","required":true,"description":"current、wide、zoom、ir。"}]'::jsonb),
  ('videoStorage', '视频存储', 'video_storage_set', '{"payload_index":"","video_storage_settings":[]}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"video_storage_settings","path":"data.video_storage_settings","type":"array","label":"视频存储镜头","required":true,"description":"current、wide、zoom、ir。"}]'::jsonb),
  ('irMeteringMode', '红外测温模式', 'ir_metering_mode_set', '{"payload_index":"","mode":0}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"mode","path":"data.mode","type":"integer","label":"测温模式","required":true,"description":"0 关闭，1 点测温，2 区域测温。"}]'::jsonb),
  ('irMeteringPoint', '红外测温点', 'ir_metering_point_set', '{"payload_index":"","x":0,"y":0}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"x","path":"data.x","type":"number","label":"测温横坐标","required":true,"description":"镜头左上角为原点。"},{"code":"y","path":"data.y","type":"number","label":"测温纵坐标","required":true,"description":"镜头左上角为原点。"}]'::jsonb),
  ('irMeteringArea', '红外测温区域', 'ir_metering_area_set', '{"payload_index":"","x":0,"y":0,"width":0,"height":0}'::jsonb, '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。"},{"code":"x","path":"data.x","type":"number","label":"测温横坐标","required":true,"description":"区域左上角。"},{"code":"y","path":"data.y","type":"number","label":"测温纵坐标","required":true,"description":"区域左上角。"},{"code":"width","path":"data.width","type":"number","label":"测温区域宽度","required":true,"description":"相对整幅画面。"},{"code":"height","path":"data.height","type":"number","label":"测温区域高度","required":true,"description":"相对整幅画面。"}]'::jsonb)
) AS spec(action_code, action_name, method_name, data, fields)
WHERE NOT EXISTS (
  SELECT 1 FROM ent_data_protocol_t1 e
  WHERE e.tenant_id = 1 AND e.deleted = false
    AND e.code = 'proto-' || version.book_code || '-live-' || spec.action_code
);

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT 1, 'protocol_mapping', model.id, spec.action_name || ' · ' || version.version_name,
  'map-live-' || spec.action_code || '-' || version.book_code, 1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', version.version_code,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', spec.slots
  ),
  'seed', false
FROM (VALUES
  ('dji-dock', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('dji-pilot', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1')
) AS version(book_code, version_code, version_name)
JOIN dynamic_model model ON model.deleted = false AND model.tenant_id = 1 AND model.code = 'protocol_mapping_standard'
CROSS JOIN (VALUES
  ('cameraMode', '切换拍摄模式', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_mode","sourceKind":"INTERACTION","path":"data.camera_mode"}]'::jsonb),
  ('photoTake', '拍一张', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'::jsonb),
  ('photoStop', '停止拍照', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'::jsonb),
  ('recordStart', '开始录像', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'::jsonb),
  ('recordStop', '停止录像', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'::jsonb),
  ('gimbalReset', '重置云台', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"reset_mode","sourceKind":"INTERACTION","path":"data.reset_mode"}]'::jsonb),
  ('screenSplit', '分屏', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"split_enabled","sourceKind":"INTERACTION","path":"data.enable"}]'::jsonb),
  ('exposureMode', '曝光模式', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"exposure_mode","sourceKind":"INTERACTION","path":"data.exposure_mode"}]'::jsonb),
  ('exposureSet', '曝光补偿', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"exposure_value","sourceKind":"INTERACTION","path":"data.exposure_value"}]'::jsonb),
  ('photoStorage', '照片存储', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"photo_storage_settings","sourceKind":"INTERACTION","path":"data.photo_storage_settings"}]'::jsonb),
  ('videoStorage', '视频存储', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"video_storage_settings","sourceKind":"INTERACTION","path":"data.video_storage_settings"}]'::jsonb),
  ('irMeteringMode', '红外测温模式', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"meter_mode","sourceKind":"INTERACTION","path":"data.mode"}]'::jsonb),
  ('irMeteringPoint', '红外测温点', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"meter_x","sourceKind":"INTERACTION","path":"data.x"},{"slot":"meter_y","sourceKind":"INTERACTION","path":"data.y"}]'::jsonb),
  ('irMeteringArea', '红外测温区域', '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"meter_x","sourceKind":"INTERACTION","path":"data.x"},{"slot":"meter_y","sourceKind":"INTERACTION","path":"data.y"},{"slot":"meter_width","sourceKind":"INTERACTION","path":"data.width"},{"slot":"meter_height","sourceKind":"INTERACTION","path":"data.height"}]'::jsonb)
) AS spec(action_code, action_name, slots)
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = spec.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1
  AND p.code = 'proto-' || version.book_code || '-live-' || spec.action_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.tenant_id = 1 AND e.deleted = false
    AND e.code = 'map-live-' || spec.action_code || '-' || version.book_code
);

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(custom_fields, '{param_slot_mapping_json}',
      '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"zoom_factor","sourceKind":"INTERACTION","path":"data.zoom_factor"}]'::jsonb),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code IN ('map-zoom-dji-dock', 'map-zoom-dji-pilot');

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(custom_fields, '{param_slot_mapping_json}',
      '[{"slot":"selected_camera","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"point_focus_camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"point_focus_x","sourceKind":"INTERACTION","path":"data.x"},{"slot":"point_focus_y","sourceKind":"INTERACTION","path":"data.y"}]'::jsonb),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code IN ('map-focus-dji-dock', 'map-focus-dji-pilot');

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, r.category_id, 'action', a.id, r.domain, r.sort, 'seed', false
FROM ent_action_t1 a
JOIN ent_action_t1 zoom
  ON zoom.deleted = false AND zoom.tenant_id = 1 AND zoom.code = 'zoom'
JOIN dynamic_entity_category_relation_t1 r
  ON r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'action' AND r.entity_id = zoom.id
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN (
    'cameraMode', 'photoTake', 'photoStop', 'recordStart', 'recordStop',
    'gimbalReset', 'screenSplit', 'exposureMode', 'exposureSet',
    'photoStorage', 'videoStorage', 'irMeteringMode', 'irMeteringPoint', 'irMeteringArea'
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 x
    WHERE x.deleted = false AND x.tenant_id = 1 AND x.entity_id = a.id AND x.category_id = r.category_id
  );

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'protocol_mapping', e.id, NULL, 0, 'seed', false
FROM ent_protocol_mapping_t1 e
JOIN dynamic_category c ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'protocol_mapping_root'
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-live-%'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_category_relation_t1 r
    WHERE r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
      AND r.entity_id = e.id AND r.category_id = c.id AND r.deleted = false
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'action_ref', NULL, (e.custom_fields->'action_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-live-%'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-live-%'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
  );

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-live-%'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
  );

COMMIT;
