-- ============================================================================
-- 点位 / 路线 · NATIVE + 巡检 SCOPED 域入口
-- 定稿：docs/superpowers/specs/2026-07-14-legacy-site-import-design.md
--
--   point (NATIVE, ent_point)     → patrol_point (SCOPED, dataScope=patrol)
--   route (NATIVE, ent_route)     → patrol_route (SCOPED, dataScope=patrol)
--
-- 须在 dynamic_purge_patrol_domain + dynamic_seed_retire_* 之后执行（import.sh 已挂序）。
-- 模型 code 用 point_patrol / route_patrol，避免与 purge 清理的旧 model_code=patrol_point 纠缠。
-- 幂等。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 1. 字段库
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  ('FLD-PNT-001', '所属设施', 'ENTITY_REF', NULL,
   '挂接 facility 实体 id', 'SYSTEM', 1, 1, 'NONE', NULL, NULL, 'facility_id', 1, 'seed'),
  ('FLD-PNT-002', '挂接停靠站', 'STRING', NULL,
   '路网停靠站 nodeId；路网导入后回填', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'station_node_id', 1, 'seed'),
  ('FLD-PNT-003', '关联设备', 'ENTITY_REF_MULTI', NULL,
   '待巡检设备 id 列表', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'equipment_ids', 1, 'seed'),
  ('FLD-RTE-010', '所属设施', 'ENTITY_REF', NULL,
   '路线所属 facility', 'SYSTEM', 1, 1, 'NONE', NULL, NULL, 'facility_id', 1, 'seed'),
  ('FLD-RTE-011', '拓扑版本', 'STRING', NULL,
   '已发布路网 topologyRef', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'topology_ref', 1, 'seed'),
  ('FLD-RTE-012', '停靠站有序列表', 'LONG_TEXT', NULL,
   'stop_ids JSON 数组', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'stop_ids', 1, 'seed'),
  ('FLD-RTE-013', '机动剖面', 'STRING', NULL,
   'mobility profile id', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'mobility_profile_id', 1, 'seed'),
  ('FLD-RTE-014', '规划结果', 'LONG_TEXT', NULL,
   'planned_route JSON；查看优先读此字段', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'planned_route', 1, 'seed'),
  ('FLD-RTE-015', '来源巡检点', 'ENTITY_REF_MULTI', NULL,
   '仅巡检路线；来源巡检点 id 列表', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'patrol_point_ids', 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  semantic_type = EXCLUDED.semantic_type,
  deleted = false,
  status = 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 2. 数据类型：point / patrol_point / route / patrol_route
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, tenant_id, creator, entry_kind, group_name
) VALUES (
  'point', '点位', NULL,
  '标准点位：业务资料 + 挂接路网停靠站；巡检域入口为 patrol_point',
  'ep:location', '点位', 20, 'active', 'USER', '{}',
  'DEDICATED', 'ent_point', false, NULL, 1, 'seed', 'NATIVE', '路径'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  storage_type = 'DEDICATED',
  dedicated_table_name = 'ent_point',
  entry_kind = 'NATIVE',
  base_entity_type_code = NULL,
  data_scope = NULL,
  group_name = '路径',
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 若曾被 retire 软删，恢复 route
UPDATE dynamic_entity_type
SET deleted = false, status = 'active', updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND code = 'route' AND deleted = true;

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, tenant_id, creator, entry_kind, group_name
) VALUES (
  'route', '路线', NULL,
  '路径规划标准计算结果落库；不等于路网；巡检域入口为 patrol_route',
  'ep:share', '路线', 21, 'active', 'USER', '{}',
  'DEDICATED', 'ent_route', false, NULL, 1, 'seed', 'NATIVE', '路径'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  storage_type = 'DEDICATED',
  dedicated_table_name = 'ent_route',
  entry_kind = 'NATIVE',
  base_entity_type_code = NULL,
  data_scope = NULL,
  group_name = '路径',
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type (
  code, name, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  tenant_id, creator, entry_kind, base_entity_type_code, data_scope, group_name
) VALUES
  ('patrol_point', '巡检点', '点位的巡检域入口；storage=point，dataScope=patrol；不做模板',
   'ep:map-location', '巡检点', 22, 'active', 'USER', '{}',
   'DEDICATED', 'ent_point', false, 1, 'seed', 'SCOPED', 'point', 'patrol', '巡检管理'),
  ('patrol_route', '巡检路线', '路线的巡检域入口；storage=route，dataScope=patrol；保存算路结果免重算',
   'ep:guide', '巡检路线', 23, 'active', 'USER', '{}',
   'DEDICATED', 'ent_route', false, 1, 'seed', 'SCOPED', 'route', 'patrol', '巡检管理')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  entry_kind = 'SCOPED',
  base_entity_type_code = EXCLUDED.base_entity_type_code,
  data_scope = EXCLUDED.data_scope,
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  storage_type = 'DEDICATED',
  group_name = EXCLUDED.group_name,
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, enable_rule_engine,
  description, status, tenant_id, creator
) VALUES
  ('point', '点位', 'DEDICATED', 'ent_point', false, '标准点位存储', 1, 1, 'seed'),
  ('route', '路线', 'DEDICATED', 'ent_route', false, '路径规划结果存储', 1, 1, 'seed'),
  ('patrol_point', '巡检点', 'DEDICATED', 'ent_point', false, '点位巡检域入口', 1, 1, 'seed'),
  ('patrol_route', '巡检路线', 'DEDICATED', 'ent_route', false, '路线巡检域入口', 1, 1, 'seed')
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  dedicated_table_name = EXCLUDED.dedicated_table_name,
  storage_type = 'DEDICATED',
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 3. 模型（storage code + data_scope）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_model (
  code, name, entity_type_code, data_scope, description, status, sort, tenant_id, creator
) VALUES
  ('point_standard', '标准点位', 'point', NULL,
   '业务中立点位：code/name/facility/station_node_id', 1, 1, 1, 'seed'),
  ('point_patrol', '巡检点', 'point', 'patrol',
   '巡检域点位；可关联设备；不做模板', 1, 2, 1, 'seed'),
  ('route_standard', '标准路线', 'route', NULL,
   '路径规划标准结果', 1, 1, 1, 'seed'),
  ('route_patrol', '巡检路线', 'route', 'patrol',
   '巡检域路线；含来源巡检点', 1, 2, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  data_scope = EXCLUDED.data_scope,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 4. 模型字段分配
-- ---------------------------------------------------------------------------
-- 标准点位
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  v.required, true, true, false, v.sort,
  v.target_entity_type, 1, 'seed'
FROM (
  VALUES
    ('point_standard', 'FLD-PNT-001', true,  10, 'facility'),
    ('point_standard', 'FLD-PNT-002', false, 20, NULL)
) AS v(model_code, field_code, required, sort, target_entity_type)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = v.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  target_entity_type = EXCLUDED.target_entity_type,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 巡检点 = 标准点位字段 + 设备
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  v.required, true, true, false, v.sort,
  v.target_entity_type, 1, 'seed'
FROM (
  VALUES
    ('point_patrol', 'FLD-PNT-001', true,  10, 'facility'),
    ('point_patrol', 'FLD-PNT-002', false, 20, NULL),
    ('point_patrol', 'FLD-PNT-003', false, 30, 'equipment')
) AS v(model_code, field_code, required, sort, target_entity_type)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = v.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  target_entity_type = EXCLUDED.target_entity_type,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 标准路线
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  v.required, true, true, false, v.sort,
  v.target_entity_type, 1, 'seed'
FROM (
  VALUES
    ('route_standard', 'FLD-RTE-010', true,  10, 'facility'),
    ('route_standard', 'FLD-RTE-011', false, 20, NULL),
    ('route_standard', 'FLD-RTE-012', false, 30, NULL),
    ('route_standard', 'FLD-RTE-013', false, 40, NULL),
    ('route_standard', 'FLD-RTE-014', false, 50, NULL)
) AS v(model_code, field_code, required, sort, target_entity_type)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = v.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  target_entity_type = EXCLUDED.target_entity_type,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 巡检路线 = 标准路线 + 来源巡检点
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  v.required, true, true, false, v.sort,
  v.target_entity_type, 1, 'seed'
FROM (
  VALUES
    ('route_patrol', 'FLD-RTE-010', true,  10, 'facility'),
    ('route_patrol', 'FLD-RTE-011', false, 20, NULL),
    ('route_patrol', 'FLD-RTE-012', false, 30, NULL),
    ('route_patrol', 'FLD-RTE-013', false, 40, NULL),
    ('route_patrol', 'FLD-RTE-014', false, 50, NULL),
    ('route_patrol', 'FLD-RTE-015', false, 60, 'point')
) AS v(model_code, field_code, required, sort, target_entity_type)
JOIN dynamic_model m ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = v.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  target_entity_type = EXCLUDED.target_entity_type,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 5. 门户：巡检业务（GROUP）→ 巡检点 / 巡检路线
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'patrol_mgmt', '巡检业务', NULL, 'GROUP',
  '巡检点、巡检路线等巡检域门户', 'ep:set-up', '巡检业务', 2, 'active', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  node_kind = 'GROUP',
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
)
SELECT
  v.code, v.name, g.id, 'LEAF', v.description, v.icon, v.alias, v.sort, 'active', 1, 'seed'
FROM (
  VALUES
    ('patrol_point', '巡检点管理', '巡检点（挂停靠站、关联设备）', 'ep:map-location', '巡检点', 1),
    ('patrol_route', '巡检路线管理', '保存的巡检算路结果', 'ep:guide', '巡检路线', 2)
) AS v(code, name, description, icon, alias, sort)
JOIN dynamic_business g ON g.deleted = false AND g.tenant_id = 1 AND g.code = 'patrol_mgmt'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  parent_id = EXCLUDED.parent_id,
  node_kind = 'LEAF',
  description = EXCLUDED.description,
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_business_entry (
  business_id, code, name, entry_type, entity_type_code, scope_config, page_config_id,
  sort, status, tenant_id, creator
)
SELECT b.id, 'default-admin', b.name, 'ENTITY_ADMIN', b.code,
  NULL, NULL, 0, 'active', 1, 'seed'
FROM dynamic_business b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.code IN ('patrol_point', 'patrol_route')
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = 'ENTITY_ADMIN',
  entity_type_code = EXCLUDED.entity_type_code,
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
