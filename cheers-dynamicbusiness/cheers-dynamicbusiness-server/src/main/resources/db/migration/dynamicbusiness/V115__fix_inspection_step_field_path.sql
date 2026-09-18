-- V115: 修复 inspection_item 步骤字段为基础字段统一路径
-- 根因：V114 仅补了元数据，未给 inspection_item 专表补 step_tree_json 物理列，导致 query-by-scene 报列不存在。
-- 修复：
-- 1) 补 ent_inspection_item / ent_inspection_item_t* 的 step_tree_json 物理列；
-- 2) 确保 step_tree_json 保持 inspection_item 基础字段（不是模型后补）；
-- 3) 模型字段分配统一标记 BASE，并清理历史 action_tree_json 分配。

SET search_path TO dynamicbusiness, public;

-- 1) 补 inspection_item 物理列（主表 + 租户分表）
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
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS step_tree_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
      tbl
    );
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.step_tree_json IS %L',
      tbl,
      '步骤树权威 JSON（检查项基础字段）'
    );
    -- 若历史曾把步骤放进 custom_fields，迁回基础字段列
    EXECUTE format(
      $sql$
      UPDATE dynamicbusiness.%I
      SET step_tree_json = CASE
            WHEN custom_fields ? 'step_tree_json'
              THEN COALESCE((custom_fields ->> 'step_tree_json')::jsonb, '[]'::jsonb)
            WHEN custom_fields ? 'action_tree_json'
              THEN COALESCE((custom_fields ->> 'action_tree_json')::jsonb, '[]'::jsonb)
            ELSE step_tree_json
          END,
          updater = COALESCE(updater, 'flyway-v115'),
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = FALSE
        AND (
          custom_fields ? 'step_tree_json'
          OR custom_fields ? 'action_tree_json'
        )
      $sql$,
      tbl
    );
  END LOOP;
END $$;

-- 2) 字段库确保存在 step_tree_json（兼容 V114 前未写入环境）
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
  'step_tree_json',
  '步骤树',
  'TEXT',
  NULL,
  '检查项步骤树结构字段',
  'BASE',
  1,
  1,
  'NONE',
  NULL,
  NULL,
  'step_tree_json',
  t.tenant_id,
  'flyway-v115'
FROM tenants t
ON CONFLICT (code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET
  name = EXCLUDED.name,
  source = EXCLUDED.source,
  semantic_type = EXCLUDED.semantic_type,
  status = EXCLUDED.status,
  updater = 'flyway-v115',
  update_time = CURRENT_TIMESTAMP;

-- 3) inspection_item 基础字段保持有效
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
  'step_tree_json',
  '步骤树',
  f.type,
  FALSE,
  '[]',
  '检查项步骤树结构字段',
  '{"createVisible":false,"editVisible":false,"detailVisible":false}',
  930,
  1,
  f.tenant_id,
  'flyway-v115'
FROM dynamic_field f
WHERE f.deleted = FALSE
  AND f.code = 'step_tree_json'
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
  updater = 'flyway-v115',
  update_time = CURRENT_TIMESTAMP;

-- 4) inspection_item 型号字段分配统一保持 BASE
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  m.code,
  'step_tree_json',
  FALSE, FALSE, FALSE, FALSE, 930,
  '[]',
  NULL,
  'BASE',
  m.tenant_id,
  'flyway-v115'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.deleted = FALSE
 AND f.code = 'step_tree_json'
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
  updater = 'flyway-v115',
  update_time = CURRENT_TIMESTAMP;

-- 5) 下线 inspection_item 历史 action_tree_json 分配，避免双键并行
UPDATE dynamic_model_field_assignment a
SET deleted = TRUE,
    updater = 'flyway-v115',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.model_id = m.id
  AND a.deleted = FALSE
  AND m.deleted = FALSE
  AND m.entity_type_code = 'inspection_item'
  AND a.field_code = 'action_tree_json';

-- 6) 清理检查项 CRUD 缓存，避免沿用旧投影
DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'inspection_item';
