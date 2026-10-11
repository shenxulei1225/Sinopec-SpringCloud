-- 型号按「对接协议 + 通讯协议」一本。
-- 已有四本改名，表示它们现在这种走法；其余走法另建型号。
-- 指令按识别码和通讯方式挪到对应型号。开跑仍认协议版本，不认型号。
-- 没有对应型号时不插入，不报错。
SET search_path TO dynamicbusiness, public;

BEGIN;

UPDATE dynamic_field
SET description = '这条指令走哪种通讯，必须和它所属型号一致。一本型号只有一种。',
    options = '[{"label":"MQTT","value":"mqtt"},{"label":"HTTPS","value":"https"},{"label":"WebSocket","value":"websocket"},{"label":"JSBridge","value":"jsbridge"},{"label":"航线文件","value":"wayline"}]',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'communication_protocol';

INSERT INTO dynamic_model (
  code, name, entity_type_code, domain, description, status, sort, tenant_id, creator
) VALUES
  ('data_protocol_dji_dock_wayline', '大疆机场航线文件', 'data_protocol', NULL, '大疆机场写进航线文件的指令', 1, 14, 1, 'seed'),
  ('data_protocol_dji_pilot_https', '大疆 Pilot HTTPS', 'data_protocol', NULL, '大疆 Pilot 走 HTTPS 的指令', 1, 15, 1, 'seed'),
  ('data_protocol_dji_pilot_ws', '大疆 Pilot WebSocket', 'data_protocol', NULL, '大疆 Pilot 走 WebSocket 的指令', 1, 16, 1, 'seed'),
  ('data_protocol_dji_pilot_jsbridge', '大疆 Pilot JSBridge', 'data_protocol', NULL, '大疆 Pilot 走 JSBridge 的指令', 1, 17, 1, 'seed'),
  ('data_protocol_dji_pilot_wayline', '大疆 Pilot 航线文件', 'data_protocol', NULL, '大疆 Pilot 写进航线文件的指令', 1, 18, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_model
SET name = renamed.name,
    description = renamed.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('data_protocol_ws', '无人机协议 WebSocket', '无人机对接协议的 WebSocket 指令'),
  ('data_protocol_robot', '机器人协议 WebSocket', '机器人对接协议的 WebSocket 指令'),
  ('data_protocol_dji_dock', '大疆机场 MQTT', '大疆机场走 MQTT 的指令'),
  ('data_protocol_dji_pilot', '大疆 Pilot MQTT', '大疆 Pilot 走 MQTT 的指令')
) AS renamed(code, name, description)
WHERE dynamic_model.deleted = false
  AND dynamic_model.tenant_id = 1
  AND dynamic_model.code = renamed.code;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, f.code, false,
  v.is_searchable, v.is_filterable, v.is_sortable,
  v.sort_order, v.field_source, 1, 'seed'
FROM (
  VALUES
    ('data_protocol_dji_dock_wayline', 'command_direction', 12, true, true, true, 'BASE'),
    ('data_protocol_dji_dock_wayline', 'protocol_version', 16, true, true, false, 'BASE'),
    ('data_protocol_dji_dock_wayline', 'communication_protocol', 18, true, true, false, 'BASE'),
    ('data_protocol_dji_dock_wayline', 'opcode', 10, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_dock_wayline', 'exchange_mode', 14, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_https', 'command_json', 300, false, false, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_https', 'field_description_json', 310, false, false, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_https', 'sample_json', 320, false, false, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_https', 'command_direction', 12, true, true, true, 'BASE'),
    ('data_protocol_dji_pilot_https', 'opcode', 10, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_https', 'exchange_mode', 14, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_https', 'protocol_version', 16, true, true, false, 'BASE'),
    ('data_protocol_dji_pilot_https', 'communication_protocol', 18, true, true, false, 'BASE'),
    ('data_protocol_dji_pilot_https', 'http_method', 22, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_https', 'http_path', 23, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_ws', 'command_json', 300, false, false, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_ws', 'field_description_json', 310, false, false, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_ws', 'sample_json', 320, false, false, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_ws', 'command_direction', 12, true, true, true, 'BASE'),
    ('data_protocol_dji_pilot_ws', 'opcode', 10, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_ws', 'exchange_mode', 14, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_ws', 'protocol_version', 16, true, true, false, 'BASE'),
    ('data_protocol_dji_pilot_ws', 'communication_protocol', 18, true, true, false, 'BASE'),
    ('data_protocol_dji_pilot_jsbridge', 'command_direction', 12, true, true, true, 'BASE'),
    ('data_protocol_dji_pilot_jsbridge', 'protocol_version', 16, true, true, false, 'BASE'),
    ('data_protocol_dji_pilot_jsbridge', 'communication_protocol', 18, true, true, false, 'BASE'),
    ('data_protocol_dji_pilot_jsbridge', 'opcode', 10, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_jsbridge', 'exchange_mode', 14, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_wayline', 'command_direction', 12, true, true, true, 'BASE'),
    ('data_protocol_dji_pilot_wayline', 'protocol_version', 16, true, true, false, 'BASE'),
    ('data_protocol_dji_pilot_wayline', 'communication_protocol', 18, true, true, false, 'BASE'),
    ('data_protocol_dji_pilot_wayline', 'opcode', 10, true, true, false, 'CUSTOM'),
    ('data_protocol_dji_pilot_wayline', 'exchange_mode', 14, true, true, false, 'CUSTOM')
) AS v(model_code, field_code, sort_order, is_searchable, is_filterable, is_sortable, field_source)
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = 1 AND f.code = v.field_code
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

UPDATE dynamic_model_field_assignment
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND model_code IN ('data_protocol_dji_dock', 'data_protocol_dji_pilot')
  AND field_code IN ('http_method', 'http_path');

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code, deleted
)
SELECT m.id, c.id, 'data_protocol', m.sort, 'seed', 1, m.code, c.code, false
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'cat-protocol-dji-uav'
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code IN (
    'data_protocol_dji_dock_wayline',
    'data_protocol_dji_pilot_https',
    'data_protocol_dji_pilot_ws',
    'data_protocol_dji_pilot_jsbridge',
    'data_protocol_dji_pilot_wayline'
  )
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_category_relation_t1 r
    WHERE r.deleted = false
      AND r.tenant_id = 1
      AND r.model_code = m.code
      AND r.category_code = c.code
      AND r.entity_type_code = 'data_protocol'
  );

UPDATE ent_data_protocol_t1
SET custom_fields = jsonb_set(
      COALESCE(custom_fields, '{}'::jsonb),
      '{communication_protocol}',
      '"wayline"',
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code LIKE '%-wpml-%'
  AND COALESCE(custom_fields->>'communication_protocol', '') IS DISTINCT FROM 'wayline';

UPDATE ent_data_protocol_t1 AS instruction
SET model_id = destination.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model source, dynamic_model destination
WHERE instruction.deleted = false
  AND instruction.tenant_id = 1
  AND instruction.model_id = source.id
  AND source.deleted = false
  AND destination.deleted = false
  AND destination.tenant_id = 1
  AND destination.code = CASE
    WHEN source.code IN ('data_protocol_dji_dock', 'data_protocol_dji_dock_wayline')
         AND instruction.code LIKE '%-wpml-%'
      THEN 'data_protocol_dji_dock_wayline'
    WHEN source.code IN ('data_protocol_dji_pilot', 'data_protocol_dji_pilot_wayline')
         AND instruction.code LIKE '%-wpml-%'
      THEN 'data_protocol_dji_pilot_wayline'
    WHEN source.code IN ('data_protocol_dji_pilot', 'data_protocol_dji_pilot_https')
         AND instruction.custom_fields->>'communication_protocol' = 'https'
      THEN 'data_protocol_dji_pilot_https'
    WHEN source.code IN ('data_protocol_dji_pilot', 'data_protocol_dji_pilot_ws')
         AND instruction.custom_fields->>'communication_protocol' = 'websocket'
      THEN 'data_protocol_dji_pilot_ws'
    WHEN source.code IN ('data_protocol_dji_pilot', 'data_protocol_dji_pilot_jsbridge')
         AND instruction.custom_fields->>'communication_protocol' = 'jsbridge'
      THEN 'data_protocol_dji_pilot_jsbridge'
    ELSE source.code
  END
  AND instruction.model_id IS DISTINCT FROM destination.id;

UPDATE dynamic_entity_field_index_t1 AS idx
SET model_id = instruction.model_id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 instruction
WHERE idx.deleted = false
  AND idx.tenant_id = 1
  AND instruction.deleted = false
  AND instruction.tenant_id = 1
  AND idx.entity_id = instruction.id
  AND idx.model_id IS DISTINCT FROM instruction.model_id;

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT instruction.id, instruction.model_id, 'communication_protocol', 'wayline', NULL, 'seed', 1, false
FROM ent_data_protocol_t1 instruction
WHERE instruction.deleted = false
  AND instruction.tenant_id = 1
  AND instruction.code LIKE '%-wpml-%'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_field_index_t1 idx
    WHERE idx.deleted = false
      AND idx.tenant_id = 1
      AND idx.entity_id = instruction.id
      AND idx.field_code = 'communication_protocol'
  );

UPDATE dynamic_entity_field_index_t1 AS idx
SET value_string = 'wayline',
    model_id = instruction.model_id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 instruction
WHERE idx.deleted = false
  AND idx.tenant_id = 1
  AND idx.field_code = 'communication_protocol'
  AND instruction.deleted = false
  AND instruction.tenant_id = 1
  AND idx.entity_id = instruction.id
  AND instruction.code LIKE '%-wpml-%'
  AND idx.value_string IS DISTINCT FROM 'wayline';

COMMIT;
