-- ============================================================================
-- inspection-method · 10 DOMAIN「设备检查内容」入口（复用 inspection_item 存储）
-- 【已废】最终定稿见 12_retire_inspection_item_domains.sql（不再用 DOMAIN 管检查内容）
-- 保留本文件仅供历史 import 顺序幂等；12 会软删本入口并清空实体 domain。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) DOMAIN 注册项（设备管理）
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

UPDATE dynamic_entity_type
SET
  name = '设备检查内容',
  entry_kind = 'DOMAIN',
  base_entity_type_code = 'inspection_item',
  domain = 'equipment',
  group_name = '设备管理',
  sort = 21,
  model_workbench_mode = COALESCE(model_workbench_mode, 'SINGLE'),
  dedicated_table_name = (
    SELECT b.dedicated_table_name FROM dynamic_entity_type b
    WHERE b.deleted = false AND b.tenant_id = 1 AND b.code = 'inspection_item'
    LIMIT 1
  ),
  storage_type = (
    SELECT b.storage_type FROM dynamic_entity_type b
    WHERE b.deleted = false AND b.tenant_id = 1 AND b.code = 'inspection_item'
    LIMIT 1
  ),
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'inspection_item_equipment';

-- 2) 现有检查内容实体归入设备域（仅填空，不覆盖已有 domain）
UPDATE ent_inspection_item_t1
SET
  domain = 'equipment',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND (domain IS NULL OR btrim(domain) = '');

-- 租户模板表若有数据一并填空
UPDATE ent_inspection_item
SET
  domain = 'equipment',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (domain IS NULL OR btrim(domain) = '');
