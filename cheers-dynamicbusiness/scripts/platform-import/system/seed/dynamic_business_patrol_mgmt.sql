-- ============================================================================
-- 系统 · 巡检管理门户（patrol_mgmt 分组 + 子菜单）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'patrol_mgmt', '巡检管理', NULL,
  'GROUP', '排期、巡检对象、巡检点、检查内容等主数据维护',
  'ep:baseball', '巡检管理',
  2, 'active',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
)
SELECT
  v.code, v.name,
  (SELECT pb.id FROM dynamic_business pb
   WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'patrol_mgmt' LIMIT 1),
  'LEAF', v.description,
  v.icon, v.alias, v.sort, 'active',
  1, 'seed'
FROM (VALUES
  ('patrol_schedule', '排期管理', '排期模板与实例（cron / 单次）', 'fa:clock-o', '排期', 1),
  ('patrol_object', '巡检对象', '巡检对象模板：设施、设备、分区', 'fa:cube', '对象', 2),
  ('patrol_point', '巡检点', '停留点 nodeId 列表与拓扑引用；按作业区分类', 'fa:map-marker', '巡检点', 3)
) AS v(code, name, description, icon, alias, sort)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = EXCLUDED.node_kind,
  description = EXCLUDED.description,
  icon = EXCLUDED.icon,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 检查内容归入巡检管理分组
UPDATE dynamic_business
SET parent_id = (
      SELECT pb.id FROM dynamic_business pb
      WHERE pb.deleted = false AND pb.tenant_id = 1 AND pb.code = 'patrol_mgmt' LIMIT 1
    ),
    sort = 4,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'inspection_item';

-- ---------- business entry ----------
INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT
  b.id, 'default-admin', v.entry_name,
  'ENTITY_ADMIN', v.entity_type_code,
  NULL, NULL,
  0, 'active',
  1, 'seed'
FROM (VALUES
  ('patrol_schedule', '排期管理', 'patrol_schedule'),
  ('patrol_object', '巡检对象管理', 'patrol_object'),
  ('patrol_point', '巡检点管理', 'patrol_point')
) AS v(business_code, entry_name, entity_type_code)
JOIN dynamic_business b
  ON b.deleted = false AND b.tenant_id = 1 AND b.code = v.business_code
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = EXCLUDED.entry_type,
  entity_type_code = EXCLUDED.entity_type_code,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
