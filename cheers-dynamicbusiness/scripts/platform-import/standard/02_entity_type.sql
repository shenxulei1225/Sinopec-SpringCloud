-- ============================================================================
-- standard · 02 实体类型 standard（规范标准 · 全网 · SINGLE）
-- 本机若已有界面创建的空壳类型，本脚本幂等补齐专用表名 / 物理列映射 / 工作台模式
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, group_name, work_scope,
  tenant_id, creator
) VALUES (
  'standard', '规范标准', NULL,
  '全网规范标准库；国标/行标/企标条目；管道/站场/数字化/HSE 引用清单',
  'fa:book', '标准', 25, 'active', 'USER',
  '{}', 'DEDICATED', 'ent_standard', FALSE,
  '{
    "standard_no":{"type":"VARCHAR","column":"standard_no"},
    "standard_level":{"type":"VARCHAR","column":"standard_level"},
    "issuing_body":{"type":"VARCHAR","column":"issuing_body"},
    "publish_year":{"type":"INTEGER","column":"publish_year"},
    "summary":{"type":"TEXT","column":"summary"},
    "source_ref":{"type":"TEXT","column":"source_ref"}
  }'::jsonb,
  'SINGLE', '知识库', 'NETWORK', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  alias = EXCLUDED.alias,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  model_workbench_mode = EXCLUDED.model_workbench_mode,
  group_name = EXCLUDED.group_name,
  work_scope = EXCLUDED.work_scope,
  storage_type = EXCLUDED.storage_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type
SET dedicated_table_name = dedicated_table_name || '_t' || tenant_id::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'standard'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'standard', '规范标准',
  'DEDICATED', 'ent_standard',
  NULL, TRUE,
  '规范标准专用表；固定列 standard_no / standard_level / issuing_body / publish_year / summary / source_ref', 1,
  '{
    "standard_no":{"type":"VARCHAR","column":"standard_no"},
    "standard_level":{"type":"VARCHAR","column":"standard_level"},
    "issuing_body":{"type":"VARCHAR","column":"issuing_body"},
    "publish_year":{"type":"INTEGER","column":"publish_year"},
    "summary":{"type":"TEXT","column":"summary"},
    "source_ref":{"type":"TEXT","column":"source_ref"}
  }'::jsonb,
  1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_config
SET dedicated_table_name = dedicated_table_name || '_t' || tenant_id::text,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'standard'
  AND deleted = false
  AND tenant_id IS NOT NULL
  AND tenant_id > 0
  AND dedicated_table_name IS NOT NULL
  AND dedicated_table_name !~ '_t[0-9]+$';
