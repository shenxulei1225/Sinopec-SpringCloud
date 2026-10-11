-- 大疆机场的一键返航：官方 services 方法 return_home。
-- 空包没有地面站操作码。Pilot 文档没有这条方法，不要从机场复制过去。
-- 其余上云指令在 dynamic_protocol_dji_cloud_commands.sql。
-- 对照行只保留机场这一条（map-land-dji-cloud）。禁止把返航套进 500104 / 200103。
SET search_path TO dynamicbusiness, public;

-- 机场、Pilot 各一个型号，挂在「大疆无人机」分类下。不另开数据目录。
INSERT INTO dynamic_model (
  code, name, entity_type_code, domain, description, status, sort, tenant_id, creator
) VALUES
  ('data_protocol_dji_dock', '大疆机场', 'data_protocol', NULL, '大疆机场 Cloud API 指令', 1, 12, 1, 'seed'),
  ('data_protocol_dji_pilot', '大疆 Pilot', 'data_protocol', NULL, '大疆 Pilot Cloud API 指令', 1, 13, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
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
  dst.id, src.field_id, dst.code, src.field_code, src.required,
  src.is_searchable, src.is_filterable, src.is_sortable, src.sort, src.field_source, 1, 'seed'
FROM dynamic_model_field_assignment src
JOIN dynamic_model ws ON ws.id = src.model_id AND ws.code = 'data_protocol_ws' AND ws.deleted = false
JOIN dynamic_model dst ON dst.deleted = false AND dst.tenant_id = 1
  AND dst.code IN ('data_protocol_dji_dock', 'data_protocol_dji_pilot')
WHERE src.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  model_id = EXCLUDED.model_id,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code, deleted
)
SELECT m.id, c.id, 'data_protocol', m.sort, 'seed', 1, m.code, c.code, false
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'cat-protocol-dji-uav'
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code IN ('data_protocol_dji_dock', 'data_protocol_dji_pilot')
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO ent_data_protocol_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, command_direction, creator, deleted
)
SELECT
  1, 'data_protocol', m.id, '返航', 'proto-dji-return-home', 1,
  $${"opcode":0,"command_direction":"downlink","exchange_mode":"sync_wait_result","protocol_version":["dji-dock-1.16.1"],"command_json":{"outbound":{"method":"return_home","data":{}}},"field_description_json":{"outbound":[{"code":"method","path":"method","type":"string","label":"上云方法","required":true,"description":"官方 services 方法 return_home。设备答卷 result=0 只表示收下了，不是任务完成。"},{"code":"data","path":"data","type":"object","label":"方法参数","required":true,"description":"返航没有额外参数。"}]},"sample_json":{"outbound":{"tid":"","bid":"","timestamp":0,"method":"return_home","data":{}}}}$$::jsonb,
  'downlink', 'seed', false
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'data_protocol_dji_dock'
AND NOT EXISTS (
  SELECT 1 FROM ent_data_protocol_t1 e
  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'proto-dji-return-home'
);

UPDATE ent_data_protocol_t1 e
SET
  name = '返航',
  model_id = (SELECT id FROM dynamic_model WHERE deleted = false AND tenant_id = 1 AND code = 'data_protocol_dji_dock'),
  command_direction = 'downlink',
  custom_fields = custom_fields || jsonb_build_object(
    'communication_protocol', 'mqtt',
    'protocol_channel', 'services',
    'mqtt_topic', 'thing/product/{gateway_sn}/services'
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'proto-dji-return-home';

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'data_protocol', e.id, NULL, 0, 'seed', false
FROM ent_data_protocol_t1 e
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'cat-protocol-dji-uav'
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'proto-dji-return-home'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.deleted = false AND r.tenant_id = 1 AND r.entity_type_code = 'data_protocol'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'opcode', NULL, 0, 'seed', 1, false
FROM ent_data_protocol_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'proto-dji-return-home'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'opcode'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'exchange_mode', 'sync_wait_result', NULL, 'seed', 1, false
FROM ent_data_protocol_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'proto-dji-return-home'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'exchange_mode'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_version', 'dji-dock-1.16.1', NULL, 'seed', 1, false
FROM ent_data_protocol_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'proto-dji-return-home'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1 AND idx.entity_id = e.id AND idx.field_code = 'protocol_version'
);

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
  1, 'protocol_mapping', m.id, v.name, v.code, 1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.protocol_version,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', v.slots::jsonb
  ),
  'seed', false
FROM (VALUES
  ('map-land-dji-cloud', '无人机返航 · 大疆机场', 'action-1022d966da2b45f7b73e776112e6884b', 'dji-dock-1.16.1', 'proto-dji-return-home', $$[]$$)
) AS v(code, name, action_code, protocol_version, instruction_code, slots)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'protocol_mapping_standard'
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1 AND p.code = v.instruction_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code
);

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'protocol_mapping', e.id, NULL, 0, 'seed', false
FROM ent_protocol_mapping_t1 e
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'protocol_mapping_root'
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code IN ('map-land-dji-cloud', 'map-land-dji-pilot')
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_category_relation_t1 r
  WHERE r.tenant_id = 1 AND r.entity_type_code = 'protocol_mapping'
    AND r.entity_id = e.id AND r.category_id = c.id
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'action_ref', NULL, (e.custom_fields->'action_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code IN ('map-land-dji-cloud', 'map-land-dji-pilot')
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1
    AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code IN ('map-land-dji-cloud', 'map-land-dji-pilot')
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1
    AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code IN ('map-land-dji-cloud', 'map-land-dji-pilot')
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1
    AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
);

-- 已有的大疆返航若还在地面站型号或旧版本上，改到机场型号。
UPDATE ent_data_protocol_t1 e
SET model_id = m.id,
    custom_fields = jsonb_set(e.custom_fields, '{protocol_version}', '["dji-dock-1.16.1"]'::jsonb),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'proto-dji-return-home'
  AND m.deleted = false AND m.tenant_id = 1 AND m.code = 'data_protocol_dji_dock'
  AND (e.model_id IS DISTINCT FROM m.id OR e.custom_fields->'protocol_version' IS DISTINCT FROM '["dji-dock-1.16.1"]'::jsonb);

UPDATE dynamic_entity_field_index_t1 idx
SET model_id = e.model_id,
    value_string = CASE WHEN idx.field_code = 'protocol_version' THEN 'dji-dock-1.16.1' ELSE idx.value_string END,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
WHERE idx.deleted = false AND idx.entity_id = e.id AND e.code = 'proto-dji-return-home';

UPDATE dynamic_entity_category_relation_t1 r
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 e
JOIN dynamic_category gs ON gs.deleted = false AND gs.tenant_id = 1 AND gs.code = 'cat-protocol-gs'
WHERE r.deleted = false AND r.entity_type_code = 'data_protocol'
  AND r.entity_id = e.id AND e.code IN ('proto-dji-return-home', 'proto-dji-pilot-return-home')
  AND r.category_id = gs.id;

UPDATE ent_protocol_mapping_t1 e
SET name = '无人机返航 · 大疆机场',
    custom_fields = jsonb_set(
      jsonb_set(e.custom_fields, '{mapped_protocol_version}', '"dji-dock-1.16.1"'),
      '{protocol_instruction_ref}',
      jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol')
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_data_protocol_t1 p
WHERE e.deleted = false AND e.code = 'map-land-dji-cloud'
  AND p.deleted = false AND p.code = 'proto-dji-return-home';

UPDATE dynamic_entity_field_index_t1 idx
SET value_string = e.custom_fields->>'mapped_protocol_version',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 e
WHERE idx.deleted = false AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
  AND e.code IN ('map-land-dji-cloud', 'map-land-dji-pilot');

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, v.field_code, v.value_string, v.value_number, 'seed', 1, false
FROM ent_data_protocol_t1 e
JOIN (VALUES
  ('opcode', NULL, 0::bigint),
  ('exchange_mode', 'sync_wait_result', NULL),
  ('protocol_version', 'dji-pilot-1.16.1', NULL)
) AS v(field_code, value_string, value_number) ON true
WHERE e.deleted = false AND e.code = 'proto-dji-pilot-return-home'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.entity_id = e.id AND idx.field_code = v.field_code
);

INSERT INTO model_crud_form_definition (
  entity_type_code, model_id, crud_form_fields, version, tenant_id, creator, deleted
)
SELECT
  src.entity_type_code,
  dst.id,
  jsonb_set(src.crud_form_fields::jsonb, '{modelId}', to_jsonb(dst.id)),
  src.version,
  1,
  'seed',
  false
FROM model_crud_form_definition src
JOIN dynamic_model ws ON ws.id = src.model_id AND ws.code = 'data_protocol_ws' AND ws.deleted = false
JOIN dynamic_model dst ON dst.deleted = false AND dst.code IN ('data_protocol_dji_dock', 'data_protocol_dji_pilot')
WHERE src.deleted = false
AND NOT EXISTS (
  SELECT 1 FROM model_crud_form_definition existing
  WHERE existing.deleted = false AND existing.model_id = dst.id AND existing.entity_type_code = src.entity_type_code
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, v.field_code, v.value_string, v.value_number, 'seed', 1, false
FROM ent_data_protocol_t1 e
JOIN (VALUES
  ('opcode', NULL, 0::bigint),
  ('exchange_mode', 'sync_wait_result', NULL),
  ('protocol_version', 'dji-pilot-1.16.1', NULL)
) AS v(field_code, value_string, value_number) ON true
WHERE e.deleted = false AND e.code = 'proto-dji-pilot-return-home'
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.entity_id = e.id AND idx.field_code = v.field_code
);

INSERT INTO model_crud_form_definition (
  entity_type_code, model_id, crud_form_fields, version, tenant_id, creator, deleted
)
SELECT
  src.entity_type_code,
  dst.id,
  jsonb_set(src.crud_form_fields::jsonb, '{modelId}', to_jsonb(dst.id)),
  src.version,
  1,
  'seed',
  false
FROM model_crud_form_definition src
JOIN dynamic_model ws ON ws.id = src.model_id AND ws.code = 'data_protocol_ws' AND ws.deleted = false
JOIN dynamic_model dst ON dst.deleted = false AND dst.code IN ('data_protocol_dji_dock', 'data_protocol_dji_pilot')
WHERE src.deleted = false
AND NOT EXISTS (
  SELECT 1 FROM model_crud_form_definition existing
  WHERE existing.deleted = false AND existing.model_id = dst.id AND existing.entity_type_code = src.entity_type_code
);

UPDATE dynamic_field
SET options = replace(
      options,
      '{"label":"大疆上云","value":"dji-cloud"}',
      '{"label":"大疆机场 1.16.1","value":"dji-dock-1.16.1"},{"label":"大疆 Pilot 1.16.1","value":"dji-pilot-1.16.1"}'
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND options LIKE '%"dji-cloud"%';

UPDATE ent_equipment_t1
SET platform_protocol = CASE cloud_access_mode WHEN 'pilot' THEN 'dji-pilot-1.16.1' ELSE 'dji-dock-1.16.1' END,
    protocol_versions = CASE cloud_access_mode
      WHEN 'pilot' THEN '["dji-pilot-1.16.1"]'::jsonb
      ELSE '["dji-dock-1.16.1"]'::jsonb END,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND platform_protocol = 'dji-cloud';
