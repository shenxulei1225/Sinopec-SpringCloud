-- ============================================================================
-- 系统 · dynamic_business_entry
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_business_entry: 15 row(s), upsert by (business code, entry code)

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '收费管理管理',
  'ENTITY_ADMIN', 'billing',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'billing'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '入廊客户管理',
  'ENTITY_ADMIN', 'customer',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'customer'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '应急管理管理',
  'ENTITY_ADMIN', 'emergency',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'emergency'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '应急资源管理',
  'ENTITY_ADMIN', 'emergency_resource',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'emergency_resource'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '应急队伍管理',
  'ENTITY_ADMIN', 'emergency_team',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'emergency_team'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '检查内容管理',
  'ENTITY_ADMIN', 'inspection_item',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'inspection_item'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '任务管理',
  'ENTITY_ADMIN', 'task',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'patrol'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '区域管理管理',
  'ENTITY_ADMIN', 'region',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'region'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '设施管理',
  'ENTITY_ADMIN', 'facility',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'facility'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '管线管理',
  'ENTITY_ADMIN', 'pipeline',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'pipeline'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '设备管理管理',
  'ENTITY_ADMIN', 'equipment',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'equipment'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '故障管理管理',
  'ENTITY_ADMIN', 'fault',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'fault'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '维修管理管理',
  'ENTITY_ADMIN', 'maintenance',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'maintenance'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', '备件管理管理',
  'ENTITY_ADMIN', 'spare_parts',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'spare_parts'
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  scope_config = EXCLUDED.scope_config,
  page_config_id = EXCLUDED.page_config_id,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
