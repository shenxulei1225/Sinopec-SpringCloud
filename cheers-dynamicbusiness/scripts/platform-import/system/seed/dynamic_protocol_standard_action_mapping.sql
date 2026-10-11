-- 动作是一套标准。协议只提供自己的指令。
-- 撤掉按大疆协议复制的动作。标准动作按自己的参数，对上大疆里同一条能力的指令。
SET search_path TO dynamicbusiness;
BEGIN;

UPDATE dynamic_entity_field_index_t1 idx
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
JOIN ent_action_t1 a ON a.id::text = (m.custom_fields->'action_ref'->>'id')
WHERE a.tenant_id = 1 AND a.code LIKE 'act-dji-%'
  AND idx.tenant_id = 1 AND idx.entity_id = m.id AND idx.deleted = false;

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
JOIN ent_action_t1 a ON a.id::text = (m.custom_fields->'action_ref'->>'id')
WHERE a.tenant_id = 1 AND a.code LIKE 'act-dji-%'
  AND r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
  AND r.entity_id = m.id AND r.deleted = false;

UPDATE ent_protocol_mapping_t1 m
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_action_t1 a
WHERE a.tenant_id = 1 AND a.code LIKE 'act-dji-%'
  AND (m.custom_fields->'action_ref'->>'id') = a.id::text
  AND m.deleted = false;

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_action_t1 a
WHERE a.tenant_id = 1 AND a.code LIKE 'act-dji-%'
  AND r.tenant_id = 1 AND r.entity_type_code = 'action'
  AND r.entity_id = a.id AND r.deleted = false;

UPDATE ent_action_t1
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND code LIKE 'act-dji-%' AND deleted = false;

INSERT INTO dynamic_field (code, name, type, description, source, status, tenant_id, creator)
VALUES
  ('camera_type', '相机类型', 'STRING', '大疆相机类型：广角、变焦、红外。标准动作上的参数，哪份协议用到就在对照里填写。', 'LIBRARY', 1, 1, 'seed'),
  ('security_takeoff_height', '安全起飞高度', 'NUMBER', '一键起飞先升高到的相对高度，单位米。', 'LIBRARY', 1, 1, 'seed'),
  ('rth_mode', '返航高度模式', 'INTEGER', '0 智能高度，1 设定高度。', 'LIBRARY', 1, 1, 'seed'),
  ('rth_altitude', '返航高度', 'INTEGER', '相对起飞点的返航高度，单位米。', 'LIBRARY', 1, 1, 'seed'),
  ('rc_lost_action', '失控动作', 'INTEGER', '0 悬停，1 降落，2 返航。', 'LIBRARY', 1, 1, 'seed'),
  ('commander_mode_lost_action', '指点飞行失控动作', 'INTEGER', '0 继续指点飞行，1 退出并执行普通失控动作。', 'LIBRARY', 1, 1, 'seed'),
  ('commander_flight_mode', '指点飞行高度模式', 'INTEGER', '0 智能高度，1 设定高度。', 'LIBRARY', 1, 1, 'seed'),
  ('commander_flight_height', '指点飞行高度', 'NUMBER', '相对起飞点的飞行高度，单位米。', 'LIBRARY', 1, 1, 'seed'),
  ('flight_id', '飞行任务编号', 'STRING', '一键起飞任务编号。', 'LIBRARY', 1, 1, 'seed'),
  ('max_speed', '最大速度', 'INTEGER', '飞向目标过程中的最大速度，单位米每秒。', 'LIBRARY', 1, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false DO NOTHING;

-- 变焦、对焦补上相机类型。起飞补上一键起飞除坐标以外的必填项。坐标仍用原来的「坐标」。
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'executionMeans' = 'UAV' THEN jsonb_set(
              elem,
              '{fields}',
              COALESCE(elem->'fields', '[]'::jsonb) || (
                SELECT COALESCE(jsonb_agg(to_jsonb(add.field)), '[]'::jsonb)
                FROM (
                  VALUES
                    ('{"fieldCode":"camera_type","required":true,"defaultValue":null}'::jsonb)
                ) AS add(field)
                WHERE NOT EXISTS (
                  SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                  WHERE have->>'fieldCode' = (add.field->>'fieldCode')
                )
              )
            )
            ELSE elem
          END
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code IN ('zoom', 'focus');

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'executionMeans' = 'UAV' THEN jsonb_set(
              elem,
              '{fields}',
              COALESCE(elem->'fields', '[]'::jsonb) || (
                SELECT COALESCE(jsonb_agg(to_jsonb(add.field)), '[]'::jsonb)
                FROM (
                  VALUES
                    ('{"fieldCode":"security_takeoff_height","required":true,"defaultValue":null}'::jsonb),
                    ('{"fieldCode":"rth_mode","required":true,"defaultValue":null}'::jsonb),
                    ('{"fieldCode":"rth_altitude","required":true,"defaultValue":null}'::jsonb),
                    ('{"fieldCode":"rc_lost_action","required":true,"defaultValue":null}'::jsonb),
                    ('{"fieldCode":"commander_mode_lost_action","required":true,"defaultValue":null}'::jsonb),
                    ('{"fieldCode":"commander_flight_mode","required":true,"defaultValue":null}'::jsonb),
                    ('{"fieldCode":"commander_flight_height","required":true,"defaultValue":null}'::jsonb),
                    ('{"fieldCode":"flight_id","required":true,"defaultValue":null}'::jsonb),
                    ('{"fieldCode":"max_speed","required":true,"defaultValue":null}'::jsonb)
                ) AS add(field)
                WHERE NOT EXISTS (
                  SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                  WHERE have->>'fieldCode' = (add.field->>'fieldCode')
                )
              )
            )
            ELSE elem
          END
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'action-2b44a1f882f54438b6a1864e3636c889';

-- 指令空包写入文档里的字段，对照才能填进去。
UPDATE ent_data_protocol_t1 p
SET custom_fields = jsonb_set(
      p.custom_fields,
      '{command_json,outbound,data}',
      v.data,
      true
    )
FROM (
  VALUES
    ('camera_photo_take', '{"payload_index":""}'::jsonb),
    ('camera_recording_start', '{"payload_index":""}'::jsonb),
    ('camera_recording_stop', '{"payload_index":""}'::jsonb),
    ('camera_focal_length_set', '{"payload_index":"","camera_type":"","zoom_factor":0}'::jsonb),
    ('camera_point_focus_action', '{"payload_index":"","camera_type":"","x":0,"y":0}'::jsonb),
    ('takeoff_to_point', '{"target_latitude":0,"target_longitude":0,"target_height":0,"security_takeoff_height":0,"rth_mode":0,"rth_altitude":0,"rc_lost_action":0,"commander_mode_lost_action":0,"commander_flight_mode":0,"commander_flight_height":0,"flight_id":"","max_speed":0}'::jsonb)
) AS v(method, data)
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.custom_fields->'command_json'->'outbound'->>'method' = v.method
  AND p.custom_fields->'protocol_version' ?| ARRAY['dji-dock-1.16.1','dji-pilot-1.16.1'];

UPDATE ent_data_protocol_t1 p
SET custom_fields = jsonb_set(
      p.custom_fields,
      '{field_description_json,outbound}',
      COALESCE(p.custom_fields->'field_description_json'->'outbound', '[]'::jsonb) || v.fields,
      true
    )
FROM (
  VALUES
    ('camera_photo_take', '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。"}]'::jsonb),
    ('camera_recording_start', '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。"}]'::jsonb),
    ('camera_recording_stop', '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。"}]'::jsonb),
    ('camera_focal_length_set', '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角、变焦或红外"},{"code":"zoom_factor","path":"data.zoom_factor","type":"number","label":"变焦倍数","required":true,"description":"变焦倍数"}]'::jsonb),
    ('camera_point_focus_action', '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。"},{"code":"camera_type","path":"data.camera_type","type":"string","label":"相机类型","required":true,"description":"广角或变焦"},{"code":"x","path":"data.x","type":"number","label":"对焦点横坐标","required":true,"description":"镜头左上角为原点的水平位置"},{"code":"y","path":"data.y","type":"number","label":"对焦点纵坐标","required":true,"description":"镜头左上角为原点的竖直位置"}]'::jsonb),
    ('takeoff_to_point', '[{"code":"target_latitude","path":"data.target_latitude","type":"number","label":"目标点纬度","required":true,"description":"从动作的坐标取纬度"},{"code":"target_longitude","path":"data.target_longitude","type":"number","label":"目标点经度","required":true,"description":"从动作的坐标取经度"},{"code":"target_height","path":"data.target_height","type":"number","label":"目标点高度","required":true,"description":"从动作的坐标取高程"},{"code":"security_takeoff_height","path":"data.security_takeoff_height","type":"number","label":"安全起飞高度","required":true,"description":"先升高到的相对高度"},{"code":"rth_mode","path":"data.rth_mode","type":"integer","label":"返航高度模式","required":true,"description":"0 智能高度，1 设定高度"},{"code":"rth_altitude","path":"data.rth_altitude","type":"integer","label":"返航高度","required":true,"description":"相对起飞点高度"},{"code":"rc_lost_action","path":"data.rc_lost_action","type":"integer","label":"失控动作","required":true,"description":"0 悬停，1 降落，2 返航"},{"code":"commander_mode_lost_action","path":"data.commander_mode_lost_action","type":"integer","label":"指点飞行失控动作","required":true,"description":"0 继续，1 退出"},{"code":"commander_flight_mode","path":"data.commander_flight_mode","type":"integer","label":"指点飞行高度模式","required":true,"description":"0 智能高度，1 设定高度"},{"code":"commander_flight_height","path":"data.commander_flight_height","type":"number","label":"指点飞行高度","required":true,"description":"相对起飞点高度"},{"code":"flight_id","path":"data.flight_id","type":"string","label":"飞行任务编号","required":true,"description":"一键起飞任务编号"},{"code":"max_speed","path":"data.max_speed","type":"integer","label":"最大速度","required":true,"description":"米每秒"}]'::jsonb)
) AS v(method, fields)
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.custom_fields->'command_json'->'outbound'->>'method' = v.method
  AND p.custom_fields->'protocol_version' ?| ARRAY['dji-dock-1.16.1','dji-pilot-1.16.1']
  AND NOT EXISTS (
    SELECT 1
    FROM jsonb_array_elements(COALESCE(p.custom_fields->'field_description_json'->'outbound', '[]'::jsonb)) item
    WHERE item->>'path' = 'data.payload_index' OR item->>'path' = 'data.target_latitude'
  );

UPDATE ent_data_protocol_t1 p
SET command_json = p.custom_fields->'command_json',
    field_description_json = p.custom_fields->'field_description_json'
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.custom_fields->'command_json'->'outbound'->>'method' IN (
    'camera_photo_take','camera_recording_start','camera_recording_stop',
    'camera_focal_length_set','camera_point_focus_action','takeoff_to_point'
  );

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
FROM (
  VALUES
    ('map-takephoto-dji-dock', 'takePhoto', '单拍', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-photo-take', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'),
    ('map-takephoto-dji-pilot', 'takePhoto', '单拍', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-photo-take', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'),
    ('map-zoom-dji-dock', 'zoom', '变焦', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-focal-length-set', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"focal_length","sourceKind":"ACTION_PARAM","path":"data.zoom_factor"}]'),
    ('map-zoom-dji-pilot', 'zoom', '变焦', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-focal-length-set', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"focal_length","sourceKind":"ACTION_PARAM","path":"data.zoom_factor"}]'),
    ('map-start-record-dji-dock', 'startRecord', '开始录像', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-recording-start', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'),
    ('map-start-record-dji-pilot', 'startRecord', '开始录像', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-recording-start', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'),
    ('map-stop-record-dji-dock', 'stopRecord', '结束录像', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-recording-stop', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'),
    ('map-stop-record-dji-pilot', 'stopRecord', '结束录像', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-recording-stop', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'),
    ('map-focus-dji-dock', 'focus', '对焦', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-point-focus-action', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"focus_x","sourceKind":"ACTION_PARAM","path":"data.x"},{"slot":"focus_y","sourceKind":"ACTION_PARAM","path":"data.y"}]'),
    ('map-focus-dji-pilot', 'focus', '对焦', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-point-focus-action', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_type","sourceKind":"ACTION_PARAM","path":"data.camera_type"},{"slot":"focus_x","sourceKind":"ACTION_PARAM","path":"data.x"},{"slot":"focus_y","sourceKind":"ACTION_PARAM","path":"data.y"}]'),
    ('map-takeoff-dji-dock', 'action-2b44a1f882f54438b6a1864e3636c889', '无人机起飞', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-takeoff-to-point', '[{"slot":"FLD-LOC-014","sourcePath":"latitude","path":"data.target_latitude"},{"slot":"FLD-LOC-014","sourcePath":"longitude","path":"data.target_longitude"},{"slot":"FLD-LOC-014","sourcePath":"height","path":"data.target_height"},{"slot":"security_takeoff_height","path":"data.security_takeoff_height"},{"slot":"rth_mode","path":"data.rth_mode"},{"slot":"rth_altitude","path":"data.rth_altitude"},{"slot":"rc_lost_action","path":"data.rc_lost_action"},{"slot":"commander_mode_lost_action","path":"data.commander_mode_lost_action"},{"slot":"commander_flight_mode","path":"data.commander_flight_mode"},{"slot":"commander_flight_height","path":"data.commander_flight_height"},{"slot":"flight_id","path":"data.flight_id"},{"slot":"max_speed","path":"data.max_speed"}]')
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
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-%dji-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'action_ref', NULL, (e.custom_fields->'action_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-%dji-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-%dji-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code LIKE 'map-%dji-%'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
);

COMMIT;
