-- 还空着的标准动作：协议里有对应下发指令的，选上那条指令，并把动作参数填进空包已有字段。
-- 占位字段「默认0000」写在指令空包里，不做成动作参数。
SET search_path TO dynamicbusiness;
BEGIN;

INSERT INTO dynamic_field (code, name, type, description, source, status, tenant_id, creator)
VALUES
  ('stream_url', '视频流地址', 'STRING', '推流或录像要送到的地址。东古录像填视频流地址，直播填直播地址。', 'LIBRARY', 1, 1, 'seed'),
  ('record_rotate', '拍摄中旋转', 'STRING', '录像过程中摄像机是否旋转。', 'LIBRARY', 1, 1, 'seed'),
  ('gas_frequency', '气体上报频率', 'INTEGER', '气体检测打开后，隔多少秒上报一次。', 'LIBRARY', 1, 1, 'seed'),
  ('url_type', '直播协议类型', 'INTEGER', '0 声网，1 RTMP，3 GB28181，4 WebRTC。', 'LIBRARY', 1, 1, 'seed'),
  ('video_id', '直播视频流编号', 'STRING', '正在推的那路视频，格式是设备序列号/相机的负载索引编号/码流索引。', 'LIBRARY', 1, 1, 'seed'),
  ('video_quality', '直播清晰度', 'INTEGER', '0 自适应，1 流畅，2 标清，3 高清，4 超清。', 'LIBRARY', 1, 1, 'seed'),
  ('fly_to_id', '飞向目标点编号', 'STRING', '这一次飞向目标点的编号。', 'LIBRARY', 1, 1, 'seed'),
  ('camera_mode', '相机模式', 'INTEGER', '0 拍照，1 录像，2 智能低光，3 全景拍照。', 'LIBRARY', 1, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false DO NOTHING;

-- 标准动作补上指令必填、而动作上还没有的参数。已有的不重复加。
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
                  WHERE have->>'fieldCode' = add.field->>'fieldCode'
                )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN ('act-robot-shoot', 'startRecord', 'stopRecord', 'takePhoto');

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
                ('{"fieldCode":"yaw","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"pitch","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"focal_length","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"shot_count","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"F-2b8fa8bda0b94663979c112af85325dc","required":true,"defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code IN ('takePhoto', 'act-robot-hold');

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
                ('{"fieldCode":"yaw","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"pitch","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"focal_length","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"F-2b8fa8bda0b94663979c112af85325dc","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"stream_url","required":true,"valueSource":"SYSTEM","defaultValue":null}'::jsonb),
                ('{"fieldCode":"record_rotate","required":true,"defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'startRecord';

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
                ('{"fieldCode":"F-2b8fa8bda0b94663979c112af85325dc","required":true,"defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN ('stopRecord', 'action-ab595a61ca164a2c9db74e1311e747a5');

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
                ('{"fieldCode":"F-2b8fa8bda0b94663979c112af85325dc","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"gas_frequency","required":true,"defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'action-647f9f172c3b489ea862a29c0e8396a2';

UPDATE ent_action_t1
SET param_slots_json = '{"version":2,"methods":[{"executionMeans":"UAV","fields":[{"fieldCode":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","required":true,"defaultValue":null},{"fieldCode":"action_duration","required":false,"defaultValue":120,"durationUnit":"min"}]}]}'::jsonb,
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'act-uav-return-charge'
  AND param_slots_json->'methods' IS NULL;

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          CASE WHEN elem->>'executionMeans' = 'UAV' THEN jsonb_set(
            elem,
            '{fields}',
            COALESCE(elem->'fields', '[]'::jsonb) || (
              SELECT COALESCE(jsonb_agg(add.field), '[]'::jsonb)
              FROM (VALUES
                ('{"fieldCode":"max_speed","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"fly_to_id","required":true,"valueSource":"SYSTEM","defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          ) ELSE elem END
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND a.code IN ('action-065340437bcb46788b7dc261fab81c63', 'act-uav-return-charge');

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
                ('{"fieldCode":"stream_url","required":true,"valueSource":"SYSTEM","defaultValue":null}'::jsonb),
                ('{"fieldCode":"url_type","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"video_id","required":true,"defaultValue":null}'::jsonb),
                ('{"fieldCode":"video_quality","required":true,"defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'action-a2bf6d76c8d04602bab0d18aa62f6958';

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
                ('{"fieldCode":"video_id","required":true,"defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'action-a2f8ee6c1ab5495fa565742c82d97413';

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
                ('{"fieldCode":"camera_mode","required":true,"defaultValue":null}'::jsonb)
              ) AS add(field)
              WHERE NOT EXISTS (
                SELECT 1 FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) have
                WHERE have->>'fieldCode' = add.field->>'fieldCode'
              )
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'panoShot';

-- 默认 0000 的占位写进空包，不再要求动作另填一格。
UPDATE ent_data_protocol_t1 p
SET command_json = CASE
      WHEN jsonb_typeof(command_json->'outbound'->'request') = 'object'
        THEN jsonb_set(command_json, '{outbound,request,blank}', '"0000"'::jsonb)
      ELSE command_json
    END,
    custom_fields = CASE
      WHEN jsonb_typeof(custom_fields->'field_description_json'->'outbound') = 'array' THEN jsonb_set(
        jsonb_set(custom_fields, '{command_json,outbound,request,blank}', '"0000"'::jsonb),
        '{field_description_json,outbound}',
        (
          SELECT jsonb_agg(
            CASE WHEN item->>'path' = 'request.blank'
              THEN jsonb_set(item, '{required}', 'false'::jsonb)
              ELSE item END
          )
          FROM jsonb_array_elements(custom_fields->'field_description_json'->'outbound') item
        )
      )
      ELSE jsonb_set(custom_fields, '{command_json,outbound,request,blank}', '"0000"'::jsonb)
    END,
    field_description_json = CASE
      WHEN jsonb_typeof(field_description_json->'outbound') = 'array' THEN jsonb_set(
        field_description_json,
        '{outbound}',
        (
          SELECT jsonb_agg(
            CASE WHEN item->>'path' = 'request.blank'
              THEN jsonb_set(item, '{required}', 'false'::jsonb)
              ELSE item END
          )
          FROM jsonb_array_elements(field_description_json->'outbound') item
        )
      )
      ELSE field_description_json
    END
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.code IN ('proto-uav-200103-land', 'proto-uav-200402-gas-off', 'proto-uav-200202-record-off');

UPDATE ent_data_protocol_t1 p
SET custom_fields = jsonb_set(p.custom_fields, '{command_json,outbound,data}', v.data, true)
FROM (VALUES
  ('fly_to_point', '{"fly_to_id":"","max_speed":0,"points":[{"latitude":0,"longitude":0,"height":0}]}'::jsonb),
  ('live_start_push', '{"url_type":0,"url":"","video_id":"","video_quality":0}'::jsonb),
  ('live_stop_push', '{"video_id":""}'::jsonb),
  ('speaker_audio_play_start', '{"psdk_index":0,"file":{"name":"","url":"","md5":"","format":"pcm"}}'::jsonb),
  ('camera_mode_switch', '{"payload_index":"","camera_mode":0}'::jsonb)
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
FROM (VALUES
  ('fly_to_point', '[{"code":"fly_to_id","path":"data.fly_to_id","type":"string","label":"飞向目标点编号","required":true,"description":"这一次飞向目标点的编号"},{"code":"max_speed","path":"data.max_speed","type":"integer","label":"最大速度","required":true,"description":"米每秒"},{"code":"latitude","path":"data.points.0.latitude","type":"number","label":"目标点纬度","required":true,"description":"从动作的位置取纬度"},{"code":"longitude","path":"data.points.0.longitude","type":"number","label":"目标点经度","required":true,"description":"从动作的位置取经度"},{"code":"height","path":"data.points.0.height","type":"number","label":"目标点高度","required":true,"description":"从动作的位置取高程"}]'::jsonb),
  ('live_start_push', '[{"code":"url_type","path":"data.url_type","type":"integer","label":"直播协议类型","required":true,"description":"0 声网，1 RTMP，3 GB28181，4 WebRTC"},{"code":"url","path":"data.url","type":"string","label":"直播地址","required":true,"description":"按协议类型填写的直播参数"},{"code":"video_id","path":"data.video_id","type":"string","label":"直播视频流编号","required":true,"description":"设备序列号/相机的负载索引编号/码流索引"},{"code":"video_quality","path":"data.video_quality","type":"integer","label":"直播清晰度","required":true,"description":"0 自适应到 4 超清"}]'::jsonb),
  ('live_stop_push', '[{"code":"video_id","path":"data.video_id","type":"string","label":"直播视频流编号","required":true,"description":"要停止的那路视频"}]'::jsonb),
  ('speaker_audio_play_start', '[{"code":"psdk_index","path":"data.psdk_index","type":"integer","label":"喊话器负载索引","required":true,"description":"喊话器挂在哪一路"},{"code":"name","path":"data.file.name","type":"string","label":"音频文件名","required":true,"description":"播放文件名"},{"code":"url","path":"data.file.url","type":"string","label":"音频地址","required":true,"description":"机场下载音频的地址"},{"code":"md5","path":"data.file.md5","type":"string","label":"音频校验","required":true,"description":"文件内容的校验"}]'::jsonb),
  ('camera_mode_switch', '[{"code":"payload_index","path":"data.payload_index","type":"string","label":"相机的负载索引编号","required":true,"description":"类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。"},{"code":"camera_mode","path":"data.camera_mode","type":"integer","label":"相机模式","required":true,"description":"0 拍照，1 录像，2 智能低光，3 全景拍照"}]'::jsonb)
) AS v(method, fields)
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.custom_fields->'command_json'->'outbound'->>'method' = v.method
  AND p.custom_fields->'protocol_version' ?| ARRAY['dji-dock-1.16.1','dji-pilot-1.16.1']
  AND NOT EXISTS (
    SELECT 1 FROM jsonb_array_elements(COALESCE(p.custom_fields->'field_description_json'->'outbound', '[]'::jsonb)) item
    WHERE item->>'path' IN ('data.fly_to_id', 'data.url', 'data.video_id', 'data.file.name', 'data.camera_mode')
  );

UPDATE ent_data_protocol_t1 p
SET command_json = p.custom_fields->'command_json',
    field_description_json = p.custom_fields->'field_description_json'
WHERE p.deleted = false AND p.tenant_id = 1
  AND p.custom_fields->'command_json'->'outbound'->>'method' IN (
    'fly_to_point','live_start_push','live_stop_push','speaker_audio_play_start','camera_mode_switch'
  );

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[{"slot":"gas_frequency","path":"request.frequency"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]'::jsonb
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'map-gas-on-uav-ws';

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]'::jsonb
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'map-gas-off-uav-ws';

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[{"slot":"action_duration","path":"request.time"},{"slot":"shot_count","path":"request.number"},{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"}]'::jsonb
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code IN ('map-act-robot-hold-uav-ws', 'map-act-robot-hold-robot-ws');

-- 历史库可能已 INSERT 对照行但未写入 slots（界面显示「参数未齐」）。
UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"stream_url","sourceKind":"SYSTEM","path":"request.rstp"},{"slot":"record_rotate","path":"request.rotate"},{"slot":"action_duration","path":"request.time"}]'::jsonb
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'map-start-record-uav-ws'
  AND jsonb_array_length(COALESCE(custom_fields->'param_slot_mapping_json', '[]'::jsonb)) = 0;

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]'::jsonb
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'map-stop-record-uav-ws'
  AND jsonb_array_length(COALESCE(custom_fields->'param_slot_mapping_json', '[]'::jsonb)) = 0;

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
  ('map-photo-dji-dock', 'act-robot-shoot', '拍照', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-photo-take', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'),
  ('map-photo-dji-pilot', 'act-robot-shoot', '拍照', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-photo-take', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"}]'),
  ('map-takephoto-uav-ws', 'takePhoto', '单拍', 'uav-ws', '无人机对接协议', 'proto-uav-200301-photo', '[{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"shot_count","path":"request.number"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]'),
  ('map-start-record-uav-ws', 'startRecord', '开始录像', 'uav-ws', '无人机对接协议', 'proto-uav-200201-record-on', '[{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"stream_url","sourceKind":"SYSTEM","path":"request.rstp"},{"slot":"record_rotate","path":"request.rotate"},{"slot":"action_duration","path":"request.time"}]'),
  ('map-stop-record-uav-ws', 'stopRecord', '结束录像', 'uav-ws', '无人机对接协议', 'proto-uav-200202-record-off', '[{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]'),
  ('map-start-record-robot-ws', 'startRecord', '开始录像', 'robot-ws', '机器人对接协议', 'proto-uav-200201-record-on', '[{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"stream_url","sourceKind":"SYSTEM","path":"request.rstp"},{"slot":"record_rotate","path":"request.rotate"},{"slot":"action_duration","path":"request.time"}]'),
  ('map-stop-record-robot-ws', 'stopRecord', '结束录像', 'robot-ws', '机器人对接协议', 'proto-uav-200202-record-off', '[{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]'),
  ('map-uav-land-uav-ws', 'act-uav-land', '降落', 'uav-ws', '无人机对接协议', 'proto-uav-200103-land', '[]'),
  ('map-uav-land-dji-dock', 'act-uav-land', '降落', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-return-home', '[]'),
  ('map-arrive-dji-dock', 'action-065340437bcb46788b7dc261fab81c63', '到达指定位置', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-fly-to-point', '[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.latitude","path":"data.points.0.latitude"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.longitude","path":"data.points.0.longitude"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.height","path":"data.points.0.height"},{"slot":"max_speed","path":"data.max_speed"},{"slot":"fly_to_id","path":"data.fly_to_id"}]'),
  ('map-arrive-dji-pilot', 'action-065340437bcb46788b7dc261fab81c63', '飞向目标点', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-fly-to-point', '[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.latitude","path":"data.points.0.latitude"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.longitude","path":"data.points.0.longitude"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.height","path":"data.points.0.height"},{"slot":"max_speed","path":"data.max_speed"},{"slot":"fly_to_id","path":"data.fly_to_id"}]'),
  ('map-return-charge-dji-dock', 'act-uav-return-charge', '返回充电', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-fly-to-point', '[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.latitude","path":"data.points.0.latitude"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.longitude","path":"data.points.0.longitude"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.height","path":"data.points.0.height"},{"slot":"max_speed","path":"data.max_speed"},{"slot":"fly_to_id","path":"data.fly_to_id"}]'),
  ('map-uav-battery-uav-ws', 'action-c6d414f4f24c442eb5947dccdf802b37', '电量检查', 'uav-ws', '无人机对接协议', 'proto-uav-500101-status', '[]'),
  ('map-device-status-uav-ws', 'action-1a0df020d2064b94abd5bad0a6bba0d1', '设备状态检查', 'uav-ws', '无人机对接协议', 'proto-uav-500101-status', '[]'),
  ('map-self-check-uav-ws', 'act-robot-self-check', '开机自检', 'uav-ws', '无人机对接协议', 'proto-uav-500101-status', '[]'),
  ('map-self-check-robot-ws', 'act-robot-self-check', '开机自检', 'robot-ws', '机器人对接协议', 'proto-robot-500101-status', '[]'),
  ('map-preflight-uav-ws', 'action-4019fb901ff3411f99603b1f5cee5119', '起飞前检查', 'uav-ws', '无人机对接协议', 'proto-uav-500101-status', '[]'),
  ('map-stream-on-uav-ws', 'action-a2bf6d76c8d04602bab0d18aa62f6958', '摄像头视频回传开启', 'uav-ws', '无人机对接协议', 'proto-uav-500401-stream-on', '[{"slot":"stream_url","sourceKind":"SYSTEM","path":"rtmp"}]'),
  ('map-stream-on-robot-ws', 'action-a2bf6d76c8d04602bab0d18aa62f6958', '摄像头视频回传开启', 'robot-ws', '机器人对接协议', 'proto-robot-500401-stream-on', '[{"slot":"stream_url","sourceKind":"SYSTEM","path":"rtmp"}]'),
  ('map-stream-on-dji-dock', 'action-a2bf6d76c8d04602bab0d18aa62f6958', '摄像头视频回传开启', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-live-start-push', '[{"slot":"url_type","path":"data.url_type"},{"slot":"stream_url","sourceKind":"SYSTEM","path":"data.url"},{"slot":"video_id","path":"data.video_id"},{"slot":"video_quality","path":"data.video_quality"}]'),
  ('map-stream-on-dji-pilot', 'action-a2bf6d76c8d04602bab0d18aa62f6958', '摄像头视频回传开启', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-live-start-push', '[{"slot":"url_type","path":"data.url_type"},{"slot":"stream_url","sourceKind":"SYSTEM","path":"data.url"},{"slot":"video_id","path":"data.video_id"},{"slot":"video_quality","path":"data.video_quality"}]'),
  ('map-stream-off-uav-ws', 'action-a2f8ee6c1ab5495fa565742c82d97413', '摄像头视频回传关闭', 'uav-ws', '无人机对接协议', 'proto-uav-500402-stream-off', '[]'),
  ('map-stream-off-robot-ws', 'action-a2f8ee6c1ab5495fa565742c82d97413', '摄像头视频回传关闭', 'robot-ws', '机器人对接协议', 'proto-robot-500402-stream-off', '[]'),
  ('map-stream-off-dji-dock', 'action-a2f8ee6c1ab5495fa565742c82d97413', '摄像头视频回传关闭', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-live-stop-push', '[{"slot":"video_id","path":"data.video_id"}]'),
  ('map-stream-off-dji-pilot', 'action-a2f8ee6c1ab5495fa565742c82d97413', '摄像头视频回传关闭', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-live-stop-push', '[{"slot":"video_id","path":"data.video_id"}]'),
  ('map-megaphone-dji-dock', 'megaphone', '喊话器', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-speaker-audio-play-start', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.psdk_index"},{"slot":"megaphone_file_name","sourceKind":"ACTION_PARAM","path":"data.file.name"},{"slot":"megaphone_file_path","sourceKind":"ACTION_PARAM","path":"data.file.url"},{"slot":"megaphone_file_md5","sourceKind":"ACTION_PARAM","path":"data.file.md5"}]'),
  ('map-pano-dji-dock', 'panoShot', '全景拍照', 'dji-dock-1.16.1', '大疆机场', 'proto-dji-dock-camera-mode-switch', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_mode","sourceKind":"ACTION_PARAM","path":"data.camera_mode"}]'),
  ('map-pano-dji-pilot', 'panoShot', '全景拍照', 'dji-pilot-1.16.1', '大疆 Pilot', 'proto-dji-pilot-camera-mode-switch', '[{"slot":"payload_position_index","sourceKind":"EXECUTOR_MOUNT","path":"data.payload_index"},{"slot":"camera_mode","sourceKind":"ACTION_PARAM","path":"data.camera_mode"}]')
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
  AND e.code IN (
    'map-photo-dji-dock','map-photo-dji-pilot','map-takephoto-uav-ws','map-start-record-uav-ws','map-stop-record-uav-ws','map-start-record-robot-ws','map-stop-record-robot-ws',
    'map-uav-land-uav-ws','map-uav-land-dji-dock','map-arrive-dji-dock','map-arrive-dji-pilot','map-return-charge-dji-dock',
    'map-uav-battery-uav-ws','map-device-status-uav-ws','map-self-check-uav-ws','map-self-check-robot-ws','map-preflight-uav-ws',
    'map-stream-on-uav-ws','map-stream-on-robot-ws','map-stream-on-dji-dock','map-stream-on-dji-pilot',
    'map-stream-off-uav-ws','map-stream-off-robot-ws','map-stream-off-dji-dock','map-stream-off-dji-pilot',
    'map-megaphone-dji-dock','map-pano-dji-dock','map-pano-dji-pilot'
  )
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'action_ref', NULL, (e.custom_fields->'action_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'map-photo-dji-dock','map-photo-dji-pilot','map-takephoto-uav-ws','map-start-record-uav-ws','map-stop-record-uav-ws','map-start-record-robot-ws','map-stop-record-robot-ws',
    'map-uav-land-uav-ws','map-uav-land-dji-dock','map-arrive-dji-dock','map-arrive-dji-pilot','map-return-charge-dji-dock',
    'map-uav-battery-uav-ws','map-device-status-uav-ws','map-self-check-uav-ws','map-self-check-robot-ws','map-preflight-uav-ws',
    'map-stream-on-uav-ws','map-stream-on-robot-ws','map-stream-on-dji-dock','map-stream-on-dji-pilot',
    'map-stream-off-uav-ws','map-stream-off-robot-ws','map-stream-off-dji-dock','map-stream-off-dji-pilot',
    'map-megaphone-dji-dock','map-pano-dji-dock','map-pano-dji-pilot'
  )
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'map-photo-dji-dock','map-photo-dji-pilot','map-takephoto-uav-ws','map-start-record-uav-ws','map-stop-record-uav-ws','map-start-record-robot-ws','map-stop-record-robot-ws',
    'map-uav-land-uav-ws','map-uav-land-dji-dock','map-arrive-dji-dock','map-arrive-dji-pilot','map-return-charge-dji-dock',
    'map-uav-battery-uav-ws','map-device-status-uav-ws','map-self-check-uav-ws','map-self-check-robot-ws','map-preflight-uav-ws',
    'map-stream-on-uav-ws','map-stream-on-robot-ws','map-stream-on-dji-dock','map-stream-on-dji-pilot',
    'map-stream-off-uav-ws','map-stream-off-robot-ws','map-stream-off-dji-dock','map-stream-off-dji-pilot',
    'map-megaphone-dji-dock','map-pano-dji-dock','map-pano-dji-pilot'
  )
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'map-photo-dji-dock','map-photo-dji-pilot','map-takephoto-uav-ws','map-start-record-uav-ws','map-stop-record-uav-ws','map-start-record-robot-ws','map-stop-record-robot-ws',
    'map-uav-land-uav-ws','map-uav-land-dji-dock','map-arrive-dji-dock','map-arrive-dji-pilot','map-return-charge-dji-dock',
    'map-uav-battery-uav-ws','map-device-status-uav-ws','map-self-check-uav-ws','map-self-check-robot-ws','map-preflight-uav-ws',
    'map-stream-on-uav-ws','map-stream-on-robot-ws','map-stream-on-dji-dock','map-stream-on-dji-pilot',
    'map-stream-off-uav-ws','map-stream-off-robot-ws','map-stream-off-dji-dock','map-stream-off-dji-pilot',
    'map-megaphone-dji-dock','map-pano-dji-dock','map-pano-dji-pilot'
  )
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
);

-- 推流地址由任务生成时分配，动作参数是系统生成，对照也标成系统生成。
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            elem,
            '{fields}',
            (
              SELECT jsonb_agg(
                CASE
                  WHEN f->>'fieldCode' = 'stream_url'
                    THEN jsonb_set(f, '{valueSource}', '"SYSTEM"')
                  ELSE f
                END
              )
              FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND a.param_slots_json::text LIKE '%stream_url%';

UPDATE ent_protocol_mapping_t1 e
SET custom_fields = jsonb_set(
      e.custom_fields,
      '{param_slot_mapping_json}',
      (
        SELECT jsonb_agg(
          CASE
            WHEN elem->>'slot' = 'stream_url'
              THEN elem || '{"sourceKind":"SYSTEM"}'::jsonb
            ELSE elem
          END
        )
        FROM jsonb_array_elements(e.custom_fields->'param_slot_mapping_json') elem
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND jsonb_typeof(e.custom_fields->'param_slot_mapping_json') = 'array'
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(e.custom_fields->'param_slot_mapping_json') elem
    WHERE elem->>'slot' = 'stream_url'
  );

COMMIT;
