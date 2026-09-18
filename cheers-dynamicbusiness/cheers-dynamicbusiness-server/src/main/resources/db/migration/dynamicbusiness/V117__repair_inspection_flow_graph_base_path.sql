-- V117: 修复 inspection_item 的 flow_graph_json 统一基础字段路径
-- 目标：
-- 1) 补 ent_inspection_item / ent_inspection_item_t* 的 flow_graph_json 物理列；
-- 2) 确保 inspection_item 的 flow_graph_json 基础字段有效；
-- 3) 检查项模型分配 flow_graph_json 统一为 BASE；
-- 4) 清理检查项 CRUD 缓存，触发表单重算。

SET search_path TO dynamicbusiness, public;

-- 1) 补 inspection_item 实体表物理列
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_inspection_item(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS flow_graph_json JSONB NOT NULL DEFAULT ''{}''::jsonb',
      tbl
    );
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.flow_graph_json IS %L',
      tbl,
      '流程图权威 JSON（检查项基础字段）'
    );
  END LOOP;
END $$;

-- 2) 字段库确保存在 flow_graph_json
WITH tenants AS (
  SELECT DISTINCT tenant_id
  FROM dynamic_model
  WHERE deleted = FALSE
    AND entity_type_code = 'inspection_item'
)
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  'flow_graph_json',
  '流程图',
  'TEXT',
  NULL,
  '检查项流程图结构字段',
  'BASE',
  1,
  1,
  'NONE',
  NULL,
  NULL,
  'sop_flow_graph',
  t.tenant_id,
  'flyway-v117'
FROM tenants t
ON CONFLICT (code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET
  name = EXCLUDED.name,
  source = EXCLUDED.source,
  semantic_type = EXCLUDED.semantic_type,
  status = EXCLUDED.status,
  updater = 'flyway-v117',
  update_time = CURRENT_TIMESTAMP;

-- 3) 检查项基础字段确保有效
INSERT INTO dynamic_entity_type_base_field (
  entity_type_code,
  library_field_id,
  field_code,
  field_name,
  data_type,
  required,
  default_value,
  description,
  type_config,
  sort_order,
  status,
  tenant_id,
  creator
)
SELECT
  'inspection_item',
  f.id,
  'flow_graph_json',
  '流程图',
  f.type,
  FALSE,
  '{}',
  '检查项流程图结构字段',
  '{"createVisible":false,"editVisible":false,"detailVisible":false}',
  940,
  1,
  f.tenant_id,
  'flyway-v117'
FROM dynamic_field f
WHERE f.deleted = FALSE
  AND f.code = 'flow_graph_json'
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = FALSE
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
  updater = 'flyway-v117',
  update_time = CURRENT_TIMESTAMP;

-- 4) 模型字段分配统一为 BASE
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  m.code,
  'flow_graph_json',
  FALSE, FALSE, FALSE, FALSE, 940,
  '{}',
  NULL,
  'BASE',
  m.tenant_id,
  'flyway-v117'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.deleted = FALSE
 AND f.code = 'flow_graph_json'
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = FALSE
  AND m.entity_type_code = 'inspection_item'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  field_source = EXCLUDED.field_source,
  updater = 'flyway-v117',
  update_time = CURRENT_TIMESTAMP;

-- 5) 清理检查项 CRUD 缓存
DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'inspection_item';
