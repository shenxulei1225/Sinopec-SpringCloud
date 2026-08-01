-- ============================================================================
-- inspection-method · 02 实体类型
-- storage_type=DEDICATED；model_workbench_mode=SINGLE；专用表 ent_inspection_method
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, tenant_id, creator
) VALUES (
  'inspection_method', '检查方法', NULL,
  '检查方法：同表承载方法模板与按台实例；标准库只展示 is_template=true；Wave 1 不挂 equipment_id',
  'fa:wrench', '检查方法', 21, 'active', 'USER',
  '{}', 'DEDICATED', 'ent_inspection_method', FALSE,
  NULL, 'SINGLE', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  model_workbench_mode = EXCLUDED.model_workbench_mode,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 租户物理隔离：元数据表名补 _t{tenantId}（对齐 V37）
UPDATE dynamic_entity_type
SET dedicated_table_name = dedicated_table_name || '_t' || tenant_id::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'inspection_method'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'inspection_method', '检查方法',
  'DEDICATED', 'ent_inspection_method',
  NULL, TRUE,
  '检查方法专用表；固定列 is_template / action_duration_sec', 1,
  '{"is_template": {"type": "BOOLEAN", "column": "is_template"}, "action_duration_sec": {"type": "BIGINT", "column": "action_duration_sec"}}'::jsonb,
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  storage_type = EXCLUDED.storage_type,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  enable_rule_engine = EXCLUDED.enable_rule_engine,
  description = EXCLUDED.description,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_config
SET dedicated_table_name = dedicated_table_name || '_t' || tenant_id::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_method'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';
