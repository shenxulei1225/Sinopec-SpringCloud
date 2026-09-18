-- ============================================================================
-- data_protocol 协议矩阵与检索字段标准化
--
-- 目标：
-- 1) data_protocol 统一为全网目录（work_scope = NETWORK）
-- 2) 补齐协议类型矩阵模型（WS/MQTT/HTTP/OPC/Modbus/BACnet/IEC/DNP3）
-- 3) 字段按“字段库 -> 基础字段/模型字段分配”标准路径落库
-- 4) 可检索字段显式开启 is_searchable，纳入动态字段索引同步链路
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 目录作用域：全网
UPDATE dynamic_entity_type
SET work_scope = 'NETWORK',
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'data_protocol';

-- 2) 字段库补齐（检索元字段 + 协议专用字段）
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  ('protocol_type_code', '协议类型', 'STRING', NULL, '协议类型编码（如 WS/MQTT/OPCUA/MODBUS）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_type_code', 1, 'flyway'),
  ('protocol_command_code', '指令编码', 'STRING', NULL, '协议指令编码（设备侧唯一标识）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_command_code', 1, 'flyway'),
  ('protocol_command_name', '指令名称', 'STRING', NULL, '协议指令名称（业务可读）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_command_name', 1, 'flyway'),
  ('protocol_transport_layer', '传输层', 'STRING', NULL, '传输层协议（TCP/UDP/WebSocket 等）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_transport_layer', 1, 'flyway'),
  ('protocol_endpoint', '接入端点', 'STRING', NULL, '协议接入端点（topic/path/node 等）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_endpoint', 1, 'flyway'),
  ('protocol_payload_format', '报文格式', 'STRING', NULL, '报文编码格式（JSON/BINARY/XML）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_payload_format', 1, 'flyway'),
  ('protocol_poll_interval_ms', '轮询间隔(ms)', 'INTEGER', 'ms', '轮询类协议的采集间隔', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_poll_interval_ms', 1, 'flyway'),
  ('protocol_qos_level', 'QoS级别', 'INTEGER', NULL, 'MQTT 等协议的 QoS 等级', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'protocol_qos_level', 1, 'flyway'),
  ('ws_opcode', 'WebSocket操作码', 'INTEGER', NULL, 'WebSocket 帧 opcode', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'ws_opcode', 1, 'flyway'),
  ('mqtt_topic', 'MQTT主题', 'STRING', NULL, 'MQTT 发布/订阅主题', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'mqtt_topic', 1, 'flyway'),
  ('opcua_node_id', 'OPC UA节点ID', 'STRING', NULL, 'OPC UA NodeId', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'opcua_node_id', 1, 'flyway'),
  ('opcua_namespace', 'OPC UA命名空间', 'STRING', NULL, 'OPC UA NamespaceIndex 或 URI', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'opcua_namespace', 1, 'flyway'),
  ('opcda_item_id', 'OPC DA项ID', 'STRING', NULL, 'OPC DA ItemId', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'opcda_item_id', 1, 'flyway'),
  ('modbus_slave_id', 'Modbus从站地址', 'INTEGER', NULL, 'Modbus slave id / unit id', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'modbus_slave_id', 1, 'flyway'),
  ('modbus_function_code', 'Modbus功能码', 'INTEGER', NULL, 'Modbus Function Code', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'modbus_function_code', 1, 'flyway'),
  ('modbus_register_address', 'Modbus寄存器地址', 'INTEGER', NULL, 'Modbus 寄存器地址', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'modbus_register_address', 1, 'flyway'),
  ('http_method', 'HTTP方法', 'STRING', NULL, 'HTTP 请求方法（GET/POST/PUT）', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'http_method', 1, 'flyway'),
  ('http_path', 'HTTP路径', 'STRING', NULL, 'HTTP 路径或资源地址', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'http_path', 1, 'flyway'),
  ('bacnet_object_type', 'BACnet对象类型', 'STRING', NULL, 'BACnet Object Type', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'bacnet_object_type', 1, 'flyway'),
  ('bacnet_instance_number', 'BACnet实例号', 'INTEGER', NULL, 'BACnet Object Instance Number', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'bacnet_instance_number', 1, 'flyway'),
  ('iec104_asdu_type', 'IEC104 ASDU类型', 'INTEGER', NULL, 'IEC104 ASDU Type ID', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'iec104_asdu_type', 1, 'flyway'),
  ('iec61850_ln_ref', 'IEC61850逻辑节点', 'STRING', NULL, 'IEC61850 逻辑节点引用', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'iec61850_ln_ref', 1, 'flyway'),
  ('dnp3_point_index', 'DNP3点位索引', 'INTEGER', NULL, 'DNP3 Point Index', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'dnp3_point_index', 1, 'flyway'),
  ('dlt645_data_identifier', 'DL/T645数据标识', 'STRING', NULL, 'DL/T645 数据标识符', 'SYSTEM', 1, NULL, 'BTREE', NULL, NULL, 'dlt645_data_identifier', 1, 'flyway')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  semantic_type = EXCLUDED.semantic_type,
  source = EXCLUDED.source,
  status = 1,
  deleted = false,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP;

-- 3) 类型基础字段（目录通用检索字段）
INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'data_protocol',
  f.id,
  v.field_code,
  v.field_name,
  v.data_type,
  false,
  v.default_value,
  v.description,
  '{"createVisible":true}'::jsonb,
  v.sort_order,
  1,
  1,
  'flyway'
FROM dynamic_field f
JOIN (
  VALUES
    ('protocol_type_code', '协议类型', 'TEXT', '', '协议类型编码（支持目录检索）', 20),
    ('protocol_command_code', '指令编码', 'TEXT', '', '设备协议指令编码', 30),
    ('protocol_command_name', '指令名称', 'TEXT', '', '业务可读的指令名称', 40),
    ('protocol_transport_layer', '传输层', 'TEXT', '', 'TCP/UDP/WebSocket 等', 50),
    ('protocol_endpoint', '接入端点', 'TEXT', '', 'topic/path/node 等接入标识', 60),
    ('protocol_payload_format', '报文格式', 'TEXT', '', 'JSON/BINARY/XML', 70),
    ('protocol_poll_interval_ms', '轮询间隔(ms)', 'NUMBER', '0', '轮询类协议采集间隔', 80),
    ('protocol_qos_level', 'QoS级别', 'NUMBER', '0', 'MQTT 等协议 QoS', 90)
) AS v(field_code, field_name, data_type, default_value, description, sort_order)
  ON f.code = v.field_code
 AND f.deleted = false
 AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  default_value = EXCLUDED.default_value,
  description = EXCLUDED.description,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  deleted = false,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP;

-- 4) 协议模型矩阵（按协议类型分模型）
INSERT INTO dynamic_model (
  code, name, entity_type_code, domain, description, status, sort, tenant_id, creator
) VALUES
  ('data_protocol_standard', '协议定义（通用）', 'data_protocol', NULL, '协议定义通用模型', 1, 1, 1, 'flyway'),
  ('data_protocol_ws', '协议定义（WebSocket）', 'data_protocol', NULL, 'WebSocket 协议指令模型', 1, 10, 1, 'flyway'),
  ('data_protocol_mqtt', '协议定义（MQTT）', 'data_protocol', NULL, 'MQTT 协议指令模型', 1, 20, 1, 'flyway'),
  ('data_protocol_http', '协议定义（HTTP）', 'data_protocol', NULL, 'HTTP 协议指令模型', 1, 30, 1, 'flyway'),
  ('data_protocol_opcua', '协议定义（OPC UA）', 'data_protocol', NULL, 'OPC UA 协议指令模型', 1, 40, 1, 'flyway'),
  ('data_protocol_opcda', '协议定义（OPC DA）', 'data_protocol', NULL, 'OPC DA 协议指令模型', 1, 50, 1, 'flyway'),
  ('data_protocol_modbus', '协议定义（Modbus）', 'data_protocol', NULL, 'Modbus 协议指令模型', 1, 60, 1, 'flyway'),
  ('data_protocol_bacnet', '协议定义（BACnet）', 'data_protocol', NULL, 'BACnet 协议指令模型', 1, 70, 1, 'flyway'),
  ('data_protocol_iec104', '协议定义（IEC104）', 'data_protocol', NULL, 'IEC 60870-5-104 协议指令模型', 1, 80, 1, 'flyway'),
  ('data_protocol_iec61850', '协议定义（IEC61850）', 'data_protocol', NULL, 'IEC 61850 协议指令模型', 1, 90, 1, 'flyway'),
  ('data_protocol_dnp3', '协议定义（DNP3）', 'data_protocol', NULL, 'DNP3 协议指令模型', 1, 100, 1, 'flyway'),
  ('data_protocol_dlt645', '协议定义（DL/T645）', 'data_protocol', NULL, 'DL/T 645 协议指令模型', 1, 110, 1, 'flyway')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP;

-- 5) 所有协议模型统一字段（通用检索 + 结构化字段）
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
  'BASE',
  1,
  'flyway'
FROM dynamic_model m
JOIN (
  VALUES
    ('protocol_type_code', 20, true, true, true),
    ('protocol_command_code', 30, true, true, true),
    ('protocol_command_name', 40, true, true, true),
    ('protocol_transport_layer', 50, true, true, false),
    ('protocol_endpoint', 60, true, true, false),
    ('protocol_payload_format', 70, true, true, false),
    ('protocol_poll_interval_ms', 80, true, true, true),
    ('protocol_qos_level', 90, true, true, true),
    ('protocol_command_schema_json', 100, false, false, false),
    ('protocol_field_translation_rules_json', 110, false, false, false),
    ('protocol_step_signal_mapping_rules_json', 120, false, false, false),
    ('protocol_sample_messages_json', 130, false, false, false)
) AS v(field_code, sort_order, is_searchable, is_filterable, is_sortable)
  ON 1 = 1
JOIN dynamic_field f
  ON f.code = v.field_code
 AND f.deleted = false
 AND f.tenant_id = 1
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code IN (
    'data_protocol_standard',
    'data_protocol_ws',
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
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  deleted = false,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP;

-- 6) 协议专用差异字段（按模型分配）
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
  'flyway'
FROM (
  VALUES
    ('data_protocol_ws', 'ws_opcode', 200),
    ('data_protocol_ws', 'http_path', 210),
    ('data_protocol_mqtt', 'mqtt_topic', 200),
    ('data_protocol_mqtt', 'protocol_qos_level', 210),
    ('data_protocol_http', 'http_method', 200),
    ('data_protocol_http', 'http_path', 210),
    ('data_protocol_opcua', 'opcua_node_id', 200),
    ('data_protocol_opcua', 'opcua_namespace', 210),
    ('data_protocol_opcda', 'opcda_item_id', 200),
    ('data_protocol_modbus', 'modbus_slave_id', 200),
    ('data_protocol_modbus', 'modbus_function_code', 210),
    ('data_protocol_modbus', 'modbus_register_address', 220),
    ('data_protocol_bacnet', 'bacnet_object_type', 200),
    ('data_protocol_bacnet', 'bacnet_instance_number', 210),
    ('data_protocol_iec104', 'iec104_asdu_type', 200),
    ('data_protocol_iec61850', 'iec61850_ln_ref', 200),
    ('data_protocol_dnp3', 'dnp3_point_index', 200),
    ('data_protocol_dlt645', 'dlt645_data_identifier', 200)
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
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP;
