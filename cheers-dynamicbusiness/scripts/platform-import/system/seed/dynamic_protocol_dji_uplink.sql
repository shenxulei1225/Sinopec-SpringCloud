-- 大疆机场和 Pilot 的上报按设备推上来的 MQTT 主题各记一条，不按主题里的方法再拆。
-- 飞机定时上报、状态变更、事件上报、设备请求、拓扑更新两边各一条。
-- 机场定时上报和指令飞行上行只给机场，不抄到 Pilot。
-- 服务下发的回执不登记。状态变更和事件只写主题外壳，不写死某种属性或某种事件的正文。
SET search_path TO dynamicbusiness, public;

WITH spec AS (
  SELECT * FROM (VALUES
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-osd-aircraft', '飞机定时上报', 'thing/product/{aircraft_sn}/osd', 'aircraft'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-osd-aircraft', '飞机定时上报', 'thing/product/{aircraft_sn}/osd', 'aircraft'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-osd-dock', '机场定时上报', 'thing/product/{gateway_sn}/osd', 'dock'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-status-update-topo', '拓扑更新', 'sys/product/{gateway_sn}/status', 'topo'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-status-update-topo', '拓扑更新', 'sys/product/{gateway_sn}/status', 'topo'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-state', '状态变更', 'thing/product/{device_sn}/state', 'state'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-state', '状态变更', 'thing/product/{device_sn}/state', 'state'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-events', '事件上报', 'thing/product/{device_sn}/events', 'events'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-events', '事件上报', 'thing/product/{device_sn}/events', 'events'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-requests', '设备请求', 'thing/product/{gateway_sn}/requests', 'requests'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-requests', '设备请求', 'thing/product/{gateway_sn}/requests', 'requests'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-drc-up', '指令飞行上行', 'thing/product/{gateway_sn}/drc/up', 'drc')
  ) AS v(model_code, version, code, name, topic, kind)
),
body AS (
  SELECT 'aircraft' AS kind,
    $p${"timestamp":0,"data":{"gear":0,"mode_code":0,"elevation":0,"horizontal_speed":0,"vertical_speed":0,"home_distance":0,"attitude_head":0,"battery":{"capacity_percent":0,"remain_flight_time":0},"position_state":{"quality":0,"gps_number":0,"rtk_number":0},"storage":{"total":0,"used":0},"cameras":[{"remain_photo_num":0,"remain_record_duration":0,"recording_state":0,"record_time":0}]}}$p$::jsonb AS packet,
    $f$[{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。没有就不填。"},{"code":"data","path":"data","type":"object","label":"状态正文","required":true,"description":"飞机定时上报。没有高度、水平速度、离返航点距离或剩余飞行时间的，不是这一包。"},{"code":"gear","path":"data.gear","type":"integer","label":"挡位","required":false,"description":"按上云枚举。"},{"code":"mode_code","path":"data.mode_code","type":"integer","label":"飞行模式","required":false,"description":"飞机工作模式。不是机场的模式。"},{"code":"elevation","path":"data.elevation","type":"number","label":"相对起飞高度","required":false,"description":null},{"code":"horizontal_speed","path":"data.horizontal_speed","type":"number","label":"水平速度","required":false,"description":null},{"code":"vertical_speed","path":"data.vertical_speed","type":"number","label":"垂直速度","required":false,"description":null},{"code":"home_distance","path":"data.home_distance","type":"number","label":"离返航点距离","required":false,"description":null},{"code":"attitude_head","path":"data.attitude_head","type":"integer","label":"航向","required":false,"description":"0 为正北，顺时针。没有航向就空着，不写成 0。"},{"code":"battery","path":"data.battery","type":"object","label":"电池","required":false,"description":null},{"code":"capacity_percent","path":"data.battery.capacity_percent","type":"integer","label":"电量百分比","required":false,"description":"32767 表示读不到，不当成百分比。"},{"code":"remain_flight_time","path":"data.battery.remain_flight_time","type":"integer","label":"剩余飞行时间","required":false,"description":"秒。"},{"code":"position_state","path":"data.position_state","type":"object","label":"定位","required":false,"description":null},{"code":"quality","path":"data.position_state.quality","type":"integer","label":"定位质量","required":false,"description":null},{"code":"gps_number","path":"data.position_state.gps_number","type":"integer","label":"GPS 星数","required":false,"description":null},{"code":"rtk_number","path":"data.position_state.rtk_number","type":"integer","label":"RTK 星数","required":false,"description":null},{"code":"storage","path":"data.storage","type":"object","label":"存储","required":false,"description":null},{"code":"total","path":"data.storage.total","type":"integer","label":"总容量","required":false,"description":null},{"code":"used","path":"data.storage.used","type":"integer","label":"已用","required":false,"description":null},{"code":"cameras","path":"data.cameras","type":"array","label":"相机","required":false,"description":null},{"code":"remain_photo_num","path":"data.cameras[].remain_photo_num","type":"integer","label":"剩余拍照张数","required":false,"description":null},{"code":"remain_record_duration","path":"data.cameras[].remain_record_duration","type":"integer","label":"剩余录像时长","required":false,"description":"秒。"},{"code":"recording_state","path":"data.cameras[].recording_state","type":"integer","label":"是否在录像","required":false,"description":"1 表示正在录像。"},{"code":"record_time","path":"data.cameras[].record_time","type":"integer","label":"已录时长","required":false,"description":"正在录像时才有。"}]$f$::jsonb AS fields
  UNION ALL
  SELECT 'dock',
    $p${"timestamp":0,"data":{"drone_in_dock":0}}$p$::jsonb,
    $f$[{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。"},{"code":"data","path":"data","type":"object","label":"状态正文","required":true,"description":"机场自己的定时上报。实时画面不拿它当飞机状态。"},{"code":"drone_in_dock","path":"data.drone_in_dock","type":"integer","label":"飞机是否在舱","required":true,"description":"有这个字段才是机场这一包。"}]$f$::jsonb
  UNION ALL
  SELECT 'topo',
    $p${"method":"update_topo","tid":"","bid":"","data":{"sub_devices":[{"sn":""}]}}$p$::jsonb,
    $f$[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"update_topo。"},{"code":"tid","path":"tid","type":"string","label":"事务编号","required":true,"description":"没有事务编号就不回。"},{"code":"bid","path":"bid","type":"string","label":"业务编号","required":true,"description":"没有业务编号就不回。"},{"code":"data","path":"data","type":"object","label":"拓扑正文","required":true,"description":null},{"code":"sub_devices","path":"data.sub_devices","type":"array","label":"子设备","required":false,"description":"当前挂着的飞机。"},{"code":"sn","path":"data.sub_devices[].sn","type":"string","label":"飞机序列号","required":false,"description":"空序列号跳过。多架时不挑第一架。"}]$f$::jsonb
  UNION ALL
  SELECT 'state',
    $p${"tid":"","bid":"","timestamp":0,"data":{}}$p$::jsonb,
    $f$[{"code":"tid","path":"tid","type":"string","label":"事务编号","required":false,"description":null},{"code":"bid","path":"bid","type":"string","label":"业务编号","required":false,"description":null},{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。"},{"code":"data","path":"data","type":"object","label":"变更的属性","required":true,"description":"有变化才推一次。具体属性随设备型号变，不在这条里写死。"}]$f$::jsonb
  UNION ALL
  SELECT 'events',
    $p${"method":"","tid":"","bid":"","timestamp":0,"need_reply":0,"data":{}}$p$::jsonb,
    $f$[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"同一条主题里用方法区分任务进度、告警等。不把某一种事件的正文字段写死。"},{"code":"tid","path":"tid","type":"string","label":"事务编号","required":false,"description":null},{"code":"bid","path":"bid","type":"string","label":"业务编号","required":false,"description":null},{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。"},{"code":"need_reply","path":"need_reply","type":"integer","label":"是否要回复","required":false,"description":"1 表示设备在等云端回复。"},{"code":"data","path":"data","type":"object","label":"事件正文","required":false,"description":"正文跟 method 走，这里不展开。"}]$f$::jsonb
  UNION ALL
  SELECT 'requests',
    $p${"method":"","tid":"","bid":"","timestamp":0,"data":{}}$p$::jsonb,
    $f$[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"许可、组织绑定、上传凭证、航线文件地址都从这条主题上来。云端的回答在 requests_reply，不写在这一包。"},{"code":"tid","path":"tid","type":"string","label":"事务编号","required":true,"description":"没有事务编号就不回。"},{"code":"bid","path":"bid","type":"string","label":"业务编号","required":true,"description":"没有业务编号就不回。"},{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。"},{"code":"data","path":"data","type":"object","label":"请求正文","required":false,"description":"正文跟 method 走，这里不展开。"}]$f$::jsonb
  UNION ALL
  SELECT 'drc',
    $p${"method":"","seq":0,"data":{}}$p$::jsonb,
    $f$[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"指令飞行时设备推上来的。杆量下发不在这一条。"},{"code":"seq","path":"seq","type":"integer","label":"序号","required":false,"description":null},{"code":"data","path":"data","type":"object","label":"上行正文","required":false,"description":"正文跟 method 走，这里不展开。"}]$f$::jsonb
)
INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, command_direction, creator, deleted
)
SELECT
  1, 'data_protocol', m.id, spec.name, spec.code, 1,
  jsonb_build_object(
    'opcode', 0,
    'command_direction', 'uplink',
    'exchange_mode', 'active_uplink',
    'protocol_version', jsonb_build_array(spec.version),
    'communication_protocol', 'mqtt',
    'mqtt_topic', spec.topic,
    'command_json', jsonb_build_object('outbound', body.packet),
    'field_description_json', jsonb_build_object('outbound', body.fields),
    'sample_json', jsonb_build_object('outbound', body.packet)
  ),
  'uplink', 'seed', false
FROM spec
JOIN body ON body.kind = spec.kind
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = spec.model_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_data_protocol_t1 e
  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = spec.code
);

UPDATE ent_data_protocol_t1 e
SET
  name = spec.name,
  model_id = m.id,
  command_direction = 'uplink',
  custom_fields = jsonb_build_object(
    'opcode', 0,
    'command_direction', 'uplink',
    'exchange_mode', 'active_uplink',
    'protocol_version', jsonb_build_array(spec.version),
    'communication_protocol', 'mqtt',
    'mqtt_topic', spec.topic,
    'command_json', jsonb_build_object('outbound', body.packet),
    'field_description_json', jsonb_build_object('outbound', body.fields),
    'sample_json', jsonb_build_object('outbound', body.packet)
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (
  SELECT * FROM (VALUES
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-osd-aircraft', '飞机定时上报', 'thing/product/{aircraft_sn}/osd', 'aircraft'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-osd-aircraft', '飞机定时上报', 'thing/product/{aircraft_sn}/osd', 'aircraft'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-osd-dock', '机场定时上报', 'thing/product/{gateway_sn}/osd', 'dock'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-status-update-topo', '拓扑更新', 'sys/product/{gateway_sn}/status', 'topo'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-status-update-topo', '拓扑更新', 'sys/product/{gateway_sn}/status', 'topo'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-state', '状态变更', 'thing/product/{device_sn}/state', 'state'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-state', '状态变更', 'thing/product/{device_sn}/state', 'state'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-events', '事件上报', 'thing/product/{device_sn}/events', 'events'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-events', '事件上报', 'thing/product/{device_sn}/events', 'events'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-requests', '设备请求', 'thing/product/{gateway_sn}/requests', 'requests'),
    ('data_protocol_dji_pilot', 'dji-pilot-1.16.1', 'proto-dji-pilot-requests', '设备请求', 'thing/product/{gateway_sn}/requests', 'requests'),
    ('data_protocol_dji_dock', 'dji-dock-1.16.1', 'proto-dji-dock-drc-up', '指令飞行上行', 'thing/product/{gateway_sn}/drc/up', 'drc')
  ) AS v(model_code, version, code, name, topic, kind)
) spec
JOIN (
  SELECT 'aircraft' AS kind,
    $p${"timestamp":0,"data":{"gear":0,"mode_code":0,"elevation":0,"horizontal_speed":0,"vertical_speed":0,"home_distance":0,"attitude_head":0,"battery":{"capacity_percent":0,"remain_flight_time":0},"position_state":{"quality":0,"gps_number":0,"rtk_number":0},"storage":{"total":0,"used":0},"cameras":[{"remain_photo_num":0,"remain_record_duration":0,"recording_state":0,"record_time":0}]}}$p$::jsonb AS packet,
    $f$[{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。没有就不填。"},{"code":"data","path":"data","type":"object","label":"状态正文","required":true,"description":"飞机定时上报。没有高度、水平速度、离返航点距离或剩余飞行时间的，不是这一包。"},{"code":"gear","path":"data.gear","type":"integer","label":"挡位","required":false,"description":"按上云枚举。"},{"code":"mode_code","path":"data.mode_code","type":"integer","label":"飞行模式","required":false,"description":"飞机工作模式。不是机场的模式。"},{"code":"elevation","path":"data.elevation","type":"number","label":"相对起飞高度","required":false,"description":null},{"code":"horizontal_speed","path":"data.horizontal_speed","type":"number","label":"水平速度","required":false,"description":null},{"code":"vertical_speed","path":"data.vertical_speed","type":"number","label":"垂直速度","required":false,"description":null},{"code":"home_distance","path":"data.home_distance","type":"number","label":"离返航点距离","required":false,"description":null},{"code":"attitude_head","path":"data.attitude_head","type":"integer","label":"航向","required":false,"description":"0 为正北，顺时针。没有航向就空着，不写成 0。"},{"code":"battery","path":"data.battery","type":"object","label":"电池","required":false,"description":null},{"code":"capacity_percent","path":"data.battery.capacity_percent","type":"integer","label":"电量百分比","required":false,"description":"32767 表示读不到，不当成百分比。"},{"code":"remain_flight_time","path":"data.battery.remain_flight_time","type":"integer","label":"剩余飞行时间","required":false,"description":"秒。"},{"code":"position_state","path":"data.position_state","type":"object","label":"定位","required":false,"description":null},{"code":"quality","path":"data.position_state.quality","type":"integer","label":"定位质量","required":false,"description":null},{"code":"gps_number","path":"data.position_state.gps_number","type":"integer","label":"GPS 星数","required":false,"description":null},{"code":"rtk_number","path":"data.position_state.rtk_number","type":"integer","label":"RTK 星数","required":false,"description":null},{"code":"storage","path":"data.storage","type":"object","label":"存储","required":false,"description":null},{"code":"total","path":"data.storage.total","type":"integer","label":"总容量","required":false,"description":null},{"code":"used","path":"data.storage.used","type":"integer","label":"已用","required":false,"description":null},{"code":"cameras","path":"data.cameras","type":"array","label":"相机","required":false,"description":null},{"code":"remain_photo_num","path":"data.cameras[].remain_photo_num","type":"integer","label":"剩余拍照张数","required":false,"description":null},{"code":"remain_record_duration","path":"data.cameras[].remain_record_duration","type":"integer","label":"剩余录像时长","required":false,"description":"秒。"},{"code":"recording_state","path":"data.cameras[].recording_state","type":"integer","label":"是否在录像","required":false,"description":"1 表示正在录像。"},{"code":"record_time","path":"data.cameras[].record_time","type":"integer","label":"已录时长","required":false,"description":"正在录像时才有。"}]$f$::jsonb AS fields
  UNION ALL
  SELECT 'dock',
    $p${"timestamp":0,"data":{"drone_in_dock":0}}$p$::jsonb,
    $f$[{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。"},{"code":"data","path":"data","type":"object","label":"状态正文","required":true,"description":"机场自己的定时上报。实时画面不拿它当飞机状态。"},{"code":"drone_in_dock","path":"data.drone_in_dock","type":"integer","label":"飞机是否在舱","required":true,"description":"有这个字段才是机场这一包。"}]$f$::jsonb
  UNION ALL
  SELECT 'topo',
    $p${"method":"update_topo","tid":"","bid":"","data":{"sub_devices":[{"sn":""}]}}$p$::jsonb,
    $f$[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"update_topo。"},{"code":"tid","path":"tid","type":"string","label":"事务编号","required":true,"description":"没有事务编号就不回。"},{"code":"bid","path":"bid","type":"string","label":"业务编号","required":true,"description":"没有业务编号就不回。"},{"code":"data","path":"data","type":"object","label":"拓扑正文","required":true,"description":null},{"code":"sub_devices","path":"data.sub_devices","type":"array","label":"子设备","required":false,"description":"当前挂着的飞机。"},{"code":"sn","path":"data.sub_devices[].sn","type":"string","label":"飞机序列号","required":false,"description":"空序列号跳过。多架时不挑第一架。"}]$f$::jsonb
  UNION ALL
  SELECT 'state',
    $p${"tid":"","bid":"","timestamp":0,"data":{}}$p$::jsonb,
    $f$[{"code":"tid","path":"tid","type":"string","label":"事务编号","required":false,"description":null},{"code":"bid","path":"bid","type":"string","label":"业务编号","required":false,"description":null},{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。"},{"code":"data","path":"data","type":"object","label":"变更的属性","required":true,"description":"有变化才推一次。具体属性随设备型号变，不在这条里写死。"}]$f$::jsonb
  UNION ALL
  SELECT 'events',
    $p${"method":"","tid":"","bid":"","timestamp":0,"need_reply":0,"data":{}}$p$::jsonb,
    $f$[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"同一条主题里用方法区分任务进度、告警等。不把某一种事件的正文字段写死。"},{"code":"tid","path":"tid","type":"string","label":"事务编号","required":false,"description":null},{"code":"bid","path":"bid","type":"string","label":"业务编号","required":false,"description":null},{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。"},{"code":"need_reply","path":"need_reply","type":"integer","label":"是否要回复","required":false,"description":"1 表示设备在等云端回复。"},{"code":"data","path":"data","type":"object","label":"事件正文","required":false,"description":"正文跟 method 走，这里不展开。"}]$f$::jsonb
  UNION ALL
  SELECT 'requests',
    $p${"method":"","tid":"","bid":"","timestamp":0,"data":{}}$p$::jsonb,
    $f$[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"许可、组织绑定、上传凭证、航线文件地址都从这条主题上来。云端的回答在 requests_reply，不写在这一包。"},{"code":"tid","path":"tid","type":"string","label":"事务编号","required":true,"description":"没有事务编号就不回。"},{"code":"bid","path":"bid","type":"string","label":"业务编号","required":true,"description":"没有业务编号就不回。"},{"code":"timestamp","path":"timestamp","type":"integer","label":"发送时间","required":false,"description":"毫秒时间戳。"},{"code":"data","path":"data","type":"object","label":"请求正文","required":false,"description":"正文跟 method 走，这里不展开。"}]$f$::jsonb
  UNION ALL
  SELECT 'drc',
    $p${"method":"","seq":0,"data":{}}$p$::jsonb,
    $f$[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"指令飞行时设备推上来的。杆量下发不在这一条。"},{"code":"seq","path":"seq","type":"integer","label":"序号","required":false,"description":null},{"code":"data","path":"data","type":"object","label":"上行正文","required":false,"description":"正文跟 method 走，这里不展开。"}]$f$::jsonb
) body ON body.kind = spec.kind
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = spec.model_code
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = spec.code;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'data_protocol', e.id, NULL, 0, 'seed', false
FROM ent_data_protocol_t1 e
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'cat-protocol-dji-uav'
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'proto-dji-dock-osd-aircraft',
    'proto-dji-pilot-osd-aircraft',
    'proto-dji-dock-osd-dock',
    'proto-dji-dock-status-update-topo',
    'proto-dji-pilot-status-update-topo',
    'proto-dji-dock-state',
    'proto-dji-pilot-state',
    'proto-dji-dock-events',
    'proto-dji-pilot-events',
    'proto-dji-dock-requests',
    'proto-dji-pilot-requests',
    'proto-dji-dock-drc-up'
  )
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'data_protocol'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'opcode', NULL, 0, 'seed', 1, false
FROM ent_data_protocol_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'proto-dji-dock-osd-aircraft',
    'proto-dji-pilot-osd-aircraft',
    'proto-dji-dock-osd-dock',
    'proto-dji-dock-status-update-topo',
    'proto-dji-pilot-status-update-topo',
    'proto-dji-dock-state',
    'proto-dji-pilot-state',
    'proto-dji-dock-events',
    'proto-dji-pilot-events',
    'proto-dji-dock-requests',
    'proto-dji-pilot-requests',
    'proto-dji-dock-drc-up'
  )
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'opcode'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'exchange_mode', 'active_uplink', NULL, 'seed', 1, false
FROM ent_data_protocol_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'proto-dji-dock-osd-aircraft',
    'proto-dji-pilot-osd-aircraft',
    'proto-dji-dock-osd-dock',
    'proto-dji-dock-status-update-topo',
    'proto-dji-pilot-status-update-topo',
    'proto-dji-dock-state',
    'proto-dji-pilot-state',
    'proto-dji-dock-events',
    'proto-dji-pilot-events',
    'proto-dji-dock-requests',
    'proto-dji-pilot-requests',
    'proto-dji-dock-drc-up'
  )
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'exchange_mode'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_version', e.custom_fields->'protocol_version'->>0, NULL, 'seed', 1, false
FROM ent_data_protocol_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND e.code IN (
    'proto-dji-dock-osd-aircraft',
    'proto-dji-pilot-osd-aircraft',
    'proto-dji-dock-osd-dock',
    'proto-dji-dock-status-update-topo',
    'proto-dji-pilot-status-update-topo',
    'proto-dji-dock-state',
    'proto-dji-pilot-state',
    'proto-dji-dock-events',
    'proto-dji-pilot-events',
    'proto-dji-dock-requests',
    'proto-dji-pilot-requests',
    'proto-dji-dock-drc-up'
  )
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'protocol_version'
);
