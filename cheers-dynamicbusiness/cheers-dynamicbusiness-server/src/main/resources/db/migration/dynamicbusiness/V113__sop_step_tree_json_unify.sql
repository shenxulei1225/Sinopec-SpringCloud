-- V113: SOP 步骤字段统一为 step_tree_json（退役 action_tree_json 作为步骤权威）
-- 目标：
-- 1) 物理列新增 step_tree_json，并从 action_tree_json 一次性回填存量；
-- 2) 元数据将 SOP 的步骤字段编码切到 step_tree_json；
-- 3) 清理 CRUD 缓存，避免继续投影旧字段。

SET search_path TO dynamicbusiness, public;

-- 1) ent_sop / ent_sop_t* 新增通用步骤字段
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_sop(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS step_tree_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
      tbl
    );
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.step_tree_json IS %L',
      tbl,
      '步骤树权威 JSON（通用步骤能力字段，不绑定动作库业务语义）'
    );
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.action_tree_json IS %L',
      tbl,
      '【历史兼容】旧步骤字段（V113 后不再作为步骤权威）'
    );
  END LOOP;
END $$;

-- 2) 存量回填：仅在 step_tree_json 为空时用 action_tree_json 回填
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_sop(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      $q$
      UPDATE dynamicbusiness.%I
      SET step_tree_json = action_tree_json,
          updater = COALESCE(updater, 'flyway-v113'),
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = FALSE
        AND (step_tree_json IS NULL OR step_tree_json = '[]'::jsonb)
        AND action_tree_json IS NOT NULL
        AND jsonb_typeof(action_tree_json) = 'array'
        AND jsonb_array_length(action_tree_json) > 0
      $q$,
      tbl
    );
  END LOOP;
END $$;

-- 3) SOP 基础字段元数据切换为 step_tree_json
WITH action_rows AS (
  SELECT *
  FROM dynamic_entity_type_base_field
  WHERE deleted = FALSE
    AND entity_type_code = 'sop'
    AND field_code = 'action_tree_json'
)
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
  entity_type_code,
  library_field_id,
  'step_tree_json',
  CASE WHEN COALESCE(NULLIF(field_name, ''), '') = '' THEN '步骤树' ELSE field_name END,
  data_type,
  required,
  default_value,
  CASE
    WHEN COALESCE(NULLIF(description, ''), '') = '' THEN '通用步骤树结构字段'
    ELSE description
  END,
  COALESCE(type_config, '{"createVisible":false,"editVisible":false,"detailVisible":false}'),
  sort_order,
  status,
  tenant_id,
  COALESCE(updater, creator, 'flyway-v113')
FROM action_rows
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
  updater = 'flyway-v113',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_base_field
SET deleted = TRUE,
    status = 0,
    updater = 'flyway-v113',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'sop'
  AND field_code = 'action_tree_json';

-- 4) SOP 型号字段分配切换（action_tree_json -> step_tree_json）
-- 4.1 已有 step_tree_json 分配时：用 action_tree_json 的配置覆盖 step_tree_json（不新增行，避免唯一键冲突）
UPDATE dynamic_model_field_assignment target
SET required = source.required,
    is_searchable = source.is_searchable,
    is_filterable = source.is_filterable,
    is_sortable = source.is_sortable,
    sort = source.sort,
    default_value = source.default_value,
    target_entity_type = source.target_entity_type,
    field_source = source.field_source,
    updater = 'flyway-v113',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model_field_assignment source
JOIN dynamic_model m ON m.id = source.model_id
WHERE source.deleted = FALSE
  AND target.deleted = FALSE
  AND m.deleted = FALSE
  AND m.entity_type_code = 'sop'
  AND source.field_code = 'action_tree_json'
  AND target.field_code = 'step_tree_json'
  AND target.model_id = source.model_id
  AND target.tenant_id = source.tenant_id;

-- 4.2 尚无 step_tree_json 分配时：把 action_tree_json 行就地改名为 step_tree_json（保留同一 field_id）
UPDATE dynamic_model_field_assignment source
SET field_code = 'step_tree_json',
    updater = 'flyway-v113',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE source.model_id = m.id
  AND source.deleted = FALSE
  AND m.deleted = FALSE
  AND m.entity_type_code = 'sop'
  AND source.field_code = 'action_tree_json'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment target
    WHERE target.deleted = FALSE
      AND target.model_id = source.model_id
      AND target.tenant_id = source.tenant_id
      AND target.field_code = 'step_tree_json'
  );

-- 4.3 收口残留 action_tree_json 分配行（仅剩“已有 step_tree_json 分配”的场景）
UPDATE dynamic_model_field_assignment source
SET deleted = TRUE,
    updater = 'flyway-v113',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE source.model_id = m.id
  AND source.deleted = FALSE
  AND m.deleted = FALSE
  AND m.entity_type_code = 'sop'
  AND source.field_code = 'action_tree_json';

-- 5) 清 SOP CRUD 缓存，强制下次按新字段重算
DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'sop';
