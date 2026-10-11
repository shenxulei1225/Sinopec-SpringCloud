-- 按大疆上云服务指令的 data 收口动作参数。
-- 负责：指令对错、动作上多余的参数、对照里已经指向错误指令的行。
-- 不负责：把相机的负载索引编号改成运行时从负载拼出来；航线文件动作；东古指令仍要的参数。
-- 相机的负载索引编号是开始拍照、录像、变焦、点对焦报文里的文本列，格式为类型-子类型-云台位置。
-- 它留在「这条指令确实有这一列」的动作上。没有指令用到的参数删掉。

SET search_path TO dynamicbusiness;

BEGIN;

-- 拍照是东古的偏航、俯仰、焦距、张数，不是大疆开始拍照。
-- 降落不是一键返航。全景拍照不是切换相机模式。这两条大疆都没有对应的一条服务指令。
UPDATE dynamic_entity_field_index_t1 idx
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
WHERE m.tenant_id = 1
  AND m.code IN (
    'map-photo-dji-dock', 'map-photo-dji-pilot',
    'map-uav-land-dji-dock',
    'map-pano-dji-dock', 'map-pano-dji-pilot'
  )
  AND idx.tenant_id = 1 AND idx.entity_id = m.id AND idx.deleted = false;

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
WHERE m.tenant_id = 1
  AND m.code IN (
    'map-photo-dji-dock', 'map-photo-dji-pilot',
    'map-uav-land-dji-dock',
    'map-pano-dji-dock', 'map-pano-dji-pilot'
  )
  AND r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
  AND r.entity_id = m.id AND r.deleted = false;

UPDATE ent_protocol_mapping_t1
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false
  AND code IN (
    'map-photo-dji-dock', 'map-photo-dji-pilot',
    'map-uav-land-dji-dock',
    'map-pano-dji-dock', 'map-pano-dji-pilot'
  );

-- 单拍不该再走东古拍照。东古拍照仍由「拍照」承担。
UPDATE dynamic_entity_field_index_t1 idx
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
JOIN ent_action_t1 a ON a.id::text = (m.custom_fields->'action_ref'->>'id')
WHERE a.tenant_id = 1 AND a.code = 'takePhoto'
  AND m.custom_fields->>'mapped_protocol_version' = 'uav-ws'
  AND m.deleted = false
  AND idx.tenant_id = 1 AND idx.entity_id = m.id AND idx.deleted = false;

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
JOIN ent_action_t1 a ON a.id::text = (m.custom_fields->'action_ref'->>'id')
WHERE a.tenant_id = 1 AND a.code = 'takePhoto'
  AND m.custom_fields->>'mapped_protocol_version' = 'uav-ws'
  AND m.deleted = false
  AND r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
  AND r.entity_id = m.id AND r.deleted = false;

UPDATE ent_protocol_mapping_t1 m
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_action_t1 a
WHERE a.tenant_id = 1 AND a.code = 'takePhoto'
  AND a.id::text = (m.custom_fields->'action_ref'->>'id')
  AND m.custom_fields->>'mapped_protocol_version' = 'uav-ws'
  AND m.deleted = false;

-- 返回充电对上一键返航。一键返航的 data 为空，不再填飞向目标点。
UPDATE ent_protocol_mapping_t1 m
SET custom_fields = jsonb_set(
      jsonb_set(
        m.custom_fields,
        '{protocol_instruction_ref}',
        jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol')
      ),
      '{param_slot_mapping_json}',
      '[]'::jsonb
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 p
WHERE m.tenant_id = 1 AND m.deleted = false AND m.code = 'map-return-charge-dji-dock'
  AND p.tenant_id = 1 AND p.deleted = false AND p.code = 'proto-dji-return-home';

UPDATE dynamic_entity_field_index_t1 idx
SET value_number = p.id, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 m
JOIN ent_data_protocol_t1 p
  ON p.tenant_id = 1 AND p.deleted = false AND p.code = 'proto-dji-return-home'
WHERE m.code = 'map-return-charge-dji-dock'
  AND idx.tenant_id = 1 AND idx.deleted = false
  AND idx.entity_id = m.id AND idx.field_code = 'protocol_instruction_ref';

CREATE TEMP TABLE keep_slot (action_code text, field_code text) ON COMMIT DROP;

INSERT INTO keep_slot (action_code, field_code) VALUES
  -- 拍照：东古拍照仍要这些。大疆开始拍照已撤掉。
  ('act-robot-shoot', 'yaw'),
  ('act-robot-shoot', 'pitch'),
  ('act-robot-shoot', 'focal_length'),
  ('act-robot-shoot', 'shot_count'),
  ('act-robot-shoot', 'F-2b8fa8bda0b94663979c112af85325dc'),
  ('act-robot-shoot', 'action_duration'),
  -- 单拍：开始拍照只有相机的负载索引编号。
  ('takePhoto', 'payload_index'),
  -- 开始录像：大疆只要相机的负载索引编号；东古启动录像仍要角度、焦距、推流。
  ('startRecord', 'payload_index'),
  ('startRecord', 'yaw'),
  ('startRecord', 'pitch'),
  ('startRecord', 'focal_length'),
  ('startRecord', 'F-2b8fa8bda0b94663979c112af85325dc'),
  ('startRecord', 'stream_url'),
  ('startRecord', 'record_rotate'),
  ('startRecord', 'action_duration'),
  -- 结束录像：大疆只要相机的负载索引编号；东古关闭录像仍要驻留和时长。
  ('stopRecord', 'payload_index'),
  ('stopRecord', 'F-2b8fa8bda0b94663979c112af85325dc'),
  ('stopRecord', 'action_duration'),
  -- 变焦：相机的负载索引编号、相机类型、变焦倍数。
  ('zoom', 'payload_index'),
  ('zoom', 'camera_type'),
  ('zoom', 'zoom_factor'),
  -- 点对焦：相机的负载索引编号、相机类型、对焦点坐标。
  ('focus', 'payload_index'),
  ('focus', 'camera_type'),
  ('focus', 'focus_x'),
  ('focus', 'focus_y'),
  -- 喊话器：psdk 负载索引、文件名、下载链接、md5。格式留在指令空包。
  ('megaphone', 'payload_position_index'),
  ('megaphone', 'megaphone_file_name'),
  ('megaphone', 'megaphone_file_path'),
  ('megaphone', 'megaphone_file_md5'),
  -- 返回充电：东古移动仍要位置。一键返航本身没有参数。
  ('act-uav-return-charge', 'F-e49f76bdca3e4e1393e8e2f1cc0e7867'),
  -- 起飞：一键起飞的必填项。东古起飞仍用坐标。
  ('action-2b44a1f882f54438b6a1864e3636c889', 'FLD-LOC-014'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'max_speed'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'commander_mode_lost_action'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'commander_flight_mode'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'flight_id'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'rth_altitude'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'commander_flight_height'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'rc_lost_action'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'rth_mode'),
  ('action-2b44a1f882f54438b6a1864e3636c889', 'security_takeoff_height'),
  -- 开始直播：协议类型、地址、视频流编号、清晰度。东古启动推流仍用地址。
  ('action-a2bf6d76c8d04602bab0d18aa62f6958', 'url_type'),
  ('action-a2bf6d76c8d04602bab0d18aa62f6958', 'stream_url'),
  ('action-a2bf6d76c8d04602bab0d18aa62f6958', 'video_id'),
  ('action-a2bf6d76c8d04602bab0d18aa62f6958', 'video_quality'),
  -- 停止直播：只有视频流编号。
  ('action-a2f8ee6c1ab5495fa565742c82d97413', 'video_id');

-- 全景拍照、无人机返航、降落：清掉动作参数。返航和降落的指令 data 为空；全景没有对应的一条服务指令。
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            elem,
            '{fields}',
            COALESCE(
              (
                SELECT jsonb_agg(f)
                FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
                WHERE EXISTS (
                  SELECT 1 FROM keep_slot k
                  WHERE k.action_code = a.code AND k.field_code = f->>'fieldCode'
                )
              ),
              '[]'::jsonb
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
  AND a.code IN (
    'act-robot-shoot', 'takePhoto', 'startRecord', 'stopRecord', 'zoom', 'focus',
    'panoShot', 'megaphone', 'act-uav-return-charge', 'act-uav-land',
    'action-1022d966da2b45f7b73e776112e6884b',
    'action-2b44a1f882f54438b6a1864e3636c889',
    'action-a2bf6d76c8d04602bab0d18aa62f6958',
    'action-a2f8ee6c1ab5495fa565742c82d97413'
  );

UPDATE dynamic_field
SET name = '相机的负载索引编号',
    description = '类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。52-0-0 只是举例，不是默认值。开始拍照、开始录像、停止录像、变焦、点对焦的报文有这一列。',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'payload_index';

COMMIT;
