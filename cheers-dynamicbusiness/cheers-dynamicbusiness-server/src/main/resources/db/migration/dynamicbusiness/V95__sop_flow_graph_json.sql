-- V95: SOP 流程图字段（flow_graph_json）
-- 目标：在不影响步骤树权威 action_tree_json 的前提下，补充流程图只读展示数据列。
-- 说明：本版仅建列与注释；编辑器能力后续迭代接入。

SET search_path TO dynamicbusiness, public;

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
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS flow_graph_json JSONB NOT NULL DEFAULT ''{}''::jsonb',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.flow_graph_json IS %L',
      tbl,
      'SOP 流程图展示 JSON（节点布局/连线）；步骤执行权威仍为 action_tree_json');
  END LOOP;
END $$;

-- 若 SOP 基础字段已存在该项，统一默认配置为「不在通用 CRUD 表单直接展示」。
UPDATE dynamic_entity_type_base_field
SET
  type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}',
  updater = 'flyway-v95',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'sop'
  AND field_code = 'flow_graph_json';
