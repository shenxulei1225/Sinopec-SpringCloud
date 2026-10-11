-- ============================================================================
-- 大疆上云执行设备
-- 前置：Flyway V140（cloud_access_mode / gateway_sn / aircraft_sn）
-- 机场用 dji-dock-1.16.1，Pilot 用 dji-pilot-1.16.1。1.16.1 是上云 API 发布号。与东古 uav-ws 分开。都挂在金桥。
-- 网关序列号、飞机序列号留空，实机编号到位后再写，禁止编造。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 协议枚举加上大疆上云（不改东古 uav-ws 的取值）
UPDATE dynamic_field
SET
  options = '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"},{"label":"大疆机场 1.16.1","value":"dji-dock-1.16.1"},{"label":"大疆 Pilot 1.16.1","value":"dji-pilot-1.16.1"}]',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE code IN ('platform_protocol', 'protocol_versions', 'mapped_protocol_version', 'protocol_version')
  AND tenant_id = 1
  AND deleted = false;

-- 2) 接入方式
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES (
  'cloud_access_mode',
  '接入方式',
  'ENUM',
  NULL,
  '大疆上云时，这台飞机经机场还是经 Pilot 连上云。其它协议留空。',
  'BASE',
  1,
  NULL,
  'BTREE',
  '[{"label":"机场","value":"dock"},{"label":"Pilot","value":"pilot"}]',
  NULL,
  'cloud_access_mode',
  1,
  'seed'
)
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

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES
(
  'gateway_sn',
  '网关序列号',
  'TEXT',
  NULL,
  '上云网关序列号。机场填机场的序列号，Pilot 填遥控器的序列号。没有实机编号就空着。',
  'BASE', 1, NULL, 'BTREE', NULL, NULL, 'gateway_sn', 1, 'seed'
),
(
  'aircraft_sn',
  '飞机序列号',
  'TEXT',
  NULL,
  '这台网关下面实际飞行的飞机序列号。没有实机编号就空着。',
  'BASE', 1, NULL, 'BTREE', NULL, NULL, 'aircraft_sn', 1, 'seed'
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

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'equipment',
  f.id,
  f.code,
  f.name,
  f.type,
  false,
  NULL,
  f.description,
  '{"createVisible":true,"editVisible":true,"detailVisible":true}'::jsonb,
  CASE f.code
    WHEN 'cloud_access_mode' THEN 8
    WHEN 'gateway_sn' THEN 9
    WHEN 'aircraft_sn' THEN 10
  END,
  1,
  false,
  true,
  false,
  1,
  'seed'
FROM dynamic_field f
WHERE f.code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn')
  AND f.deleted = false
  AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = 1,
  type_config = EXCLUDED.type_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
)
SELECT
  m.id,
  m.code,
  f.id,
  f.code,
  false,
  false,
  true,
  false,
  CASE f.code
    WHEN 'cloud_access_mode' THEN 8
    WHEN 'gateway_sn' THEN 9
    WHEN 'aircraft_sn' THEN 10
  END,
  'BASE',
  m.tenant_id,
  'seed'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn')
 AND f.deleted = false
 AND f.tenant_id = 1
WHERE m.entity_type_code = 'equipment'
  AND m.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  model_id = EXCLUDED.model_id,
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_config
SET
  physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
    || '{"cloud_access_mode":{"type":"VARCHAR","column":"cloud_access_mode"},"gateway_sn":{"type":"VARCHAR","column":"gateway_sn"},"aircraft_sn":{"type":"VARCHAR","column":"aircraft_sn"}}'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND tenant_id = 1
  AND deleted = false;

UPDATE dynamic_entity_type
SET
  physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
    || '{"cloud_access_mode":{"type":"VARCHAR","column":"cloud_access_mode"},"gateway_sn":{"type":"VARCHAR","column":"gateway_sn"},"aircraft_sn":{"type":"VARCHAR","column":"aircraft_sn"}}'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE code = 'equipment'
  AND tenant_id = 1
  AND deleted = false;

-- 3) 金桥两台执行设备。序列号空着。
-- 机场这台的产品型号是「大疆机场」（dji-dock-1），不是按体型登记的「小型无人机」。
-- Pilot 这台的产品型号是 Matrice 30（dji-m30）。台账没有写明具体哪一款时，按任意一款大疆飞机落在这一款。
DO $$
BEGIN
  IF to_regclass('dynamicbusiness.ent_equipment_t1') IS NULL THEN
    RAISE NOTICE 'ent_equipment_t1 尚不存在，跳过大疆设备行';
    RETURN;
  END IF;

  INSERT INTO ent_equipment_t1 (
    id, entity_type_code, model_id, name, code, facility_id,
    device_code, status, sort, execution_means, platform_protocol, protocol_versions,
    cloud_access_mode, gateway_sn, aircraft_sn,
    creator, updater, deleted, tenant_id, guid
  )
  SELECT
    900157, 'equipment', dock_model.id, '金桥大疆机场无人机', 'EQ-DJI-DOCK-JQ-01', 44,
    'EQ-DJI-DOCK-JQ-01', 1, 30, '["UAV"]'::jsonb, 'dji-dock-1.16.1', '["dji-dock-1.16.1"]'::jsonb,
    'dock', NULL, NULL,
    'seed', 'seed', false, 1, 'guid-eq-dji-dock-jq-01'
  FROM dynamic_model AS dock_model
  WHERE dock_model.code = 'dji-dock-1'
    AND dock_model.tenant_id = 1
    AND dock_model.deleted = false
    AND EXISTS (SELECT 1 FROM ent_facility_t1 WHERE id = 44 AND deleted = false)
    AND NOT EXISTS (
      SELECT 1 FROM ent_equipment_t1 WHERE code = 'EQ-DJI-DOCK-JQ-01' AND deleted = false
    );

  INSERT INTO ent_equipment_t1 (
    id, entity_type_code, model_id, name, code, facility_id,
    device_code, status, sort, execution_means, platform_protocol, protocol_versions,
    cloud_access_mode, gateway_sn, aircraft_sn,
    creator, updater, deleted, tenant_id, guid
  )
  SELECT
    900158, 'equipment', pilot_model.id, '金桥大疆 Pilot 无人机', 'EQ-DJI-PILOT-JQ-01', 44,
    'EQ-DJI-PILOT-JQ-01', 1, 40, '["UAV"]'::jsonb, 'dji-pilot-1.16.1', '["dji-pilot-1.16.1"]'::jsonb,
    'pilot', NULL, NULL,
    'seed', 'seed', false, 1, 'guid-eq-dji-pilot-jq-01'
  FROM dynamic_model AS pilot_model
  WHERE pilot_model.code = 'dji-m30'
    AND pilot_model.tenant_id = 1
    AND pilot_model.deleted = false
    AND EXISTS (SELECT 1 FROM ent_facility_t1 WHERE id = 44 AND deleted = false)
    AND NOT EXISTS (
      SELECT 1 FROM ent_equipment_t1 WHERE code = 'EQ-DJI-PILOT-JQ-01' AND deleted = false
    );

  PERFORM setval(
    'ent_equipment_t1_id_seq',
    GREATEST((SELECT MAX(id) FROM ent_equipment_t1), 1)
  );

  INSERT INTO dynamic_entity_category_relation_t1 (
    entity_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id
  )
  SELECT e.id, c.category_id, 'equipment', 1024, 'seed', 'seed', false, 1
  FROM ent_equipment_t1 e
  CROSS JOIN (VALUES (409), (2104797)) AS c(category_id)
  WHERE e.code IN ('EQ-DJI-DOCK-JQ-01', 'EQ-DJI-PILOT-JQ-01')
    AND e.deleted = false
    AND NOT EXISTS (
      SELECT 1 FROM dynamic_entity_category_relation_t1 r
      WHERE r.entity_id = e.id
        AND r.category_id = c.category_id
        AND r.deleted = false
    );
END $$;
