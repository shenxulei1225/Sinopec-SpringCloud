-- 指令协议对接：型号、对照字段、对照行。目录本体须已由创建接口建出。
-- 界面保存对照只认型号已分配的引用字段，不查旧业务类型关联许可表。
-- 不负责：开跑翻译、回写、对端协议。禁止发明没有协议指令的对接行。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  ('action_ref', '动作', 'ENTITY_REF', NULL, '这条对照对应哪条动作。只存动作实体 id。', 'SYSTEM', 1, 1, 'BTREE', NULL, 'dynamic-entity:action', 'action', 1, 'seed'),
  ('mapped_protocol_version', '协议版本', 'ENUM', NULL, '这条对照适用哪一份对接协议。一条对照只选一种，不要复用指令上的多选协议版本字段。', 'SYSTEM', 1, NULL, 'BTREE', '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"}]', NULL, 'mapped_protocol_version', 1, 'seed'),
  ('protocol_instruction_ref', '协议指令', 'ENTITY_REF', NULL, '这条对照落到协议目录里的哪一条指令。只存指令实体 id。', 'SYSTEM', 1, 1, 'BTREE', NULL, 'dynamic-entity:data_protocol', 'data_protocol', 1, 'seed'),
  ('param_slot_mapping_json', '参数对照', 'JSON', NULL, '动作参数填到指令哪一项。元素是 {slot, sourcePath, path}。引用参数用 sourcePath 写出取哪一项。不记何时返回。', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'PARAM_SLOT_MAPPING', 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name, type = EXCLUDED.type, description = EXCLUDED.description,
  options = EXCLUDED.options, provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type, max_relations = EXCLUDED.max_relations,
  index_strategy = EXCLUDED.index_strategy, source = EXCLUDED.source, status = 1,
  deleted = false, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, domain, description, status, sort, tenant_id, creator, governance_status
)
SELECT
  'protocol_mapping_standard', '指令协议对照', 'protocol_mapping', NULL,
  '动作加协议版本对应哪条协议指令，以及参数槽填到空包哪条路径。不记何时返回。',
  1, 10, 1, 'seed', 'COMPANY'
WHERE EXISTS (
  SELECT 1 FROM dynamic_entity_type t
  WHERE t.deleted = false AND t.tenant_id = 1 AND t.code = 'protocol_mapping'
)
AND NOT EXISTS (
  SELECT 1 FROM dynamic_model m
  WHERE m.deleted = false AND m.tenant_id = 1 AND m.entity_type_code = 'protocol_mapping'
);

UPDATE dynamic_model
SET name = '指令协议对照',
    description = '动作加协议版本对应哪条协议指令，以及参数槽填到空包哪条路径。不记何时返回。',
    governance_status = 'COMPANY',
    status = 1,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND entity_type_code = 'protocol_mapping'
  AND code <> 'protocol_mapping_standard'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model x
    WHERE x.deleted = false AND x.tenant_id = 1 AND x.code = 'protocol_mapping_standard'
  );

UPDATE dynamic_model
SET code = 'protocol_mapping_standard',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND entity_type_code = 'protocol_mapping'
  AND code <> 'protocol_mapping_standard'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model x
    WHERE x.deleted = false AND x.tenant_id = 1 AND x.code = 'protocol_mapping_standard'
  );

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, mapping.required, mapping.searchable, mapping.searchable, false,
       mapping.sort_order, 'CUSTOM', 1, 'seed'
FROM (VALUES
  ('action_ref', 10, true, true),
  ('mapped_protocol_version', 20, true, true),
  ('protocol_instruction_ref', 30, true, true),
  ('param_slot_mapping_json', 40, false, false)
) AS mapping(field_code, sort_order, required, searchable)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'protocol_mapping_standard'
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = mapping.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id, sort = EXCLUDED.sort, required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable, is_filterable = EXCLUDED.is_filterable,
  field_source = EXCLUDED.field_source, deleted = false,
  updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code, deleted
)
SELECT m.id, c.id, 'protocol_mapping', m.sort, 'seed', 1, m.code, c.code, false
FROM dynamic_model m
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'protocol_mapping_root'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'protocol_mapping_standard'
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id, category_id = EXCLUDED.category_id,
  deleted = false, updater = 'seed', update_time = CURRENT_TIMESTAMP;

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
  ('map-arrive-uav-ws', '到达指定位置 · 无人机对接协议', 'action-065340437bcb46788b7dc261fab81c63', 'uav-ws', 'proto-uav-200102-move', $$[{"slot":"location_ref","sourcePath":"FLD-PNT-002","path":"request.pointId"}]$$),
  ('map-arrive-robot-ws', '到达指定位置 · 机器人对接协议', 'action-065340437bcb46788b7dc261fab81c63', 'robot-ws', 'proto-robot-200102-move', $$[{"slot":"location_ref","sourcePath":"FLD-PNT-002","path":"request.pointId"}]$$),
  ('map-photo-uav-ws', '拍照 · 无人机对接协议', 'act-robot-shoot', 'uav-ws', 'proto-uav-200301-photo', $$[{"slot":"shot_count","path":"request.number"}]$$),
  ('map-photo-robot-ws', '拍照 · 机器人对接协议', 'act-robot-shoot', 'robot-ws', 'proto-robot-200301-photo', $$[{"slot":"shot_count","path":"request.number"}]$$),
  ('map-takeoff-uav-ws', '无人机起飞 · 无人机对接协议', 'action-2b44a1f882f54438b6a1864e3636c889', 'uav-ws', 'proto-uav-200101-takeoff', $$[]$$),
  ('map-land-uav-ws', '无人机返航 · 无人机对接协议', 'action-1022d966da2b45f7b73e776112e6884b', 'uav-ws', 'proto-uav-200103-land', $$[]$$),
  ('map-gas-on-uav-ws', '气体检测打开 · 无人机对接协议', 'action-647f9f172c3b489ea862a29c0e8396a2', 'uav-ws', 'proto-uav-200401-gas-on', $$[]$$),
  ('map-gas-off-uav-ws', '气体检测关闭 · 无人机对接协议', 'action-ab595a61ca164a2c9db74e1311e747a5', 'uav-ws', 'proto-uav-200402-gas-off', $$[]$$)
) AS v(code, name, action_code, protocol_version, instruction_code, slots)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'protocol_mapping_standard'
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1 AND p.code = v.instruction_code
WHERE NOT EXISTS (
  SELECT 1 FROM ent_protocol_mapping_t1 e
  WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code
);

UPDATE ent_protocol_mapping_t1 e
SET
  name = v.name,
  custom_fields = jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.protocol_version,
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', v.slots::jsonb
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('map-arrive-uav-ws', '到达指定位置 · 无人机对接协议', 'action-065340437bcb46788b7dc261fab81c63', 'uav-ws', 'proto-uav-200102-move', $$[{"slot":"location_ref","sourcePath":"FLD-PNT-002","path":"request.pointId"}]$$),
  ('map-arrive-robot-ws', '到达指定位置 · 机器人对接协议', 'action-065340437bcb46788b7dc261fab81c63', 'robot-ws', 'proto-robot-200102-move', $$[{"slot":"location_ref","sourcePath":"FLD-PNT-002","path":"request.pointId"}]$$),
  ('map-photo-uav-ws', '拍照 · 无人机对接协议', 'act-robot-shoot', 'uav-ws', 'proto-uav-200301-photo', $$[{"slot":"shot_count","path":"request.number"}]$$),
  ('map-photo-robot-ws', '拍照 · 机器人对接协议', 'act-robot-shoot', 'robot-ws', 'proto-robot-200301-photo', $$[{"slot":"shot_count","path":"request.number"}]$$),
  ('map-takeoff-uav-ws', '无人机起飞 · 无人机对接协议', 'action-2b44a1f882f54438b6a1864e3636c889', 'uav-ws', 'proto-uav-200101-takeoff', $$[]$$),
  ('map-land-uav-ws', '无人机返航 · 无人机对接协议', 'action-1022d966da2b45f7b73e776112e6884b', 'uav-ws', 'proto-uav-200103-land', $$[]$$),
  ('map-gas-on-uav-ws', '气体检测打开 · 无人机对接协议', 'action-647f9f172c3b489ea862a29c0e8396a2', 'uav-ws', 'proto-uav-200401-gas-on', $$[]$$),
  ('map-gas-off-uav-ws', '气体检测关闭 · 无人机对接协议', 'action-ab595a61ca164a2c9db74e1311e747a5', 'uav-ws', 'proto-uav-200402-gas-off', $$[]$$)
) AS v(code, name, action_code, protocol_version, instruction_code, slots)
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1 AND p.code = v.instruction_code
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code;

INSERT INTO dynamic_entity_category_relation_t1 (
  tenant_id, category_id, entity_type_code, entity_id, domain, sort, creator, deleted
)
SELECT 1, c.id, 'protocol_mapping', e.id, NULL, 0, 'seed', false
FROM ent_protocol_mapping_t1 e
JOIN dynamic_category c
  ON c.deleted = false AND c.tenant_id = 1 AND c.code = 'protocol_mapping_root'
WHERE e.deleted = false AND e.tenant_id = 1
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
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1
    AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'mapped_protocol_version', e.custom_fields->>'mapped_protocol_version', NULL, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1
    AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
);

INSERT INTO dynamic_entity_field_index_t1
  (entity_id, model_id, field_code, value_string, value_number, creator, tenant_id, deleted)
SELECT e.id, e.model_id, 'protocol_instruction_ref', NULL, (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint, 'seed', 1, false
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
AND NOT EXISTS (
  SELECT 1 FROM dynamic_entity_field_index_t1 idx
  WHERE idx.deleted = false AND idx.tenant_id = 1
    AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
);

UPDATE dynamic_entity_field_index_t1 idx
SET value_number = (e.custom_fields->'action_ref'->>'id')::bigint,
    value_string = NULL,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND idx.entity_id = e.id AND idx.field_code = 'action_ref'
  AND idx.tenant_id = 1;

UPDATE dynamic_entity_field_index_t1 idx
SET value_string = e.custom_fields->>'mapped_protocol_version',
    value_number = NULL,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND idx.entity_id = e.id AND idx.field_code = 'mapped_protocol_version'
  AND idx.tenant_id = 1;

UPDATE dynamic_entity_field_index_t1 idx
SET value_number = (e.custom_fields->'protocol_instruction_ref'->>'id')::bigint,
    value_string = NULL,
    deleted = false,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM ent_protocol_mapping_t1 e
WHERE e.deleted = false AND e.tenant_id = 1
  AND idx.entity_id = e.id AND idx.field_code = 'protocol_instruction_ref'
  AND idx.tenant_id = 1;
