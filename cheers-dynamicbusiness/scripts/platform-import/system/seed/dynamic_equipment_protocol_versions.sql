-- ============================================================================
-- 设备 · 当前使用的协议 + 支持的协议
-- 前置：Flyway V128（protocol_versions 数组列）+ V133（platform_protocol 单值列）
-- 定稿：当前使用的协议单选一行可改；支持的协议多选落已有 protocol_versions 列。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 字段库：当前使用的平台对接协议（单选）
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES (
  'platform_protocol',
  '设备默认使用的协议',
  'ENUM',
  NULL,
  '这台设备默认按哪一种平台对接协议下发。不是软件版本，也不是支持清单。任务里可临时改，不改就用这条。',
  'BASE',
  1,
  NULL,
  'BTREE',
  '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"}]',
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
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2) 挂到 equipment 类型基础字段（靠前，避免沉在长表单最底）
INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'equipment',
  f.id,
  'platform_protocol',
  '设备默认使用的协议',
  'ENUM',
  false,
  NULL,
  '这台设备默认按哪一种平台对接协议下发。详情只显示当前这一条，双击可改。任务里可临时改，不改就用这条。',
  '{"createVisible":true,"editVisible":true,"detailVisible":true}'::jsonb,
  6,
  1,
  false,
  true,
  false,
  1,
  'seed'
FROM dynamic_field f
WHERE f.code = 'platform_protocol'
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
  sort_order = EXCLUDED.sort_order,
  status = 1,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  type_config = EXCLUDED.type_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 3) 挂到全部设备型号
INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
)
SELECT
  m.id,
  m.code,
  f.id,
  'platform_protocol',
  false,
  false,
  true,
  false,
  6,
  'BASE',
  m.tenant_id,
  'seed'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'platform_protocol'
 AND f.deleted = false
 AND f.tenant_id = 1
WHERE m.entity_type_code = 'equipment'
  AND m.deleted = false
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

-- 4) 支持的协议：复用 protocol_versions 列，界面名禁止再叫「协议版本」
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
VALUES (
  'protocol_versions',
  '支持的协议',
  'MULTI_SELECT',
  NULL,
  '这台设备支持哪些平台对接协议。双击多选。不是软件版本，也不是当前正在用的那一条。',
  'BASE',
  1,
  NULL,
  'GIN',
  '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"}]',
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
  'protocol_versions',
  '支持的协议',
  'MULTI_SELECT',
  false,
  NULL,
  '这台设备支持哪些平台对接协议。双击多选。',
  '{"createVisible":true,"editVisible":true,"detailVisible":true}'::jsonb,
  7,
  1,
  false,
  true,
  false,
  1,
  'seed'
FROM dynamic_field f
WHERE f.code = 'protocol_versions'
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
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
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
  'protocol_versions',
  false,
  false,
  true,
  false,
  7,
  'BASE',
  m.tenant_id,
  'seed'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'protocol_versions'
 AND f.deleted = false
 AND f.tenant_id = 1
WHERE m.entity_type_code = 'equipment'
  AND m.deleted = false
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

-- 5) 专用列映射
UPDATE dynamic_entity_type_config
SET
  physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
    || '{"platform_protocol":{"type":"VARCHAR","column":"platform_protocol"},"protocol_versions":{"type":"JSONB","column":"protocol_versions"}}'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND tenant_id = 1
  AND deleted = false;

UPDATE dynamic_entity_type
SET
  physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
    || '{"platform_protocol":{"type":"VARCHAR","column":"platform_protocol"},"protocol_versions":{"type":"JSONB","column":"protocol_versions"}}'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE code = 'equipment'
  AND tenant_id = 1
  AND deleted = false;
