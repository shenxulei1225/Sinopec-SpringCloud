-- ============================================================================
-- 设备 · 当前使用的协议 + 支持的协议
-- 这两项是扩展字段，不进设备类型基础字段，不落专用表列。
-- 分给机场（dji-dock-*）、飞机（dji-m*）、东古小型、东古大型、机器人。摄像机、遥控器、储罐不继承。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES (
  'platform_protocol',
  '设备默认使用的协议',
  'ENUM',
  NULL,
  '这台设备默认按哪一种平台对接协议下发。大疆机场和大疆 Pilot 的选项带上云 API 发布号 1.16.1。不是支持清单。任务里可临时改，不改就用这条。',
  'USER',
  1,
  NULL,
  'NONE',
  '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"},{"label":"大疆机场 1.16.1","value":"dji-dock-1.16.1"},{"label":"大疆 Pilot 1.16.1","value":"dji-pilot-1.16.1"}]',
  NULL,
  'platform_protocol',
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
  index_strategy = EXCLUDED.index_strategy,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES (
  'protocol_versions',
  '支持的协议',
  'MULTI_SELECT',
  NULL,
  '这台设备支持哪些平台对接协议。双击多选。大疆机场和大疆 Pilot 的选项带上云 API 发布号 1.16.1。不是当前正在用的那一条。',
  'USER',
  1,
  NULL,
  'NONE',
  '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"},{"label":"大疆机场 1.16.1","value":"dji-dock-1.16.1"},{"label":"大疆 Pilot 1.16.1","value":"dji-pilot-1.16.1"}]',
  NULL,
  NULL,
  1,
  'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  source = EXCLUDED.source,
  index_strategy = EXCLUDED.index_strategy,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_base_field
SET status = 0,
    deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND field_code IN ('platform_protocol', 'protocol_versions')
  AND deleted = false;

UPDATE dynamic_model_field_assignment a
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.model_id = m.id
  AND a.deleted = false
  AND a.field_code IN ('platform_protocol', 'protocol_versions')
  AND m.entity_type_code = 'equipment'
  AND NOT (
    m.code LIKE 'dji-dock-%'
    OR m.code LIKE 'dji-m%'
    OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
    OR (
      m.name LIKE '%机器人%'
      AND m.name NOT LIKE '%无人机%'
      AND m.code NOT LIKE 'dji-%'
      AND m.code NOT LIKE 'donggu-%'
      AND m.code NOT LIKE 'hik-%'
    )
  );

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
  false,
  false,
  CASE WHEN f.code = 'platform_protocol' THEN 6 ELSE 7 END,
  'CUSTOM',
  m.tenant_id,
  'seed'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code IN ('platform_protocol', 'protocol_versions')
 AND f.deleted = false
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (
    m.code LIKE 'dji-dock-%'
    OR m.code LIKE 'dji-m%'
    OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
    OR (
      m.name LIKE '%机器人%'
      AND m.name NOT LIKE '%无人机%'
      AND m.code NOT LIKE 'dji-%'
      AND m.code NOT LIKE 'donggu-%'
      AND m.code NOT LIKE 'hik-%'
    )
  )
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  model_id = EXCLUDED.model_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type
SET physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
      - 'platform_protocol' - 'protocol_versions',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'equipment'
  AND deleted = false;

UPDATE dynamic_entity_type_config
SET physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
      - 'platform_protocol' - 'protocol_versions',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND deleted = false;
