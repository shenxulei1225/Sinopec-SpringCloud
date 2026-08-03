-- ============================================================================
-- inspection-method · 15 实例挂靠字段（equipment_id / inspection_item_id）
-- 前置：Flyway V50（物理列）；不改 MULTI 字段 equipment_ids 类型，新建单选 equipment_id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 字段库：equipment_id（单选 REF → equipment）
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'equipment_id', '所属设备', 'ENTITY_REF',
  NULL, '方法实例挂靠的设备（单选 REF → equipment）；模板行为空；禁止用本类 model_id 冒充',
  'BASE', 1,
  1, 'NONE',
  NULL, 'dynamic-entity:equipment',
  'equipment_id', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  max_relations = EXCLUDED.max_relations,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2) 字段库：inspection_item_id（单选 REF → inspection_item）
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'inspection_item_id', '对应检查内容', 'ENTITY_REF',
  NULL, '方法实例对应的检查内容（单选 REF → inspection_item）；模板行为空',
  'BASE', 1,
  1, 'NONE',
  NULL, 'dynamic-entity:inspection_item',
  'inspection_item_id', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  max_relations = EXCLUDED.max_relations,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 3) 挂到 inspection_method 基础字段（类型级 required=false；实例必填由创建侧保证）
INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'inspection_method',
  f.id,
  'equipment_id',
  '所属设备',
  'REF',
  FALSE,
  NULL,
  '方法实例所属设备；模板为空',
  NULL,
  30,
  1,
  1,
  'seed'
FROM dynamic_field f
WHERE f.deleted = false AND f.tenant_id = 1 AND f.code = 'equipment_id'
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'inspection_method',
  f.id,
  'inspection_item_id',
  '对应检查内容',
  'REF',
  FALSE,
  NULL,
  '方法实例对应检查内容；模板为空',
  NULL,
  40,
  1,
  1,
  'seed'
FROM dynamic_field f
WHERE f.deleted = false AND f.tenant_id = 1 AND f.code = 'inspection_item_id'
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 4) 型号字段分配（可筛：按设备列已准备实例）
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, f.code,
  false, true, true, false, 30,
  NULL, 'equipment', 'BASE', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'equipment_id'
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.entity_type_code = 'inspection_method'
  AND m.code = 'inspection_method'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  sort = EXCLUDED.sort,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  target_entity_type = EXCLUDED.target_entity_type,
  field_source = EXCLUDED.field_source,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, f.code,
  false, true, true, false, 40,
  NULL, 'inspection_item', 'BASE', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'inspection_item_id'
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.entity_type_code = 'inspection_method'
  AND m.code = 'inspection_method'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  sort = EXCLUDED.sort,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  target_entity_type = EXCLUDED.target_entity_type,
  field_source = EXCLUDED.field_source,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
