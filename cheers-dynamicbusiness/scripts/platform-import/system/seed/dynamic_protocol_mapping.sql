-- 指令协议对接：型号、对照字段、对照行。目录本体须已由创建接口建出。
-- 界面保存对照只认型号已分配的引用字段，不查旧业务类型关联许可表。
-- 不负责：开跑翻译、回写、对端协议。禁止发明没有协议指令的对接行。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  ('action_ref', '动作', 'ENTITY_REF', NULL, '这条对照对应哪条动作。只存动作实体 id。', 'SYSTEM', 1, 1, 'BTREE', NULL, 'dynamic-entity:action', 'action', 1, 'seed'),
  ('mapped_protocol_version', '协议版本', 'ENUM', NULL, '这条对照适用哪一份对接协议。一条对照只选一种，不要复用指令上的多选协议版本字段。', 'SYSTEM', 1, NULL, 'BTREE', '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"},{"label":"大疆机场 1.16.1","value":"dji-dock-1.16.1"},{"label":"大疆 Pilot 1.16.1","value":"dji-pilot-1.16.1"}]', NULL, 'mapped_protocol_version', 1, 'seed'),
  ('protocol_mapping_status', '处理状态', 'ENUM', NULL, '动作在这份协议下的确认结果。CONFIGURED 表示已配指令；UNSUPPORTED 表示明确不支持。', 'SYSTEM', 1, NULL, 'BTREE', '[{"label":"已配置","value":"CONFIGURED"},{"label":"不支持","value":"UNSUPPORTED"}]', NULL, 'protocol_mapping_status', 1, 'seed'),
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
  ('protocol_mapping_status', 30, true, true),
  ('protocol_instruction_ref', 40, false, true),
  ('param_slot_mapping_json', 50, false, false)
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

-- 动作目录明确写出每份协议下由谁执行。页面和任务核对只读该字段，不再按动作名称猜。
UPDATE ent_action_t1 a
SET custom_fields = jsonb_set(
      COALESCE(a.custom_fields, '{}'::jsonb) - 'protocol_dispatch_role_by_version',
      '{protocol_dispatch_role}',
      v.roles::jsonb,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('act-ground-patrol', $${"uav-ws":"WAYLINE_ORCHESTRATION","robot-ws":"WAYLINE_ORCHESTRATION","dji-dock-1.16.1":"WAYLINE_ORCHESTRATION","dji-pilot-1.16.1":"WAYLINE_ORCHESTRATION"}$$),
  ('act-uav-wayline-prepare', $${"uav-ws":"WAYLINE_ORCHESTRATION","robot-ws":"WAYLINE_ORCHESTRATION","dji-dock-1.16.1":"WAYLINE_ORCHESTRATION","dji-pilot-1.16.1":"WAYLINE_ORCHESTRATION"}$$),
  ('action-c607b9038479421cadff4bb867205f0e', $${"uav-ws":"DEVICE_REPORT","robot-ws":"DEVICE_REPORT","dji-dock-1.16.1":"DEVICE_REPORT","dji-pilot-1.16.1":"DEVICE_REPORT"}$$),
  ('act-robot-self-check', $${"uav-ws":"PLATFORM_CHECK","robot-ws":"PLATFORM_CHECK","dji-dock-1.16.1":"PLATFORM_CHECK","dji-pilot-1.16.1":"PLATFORM_CHECK"}$$),
  ('action-4019fb901ff3411f99603b1f5cee5119', $${"dji-dock-1.16.1":"PLATFORM_CHECK","dji-pilot-1.16.1":"PLATFORM_CHECK"}$$),
  ('action-1a0df020d2064b94abd5bad0a6bba0d1', $${"dji-dock-1.16.1":"PLATFORM_CHECK","dji-pilot-1.16.1":"PLATFORM_CHECK"}$$),
  ('action-c6d414f4f24c442eb5947dccdf802b37', $${"dji-dock-1.16.1":"PLATFORM_CHECK","dji-pilot-1.16.1":"PLATFORM_CHECK"}$$),
  ('act-robot-battery-check', $${"dji-dock-1.16.1":"PLATFORM_CHECK","dji-pilot-1.16.1":"PLATFORM_CHECK"}$$)
) AS v(action_code, roles)
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.code = v.action_code;

-- 任务头尾是复合动作。协议页不把容器动作列成待确认。
UPDATE ent_action_t1
SET is_composite = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code IN ('act-uav-head', 'act-uav-tail', 'act-robot-head', 'act-robot-tail');

-- 东古协议只接“指令起飞/指令降落”。旧种子误绑到大疆“一键起飞/一键返航”，必须停用。
UPDATE ent_protocol_mapping_t1
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code IN ('map-takeoff-uav-ws', 'map-land-uav-ws');

-- 飞向目标点的旧编码与正式种子重复。保留 map-arrive-uav-ws，避免网关读到同一协议动作两条对照。
UPDATE ent_protocol_mapping_t1
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'map-action-065340437bcb46788b7dc261fab81c63-uav-ws'
  AND EXISTS (
    SELECT 1
    FROM ent_protocol_mapping_t1 canonical
    WHERE canonical.deleted = false
      AND canonical.tenant_id = 1
      AND canonical.code = 'map-arrive-uav-ws'
  );

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
  1, 'protocol_mapping', m.id, v.name, v.code, 1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.protocol_version,
    'protocol_mapping_status', 'CONFIGURED',
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', v.slots::jsonb
  ),
  'seed', false
FROM (VALUES
  ('map-arrive-uav-ws', '到达指定位置 · 无人机对接协议', 'action-065340437bcb46788b7dc261fab81c63', 'uav-ws', 'proto-uav-200102-move', $$[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-PNT-002","path":"request.pointId"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.longitude","path":"request.lng"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.latitude","path":"request.lat"}]$$),
  ('map-arrive-robot-ws', '到达指定位置 · 机器人对接协议', 'act-robot-arrive', 'robot-ws', 'proto-robot-200102-move', $$[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-PNT-002","path":"request.pointId"}]$$),
  ('map-photo-uav-ws', '拍照 · 无人机对接协议', 'takePhoto', 'uav-ws', 'proto-uav-200301-photo', $$[{"slot":"shot_count","path":"request.number"},{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]$$),
  ('map-photo-robot-ws', '拍照 · 机器人对接协议', 'takePhoto', 'robot-ws', 'proto-robot-200301-photo', $$[{"slot":"shot_count","path":"request.number"},{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]$$),
  ('map-return-robot-ws', '返回充电 · 机器人对接协议', 'act-robot-return-charge', 'robot-ws', 'proto-robot-200102-move', $$[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-PNT-002","path":"request.pointId"}]$$),
  ('map-uav-takeoff-uav-ws', '指令起飞 · 无人机对接协议', 'act-uav-takeoff', 'uav-ws', 'proto-uav-200101-takeoff', $$[{"slot":"FLD-LOC-014","sourcePath":"longitude","path":"request.lng"},{"slot":"FLD-LOC-014","sourcePath":"latitude","path":"request.lat"},{"slot":"FLD-LOC-014","sourcePath":"height","path":"request.height"}]$$),
  ('map-uav-land-uav-ws', '指令降落 · 无人机对接协议', 'act-uav-land', 'uav-ws', 'proto-uav-200103-land', $$[]$$),
  ('map-gas-on-uav-ws', '气体检测打开 · 无人机对接协议', 'action-647f9f172c3b489ea862a29c0e8396a2', 'uav-ws', 'proto-uav-200401-gas-on', $$[{"slot":"gas_frequency","path":"request.frequency"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]$$),
  ('map-gas-off-uav-ws', '气体检测关闭 · 无人机对接协议', 'action-ab595a61ca164a2c9db74e1311e747a5', 'uav-ws', 'proto-uav-200402-gas-off', $$[{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]$$),
  ('map-battery-uav-ws', '电量检查 · 无人机对接协议', 'act-robot-battery-check', 'uav-ws', 'proto-uav-500101-status', $$[]$$),
  ('map-battery-robot-ws', '电量检查 · 机器人对接协议', 'act-robot-battery-check', 'robot-ws', 'proto-robot-500101-status', $$[]$$),
  ('map-land-dji-cloud', '无人机返航 · 大疆机场', 'action-1022d966da2b45f7b73e776112e6884b', 'dji-dock-1.16.1', 'proto-dji-return-home', $$[]$$)
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
    'protocol_mapping_status', 'CONFIGURED',
    'protocol_instruction_ref', jsonb_build_object('id', p.id, 'entityTypeCode', 'data_protocol'),
    'param_slot_mapping_json', v.slots::jsonb
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('map-arrive-uav-ws', '到达指定位置 · 无人机对接协议', 'action-065340437bcb46788b7dc261fab81c63', 'uav-ws', 'proto-uav-200102-move', $$[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-PNT-002","path":"request.pointId"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.longitude","path":"request.lng"},{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-LOC-014.latitude","path":"request.lat"}]$$),
  ('map-arrive-robot-ws', '到达指定位置 · 机器人对接协议', 'act-robot-arrive', 'robot-ws', 'proto-robot-200102-move', $$[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-PNT-002","path":"request.pointId"}]$$),
  ('map-photo-uav-ws', '拍照 · 无人机对接协议', 'takePhoto', 'uav-ws', 'proto-uav-200301-photo', $$[{"slot":"shot_count","path":"request.number"},{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]$$),
  ('map-photo-robot-ws', '拍照 · 机器人对接协议', 'takePhoto', 'robot-ws', 'proto-robot-200301-photo', $$[{"slot":"shot_count","path":"request.number"},{"slot":"yaw","path":"request.angleLevel"},{"slot":"pitch","path":"request.angleVertical"},{"slot":"focal_length","path":"request.focuses"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]$$),
  ('map-return-robot-ws', '返回充电 · 机器人对接协议', 'act-robot-return-charge', 'robot-ws', 'proto-robot-200102-move', $$[{"slot":"F-e49f76bdca3e4e1393e8e2f1cc0e7867","sourcePath":"FLD-PNT-002","path":"request.pointId"}]$$),
  ('map-uav-takeoff-uav-ws', '指令起飞 · 无人机对接协议', 'act-uav-takeoff', 'uav-ws', 'proto-uav-200101-takeoff', $$[{"slot":"FLD-LOC-014","sourcePath":"longitude","path":"request.lng"},{"slot":"FLD-LOC-014","sourcePath":"latitude","path":"request.lat"},{"slot":"FLD-LOC-014","sourcePath":"height","path":"request.height"}]$$),
  ('map-uav-land-uav-ws', '指令降落 · 无人机对接协议', 'act-uav-land', 'uav-ws', 'proto-uav-200103-land', $$[]$$),
  ('map-gas-on-uav-ws', '气体检测打开 · 无人机对接协议', 'action-647f9f172c3b489ea862a29c0e8396a2', 'uav-ws', 'proto-uav-200401-gas-on', $$[{"slot":"gas_frequency","path":"request.frequency"},{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]$$),
  ('map-gas-off-uav-ws', '气体检测关闭 · 无人机对接协议', 'action-ab595a61ca164a2c9db74e1311e747a5', 'uav-ws', 'proto-uav-200402-gas-off', $$[{"slot":"F-2b8fa8bda0b94663979c112af85325dc","path":"request.parking"},{"slot":"action_duration","path":"request.time"}]$$),
  ('map-battery-uav-ws', '电量检查 · 无人机对接协议', 'act-robot-battery-check', 'uav-ws', 'proto-uav-500101-status', $$[]$$),
  ('map-battery-robot-ws', '电量检查 · 机器人对接协议', 'act-robot-battery-check', 'robot-ws', 'proto-robot-500101-status', $$[]$$),
  ('map-land-dji-cloud', '无人机返航 · 大疆机场', 'action-1022d966da2b45f7b73e776112e6884b', 'dji-dock-1.16.1', 'proto-dji-return-home', $$[]$$)
) AS v(code, name, action_code, protocol_version, instruction_code, slots)
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
JOIN ent_data_protocol_t1 p ON p.deleted = false AND p.tenant_id = 1 AND p.code = v.instruction_code
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code;

-- 只有协议资料明确写明不支持的组合才落 UNSUPPORTED。其余缺行仍是待确认。
-- 种子证据被撤回时同步停用旧结论，禁止让错误的“不支持”继续留在运行时。
UPDATE ent_protocol_mapping_t1
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND creator = 'seed'
  AND code LIKE 'unsupported-%'
  AND code NOT IN ('unsupported-fly-to-point-robot-ws', 'unsupported-takeoff-robot-ws', 'unsupported-return-robot-ws', 'unsupported-gas-on-robot-ws', 'unsupported-gas-off-robot-ws', 'unsupported-gimbalRotate-robot-ws', 'unsupported-hold-robot-ws', 'unsupported-hold-uav-ws', 'unsupported-action-2b44a1f882f54438b6a1864e3636c889-uav-ws', 'unsupported-action-1022d966da2b45f7b73e776112e6884b-uav-ws', 'unsupported-customDirName-uav-ws', 'unsupported-orientedShoot-uav-ws', 'unsupported-rotateYaw-uav-ws', 'unsupported-searchlightOff-uav-ws', 'unsupported-megaphone-uav-ws', 'unsupported-gimbalEvenlyRotate-uav-ws', 'unsupported-recordPointCloudResume-uav-ws', 'unsupported-searchlightOn-uav-ws', 'unsupported-searchlightStrobe-uav-ws', 'unsupported-recordPointCloudStart-uav-ws', 'unsupported-panoShot-uav-ws', 'unsupported-speakerTtsPlay-uav-ws', 'unsupported-focus-uav-ws', 'unsupported-lightModeOff-uav-ws', 'unsupported-speakerPlayOnce-uav-ws', 'unsupported-speakerPlayLoop-uav-ws', 'unsupported-screenDrag-uav-ws', 'unsupported-lightModeOn-uav-ws', 'unsupported-frameZoom-uav-ws', 'unsupported-cameraMode-uav-ws', 'unsupported-zoom-uav-ws', 'unsupported-focusMode-uav-ws', 'unsupported-focusValue-uav-ws', 'unsupported-screenSplit-uav-ws', 'unsupported-iso-uav-ws', 'unsupported-aperture-uav-ws', 'unsupported-speakerVolumeSet-uav-ws', 'unsupported-speakerTtsSet-uav-ws', 'unsupported-irMeteringPoint-uav-ws', 'unsupported-irMeteringMode-uav-ws', 'unsupported-irMeteringArea-uav-ws', 'unsupported-shutter-uav-ws', 'unsupported-exposureSet-uav-ws', 'unsupported-exposureMode-uav-ws', 'unsupported-videoStorage-uav-ws', 'unsupported-lightBrightnessSet-uav-ws', 'unsupported-photoStorage-uav-ws', 'unsupported-cameraAim-uav-ws', 'unsupported-lightModeStrobe-uav-ws', 'unsupported-lightModeAlternate-uav-ws', 'unsupported-lightModeFastStrobe-uav-ws', 'unsupported-speakerStop-uav-ws', 'unsupported-lightFineRight-uav-ws', 'unsupported-lightFineLeft-uav-ws', 'unsupported-lightCalibrate-uav-ws', 'unsupported-speakerReplay-uav-ws', 'unsupported-gimbalReset-uav-ws', 'unsupported-recordPointCloudStop-uav-ws', 'unsupported-photoStop-uav-ws', 'unsupported-hover-uav-ws', 'unsupported-gimbalRotate-uav-ws', 'unsupported-recordPointCloudPause-uav-ws', 'unsupported-searchlightOff-dji-pilot-1-16-1', 'unsupported-searchlightOn-dji-pilot-1-16-1', 'unsupported-searchlightStrobe-dji-pilot-1-16-1', 'unsupported-lightModeOff-dji-pilot-1-16-1', 'unsupported-lightModeOn-dji-pilot-1-16-1', 'unsupported-lightModeStrobe-dji-pilot-1-16-1', 'unsupported-lightModeFastStrobe-dji-pilot-1-16-1', 'unsupported-lightModeAlternate-dji-pilot-1-16-1', 'unsupported-lightBrightnessSet-dji-pilot-1-16-1', 'unsupported-lightCalibrate-dji-pilot-1-16-1', 'unsupported-lightFineLeft-dji-pilot-1-16-1', 'unsupported-lightFineRight-dji-pilot-1-16-1', 'unsupported-speakerStop-dji-pilot-1-16-1', 'unsupported-speakerReplay-dji-pilot-1-16-1', 'unsupported-speakerVolumeSet-dji-pilot-1-16-1', 'unsupported-speakerPlayOnce-dji-pilot-1-16-1', 'unsupported-speakerPlayLoop-dji-pilot-1-16-1', 'unsupported-speakerTtsPlay-dji-pilot-1-16-1', 'unsupported-speakerTtsSet-dji-pilot-1-16-1', 'unsupported-megaphone-dji-pilot-1-16-1', 'unsupported-action-2b44a1f882f54438b6a1864e3636c889-dji-pilot-1-16-1', 'unsupported-action-1022d966da2b45f7b73e776112e6884b-dji-pilot-1-16-1', 'unsupported-action-647f9f172c3b489ea862a29c0e8396a2-dji-pilot-1-16-1', 'unsupported-action-ab595a61ca164a2c9db74e1311e747a5-dji-pilot-1-16-1', 'unsupported-act-uav-takeoff-dji-pilot-1-16-1', 'unsupported-act-uav-land-dji-pilot-1-16-1', 'unsupported-action-647f9f172c3b489ea862a29c0e8396a2-dji-dock-1-16-1', 'unsupported-action-ab595a61ca164a2c9db74e1311e747a5-dji-dock-1-16-1', 'unsupported-act-uav-takeoff-dji-dock-1-16-1', 'unsupported-act-uav-land-dji-dock-1-16-1');

-- 协议正文已经明确不支持时，撤销该协议动作上旧的“已配置”结论。
UPDATE ent_protocol_mapping_t1 e
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('unsupported-fly-to-point-robot-ws', 'action-065340437bcb46788b7dc261fab81c63', 'robot-ws', '机器人对接协议'),
  ('unsupported-takeoff-robot-ws', 'action-2b44a1f882f54438b6a1864e3636c889', 'robot-ws', '机器人对接协议'),
  ('unsupported-return-robot-ws', 'action-1022d966da2b45f7b73e776112e6884b', 'robot-ws', '机器人对接协议'),
  ('unsupported-gas-on-robot-ws', 'action-647f9f172c3b489ea862a29c0e8396a2', 'robot-ws', '机器人对接协议'),
  ('unsupported-gas-off-robot-ws', 'action-ab595a61ca164a2c9db74e1311e747a5', 'robot-ws', '机器人对接协议'),
  ('unsupported-gimbalRotate-robot-ws', 'gimbalRotate', 'robot-ws', '机器人对接协议'),
  ('unsupported-hold-robot-ws', 'act-robot-hold', 'robot-ws', '机器人对接协议'),
  ('unsupported-hold-uav-ws', 'act-robot-hold', 'uav-ws', '无人机对接协议'),
  ('unsupported-action-2b44a1f882f54438b6a1864e3636c889-uav-ws', 'action-2b44a1f882f54438b6a1864e3636c889', 'uav-ws', '无人机对接协议'),
  ('unsupported-action-1022d966da2b45f7b73e776112e6884b-uav-ws', 'action-1022d966da2b45f7b73e776112e6884b', 'uav-ws', '无人机对接协议'),
  ('unsupported-customDirName-uav-ws', 'customDirName', 'uav-ws', '无人机对接协议'),
  ('unsupported-orientedShoot-uav-ws', 'orientedShoot', 'uav-ws', '无人机对接协议'),
  ('unsupported-rotateYaw-uav-ws', 'rotateYaw', 'uav-ws', '无人机对接协议'),
  ('unsupported-searchlightOff-uav-ws', 'searchlightOff', 'uav-ws', '无人机对接协议'),
  ('unsupported-megaphone-uav-ws', 'megaphone', 'uav-ws', '无人机对接协议'),
  ('unsupported-gimbalEvenlyRotate-uav-ws', 'gimbalEvenlyRotate', 'uav-ws', '无人机对接协议'),
  ('unsupported-recordPointCloudResume-uav-ws', 'recordPointCloudResume', 'uav-ws', '无人机对接协议'),
  ('unsupported-searchlightOn-uav-ws', 'searchlightOn', 'uav-ws', '无人机对接协议'),
  ('unsupported-searchlightStrobe-uav-ws', 'searchlightStrobe', 'uav-ws', '无人机对接协议'),
  ('unsupported-recordPointCloudStart-uav-ws', 'recordPointCloudStart', 'uav-ws', '无人机对接协议'),
  ('unsupported-panoShot-uav-ws', 'panoShot', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerTtsPlay-uav-ws', 'speakerTtsPlay', 'uav-ws', '无人机对接协议'),
  ('unsupported-focus-uav-ws', 'focus', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeOff-uav-ws', 'lightModeOff', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerPlayOnce-uav-ws', 'speakerPlayOnce', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerPlayLoop-uav-ws', 'speakerPlayLoop', 'uav-ws', '无人机对接协议'),
  ('unsupported-screenDrag-uav-ws', 'screenDrag', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeOn-uav-ws', 'lightModeOn', 'uav-ws', '无人机对接协议'),
  ('unsupported-frameZoom-uav-ws', 'frameZoom', 'uav-ws', '无人机对接协议'),
  ('unsupported-cameraMode-uav-ws', 'cameraMode', 'uav-ws', '无人机对接协议'),
  ('unsupported-zoom-uav-ws', 'zoom', 'uav-ws', '无人机对接协议'),
  ('unsupported-focusMode-uav-ws', 'focusMode', 'uav-ws', '无人机对接协议'),
  ('unsupported-focusValue-uav-ws', 'focusValue', 'uav-ws', '无人机对接协议'),
  ('unsupported-screenSplit-uav-ws', 'screenSplit', 'uav-ws', '无人机对接协议'),
  ('unsupported-iso-uav-ws', 'iso', 'uav-ws', '无人机对接协议'),
  ('unsupported-aperture-uav-ws', 'aperture', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerVolumeSet-uav-ws', 'speakerVolumeSet', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerTtsSet-uav-ws', 'speakerTtsSet', 'uav-ws', '无人机对接协议'),
  ('unsupported-irMeteringPoint-uav-ws', 'irMeteringPoint', 'uav-ws', '无人机对接协议'),
  ('unsupported-irMeteringMode-uav-ws', 'irMeteringMode', 'uav-ws', '无人机对接协议'),
  ('unsupported-irMeteringArea-uav-ws', 'irMeteringArea', 'uav-ws', '无人机对接协议'),
  ('unsupported-shutter-uav-ws', 'shutter', 'uav-ws', '无人机对接协议'),
  ('unsupported-exposureSet-uav-ws', 'exposureSet', 'uav-ws', '无人机对接协议'),
  ('unsupported-exposureMode-uav-ws', 'exposureMode', 'uav-ws', '无人机对接协议'),
  ('unsupported-videoStorage-uav-ws', 'videoStorage', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightBrightnessSet-uav-ws', 'lightBrightnessSet', 'uav-ws', '无人机对接协议'),
  ('unsupported-photoStorage-uav-ws', 'photoStorage', 'uav-ws', '无人机对接协议'),
  ('unsupported-cameraAim-uav-ws', 'cameraAim', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeStrobe-uav-ws', 'lightModeStrobe', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeAlternate-uav-ws', 'lightModeAlternate', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeFastStrobe-uav-ws', 'lightModeFastStrobe', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerStop-uav-ws', 'speakerStop', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightFineRight-uav-ws', 'lightFineRight', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightFineLeft-uav-ws', 'lightFineLeft', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightCalibrate-uav-ws', 'lightCalibrate', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerReplay-uav-ws', 'speakerReplay', 'uav-ws', '无人机对接协议'),
  ('unsupported-gimbalReset-uav-ws', 'gimbalReset', 'uav-ws', '无人机对接协议'),
  ('unsupported-recordPointCloudStop-uav-ws', 'recordPointCloudStop', 'uav-ws', '无人机对接协议'),
  ('unsupported-photoStop-uav-ws', 'photoStop', 'uav-ws', '无人机对接协议'),
  ('unsupported-hover-uav-ws', 'hover', 'uav-ws', '无人机对接协议'),
  ('unsupported-gimbalRotate-uav-ws', 'gimbalRotate', 'uav-ws', '无人机对接协议'),
  ('unsupported-recordPointCloudPause-uav-ws', 'recordPointCloudPause', 'uav-ws', '无人机对接协议'),
  ('unsupported-searchlightOff-dji-pilot-1-16-1', 'searchlightOff', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-searchlightOn-dji-pilot-1-16-1', 'searchlightOn', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-searchlightStrobe-dji-pilot-1-16-1', 'searchlightStrobe', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeOff-dji-pilot-1-16-1', 'lightModeOff', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeOn-dji-pilot-1-16-1', 'lightModeOn', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeStrobe-dji-pilot-1-16-1', 'lightModeStrobe', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeFastStrobe-dji-pilot-1-16-1', 'lightModeFastStrobe', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeAlternate-dji-pilot-1-16-1', 'lightModeAlternate', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightBrightnessSet-dji-pilot-1-16-1', 'lightBrightnessSet', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightCalibrate-dji-pilot-1-16-1', 'lightCalibrate', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightFineLeft-dji-pilot-1-16-1', 'lightFineLeft', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightFineRight-dji-pilot-1-16-1', 'lightFineRight', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerStop-dji-pilot-1-16-1', 'speakerStop', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerReplay-dji-pilot-1-16-1', 'speakerReplay', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerVolumeSet-dji-pilot-1-16-1', 'speakerVolumeSet', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerPlayOnce-dji-pilot-1-16-1', 'speakerPlayOnce', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerPlayLoop-dji-pilot-1-16-1', 'speakerPlayLoop', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerTtsPlay-dji-pilot-1-16-1', 'speakerTtsPlay', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerTtsSet-dji-pilot-1-16-1', 'speakerTtsSet', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-megaphone-dji-pilot-1-16-1', 'megaphone', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-2b44a1f882f54438b6a1864e3636c889-dji-pilot-1-16-1', 'action-2b44a1f882f54438b6a1864e3636c889', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-1022d966da2b45f7b73e776112e6884b-dji-pilot-1-16-1', 'action-1022d966da2b45f7b73e776112e6884b', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-647f9f172c3b489ea862a29c0e8396a2-dji-pilot-1-16-1', 'action-647f9f172c3b489ea862a29c0e8396a2', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-ab595a61ca164a2c9db74e1311e747a5-dji-pilot-1-16-1', 'action-ab595a61ca164a2c9db74e1311e747a5', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-act-uav-takeoff-dji-pilot-1-16-1', 'act-uav-takeoff', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-act-uav-land-dji-pilot-1-16-1', 'act-uav-land', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-647f9f172c3b489ea862a29c0e8396a2-dji-dock-1-16-1', 'action-647f9f172c3b489ea862a29c0e8396a2', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('unsupported-action-ab595a61ca164a2c9db74e1311e747a5-dji-dock-1-16-1', 'action-ab595a61ca164a2c9db74e1311e747a5', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('unsupported-act-uav-takeoff-dji-dock-1-16-1', 'act-uav-takeoff', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('unsupported-act-uav-land-dji-dock-1-16-1', 'act-uav-land', 'dji-dock-1.16.1', '大疆机场 1.16.1')
) AS v(code, action_code, protocol_version, version_label)
JOIN ent_action_t1 a
  ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.code <> v.code
  AND e.custom_fields->>'mapped_protocol_version' = v.protocol_version
  AND (e.custom_fields->'action_ref'->>'id')::bigint = a.id;

INSERT INTO ent_protocol_mapping_t1 (
  tenant_id, entity_type_code, model_id, name, code, status, custom_fields, creator, deleted
)
SELECT
  1, 'protocol_mapping', m.id, a.name || ' · ' || v.version_label || ' · 不支持',
  v.code, 1,
  jsonb_build_object(
    'action_ref', jsonb_build_object('id', a.id, 'entityTypeCode', 'action'),
    'mapped_protocol_version', v.protocol_version,
    'protocol_mapping_status', 'UNSUPPORTED',
    'protocol_instruction_ref', NULL,
    'param_slot_mapping_json', '[]'::jsonb
  ),
  'seed', false
FROM (VALUES
  ('unsupported-fly-to-point-robot-ws', 'action-065340437bcb46788b7dc261fab81c63', 'robot-ws', '机器人对接协议'),
  ('unsupported-takeoff-robot-ws', 'action-2b44a1f882f54438b6a1864e3636c889', 'robot-ws', '机器人对接协议'),
  ('unsupported-return-robot-ws', 'action-1022d966da2b45f7b73e776112e6884b', 'robot-ws', '机器人对接协议'),
  ('unsupported-gas-on-robot-ws', 'action-647f9f172c3b489ea862a29c0e8396a2', 'robot-ws', '机器人对接协议'),
  ('unsupported-gas-off-robot-ws', 'action-ab595a61ca164a2c9db74e1311e747a5', 'robot-ws', '机器人对接协议'),
  ('unsupported-gimbalRotate-robot-ws', 'gimbalRotate', 'robot-ws', '机器人对接协议'),
  ('unsupported-hold-robot-ws', 'act-robot-hold', 'robot-ws', '机器人对接协议'),
  ('unsupported-hold-uav-ws', 'act-robot-hold', 'uav-ws', '无人机对接协议'),
  ('unsupported-action-2b44a1f882f54438b6a1864e3636c889-uav-ws', 'action-2b44a1f882f54438b6a1864e3636c889', 'uav-ws', '无人机对接协议'),
  ('unsupported-action-1022d966da2b45f7b73e776112e6884b-uav-ws', 'action-1022d966da2b45f7b73e776112e6884b', 'uav-ws', '无人机对接协议'),
  ('unsupported-customDirName-uav-ws', 'customDirName', 'uav-ws', '无人机对接协议'),
  ('unsupported-orientedShoot-uav-ws', 'orientedShoot', 'uav-ws', '无人机对接协议'),
  ('unsupported-rotateYaw-uav-ws', 'rotateYaw', 'uav-ws', '无人机对接协议'),
  ('unsupported-searchlightOff-uav-ws', 'searchlightOff', 'uav-ws', '无人机对接协议'),
  ('unsupported-megaphone-uav-ws', 'megaphone', 'uav-ws', '无人机对接协议'),
  ('unsupported-gimbalEvenlyRotate-uav-ws', 'gimbalEvenlyRotate', 'uav-ws', '无人机对接协议'),
  ('unsupported-recordPointCloudResume-uav-ws', 'recordPointCloudResume', 'uav-ws', '无人机对接协议'),
  ('unsupported-searchlightOn-uav-ws', 'searchlightOn', 'uav-ws', '无人机对接协议'),
  ('unsupported-searchlightStrobe-uav-ws', 'searchlightStrobe', 'uav-ws', '无人机对接协议'),
  ('unsupported-recordPointCloudStart-uav-ws', 'recordPointCloudStart', 'uav-ws', '无人机对接协议'),
  ('unsupported-panoShot-uav-ws', 'panoShot', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerTtsPlay-uav-ws', 'speakerTtsPlay', 'uav-ws', '无人机对接协议'),
  ('unsupported-focus-uav-ws', 'focus', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeOff-uav-ws', 'lightModeOff', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerPlayOnce-uav-ws', 'speakerPlayOnce', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerPlayLoop-uav-ws', 'speakerPlayLoop', 'uav-ws', '无人机对接协议'),
  ('unsupported-screenDrag-uav-ws', 'screenDrag', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeOn-uav-ws', 'lightModeOn', 'uav-ws', '无人机对接协议'),
  ('unsupported-frameZoom-uav-ws', 'frameZoom', 'uav-ws', '无人机对接协议'),
  ('unsupported-cameraMode-uav-ws', 'cameraMode', 'uav-ws', '无人机对接协议'),
  ('unsupported-zoom-uav-ws', 'zoom', 'uav-ws', '无人机对接协议'),
  ('unsupported-focusMode-uav-ws', 'focusMode', 'uav-ws', '无人机对接协议'),
  ('unsupported-focusValue-uav-ws', 'focusValue', 'uav-ws', '无人机对接协议'),
  ('unsupported-screenSplit-uav-ws', 'screenSplit', 'uav-ws', '无人机对接协议'),
  ('unsupported-iso-uav-ws', 'iso', 'uav-ws', '无人机对接协议'),
  ('unsupported-aperture-uav-ws', 'aperture', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerVolumeSet-uav-ws', 'speakerVolumeSet', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerTtsSet-uav-ws', 'speakerTtsSet', 'uav-ws', '无人机对接协议'),
  ('unsupported-irMeteringPoint-uav-ws', 'irMeteringPoint', 'uav-ws', '无人机对接协议'),
  ('unsupported-irMeteringMode-uav-ws', 'irMeteringMode', 'uav-ws', '无人机对接协议'),
  ('unsupported-irMeteringArea-uav-ws', 'irMeteringArea', 'uav-ws', '无人机对接协议'),
  ('unsupported-shutter-uav-ws', 'shutter', 'uav-ws', '无人机对接协议'),
  ('unsupported-exposureSet-uav-ws', 'exposureSet', 'uav-ws', '无人机对接协议'),
  ('unsupported-exposureMode-uav-ws', 'exposureMode', 'uav-ws', '无人机对接协议'),
  ('unsupported-videoStorage-uav-ws', 'videoStorage', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightBrightnessSet-uav-ws', 'lightBrightnessSet', 'uav-ws', '无人机对接协议'),
  ('unsupported-photoStorage-uav-ws', 'photoStorage', 'uav-ws', '无人机对接协议'),
  ('unsupported-cameraAim-uav-ws', 'cameraAim', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeStrobe-uav-ws', 'lightModeStrobe', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeAlternate-uav-ws', 'lightModeAlternate', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightModeFastStrobe-uav-ws', 'lightModeFastStrobe', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerStop-uav-ws', 'speakerStop', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightFineRight-uav-ws', 'lightFineRight', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightFineLeft-uav-ws', 'lightFineLeft', 'uav-ws', '无人机对接协议'),
  ('unsupported-lightCalibrate-uav-ws', 'lightCalibrate', 'uav-ws', '无人机对接协议'),
  ('unsupported-speakerReplay-uav-ws', 'speakerReplay', 'uav-ws', '无人机对接协议'),
  ('unsupported-gimbalReset-uav-ws', 'gimbalReset', 'uav-ws', '无人机对接协议'),
  ('unsupported-recordPointCloudStop-uav-ws', 'recordPointCloudStop', 'uav-ws', '无人机对接协议'),
  ('unsupported-photoStop-uav-ws', 'photoStop', 'uav-ws', '无人机对接协议'),
  ('unsupported-hover-uav-ws', 'hover', 'uav-ws', '无人机对接协议'),
  ('unsupported-gimbalRotate-uav-ws', 'gimbalRotate', 'uav-ws', '无人机对接协议'),
  ('unsupported-recordPointCloudPause-uav-ws', 'recordPointCloudPause', 'uav-ws', '无人机对接协议'),
  ('unsupported-searchlightOff-dji-pilot-1-16-1', 'searchlightOff', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-searchlightOn-dji-pilot-1-16-1', 'searchlightOn', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-searchlightStrobe-dji-pilot-1-16-1', 'searchlightStrobe', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeOff-dji-pilot-1-16-1', 'lightModeOff', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeOn-dji-pilot-1-16-1', 'lightModeOn', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeStrobe-dji-pilot-1-16-1', 'lightModeStrobe', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeFastStrobe-dji-pilot-1-16-1', 'lightModeFastStrobe', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightModeAlternate-dji-pilot-1-16-1', 'lightModeAlternate', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightBrightnessSet-dji-pilot-1-16-1', 'lightBrightnessSet', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightCalibrate-dji-pilot-1-16-1', 'lightCalibrate', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightFineLeft-dji-pilot-1-16-1', 'lightFineLeft', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-lightFineRight-dji-pilot-1-16-1', 'lightFineRight', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerStop-dji-pilot-1-16-1', 'speakerStop', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerReplay-dji-pilot-1-16-1', 'speakerReplay', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerVolumeSet-dji-pilot-1-16-1', 'speakerVolumeSet', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerPlayOnce-dji-pilot-1-16-1', 'speakerPlayOnce', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerPlayLoop-dji-pilot-1-16-1', 'speakerPlayLoop', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerTtsPlay-dji-pilot-1-16-1', 'speakerTtsPlay', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-speakerTtsSet-dji-pilot-1-16-1', 'speakerTtsSet', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-megaphone-dji-pilot-1-16-1', 'megaphone', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-2b44a1f882f54438b6a1864e3636c889-dji-pilot-1-16-1', 'action-2b44a1f882f54438b6a1864e3636c889', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-1022d966da2b45f7b73e776112e6884b-dji-pilot-1-16-1', 'action-1022d966da2b45f7b73e776112e6884b', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-647f9f172c3b489ea862a29c0e8396a2-dji-pilot-1-16-1', 'action-647f9f172c3b489ea862a29c0e8396a2', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-ab595a61ca164a2c9db74e1311e747a5-dji-pilot-1-16-1', 'action-ab595a61ca164a2c9db74e1311e747a5', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-act-uav-takeoff-dji-pilot-1-16-1', 'act-uav-takeoff', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-act-uav-land-dji-pilot-1-16-1', 'act-uav-land', 'dji-pilot-1.16.1', '大疆 Pilot 1.16.1'),
  ('unsupported-action-647f9f172c3b489ea862a29c0e8396a2-dji-dock-1-16-1', 'action-647f9f172c3b489ea862a29c0e8396a2', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('unsupported-action-ab595a61ca164a2c9db74e1311e747a5-dji-dock-1-16-1', 'action-ab595a61ca164a2c9db74e1311e747a5', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('unsupported-act-uav-takeoff-dji-dock-1-16-1', 'act-uav-takeoff', 'dji-dock-1.16.1', '大疆机场 1.16.1'),
  ('unsupported-act-uav-land-dji-dock-1-16-1', 'act-uav-land', 'dji-dock-1.16.1', '大疆机场 1.16.1')
) AS v(code, action_code, protocol_version, version_label)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'protocol_mapping_standard'
JOIN ent_action_t1 a ON a.deleted = false AND a.tenant_id = 1 AND a.code = v.action_code
WHERE NOT EXISTS (
  SELECT 1
  FROM ent_protocol_mapping_t1 e
  WHERE e.deleted = false
    AND e.tenant_id = 1
    AND e.custom_fields->>'mapped_protocol_version' = v.protocol_version
    AND (e.custom_fields->'action_ref'->>'id')::bigint = a.id
);

-- 现有完整对照一次写明已配置。运行时不再把缺少状态猜成已配置。
UPDATE ent_protocol_mapping_t1
SET custom_fields = jsonb_set(custom_fields, '{protocol_mapping_status}', '"CONFIGURED"'::jsonb, true),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND custom_fields ? 'protocol_instruction_ref'
  AND NOT (custom_fields ? 'protocol_mapping_status');

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
