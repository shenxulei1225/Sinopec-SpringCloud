-- ============================================================================
-- inspection-method · 12 收口：检查内容不再用 DOMAIN 入口 / 实体 domain 分域
-- 定稿：仅知识库 → NATIVE「标准检查内容库」；适用检查项在设备/管线工作台挂接。
-- 物理列 domain 为平台 ent_* 共用列，本脚本只清空检查内容行上的值，不 DROP COLUMN。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 卸掉侧栏 DOMAIN 入口（软删）
UPDATE dynamic_entity_type
SET
  deleted = true,
  status = 'inactive',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code IN ('inspection_item_equipment', 'inspection_item_pipeline');

-- 2) 确保 NATIVE 标准库入口仍在知识库
UPDATE dynamic_entity_type
SET
  name = '标准检查内容库',
  alias = '标准检查内容库',
  group_name = '知识库',
  entry_kind = 'NATIVE',
  base_entity_type_code = NULL,
  domain = NULL,
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code = 'inspection_item';

-- 3) 清空检查内容实体上的 domain（模板表 + 租户表）
UPDATE ent_inspection_item
SET
  domain = NULL,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND domain IS NOT NULL;

UPDATE ent_inspection_item_t1
SET
  domain = NULL,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND domain IS NOT NULL;

UPDATE ent_inspection_item_t2
SET
  domain = NULL,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND domain IS NOT NULL;
