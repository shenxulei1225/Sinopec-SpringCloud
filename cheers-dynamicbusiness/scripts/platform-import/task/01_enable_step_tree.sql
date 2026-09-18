-- ============================================================================
-- task · 01 在底座「任务」目录打开步骤树（混挂检查项 + 动作）
--
-- 巡检任务是子数据类型 task_patrol，物理表仍是 ent_task*。
-- 开能力必须对着底座编码 task，不能对着 task_patrol（没有 ent_task_patrol 表）。
--
-- 本脚本与 EntityTypeStepStructureProvisionService 同口径：
--   列码 step_tree_json、用途 STEP_TREE、挂载写在 type_config。
-- 不负责：生成头尾/按路径串检查项；那是任务创建后的组装。
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type
    WHERE deleted = FALSE AND code = 'task'
  ) THEN
    RAISE EXCEPTION '数据目录 task 不存在，不能打开步骤树';
  END IF;
END $$;

INSERT INTO dynamic_entity_type_capability (
  tenant_id, entity_type_code, capability_code, enabled, deleted,
  creator, create_time, updater, update_time
)
SELECT
  et.tenant_id, 'task', 'step-structure', TRUE, FALSE,
  'seed-task-step-tree', CURRENT_TIMESTAMP, 'seed-task-step-tree', CURRENT_TIMESTAMP
FROM dynamic_entity_type et
WHERE et.deleted = FALSE AND et.code = 'task'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type_capability c
    WHERE c.deleted = FALSE
      AND c.tenant_id = et.tenant_id
      AND c.entity_type_code = 'task'
      AND c.capability_code = 'step-structure'
  );

UPDATE dynamic_entity_type_capability
SET enabled = TRUE,
    updater = 'seed-task-step-tree',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'task'
  AND capability_code = 'step-structure'
  AND enabled IS DISTINCT FROM TRUE;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type,
  required, default_value, description, type_config, sort_order,
  status, tenant_id, creator
)
SELECT
  'task',
  f.id,
  'step_tree_json',
  '步骤树',
  f.type,
  FALSE,
  '[]',
  '任务执行步骤图（可混挂检查项、动作）',
  '{"createVisible":false,"editVisible":false,"detailVisible":false,"hangableTypeCodes":["inspection_item","action"],"allowMixed":true,"editorKind":"step_nodes"}'::jsonb,
  930,
  1,
  f.tenant_id,
  'seed-task-step-tree'
FROM dynamic_field f
WHERE f.deleted = FALSE
  AND f.code = 'step_tree_json'
  AND f.semantic_type = 'STEP_TREE'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type_base_field bf
    WHERE bf.deleted = FALSE
      AND bf.tenant_id = f.tenant_id
      AND bf.entity_type_code = 'task'
      AND bf.field_code = 'step_tree_json'
  );

UPDATE dynamic_entity_type_base_field bf
SET type_config = jsonb_strip_nulls(
      COALESCE(bf.type_config, '{}'::jsonb)
      || '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb
      || '{"hangableTypeCodes":["inspection_item","action"],"allowMixed":true,"editorKind":"step_nodes"}'::jsonb
    ),
    field_name = '步骤树',
    description = '任务执行步骤图（可混挂检查项、动作）',
    updater = 'seed-task-step-tree',
    update_time = CURRENT_TIMESTAMP
WHERE bf.deleted = FALSE
  AND bf.entity_type_code = 'task'
  AND bf.field_code = 'step_tree_json';

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, field_source, tenant_id, creator
)
SELECT
  m.id, f.id, m.code, 'step_tree_json',
  FALSE, FALSE, FALSE, FALSE, 930,
  '[]', NULL, 'BASE', m.tenant_id, 'seed-task-step-tree'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.deleted = FALSE
 AND f.tenant_id = m.tenant_id
 AND f.code = 'step_tree_json'
 AND f.semantic_type = 'STEP_TREE'
WHERE m.deleted = FALSE
  AND m.entity_type_code = 'task'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_model_field_assignment a
    WHERE a.deleted = FALSE
      AND a.tenant_id = m.tenant_id
      AND a.model_id = m.id
      AND a.field_code = 'step_tree_json'
  );

DO $$
DECLARE
  t text;
BEGIN
  FOR t IN
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_type = 'BASE TABLE'
      AND (table_name = 'ent_task' OR table_name ~ '^ent_task_t[0-9]+$')
    ORDER BY table_name
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS step_tree_json JSONB NOT NULL DEFAULT ''[]''::jsonb',
      t
    );
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.step_tree_json IS %L',
      t,
      '任务执行步骤图（步骤树能力；可混挂检查项与动作）'
    );
  END LOOP;
END $$;

DELETE FROM model_crud_form_definition WHERE entity_type_code = 'task';
