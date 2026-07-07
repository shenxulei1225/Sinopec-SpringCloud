-- ============================================================================
-- 系统 · dynamic_business
-- Generated: 2026-07-08 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
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
