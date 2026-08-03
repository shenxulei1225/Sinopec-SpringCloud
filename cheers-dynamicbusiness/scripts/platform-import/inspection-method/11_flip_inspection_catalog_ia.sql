-- ============================================================================
-- inspection-method · 11 目录对调（中间态，已废）
-- 【已废】最终定稿见 12_retire_inspection_item_domains.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) NATIVE：标准检查内容库 → 知识库
UPDATE dynamic_entity_type
SET
  name = '标准检查内容库',
  alias = '标准检查内容库',
  group_name = '知识库',
  sort = 12,
  entry_kind = 'NATIVE',
  base_entity_type_code = NULL,
  domain = NULL,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'inspection_item';

-- 2) DOMAIN：设备检查内容 → 设备管理
UPDATE dynamic_entity_type
SET
  name = '设备检查内容',
  alias = '设备检查内容',
  group_name = '设备管理',
  sort = 21,
  entry_kind = 'DOMAIN',
  base_entity_type_code = 'inspection_item',
  domain = 'equipment',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'inspection_item_equipment';

-- 若 10 未执行，补建设备 DOMAIN
INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, entry_kind, base_entity_type_code,
  domain, group_name, tenant_id, creator
)
SELECT
  'inspection_item_equipment',
  '设备检查内容',
  NULL,
  '检查内容底座的设备业务域；配置入口挂设备管理',
  COALESCE(b.icon, 'fa:calendar-check-o'),
  '设备检查内容',
  21,
  'active',
  'USER',
  '{}',
  b.storage_type,
  b.dedicated_table_name,
  COALESCE(b.enable_rule_engine, FALSE),
  b.physical_column_mapping,
  COALESCE(b.model_workbench_mode, 'SINGLE'),
  'DOMAIN',
  'inspection_item',
  'equipment',
  '设备管理',
  1,
  'seed'
FROM dynamic_entity_type b
WHERE b.deleted = false
  AND b.tenant_id = 1
  AND b.code = 'inspection_item'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type e
    WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'inspection_item_equipment'
  );

-- 3) DOMAIN：管线检查内容 → 管线
INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, model_workbench_mode, entry_kind, base_entity_type_code,
  domain, group_name, tenant_id, creator
)
SELECT
  'inspection_item_pipeline',
  '管线检查内容',
  NULL,
  '检查内容底座的管线业务域；配置入口挂管线',
  COALESCE(b.icon, 'fa:calendar-check-o'),
  '管线检查内容',
  25,
  'active',
  'USER',
  '{}',
  b.storage_type,
  b.dedicated_table_name,
  COALESCE(b.enable_rule_engine, FALSE),
  b.physical_column_mapping,
  COALESCE(b.model_workbench_mode, 'SINGLE'),
  'DOMAIN',
  'inspection_item',
  'pipeline',
  '管线',
  1,
  'seed'
FROM dynamic_entity_type b
WHERE b.deleted = false
  AND b.tenant_id = 1
  AND b.code = 'inspection_item'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type e
    WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = 'inspection_item_pipeline'
  );

UPDATE dynamic_entity_type
SET
  name = '管线检查内容',
  alias = '管线检查内容',
  group_name = '管线',
  sort = 25,
  entry_kind = 'DOMAIN',
  base_entity_type_code = 'inspection_item',
  domain = 'pipeline',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'inspection_item_pipeline';

-- 4) 分组名兜底：若仍残留「流程规范」成员，迁到知识库（分组表已改名时以 entity.group_name 为准）
UPDATE dynamic_entity_type
SET
  group_name = '知识库',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND group_name = '流程规范';
