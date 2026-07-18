-- ============================================================================
-- 系统 · dynamic_entity_type_relation
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_entity_type_relation: 11 row(s), upsert by (source, target, tenant_id)

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'equipment', 'customer',
  '关联客户管理', TRUE,
  '关联客户管理', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'equipment', 'facility',
  '所属设施', TRUE,
  '所属设施', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'equipment', 'region',
  '关联区域管理', TRUE,
  '关联区域管理', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'equipment', 'spare_parts',
  '关联备件管理', TRUE,
  '关联备件管理', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'equipment', 'zone',
  '所属分区', TRUE,
  '所属分区', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'facility', 'region',
  '所属区域', TRUE,
  '所属区域', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'facility', 'zone',
  '设施-分区', TRUE,
  '站内分区', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'region', 'equipment',
  '区域-设备关联', TRUE,
  '所属设备管理', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'zone', 'equipment',
  '分区-设备', TRUE,
  '所属设备管理', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'task', 'facility',
  '所属设施', TRUE,
  '所属设施', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'task', 'inspection_item',
  '检查项', TRUE,
  '检查项', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'zone', 'facility',
  '所属设施', TRUE,
  '所属设施', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
