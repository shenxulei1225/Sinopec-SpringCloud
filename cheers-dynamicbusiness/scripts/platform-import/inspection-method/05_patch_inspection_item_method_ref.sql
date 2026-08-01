-- ============================================================================
-- inspection-method · 05 检查内容绑定 method_template_id（REF → inspection_method）
-- 探查结论：inspection_item 无文本「检查方法」基础字段；
--   旧展示位为型号分配 FLD-INS-003（ENUM「检查方法」）。不改类型复用，新增 REF 并停用旧分配。
-- 前置：Flyway V44（ent_inspection_item.method_template_id 列）
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 字段库：method_template_id（编码 = 物理列名）
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
  'method_template_id', '检查方法', 'ENTITY_REF',
  NULL, '检查内容绑定的方法模板（单选 REF → inspection_method）；列存实体 id',
  'BASE', 1,
  1, 'NONE',
  NULL, 'dynamic-entity:inspection_method',
  'method_template_id', 1, 'seed'
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

-- 2) 挂到 inspection_item 基础字段（data_type=REF；目标由字段库 provider_code 解析）
INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'inspection_item',
  f.id,
  'method_template_id',
  '检查方法',
  'REF',
  FALSE,
  NULL,
  '方法模板（inspection_method）；列存 id，详情走 REF 查询增强展示名称',
  NULL,
  30,
  1,
  1,
  'seed'
FROM dynamic_field f
WHERE f.deleted = false AND f.tenant_id = 1 AND f.code = 'method_template_id'
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

-- 3) 型号字段分配（inspection_item 单型号）— 详情/表单可见
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, f.code,
  false, true, true, false, 30,
  NULL, 'inspection_method', 'BASE', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'method_template_id'
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.entity_type_code = 'inspection_item'
  AND m.code = 'inspection_item'
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

-- 4) 废弃旧 ENUM「检查方法」FLD-INS-003（不停改类型；停用分配 + 字段标注废弃）
UPDATE dynamic_model_field_assignment a
SET
  deleted = true,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.model_code = 'inspection_item'
  AND (
    a.field_code = 'FLD-INS-003'
    OR a.field_id IN (
      SELECT id FROM dynamic_field
      WHERE deleted = false AND code = 'FLD-INS-003'
    )
  );

UPDATE dynamic_field
SET
  status = 0,
  description = COALESCE(description, '')
    || CASE
         WHEN COALESCE(description, '') LIKE '%[deprecated:use method_template_id]%' THEN ''
         ELSE ' [deprecated:use method_template_id]'
       END,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'FLD-INS-003'
  AND tenant_id = 1;
