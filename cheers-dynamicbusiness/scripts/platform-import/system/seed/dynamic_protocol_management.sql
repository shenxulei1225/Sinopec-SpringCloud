-- ============================================================================
-- 数据协议管理目录（全局）
--
-- 目标：
-- 1) 在数据管理中提供「数据协议管理」目录（entity_type = data_protocol）
-- 2) 为协议解析插件预置两列页签字段（字段说明 / 指令 JSON）
--    全型号共用、先入字段库再按型号 CUSTOM 分配；插件认 semantic_type，字段码与页签对齐
-- 3) 报文判别号按型号用协议原名（WebSocket=opcode，Modbus=function_code，IEC104=asdu_type）
-- 4) 实体名称/编码走通用列 name、code，不再另做一套指令名称/指令编码字段
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 1. 分组：业务管理
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_group (
  group_type, code, name, sort, status, tenant_id, creator
)
SELECT 'ENTITY_TYPE', 'ETG-BUSINESS-MGMT', '业务管理', 12, 1, 1, 'seed'
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_group
  WHERE deleted = false
    AND tenant_id = 1
    AND group_type = 'ENTITY_TYPE'
    AND (code = 'ETG-BUSINESS-MGMT' OR name = '业务管理')
);

-- ---------------------------------------------------------------------------
-- 2. 字段库：协议解析语义字段
-- ---------------------------------------------------------------------------
-- 旧字段码改到去前缀名（保持 field_id，避免分配行断裂）
UPDATE dynamic_field
SET
  code = v.new_code,
  semantic_type = v.new_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('protocol_command_schema_json', 'command_json'),
  ('command_schema_json', 'command_json'),
  ('protocol_field_translation_rules_json', 'field_description_json'),
  ('field_translation_rules_json', 'field_description_json'),
  ('ws_opcode', 'opcode'),
  ('modbus_function_code', 'function_code'),
  ('iec104_asdu_type', 'asdu_type')
) AS v(old_code, new_code)
WHERE dynamic_field.tenant_id = 1
  AND dynamic_field.deleted = false
  AND dynamic_field.code = v.old_code
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_field x
    WHERE x.tenant_id = 1 AND x.deleted = false AND x.code = v.new_code
  );

UPDATE dynamic_model_field_assignment a
SET
  field_code = v.new_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('protocol_command_schema_json', 'command_json'),
  ('command_schema_json', 'command_json'),
  ('protocol_field_translation_rules_json', 'field_description_json'),
  ('field_translation_rules_json', 'field_description_json'),
  ('ws_opcode', 'opcode'),
  ('modbus_function_code', 'function_code'),
  ('iec104_asdu_type', 'asdu_type')
) AS v(old_code, new_code)
WHERE a.tenant_id = 1
  AND a.deleted = false
  AND a.field_code = v.old_code
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_field_assignment x
    WHERE x.tenant_id = 1 AND x.deleted = false
      AND x.model_code = a.model_code AND x.field_code = v.new_code
  );

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  (
    'command_json',
    '指令 JSON',
    'LONG_TEXT',
    NULL,
    '按「下发 / 正常响应 / 异常响应」分卷三份空包',
    'SYSTEM',
    1,
    NULL,
    'NONE',
    NULL,
    NULL,
    'command_json',
    1,
    'seed'
  ),
  (
    'field_description_json',
    '字段说明',
    'LONG_TEXT',
    NULL,
    '按同一三卷写字段中文名、类型、是否必填',
    'SYSTEM',
    1,
    NULL,
    'NONE',
    NULL,
    NULL,
    'field_description_json',
    1,
    'seed'
  ),
  (
    'sample_json',
    '样例',
    'LONG_TEXT',
    NULL,
    '按同一三卷写带值联调报文，不替代空包',
    'SYSTEM',
    1,
    NULL,
    'NONE',
    NULL,
    NULL,
    'sample_json',
    1,
    'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  semantic_type = EXCLUDED.semantic_type,
  source = EXCLUDED.source,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 2.1 字段库：各协议型号专用字段
-- 报文判别号用该协议自己的名字；不要发明跨协议 command_code。
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  ('opcode', '指令编码', 'INTEGER', NULL, 'WebSocket 报文里的指令编码（如 200102）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'opcode', 1, 'seed'),
  (
    'command_direction',
    '方向',
    'ENUM',
    NULL,
    '这条指令谁发给谁：下发 / 上报 / 回执',
    'SYSTEM',
    1,
    NULL,
    'BTREE',
    '[{"label":"下发","value":"downlink"},{"label":"上报","value":"uplink"},{"label":"回执","value":"ack"}]',
    NULL,
    'command_direction',
    1,
    'seed'
  ),
  (
    'exchange_mode',
    '交互方式',
    'ENUM',
    NULL,
    '这条指令自己怎么收发。回执和只定义动作内容的指令可不填。2 开头塞进任务包是算步骤时的事，不在这里选。',
    'SYSTEM',
    1,
    NULL,
    'BTREE',
    '[{"label":"同步要结果","value":"sync_wait_result"},{"label":"异步出结果","value":"async_result"},{"label":"主动上报","value":"active_uplink"},{"label":"心跳对回","value":"heartbeat"}]',
    NULL,
    'exchange_mode',
    1,
    'seed'
  ),
  (
    'protocol_version',
    '协议版本',
    'STRING',
    NULL,
    '对接协议版本编码，多个用逗号分隔',
    'SYSTEM',
    1,
    NULL,
    'BTREE',
    NULL,
    NULL,
    'protocol_version',
    1,
    'seed'
  ),
  ('mqtt_topic', 'MQTT主题', 'STRING', NULL, 'MQTT 发布/订阅主题。只写在走 MQTT 的那条指令上，不代表整个型号。', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'mqtt_topic', 1, 'seed'),
  (
    'communication_protocol',
    '通讯方式',
    'ENUM',
    NULL,
    '这条指令走哪种通讯，必须和它所属型号一致。一本型号只有一种。',
    'SYSTEM',
    1,
    NULL,
    'BTREE',
    '[{"label":"MQTT","value":"mqtt"},{"label":"HTTPS","value":"https"},{"label":"WebSocket","value":"websocket"},{"label":"JSBridge","value":"jsbridge"},{"label":"航线文件","value":"wayline"}]',
    NULL,
    'communication_protocol',
    1,
    'seed'
  ),
  (
    'protocol_channel',
    '通道',
    'ENUM',
    NULL,
    '大疆上云里这条 MQTT 指令发到哪个主题。服务下发、指令飞行下行、属性设置、设备请求、事件上报不是同一种下发。',
    'SYSTEM',
    1,
    NULL,
    'BTREE',
    '[{"label":"服务下发","value":"services"},{"label":"指令飞行下行","value":"drc_down"},{"label":"属性设置","value":"property_set"},{"label":"设备请求","value":"requests"},{"label":"事件上报","value":"events"}]',
    NULL,
    'protocol_channel',
    1,
    'seed'
  ),
  ('protocol_qos_level', 'QoS级别', 'INTEGER', NULL, 'MQTT 的 QoS 等级', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_qos_level', 1, 'seed'),
  ('opcua_node_id', 'OPC UA节点ID', 'STRING', NULL, 'OPC UA NodeId', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'opcua_node_id', 1, 'seed'),
  ('opcua_namespace', 'OPC UA命名空间', 'STRING', NULL, 'OPC UA NamespaceIndex 或 URI', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'opcua_namespace', 1, 'seed'),
  ('opcda_item_id', 'OPC DA项ID', 'STRING', NULL, 'OPC DA ItemId', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'opcda_item_id', 1, 'seed'),
  ('modbus_slave_id', 'Modbus从站地址', 'INTEGER', NULL, 'Modbus slave id / unit id', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'modbus_slave_id', 1, 'seed'),
  ('function_code', '功能码', 'INTEGER', NULL, 'Modbus Function Code', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'function_code', 1, 'seed'),
  ('modbus_register_address', 'Modbus寄存器地址', 'INTEGER', NULL, 'Modbus 寄存器地址', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'modbus_register_address', 1, 'seed'),
  ('http_method', 'HTTP方法', 'STRING', NULL, 'HTTP 请求方法（GET/POST/PUT）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'http_method', 1, 'seed'),
  ('http_path', 'HTTP路径', 'STRING', NULL, 'HTTP 路径或资源地址', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'http_path', 1, 'seed'),
  ('bacnet_object_type', 'BACnet对象类型', 'STRING', NULL, 'BACnet Object Type', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'bacnet_object_type', 1, 'seed'),
  ('bacnet_instance_number', 'BACnet实例号', 'INTEGER', NULL, 'BACnet Object Instance Number', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'bacnet_instance_number', 1, 'seed'),
  ('asdu_type', '类型标识', 'INTEGER', NULL, 'IEC 60870-5-104 Type Identification', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'asdu_type', 1, 'seed'),
  ('iec61850_ln_ref', 'IEC61850逻辑节点', 'STRING', NULL, 'IEC61850 逻辑节点引用', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'iec61850_ln_ref', 1, 'seed'),
  ('dnp3_point_index', 'DNP3点位索引', 'INTEGER', NULL, 'DNP3 Point Index', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'dnp3_point_index', 1, 'seed'),
  ('dlt645_data_identifier', 'DL/T645数据标识', 'STRING', NULL, 'DL/T645 数据标识符', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'dlt645_data_identifier', 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  semantic_type = EXCLUDED.semantic_type,
  source = EXCLUDED.source,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 停用跨协议绰号字段（名称/编码走实体通用列；协议类型由型号表达）
UPDATE dynamic_field
SET
  status = 0,
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code IN (
    'protocol_type_code',
    'protocol_command_code',
    'protocol_command_name',
    'protocol_transport_layer',
    'protocol_endpoint',
    'protocol_payload_format',
    'protocol_poll_interval_ms',
    'protocol_command_schema_json',
    'protocol_field_translation_rules_json',
    'protocol_step_signal_mapping_rules_json',
    'protocol_sample_messages_json',
    'ws_opcode',
    'modbus_function_code',
    'iec104_asdu_type'
  );

-- ---------------------------------------------------------------------------
-- 3. 数据目录（实体类型）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_entity_type (
  code,
  name,
  parent_id,
  description,
  icon,
  alias,
  sort,
  status,
  type_level,
  association_fields,
  storage_type,
  dedicated_table_name,
  enable_rule_engine,
  physical_column_mapping,
  work_scope,
  tenant_id,
  creator,
  entry_kind,
  group_name
) VALUES (
  'data_protocol',
  '数据协议管理',
  NULL,
  '统一维护多协议类型的指令包结构、字段翻译映射与步骤信号映射',
  'ep:connection',
  '协议',
  32,
  'active',
  'USER',
  '{}',
  'DEDICATED',
  'ent_data_protocol_t1',
  false,
  '{"fieldLabels":{"name":"指令名称","code":"识别码","command_direction":"方向","protocol_version":"协议版本","communication_protocol":"通讯方式"},"command_direction":{"column":"command_direction","type":"VARCHAR","length":32},"protocol_version":{"column":"protocol_version","type":"JSONB"},"communication_protocol":{"column":"communication_protocol","type":"VARCHAR","length":32}}',
  'NETWORK',
  1,
  'seed',
  'NATIVE',
  '业务管理'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = 'active',
  entry_kind = 'NATIVE',
  group_name = '业务管理',
  dedicated_table_name = 'ent_data_protocol_t1',
  storage_type = 'DEDICATED',
  work_scope = 'NETWORK',
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code,
  name,
  storage_type,
  dedicated_table_name,
  enable_rule_engine,
  description,
  status,
  tenant_id,
  creator
) VALUES (
  'data_protocol',
  '数据协议管理',
  'DEDICATED',
  'ent_data_protocol_t1',
  false,
  '协议录入与解析预览目录',
  1,
  1,
  'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 4. 类型基础字段只挂专用表真实物理列
--    方向、协议版本、通讯方式是全型号同一套台账列，必须走 BASE + 专用表列。
--    字段说明 / 指令 JSON / 样例、以及 opcode、交互方式仍走型号分配。
--    航线文件和 JSBridge 不分配这三份 JSON。
--    全网目录不挂 facility_id（表上没有该列；工作范围已是 NETWORK）。
-- ---------------------------------------------------------------------------
UPDATE dynamic_entity_type_base_field
SET
  status = 0,
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'data_protocol'
  AND deleted = false
  AND field_code IN (
    'facility_id',
    'protocol_type_code',
    'protocol_command_code',
    'protocol_command_name',
    'protocol_transport_layer',
    'protocol_endpoint',
    'protocol_payload_format',
    'protocol_poll_interval_ms',
    'protocol_qos_level',
    'protocol_command_schema_json',
    'protocol_field_translation_rules_json',
    'protocol_step_signal_mapping_rules_json',
    'protocol_sample_messages_json',
    'command_json',
    'field_description_json',
    'sample_json'
  );

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'data_protocol',
  f.id,
  'command_direction',
  '方向',
  'ENUM',
  false,
  '这条指令谁发给谁：下发 / 上报 / 回执。对象栏按此列筛选。',
  NULL,
  20,
  1,
  true,
  true,
  true,
  1,
  'seed'
FROM dynamic_field f
WHERE f.code = 'command_direction'
  AND f.deleted = false
  AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = 1,
  deleted = false,
  is_searchable = true,
  is_filterable = true,
  is_sortable = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'data_protocol',
  f.id,
  v.field_code,
  v.field_name,
  v.data_type,
  false,
  v.description,
  NULL,
  v.sort_order,
  1,
  true,
  true,
  v.is_sortable,
  1,
  'seed'
FROM (
  VALUES
    ('protocol_version', '协议版本', 'STRING', '对接协议。对象栏按此列筛选。值是数组，如 ["dji-dock-1.16.1"]。', 16, false),
    ('communication_protocol', '通讯方式', 'ENUM', '这条指令走 MQTT、HTTPS、WebSocket、JSBridge 或航线文件。对象栏按此列筛选。', 18, true)
) AS v(field_code, field_name, data_type, description, sort_order, is_sortable)
JOIN dynamic_field f
  ON f.code = v.field_code
 AND f.deleted = false
 AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = 1,
  deleted = false,
  is_searchable = true,
  is_filterable = true,
  is_sortable = EXCLUDED.is_sortable,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 5. 默认型号 + 模型字段
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_model (
  code, name, entity_type_code, domain, description, status, sort, tenant_id, creator
) VALUES
  ('data_protocol_ws', '无人机协议 WebSocket', 'data_protocol', NULL, '无人机对接协议的 WebSocket 指令', 1, 10, 1, 'seed'),
  ('data_protocol_robot', '机器人协议 WebSocket', 'data_protocol', NULL, '机器人对接协议的 WebSocket 指令', 1, 11, 1, 'seed'),
  ('data_protocol_dji_dock', '大疆机场 MQTT', 'data_protocol', NULL, '大疆机场走 MQTT 的指令', 1, 12, 1, 'seed'),
  ('data_protocol_dji_pilot', '大疆 Pilot MQTT', 'data_protocol', NULL, '大疆 Pilot 走 MQTT 的指令', 1, 13, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  m.code,
  f.code,
  false,
  v.is_searchable,
  v.is_filterable,
  v.is_sortable,
  v.sort_order,
  'CUSTOM',
  1,
  'seed'
FROM dynamic_model m
JOIN (
  VALUES
    ('command_json', 300, false, false, false),
    ('field_description_json', 310, false, false, false),
    ('sample_json', 320, false, false, false)
) AS v(field_code, sort_order, is_searchable, is_filterable, is_sortable)
  ON 1 = 1
JOIN dynamic_field f
  ON f.code = v.field_code
 AND f.deleted = false
 AND f.tenant_id = 1
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code IN (
    'data_protocol_ws',
    'data_protocol_robot',
    'data_protocol_dji_dock',
    'data_protocol_dji_pilot'
  )
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  m.code,
  f.code,
  false,
  true,
  true,
  false,
  mapping.sort_order,
  'CUSTOM',
  1,
  'seed'
FROM (
  VALUES
    ('data_protocol_ws', 'opcode', 10),
    ('data_protocol_ws', 'exchange_mode', 14),
    ('data_protocol_robot', 'opcode', 10),
    ('data_protocol_robot', 'exchange_mode', 14),
    ('data_protocol_dji_dock', 'opcode', 10),
    ('data_protocol_dji_dock', 'exchange_mode', 14),
    ('data_protocol_dji_dock', 'protocol_channel', 19),
    ('data_protocol_dji_dock', 'mqtt_topic', 21),
    ('data_protocol_dji_pilot', 'opcode', 10),
    ('data_protocol_dji_pilot', 'exchange_mode', 14),
    ('data_protocol_dji_pilot', 'protocol_channel', 19),
    ('data_protocol_dji_pilot', 'mqtt_topic', 21)
) AS mapping(model_code, field_code, sort_order)
JOIN dynamic_model m
  ON m.deleted = false
 AND m.tenant_id = 1
 AND m.code = mapping.model_code
JOIN dynamic_field f
  ON f.deleted = false
 AND f.tenant_id = 1
 AND f.code = mapping.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  m.code,
  'command_direction',
  false,
  true,
  true,
  true,
  12,
  'BASE',
  1,
  'seed'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.deleted = false
 AND f.tenant_id = 1
 AND f.code = 'command_direction'
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'data_protocol%'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  is_searchable = true,
  is_filterable = true,
  is_sortable = true,
  sort = EXCLUDED.sort,
  field_source = 'BASE',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 协议版本、通讯方式：全型号同一套台账列，对象栏要展示和筛选。
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  m.code,
  v.field_code,
  false,
  true,
  true,
  v.is_sortable,
  v.sort_order,
  'BASE',
  1,
  'seed'
FROM dynamic_model m
JOIN (
  VALUES
    ('protocol_version', 16, false),
    ('communication_protocol', 18, true)
) AS v(field_code, sort_order, is_sortable)
  ON 1 = 1
JOIN dynamic_field f
  ON f.deleted = false
 AND f.tenant_id = 1
 AND f.code = v.field_code
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'data_protocol%'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  is_searchable = true,
  is_filterable = true,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = 'BASE',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 收回误挂到所有型号的旧检索字段
UPDATE dynamic_model_field_assignment
SET
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND model_code LIKE 'data_protocol%'
  AND field_code IN (
    'protocol_type_code',
    'protocol_command_code',
    'protocol_command_name',
    'protocol_transport_layer',
    'protocol_endpoint',
    'protocol_payload_format',
    'protocol_poll_interval_ms',
    'protocol_qos_level',
    'ws_opcode',
    'modbus_function_code',
    'iec104_asdu_type',
    'protocol_command_schema_json',
    'protocol_field_translation_rules_json',
    'protocol_step_signal_mapping_rules_json',
    'protocol_sample_messages_json',
    'command_schema_json',
    'field_translation_rules_json',
    'step_signal_mapping_rules_json'
  );

UPDATE dynamic_field
SET
  deleted = true,
  status = 0,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code IN (
    'step_signal_mapping_rules_json',
    'command_schema_json',
    'field_translation_rules_json'
  );

UPDATE dynamic_field
SET
  code = 'sample_json',
  name = '样例',
  semantic_type = 'sample_json',
  description = '按同一三卷写带值联调报文，不替代空包',
  deleted = false,
  status = 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code IN ('sample_messages_json', 'protocol_sample_messages_json')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_field x
    WHERE x.tenant_id = 1 AND x.deleted = false AND x.code = 'sample_json'
  );

UPDATE dynamic_model_field_assignment
SET
  field_code = 'sample_json',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND field_code IN ('sample_messages_json', 'protocol_sample_messages_json')
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_field_assignment x
    WHERE x.tenant_id = 1 AND x.deleted = false
      AND x.model_code = dynamic_model_field_assignment.model_code
      AND x.field_code = 'sample_json'
  );

-- ---------------------------------------------------------------------------
-- 6. 目录可用性补齐（分类类型 + 编排 + 数据页布局绑定）
-- ---------------------------------------------------------------------------
-- 6.1 顶级分类节点（供数据目录树与分类栏消费）
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, tree_path, level, sort, status,
  description, tenant_id, creator, parent_code
)
SELECT
  NULL,
  '数据协议管理',
  'data_protocol_root',
  'data_protocol',
  '/0/',
  1,
  0,
  1,
  '数据协议管理顶级分类',
  1,
  'seed',
  NULL
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_category
  WHERE deleted = false
    AND tenant_id = 1
    AND code = 'data_protocol_root'
);

UPDATE dynamic_category
SET tree_path = '/' || id || '/',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'data_protocol_root'
  AND tree_path = '/0/';

-- 6.2 分类类型
INSERT INTO dynamic_category_type (
  category_type_code, name, description, status, top_level_category_id, tenant_id, creator
)
SELECT
  'data_protocol',
  '数据协议管理',
  '数据协议管理默认分类',
  1,
  c.id,
  1,
  'seed'
FROM dynamic_category c
WHERE c.deleted = false
  AND c.tenant_id = 1
  AND c.code = 'data_protocol_root'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_category_type ct
    WHERE ct.deleted = false
      AND ct.tenant_id = 1
      AND ct.category_type_code = 'data_protocol'
  );

UPDATE dynamic_category_type ct
SET top_level_category_id = c.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_category c
WHERE ct.deleted = false
  AND ct.tenant_id = 1
  AND ct.category_type_code = 'data_protocol'
  AND c.deleted = false
  AND c.tenant_id = 1
  AND c.code = 'data_protocol_root'
  AND ct.top_level_category_id IS DISTINCT FROM c.id;

-- 6.3 编排头（默认列表行选中）
INSERT INTO dm_catalog_orchestration (
  entity_type_code, enabled, selection_source, tenant_id, creator
)
SELECT
  'data_protocol',
  true,
  'LIST_ROW',
  1,
  'seed'
WHERE NOT EXISTS (
  SELECT 1
  FROM dm_catalog_orchestration
  WHERE deleted = false
    AND tenant_id = 1
    AND entity_type_code = 'data_protocol'
);

-- 6.4 若 data_layout_id 为空，优先回挂已有 data_protocol 布局
UPDATE dynamic_entity_type et
SET data_layout_id = x.layout_id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT layout_id
  FROM dm_data_tab_layout
  WHERE deleted = false
    AND tenant_id = 1
    AND entity_type_code = 'data_protocol'
  ORDER BY id
  LIMIT 1
) x
WHERE et.deleted = false
  AND et.tenant_id = 1
  AND et.code = 'data_protocol'
  AND et.data_layout_id IS NULL;

-- 6.5 仍为空时，创建默认布局头并绑定
INSERT INTO dm_workbench_layout (name, is_template, source_template_id, tenant_id, creator, settings_json)
SELECT
  '数据页签·data_protocol',
  false,
  1,
  1,
  'seed',
  '{"sections":[{"id":"filter","name":"筛选","arrange":"horizontal"},{"id":"who","name":"对象","arrange":"horizontal"},{"id":"what","name":"详情","arrange":"free"}],"sectionHidden":{}}'::jsonb
WHERE EXISTS (
  SELECT 1
  FROM dynamic_entity_type
  WHERE deleted = false
    AND tenant_id = 1
    AND code = 'data_protocol'
    AND data_layout_id IS NULL
)
  AND NOT EXISTS (
    SELECT 1
    FROM dm_workbench_layout
    WHERE deleted = false
      AND tenant_id = 1
      AND name = '数据页签·data_protocol'
  );

UPDATE dynamic_entity_type et
SET data_layout_id = w.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dm_workbench_layout w
WHERE et.deleted = false
  AND et.tenant_id = 1
  AND et.code = 'data_protocol'
  AND et.data_layout_id IS NULL
  AND w.deleted = false
  AND w.tenant_id = 1
  AND w.name = '数据页签·data_protocol';

-- 6.6 布局栏骨架（无现成行时补齐）
INSERT INTO dm_data_tab_layout (
  tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled, column_meta, layout_id, creator
)
SELECT
  1,
  'data_protocol',
  v.column_kind,
  v.tab_id,
  NULL,
  true,
  v.column_meta::jsonb,
  et.data_layout_id,
  'seed'
FROM dynamic_entity_type et
CROSS JOIN (
  VALUES
    ('CATEGORY', 'category-1', '{"label":"数据协议管理","columnSection":"filter","categoryTypeCode":"data_protocol"}'),
    ('MODEL', 'data_protocol', '{"label":"数据协议管理","columnSection":"filter","modelEntityTypeCode":"data_protocol"}'),
    ('ENTITY', 'data_protocol', '{"label":"数据协议管理","columnSection":"who","entityEntityTypeCode":"data_protocol"}'),
    ('DETAIL', 'detail', '{"label":"详情","columnSection":"what","entityEntityTypeCode":"data_protocol"}')
) AS v(column_kind, tab_id, column_meta)
WHERE et.deleted = false
  AND et.tenant_id = 1
  AND et.code = 'data_protocol'
  AND et.data_layout_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_layout d
    WHERE d.deleted = false
      AND d.layout_id = et.data_layout_id
  );

-- 筛选区同时放分类栏和型号栏时，避免区段宽度只够分类栏把型号栏挤没
UPDATE dm_data_tab_layout
SET column_meta = jsonb_set(COALESCE(column_meta, '{}'::jsonb), '{sectionWidthPx}', '448'),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'data_protocol'
  AND column_kind = 'CATEGORY'
  AND COALESCE(column_meta->>'sectionWidthPx', '') IN ('', '240');

-- ---------------------------------------------------------------------------
-- 7. 业务分类 + 型号挂接 + 示例协议实体（租户 1 物理表）
-- 直接 psql 导入后必须 evict 分类树 Redis 缓存，否则会继续显示旧的空树。
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, tree_path, level, sort, status,
  description, tenant_id, creator, parent_code
)
SELECT
  p.id,
  v.name,
  v.code,
  'data_protocol',
  p.tree_path,
  COALESCE(p.level, 1) + 1,
  v.sort,
  1,
  v.description,
  1,
  'seed',
  p.code
FROM dynamic_category p
JOIN (
  VALUES
    ('cat-protocol-dji-uav', '大疆无人机', 10, '大疆机场与大疆 Pilot'),
    ('cat-protocol-donggu-uav', '东古无人机', 20, '无人机对接协议')
) AS v(code, name, sort, description)
  ON p.deleted = false
 AND p.tenant_id = 1
 AND p.code = 'cat-protocol-uav'
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_category c
  WHERE c.deleted = false
    AND c.tenant_id = 1
    AND c.category_type_code = 'data_protocol'
    AND (c.code = v.code OR c.name = v.name)
);

-- 界面上已经建过的同名分类，收成稳定编码，避免再插一份。
UPDATE dynamic_category c
SET code = v.code,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  VALUES
    ('cat-protocol-dji-uav', '大疆无人机'),
    ('cat-protocol-donggu-uav', '东古无人机')
) AS v(code, name)
WHERE c.deleted = false
  AND c.tenant_id = 1
  AND c.category_type_code = 'data_protocol'
  AND c.name = v.name
  AND c.code <> v.code
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_category other
    WHERE other.deleted = false
      AND other.tenant_id = 1
      AND other.code = v.code
  );

UPDATE dynamic_category c
SET tree_path = p.tree_path || c.id || '/',
    level = COALESCE(p.level, 1) + 1,
    parent_code = p.code,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_category p
WHERE c.deleted = false
  AND c.tenant_id = 1
  AND c.category_type_code = 'data_protocol'
  AND c.code LIKE 'cat-protocol-%'
  AND p.deleted = false
  AND p.tenant_id = 1
  AND p.id = c.parent_id
  AND (c.tree_path IS NULL OR c.tree_path = p.tree_path OR c.tree_path NOT LIKE p.tree_path || c.id || '/%');

UPDATE dynamic_model_category_relation_t1 r
SET category_code = c.code,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_category c
WHERE r.deleted = false
  AND r.tenant_id = 1
  AND r.entity_type_code = 'data_protocol'
  AND r.category_id = c.id
  AND r.category_code IS DISTINCT FROM c.code
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_category_relation_t1 other
    WHERE other.deleted = false
      AND other.tenant_id = r.tenant_id
      AND other.entity_type_code = r.entity_type_code
      AND other.model_code = r.model_code
      AND other.category_code = c.code
      AND other.id <> r.id
  );

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code, deleted
)
SELECT
  m.id,
  c.id,
  'data_protocol',
  m.sort,
  'seed',
  1,
  m.code,
  c.code,
  false
FROM dynamic_model m
JOIN (
  VALUES
    ('data_protocol_ws', 'cat-protocol-donggu-uav'),
    ('data_protocol_robot', 'cat-protocol-robot'),
    ('data_protocol_dji_dock', 'cat-protocol-dji-uav'),
    ('data_protocol_dji_pilot', 'cat-protocol-dji-uav')
) AS map(model_code, category_code)
  ON m.deleted = false
 AND m.tenant_id = 1
 AND m.entity_type_code = 'data_protocol'
 AND m.code = map.model_code
JOIN dynamic_category c
  ON c.deleted = false
 AND c.tenant_id = 1
 AND c.code = map.category_code
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_model_category_relation_t1 r
  WHERE r.deleted = false
    AND r.tenant_id = 1
    AND r.model_code = m.code
    AND r.category_code = c.code
    AND r.entity_type_code = 'data_protocol'
);

INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
  1,
  'data_protocol',
  m.id,
  v.name,
  v.code,
  1,
  v.custom_fields::jsonb,
  'seed',
  false
FROM dynamic_model m
JOIN (
  VALUES
    (
      'data_protocol_ws',
      'proto-sample-ws-heartbeat',
      '示例·WebSocket心跳指令',
      '{"opcode":1,"communication_protocol":"websocket","command_schema_json":{"type":"object","properties":{"type":{"type":"string"},"ts":{"type":"number"}}},"field_translation_rules_json":[{"from":"type","label":"消息类型"},{"from":"ts","label":"时间戳"}],"step_signal_mapping_rules_json":[{"when":{"type":"heartbeat"},"stepRefType":"NODE_KEY","stepRef":"n-connect","status":"RUNNING"}],"sample_messages_json":[{"type":"heartbeat","ts":1710000000}]}'
    )
) AS v(model_code, code, name, custom_fields)
  ON m.deleted = false
 AND m.tenant_id = 1
 AND m.code = v.model_code
WHERE NOT EXISTS (
  SELECT 1
  FROM ent_data_protocol_t1 e
  WHERE e.deleted = false
    AND e.tenant_id = 1
    AND e.code = v.code
);

UPDATE ent_data_protocol_t1 e
SET
  name = v.name,
  custom_fields = v.custom_fields::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (
  VALUES
    ('proto-sample-ws-heartbeat', '示例·WebSocket心跳指令', '{"opcode":1,"communication_protocol":"websocket","command_schema_json":{"type":"object","properties":{"type":{"type":"string"},"ts":{"type":"number"}}},"field_translation_rules_json":[{"from":"type","label":"消息类型"},{"from":"ts","label":"时间戳"}],"step_signal_mapping_rules_json":[{"when":{"type":"heartbeat"},"stepRefType":"NODE_KEY","stepRef":"n-connect","status":"RUNNING"}],"sample_messages_json":[{"type":"heartbeat","ts":1710000000}]}')
) AS v(code, name, custom_fields)
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.code = v.code;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT
  1,
  c.id,
  'data_protocol',
  e.id,
  NULL,
  0,
  'seed',
  false
FROM ent_data_protocol_t1 e
JOIN (
  VALUES
    ('proto-sample-ws-heartbeat', 'cat-protocol-donggu-uav')
) AS map(entity_code, category_code)
  ON e.deleted = false
 AND e.tenant_id = 1
 AND e.code = map.entity_code
JOIN dynamic_category c
  ON c.deleted = false
 AND c.tenant_id = 1
 AND c.code = map.category_code
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_entity_category_relation_t1 r
  WHERE r.deleted = false
    AND r.tenant_id = 1
    AND r.entity_type_code = 'data_protocol'
    AND r.entity_id = e.id
    AND r.category_id = c.id
);

-- 可搜索字段写入租户索引表（与实体重存同步口径一致，禁止读路径补索引）
INSERT INTO dynamic_entity_field_index_t1 (
  entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted
)
SELECT
  e.id,
  e.model_id,
  f.field_code,
  f.value_string,
  f.value_number,
  'seed',
  1,
  false
FROM ent_data_protocol_t1 e
JOIN (
  VALUES
    ('proto-sample-ws-heartbeat', 'opcode', NULL, 1::numeric),
    ('proto-sample-ws-heartbeat', 'communication_protocol', 'websocket', NULL::numeric)
) AS f(entity_code, field_code, value_string, value_number)
  ON e.deleted = false
 AND e.tenant_id = 1
 AND e.code = f.entity_code
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false
    AND idx.tenant_id = 1
    AND idx.entity_id = e.id
    AND idx.field_code = f.field_code
);

UPDATE dynamic_entity_field_index_t1 idx
SET
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
WHERE idx.deleted = false
  AND idx.tenant_id = 1
  AND e.id = idx.entity_id
  AND e.tenant_id = 1
  AND e.deleted = false
  AND e.entity_type_code = 'data_protocol'
  AND idx.field_code IN (
    'protocol_command_code',
    'protocol_command_name',
    'protocol_type_code',
    'ws_opcode',
    'modbus_function_code',
    'iec104_asdu_type',
    'http_path'
  );

-- 通讯协议不再单独占型号。重跑本文件不得把这些型号和空分类加回来。
UPDATE dynamic_model
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code IN (
    'data_protocol_standard',
    'data_protocol_mqtt',
    'data_protocol_http',
    'data_protocol_opcua',
    'data_protocol_opcda',
    'data_protocol_modbus',
    'data_protocol_bacnet',
    'data_protocol_iec104',
    'data_protocol_iec61850',
    'data_protocol_dnp3',
    'data_protocol_dlt645'
  );

UPDATE ent_data_protocol_t1 e
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.model_id = m.id
  AND m.tenant_id = 1
  AND m.code IN (
    'data_protocol_standard',
    'data_protocol_mqtt',
    'data_protocol_http',
    'data_protocol_opcua',
    'data_protocol_opcda',
    'data_protocol_modbus',
    'data_protocol_bacnet',
    'data_protocol_iec104',
    'data_protocol_iec61850',
    'data_protocol_dnp3',
    'data_protocol_dlt645'
  );

UPDATE dynamic_model_category_relation_t1
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND entity_type_code = 'data_protocol'
  AND (
    model_code IN (
      'data_protocol_standard',
      'data_protocol_mqtt',
      'data_protocol_http',
      'data_protocol_opcua',
      'data_protocol_opcda',
      'data_protocol_modbus',
      'data_protocol_bacnet',
      'data_protocol_iec104',
      'data_protocol_iec61850',
      'data_protocol_dnp3',
      'data_protocol_dlt645'
    )
    OR (
      model_code IN ('data_protocol_dji_dock', 'data_protocol_dji_pilot', 'data_protocol_ws')
      AND category_code = 'cat-protocol-uav'
    )
    OR (model_code = 'data_protocol_ws' AND category_code = 'cat-protocol-gs')
  );

UPDATE dynamic_category
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND category_type_code = 'data_protocol'
  AND code IN (
    'cat-protocol-generic',
    'cat-protocol-iot',
    'cat-protocol-industrial',
    'cat-protocol-power',
    'cat-protocol-metering'
  );

-- 对象栏列表：默认展示名称+方向，并打开方向筛选（展示列只认系统+类型基础字段）
UPDATE platformresource.pr_component_props p
SET
  props = (
    jsonb_set(
      jsonb_set(
        jsonb_set(
          COALESCE(NULLIF(btrim(p.props), '')::jsonb, '{}'::jsonb),
          '{displayContent}',
          '["name","command_direction","protocol_version","communication_protocol"]'::jsonb,
          true
        ),
        '{filter,externalFilterEnabled}',
        'true'::jsonb,
        true
      ),
      '{filter,filterSelectedOptions}',
      '["command_direction","protocol_version","communication_protocol"]'::jsonb,
      true
    )
  )::text,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dm_data_tab_layout d
JOIN dynamic_entity_type et
  ON et.deleted = false
 AND et.tenant_id = 1
 AND et.code = 'data_protocol'
 AND et.data_layout_id = d.layout_id
WHERE p.deleted = false
  AND d.deleted = false
  AND d.tenant_id = 1
  AND d.column_kind = 'ENTITY'
  AND d.props_id = p.id
  AND p.component_code = 'list';

