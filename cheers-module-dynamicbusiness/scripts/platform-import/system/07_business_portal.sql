-- ============================================================================
-- 系统共用 · 07 业务门户（dynamic_business + dynamic_business_entry）
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：system/02_business_types.sql
-- 按 business code 解析 parent 与 entry 归属
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_business: 15 row(s), upsert by code

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'customer', '入廊客户', NULL,
  'LEAF', '客户管理、关系维护',
  'businessIcon:客户管理.png', '客户',
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'emergency', '应急管理', NULL,
  'LEAF', NULL,
  'businessIcon:区域管理.png', NULL,
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'patrol', '巡检管理', NULL,
  'LEAF', NULL,
  'ep:baseball', '巡检任务',
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'region', '区域管理', NULL,
  'LEAF', '智慧站场：站场/库区边界与罐组分区；Pattern C 分类即实体',
  'businessIcon:区域管理.png', '区域',
  1, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'pipeline', '管线', NULL,
  'LEAF', '管线台账、管线档案管理',
  'businessIcon:管线管理.png', '管线',
  2, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'equipment', '设备管理', NULL,
  'LEAF', '需要分类-纯分类1',
  'businessIcon:设备管理.png', '设备',
  3, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'billing', '收费管理', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'customer'
  LIMIT 1
),
  'LEAF', '各项收费',
  'businessIcon:收费管理.png', '费用',
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'emergency_resource', '应急资源', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'emergency'
  LIMIT 1
),
  'LEAF', '',
  'ep:discount', '资源',
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'emergency_team', '应急队伍', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'emergency'
  LIMIT 1
),
  'LEAF', '',
  'fa:user-circle', '队伍',
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'inspection_item', '检查内容', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'customer'
  LIMIT 1
),
  'LEAF', NULL,
  'fa:calendar-check-o', '检查项',
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'inspection_point', '巡检点位', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'patrol'
  LIMIT 1
),
  'LEAF', '',
  'ep:calendar', '点位',
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'route', '路线管理', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'patrol'
  LIMIT 1
),
  'LEAF', '',
  'ep:burger', '路线',
  0, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'fault', '故障管理', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'equipment'
  LIMIT 1
),
  'LEAF', '故障记录、故障分析',
  'businessIcon:故障管理.png', '故障',
  4, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'maintenance', '维修管理', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'equipment'
  LIMIT 1
),
  'LEAF', '分类管理（极速分类）',
  'businessIcon:维修管理.png', '维修',
  5, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'spare_parts', '备件管理', (
  SELECT pb.id FROM dynamic_business pb
  WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'equipment'
  LIMIT 1
),
  'LEAF', '备件台账、备件库存',
  'businessIcon:备件管理.png', '备件',
  5, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;


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
  b.id, 'default-admin', '巡检点位管理',
  'ENTITY_ADMIN', 'inspection_point',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'inspection_point'
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
  b.id, 'default-admin', '巡检管理管理',
  'ENTITY_ADMIN', 'patrol',
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
  b.id, 'default-admin', '路线管理管理',
  'ENTITY_ADMIN', 'route',
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code = 'route'
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
