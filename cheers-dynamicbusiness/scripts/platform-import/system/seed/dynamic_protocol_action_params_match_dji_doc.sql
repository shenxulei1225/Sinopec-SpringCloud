-- 动作参数只保留该条大疆服务指令报文里、需要人填写的列。
-- 相机的负载索引编号不进动作。东古才有的偏航、驻留、航线包路径从这些动作上拿掉。
-- 不负责：改东古拍照；不补模拟飞行等可选列。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (code, name, type, description, options, source, status, tenant_id, creator)
VALUES
  (
    'psdk_index',
    '喊话器负载序号',
    'INTEGER',
    '喊话器-开始播放音频的 psdk_index。从 0 起，不是相机的云台位置。',
    NULL,
    'LIBRARY', 1, 1, 'seed'
  ),
  (
    'point_focus_camera_type',
    '相机类型',
    'ENUM',
    '点对焦的相机类型。只有广角和变焦。',
    '[{"label":"广角","value":"wide"},{"label":"变焦","value":"zoom"}]',
    'LIBRARY', 1, 1, 'seed'
  ),
  (
    'point_focus_x',
    '对焦点坐标 x',
    'NUMBER',
    '点对焦的水平坐标。以镜头左上角为原点，0 到 1。',
    NULL,
    'LIBRARY', 1, 1, 'seed'
  ),
  (
    'point_focus_y',
    '对焦点坐标 y',
    'NUMBER',
    '点对焦的竖直坐标。以镜头左上角为原点，0 到 1。',
    NULL,
    'LIBRARY', 1, 1, 'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_field
SET type = 'ENUM',
    options = '[{"label":"红外","value":"ir"},{"label":"广角","value":"wide"},{"label":"变焦","value":"zoom"}]',
    description = '变焦的相机类型：红外、广角、变焦。',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'camera_type';

UPDATE dynamic_field SET
  name = '音频文件名',
  description = '喊话器要播放的音频文件名。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'megaphone_file_name';

UPDATE dynamic_field SET
  name = '音频文件下载链接',
  description = '机场下载这份音频的链接。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'megaphone_file_path';

UPDATE dynamic_field SET
  name = '音频文件校验',
  description = '音频文件的 md5。机场用来标识这份音频。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'megaphone_file_md5';

UPDATE dynamic_field SET
  name = '直播参数',
  description = '开始直播的 url。RTMP 填 rtmp 地址；声网、GB28181、WebRTC 按文档的参数串填写。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'stream_url';

UPDATE dynamic_field SET
  name = '直播视频流编号',
  description = '正在推的那路视频，格式是设备序列号/相机索引/码流索引。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'video_id';

UPDATE dynamic_field SET
  name = '直播质量',
  description = '0 自适应，1 流畅，2 标清，3 高清，4 超清。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'video_quality';

UPDATE dynamic_field SET
  name = '飞向目标点编号',
  description = '这一次飞向目标点的编号。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'fly_to_id';

UPDATE dynamic_field SET
  description = '飞行过程中能达到的最大速度，单位米每秒。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'max_speed';

UPDATE dynamic_field SET
  name = '返航模式',
  description = '0 智能高度，1 设定高度。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'rth_mode';

UPDATE dynamic_field SET
  description = '0 悬停，1 着陆，2 返航。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'rc_lost_action';

UPDATE dynamic_field SET
  name = '指点飞行模式',
  description = '0 智能高度飞行，1 设定高度飞行。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'commander_flight_mode';

UPDATE dynamic_field SET
  description = '一键起飞任务 UUID，全局唯一。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'flight_id';

UPDATE dynamic_field SET
  description = '相对机场起飞点先升高到的高度，单位米。',
  updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND deleted = false AND code = 'security_takeoff_height';

-- 开始录像、停止录像的服务报文没有要人填的列。返回充电对一键返航，报文为空。
UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(jsonb_set(elem, '{fields}', '[]'::jsonb))
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN ('startRecord', 'stopRecord', 'act-uav-return-charge');

UPDATE ent_protocol_mapping_t1 m
SET custom_fields = jsonb_set(m.custom_fields, '{param_slot_mapping_json}', '[]'::jsonb),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_action_t1 a
WHERE a.deleted = false AND a.tenant_id = 1
  AND a.code IN ('startRecord', 'stopRecord', 'act-uav-return-charge')
  AND a.id::text = (m.custom_fields->'action_ref'->>'id')
  AND m.deleted = false
  AND m.custom_fields->>'mapped_protocol_version' = 'uav-ws';

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT jsonb_agg(
          jsonb_set(
            elem,
            '{fields}',
            '[
              {"fieldCode":"point_focus_camera_type","required":true,"defaultValue":null},
              {"fieldCode":"point_focus_x","required":true,"defaultValue":null},
              {"fieldCode":"point_focus_y","required":true,"defaultValue":null}
            ]'::jsonb
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'focus';

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[
        {"slot":"point_focus_camera_type","path":"data.camera_type"},
        {"slot":"point_focus_x","path":"data.x"},
        {"slot":"point_focus_y","path":"data.y"}
      ]'::jsonb
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND code IN ('map-focus-dji-dock', 'map-focus-dji-pilot');

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
              SELECT COALESCE(jsonb_agg(
                CASE
                  WHEN f->>'fieldCode' = 'payload_position_index'
                    THEN '{"fieldCode":"psdk_index","required":true,"defaultValue":null}'::jsonb
                  ELSE f
                END
              ), '[]'::jsonb)
              FROM jsonb_array_elements(COALESCE(elem->'fields', '[]'::jsonb)) f
            )
          )
        )
        FROM jsonb_array_elements(a.param_slots_json->'methods') elem
      )
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false AND a.tenant_id = 1 AND a.code = 'megaphone';

UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{param_slot_mapping_json}',
      '[
        {"slot":"psdk_index","path":"data.psdk_index"},
        {"slot":"megaphone_file_name","path":"data.file.name"},
        {"slot":"megaphone_file_path","path":"data.file.url"},
        {"slot":"megaphone_file_md5","path":"data.file.md5"}
      ]'::jsonb
    ),
    updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'map-megaphone-dji-dock';

COMMIT;
