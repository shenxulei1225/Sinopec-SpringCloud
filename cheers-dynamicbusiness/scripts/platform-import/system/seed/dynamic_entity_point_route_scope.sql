-- ============================================================================
-- 点位 / 路线 / 路网 DOMAIN（VDA 5050 Route network）
-- 定稿：docs/superpowers/specs/2026-08-30-point-path-network-sop-location-ref-design.md
--
--   point (NATIVE, ent_point)              ← 通用点位（摄像头等）；目录「场站管理」
--   route_network_point (DOMAIN, SYSTEM)   ← 路网点位；domain=route_network；目录「路网」
--   route (NATIVE, ent_route)              ← 路线；目录「路网」
--   patrol_route (DOMAIN, domain=patrol)   ← 巡检路线；目录「巡检管理」
--
-- DOMAIN「巡检点」patrol_point / 型号 point_patrol 已废止（见同目录 retire_patrol_point_domain.sql）。
-- 须在 dynamic_purge_patrol_domain + dynamic_seed_retire_* 之后执行（import.sh 已挂序）。
-- 幂等。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 0. 数据目录分组：登记「路网」「三维」（有 groupId 才能右键改名/删除）
--     注意：直写 SQL 不会清 Redis 分组树缓存（dynamicbusiness:group:tree:ENTITY_TYPE:{tenant}）。
--     本地灌库后须 DEL 该 key，或走 GroupService.createGroup（会自动 evict）。
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_group (
  group_type, code, name, sort, status, tenant_id, creator
)
SELECT 'ENTITY_TYPE', 'ETG-ROUTE-NETWORK', '路网', 25, 1, 1, 'seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group
  WHERE deleted = false AND tenant_id = 1
    AND group_type = 'ENTITY_TYPE'
    AND (name = '路网' OR code = 'ETG-ROUTE-NETWORK')
);

UPDATE dynamic_group
SET name = '路网', status = 1, deleted = false, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND group_type = 'ENTITY_TYPE'
  AND code = 'ETG-ROUTE-NETWORK'
  AND name IS DISTINCT FROM '路网';

INSERT INTO dynamic_group (
  group_type, code, name, sort, status, tenant_id, creator
)
SELECT 'ENTITY_TYPE', 'ETG-SCENE-3D', '三维', 26, 1, 1, 'seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_group
  WHERE deleted = false AND tenant_id = 1
    AND group_type = 'ENTITY_TYPE'
    AND (name = '三维' OR code = 'ETG-SCENE-3D')
);

UPDATE dynamic_group
SET name = '三维', status = 1, deleted = false, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND group_type = 'ENTITY_TYPE'
  AND code = 'ETG-SCENE-3D'
  AND name IS DISTINCT FROM '三维';
-- ---------------------------------------------------------------------------
-- 1. 字段库
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
  ('FLD-PNT-001', '所属设施', 'ENTITY_REF', NULL,
   '挂接 facility 实体 id', 'SYSTEM', 1, 1, 'NONE', NULL, NULL, 'facility', 1, 'seed'),
  ('FLD-PNT-002', '路网节点', 'STRING', NULL,
   '路网节点 nodeId（停靠站/途径点/门点）；路网保存后回填', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'station_node_id', 1, 'seed'),
  ('FLD-PNT-003', '关联设备', 'ENTITY_REF_MULTI', NULL,
   '待巡检设备 id 列表', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'equipment_ids', 1, 'seed'),
  ('FLD-PNT-004', '点位类型', 'ENUM', NULL,
   '一期写死：停靠站/途径点/门点；与路网 NodeType 对齐，用户不可自增选项',
   'SYSTEM', 1, NULL, 'NONE',
   '[{"label":"停靠站","value":"STATION"},{"label":"途径点","value":"TRAVERSAL"},{"label":"门点","value":"DOOR"}]',
   NULL, 'point_kind', 1, 'seed'),
  ('FLD-PNT-005', '归属网', 'MULTI_SELECT', NULL,
   '点归属网多选：人员/地面机器人/无人机（HUMAN/GROUND_ROBOT/UAV）；权威来自三维同步；列表按 IN/CONTAINS 筛',
   'SYSTEM', 1, NULL, 'NONE',
   '[{"label":"人员","value":"HUMAN"},{"label":"地面机器人","value":"GROUND_ROBOT"},{"label":"无人机","value":"UAV"}]',
   NULL, 'network_membership', 1, 'seed'),
  ('FLD-RTE-010', '所属设施', 'ENTITY_REF', NULL,
   '路线所属 facility', 'SYSTEM', 1, 1, 'NONE', NULL, NULL, 'facility', 1, 'seed'),
  ('FLD-RTE-011', '拓扑版本', 'STRING', NULL,
   '已发布路网 topologyRef', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'topology_ref', 1, 'seed'),
  ('FLD-RTE-012', '停靠站有序列表', 'LONG_TEXT', NULL,
   'stop_ids JSON 数组', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'stop_ids', 1, 'seed'),
  ('FLD-RTE-013', '机动剖面', 'STRING', NULL,
   'mobility profile id', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'mobility_profile_id', 1, 'seed'),
  ('FLD-RTE-014', '规划结果', 'LONG_TEXT', NULL,
   'planned_route JSON；查看优先读此字段', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'planned_route', 1, 'seed'),
  ('FLD-RTE-015', '来源点位', 'ENTITY_REF_MULTI', NULL,
   '仅巡检路线；来源点位实体 id 列表（REF 点位底座台账）', 'SYSTEM', 1, NULL, 'NONE', NULL, NULL, 'source_point_ids', 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  semantic_type = EXCLUDED.semantic_type,
  deleted = false,
  status = 1,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 2. 数据类型
-- ---------------------------------------------------------------------------
-- 2a. 通用点位（NATIVE）→ 场站管理
INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, tenant_id, creator, entry_kind, group_name
) VALUES (
  'point', '点位', NULL,
  '通用点位台账（摄像头等业务点）；不含路网节点。路网点位见系统 DOMAIN「路网点位」',
  'ep:location', '点位', 20, 'active', 'USER', '{}',
  'DEDICATED', 'ent_point', false, NULL, 1, 'seed', 'NATIVE', '场站管理'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  storage_type = 'DEDICATED',
  dedicated_table_name = 'ent_point',
  entry_kind = 'NATIVE',
  base_entity_type_code = NULL,
  domain = NULL,
  group_name = '场站管理',
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 若曾被 retire 软删，恢复 route
UPDATE dynamic_entity_type
SET deleted = false, status = 'active', updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND code = 'route' AND deleted = true;

-- 2b. 路线（NATIVE）→ 路网
INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, tenant_id, creator, entry_kind, group_name
) VALUES (
  'route', '路线', NULL,
  '路径规划标准计算结果落库；不等于路网拓扑；巡检域入口为 patrol_route',
  'ep:share', '路线', 21, 'active', 'USER', '{}',
  'DEDICATED', 'ent_route', false, NULL, 1, 'seed', 'NATIVE', '路网'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  storage_type = 'DEDICATED',
  dedicated_table_name = 'ent_route',
  entry_kind = 'NATIVE',
  base_entity_type_code = NULL,
  domain = NULL,
  group_name = '路网',
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2c. 路网点位（DOMAIN，系统级不可删）
INSERT INTO dynamic_entity_type (
  code, name, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  tenant_id, creator, entry_kind, base_entity_type_code, domain, group_name
) VALUES (
  'route_network_point', '路网点位',
  '路网（VDA Route network）节点台账入口；storage=point，domain=route_network；系统级不可删',
  'ep:location', '路网点位', 22, 'active', 'SYSTEM', '{}',
  'DEDICATED', 'ent_point', false, 1, 'seed', 'DOMAIN', 'point', 'route_network', '路网'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  entry_kind = 'DOMAIN',
  base_entity_type_code = 'point',
  domain = 'route_network',
  dedicated_table_name = 'ent_point',
  storage_type = 'DEDICATED',
  type_level = 'SYSTEM',
  group_name = '路网',
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 2d. 巡检路线 DOMAIN
INSERT INTO dynamic_entity_type (
  code, name, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  tenant_id, creator, entry_kind, base_entity_type_code, domain, group_name
) VALUES
  ('patrol_route', '巡检路线', '路线的巡检域入口；storage=route，domain=patrol；保存算路结果免重算',
   'ep:guide', '巡检路线', 23, 'active', 'USER', '{}',
   'DEDICATED', 'ent_route', false, 1, 'seed', 'DOMAIN', 'route', 'patrol', '巡检管理')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  entry_kind = 'DOMAIN',
  base_entity_type_code = EXCLUDED.base_entity_type_code,
  domain = EXCLUDED.domain,
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
  ('point', '点位', 'DEDICATED', 'ent_point', false, '通用点位存储', 1, 1, 'seed'),
  ('route', '路线', 'DEDICATED', 'ent_route', false, '路径规划结果存储', 1, 1, 'seed'),
  ('route_network_point', '路网点位', 'DEDICATED', 'ent_point', false, '路网点位 DOMAIN 入口', 1, 1, 'seed'),
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
-- 3. 模型（storage code + domain）
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_model (
  code, name, entity_type_code, domain, description, status, sort, tenant_id, creator
) VALUES
  ('point_standard', '标准点位', 'point', NULL,
   '通用点位：code/name/facility；不含路网节点字段', 1, 1, 1, 'seed'),
  ('point_route_network', '路网点位', 'point', 'route_network',
   '路网同步点位：facility + 路网节点 + 点位类型（STATION/TRAVERSAL/DOOR）', 1, 2, 1, 'seed'),
  ('route_standard', '标准路线', 'route', NULL,
   '路径规划标准结果', 1, 1, 1, 'seed'),
  ('route_patrol', '巡检路线', 'route', 'patrol',
   '巡检域路线；来源点位 id 列表（REF 点位底座）', 1, 2, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  domain = EXCLUDED.domain,
  description = EXCLUDED.description,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 4. 模型字段分配
-- ---------------------------------------------------------------------------
-- 通用点位：仅所属设施（路网字段只挂路网型号）
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
    ('point_standard', 'FLD-PNT-001', true, 10, 'facility')
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

-- 从通用点位型号上拿掉路网专用字段（若曾分配）
UPDATE dynamic_model_field_assignment
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND model_code = 'point_standard'
  AND field_code IN ('FLD-PNT-002', 'FLD-PNT-004')
  AND deleted = false;

-- 路网点位型号
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
    ('point_route_network', 'FLD-PNT-001', true,  10, 'facility'),
    ('point_route_network', 'FLD-PNT-004', true,  15, NULL),
    ('point_route_network', 'FLD-PNT-005', false, 18, NULL),
    ('point_route_network', 'FLD-PNT-002', false, 20, NULL)
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

-- 巡检路线 = 标准路线 + 来源点位
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
-- 4b. 迁移：已同步的 PN-* 从标准点位迁入路网型号 + domain
-- ---------------------------------------------------------------------------
UPDATE ent_point_t1 e
SET
  model_id = m.id,
  domain = 'route_network',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'point_route_network'
  AND e.deleted = false
  AND e.code LIKE 'PN-%'
  AND (e.model_id IS DISTINCT FROM m.id OR e.domain IS DISTINCT FROM 'route_network');

-- ---------------------------------------------------------------------------
-- 5. 门户：巡检业务（GROUP）→ 巡检路线
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_business (
  code, name, parent_id, node_kind, description, icon, alias, sort, status, tenant_id, creator
) VALUES (
  'patrol_mgmt', '巡检业务', NULL, 'GROUP',
  '巡检路线等巡检域门户；路网点位在「路网 → 路网点位」；通用点位在「场站管理 → 点位」',
  'ep:set-up', '巡检业务', 2, 'active', 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  node_kind = 'GROUP',
  description = EXCLUDED.description,
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
  AND b.code IN ('patrol_route')
ON CONFLICT (business_id, code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entry_type = 'ENTITY_ADMIN',
  entity_type_code = EXCLUDED.entity_type_code,
  status = 'active',
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 6. 业务类型关联
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'point', 'facility', '所属设施', TRUE, '所属设施', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'route', 'facility', '所属设施', TRUE, '所属设施', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES (
  'point', 'equipment', '关联设备', TRUE, '关联设备', 1, 'seed'
)
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 6c. DOMAIN「路网点位」完整 bootstrap（禁止只插注册项/空布局）
--     正常建类型走：EntityTypeService.createDomain → category/layout/orchestration
--     + 前端 writeDefaultLayouts（props）。种子直插必须把下面几块一次写全，否则数据页缺配置。
-- ---------------------------------------------------------------------------
INSERT INTO dynamic_category (
  parent_id, name, code, category_type_code, tree_path, level, sort, status,
  description, tenant_id, creator, parent_code
)
SELECT
  ct.top_level_category_id,
  '路网点位',
  'route_network_point_dir',
  'point',
  '/' || ct.top_level_category_id || '/',
  1,
  0,
  1,
  '系统 DOMAIN 路网点位域分组',
  1,
  'seed',
  (SELECT code FROM dynamic_category WHERE id = ct.top_level_category_id)
FROM dynamic_category_type ct
WHERE ct.deleted = false AND ct.category_type_code = 'point'
  AND ct.top_level_category_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category c
    WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'route_network_point_dir'
  );

INSERT INTO dm_five_w_orchestration (
  entity_type_code, enabled, what_mode, what_config, how_mode, how_config,
  object_pick_from, tenant_id, creator
)
SELECT 'route_network_point', true, 'VIEW_DETAIL', '{"bindLayer":"ENTITY"}'::jsonb,
       'NONE', '{}'::jsonb, 'LIST_ROW', 1, 'seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dm_five_w_orchestration
  WHERE deleted = false AND entity_type_code = 'route_network_point'
);

INSERT INTO dm_workbench_layout (name, is_template, source_template_id, tenant_id, creator, settings_json)
SELECT '数据页签·route_network_point', false, 1, 1, 'seed', '{}'::jsonb
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_entity_type et
  WHERE et.deleted = false AND et.code = 'route_network_point' AND et.data_layout_id IS NOT NULL
)
AND NOT EXISTS (
  SELECT 1 FROM dm_workbench_layout w
  WHERE w.deleted = false AND w.tenant_id = 1 AND w.name = '数据页签·route_network_point'
);

UPDATE dynamic_entity_type et
SET data_layout_id = w.id, updater = 'seed', update_time = CURRENT_TIMESTAMP
FROM dm_workbench_layout w
WHERE et.deleted = false AND et.code = 'route_network_point'
  AND et.data_layout_id IS NULL
  AND w.deleted = false AND w.tenant_id = 1
  AND w.name = '数据页签·route_network_point';

-- 栏骨架（含底座类型码；props 下一步绑定）
INSERT INTO dm_data_tab_layout (
  tenant_id, entity_type_code, column_kind, tab_id, props_id, enabled, column_meta, layout_id, creator
)
SELECT
  1,
  'route_network_point',
  v.column_kind,
  v.tab_id,
  NULL,
  true,
  v.column_meta::jsonb,
  et.data_layout_id,
  'seed'
FROM dynamic_entity_type et
CROSS JOIN (
  VALUES
    ('CATEGORY', 'point-1',
     '{"label":"路网点位","columnKey":"point","categoryTypeCode":"point"}'),
    ('MODEL', 'point',
     '{"columnSection":"OBJECT","modelEntityTypeCode":"point"}'),
    ('ENTITY', 'point',
     '{"columnSection":"OBJECT","entityEntityTypeCode":"point"}'),
    ('DETAIL', NULL, NULL)
) AS v(column_kind, tab_id, column_meta)
WHERE et.deleted = false AND et.code = 'route_network_point' AND et.data_layout_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM dm_data_tab_layout d
    WHERE d.deleted = false AND d.layout_id = et.data_layout_id
      AND d.column_kind = v.column_kind
      AND COALESCE(d.tab_id, '') = COALESCE(v.tab_id, '')
  );

-- 展示配置：从 patrol_route 列表/树 props 克隆，data_source 改为底座 point + domain=route_network
INSERT INTO platformresource.pr_component_props (
  is_template, component_id, component_code, data_source, schema_version,
  props, name, status, sort, tenant_id, creator, template_id
)
SELECT
  true, s.component_id, s.component_code, v.data_source, s.schema_version,
  s.props, v.new_name, 1, 0, 1, 'seed', s.id
FROM (
  VALUES
    ('dm-tree:route:patrol_route:分类',
     'dm-tree:point:route_network_point:分类',
     '{"businessCategory":"category","entityTypeCode":"point","dataKind":"entity"}'),
    ('模型列表-patrol_route',
     '模型列表-route_network_point',
     '{"businessCategory":"dynamic","entityTypeCode":"point","dataKind":"model"}'),
    ('实体列表-patrol_route',
     '实体列表-route_network_point',
     '{"businessCategory":"dynamic","entityTypeCode":"point","dataKind":"entity"}')
) AS v(src_name, new_name, data_source)
JOIN platformresource.pr_component_props s
  ON s.deleted = false AND s.tenant_id = 1 AND s.name = v.src_name
WHERE NOT EXISTS (
  SELECT 1 FROM platformresource.pr_component_props p
  WHERE p.deleted = false AND p.tenant_id = 1 AND p.name = v.new_name
);
-- 注意：data_source 不要写 domain（platformresource batch 会 500）；DOMAIN 过滤由打开页 scope 注入。
-- 克隆行 template_id 必须为空（自身已是完整模板）。
UPDATE platformresource.pr_component_props
SET template_id = NULL, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND name IN (
    'dm-tree:point:route_network_point:分类',
    '模型列表-route_network_point',
    '实体列表-route_network_point'
  )
  AND template_id IS NOT NULL;

-- 路网点位实体列表：默认打开「归属网」筛选条（字段 FLD-PNT-005）
UPDATE platformresource.pr_component_props
SET props = regexp_replace(
      props,
      '"filter"\s*:\s*\{[^}]*\}',
      '"filter": {"externalFilterEnabled": true, "filterSelectedOptions": ["FLD-PNT-005"]}'
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1
  AND name = '实体列表-route_network_point'
  AND props LIKE '%"filter"%';

UPDATE dm_data_tab_layout d
SET
  props_id = p.id,
  column_meta = CASE d.column_kind
    WHEN 'CATEGORY' THEN jsonb_build_object(
      'label', '路网点位',
      'columnKey', 'point',
      'categoryTypeCode', 'point'
    )
    WHEN 'MODEL' THEN jsonb_build_object(
      'columnSection', 'OBJECT',
      'modelEntityTypeCode', 'point'
    )
    WHEN 'ENTITY' THEN jsonb_build_object(
      'columnSection', 'OBJECT',
      'entityEntityTypeCode', 'point'
    )
    ELSE d.column_meta
  END,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM platformresource.pr_component_props p,
     dynamic_entity_type et
WHERE et.deleted = false AND et.code = 'route_network_point'
  AND d.deleted = false AND d.layout_id = et.data_layout_id
  AND p.deleted = false AND p.tenant_id = 1
  AND (
    (d.column_kind = 'CATEGORY' AND p.name = 'dm-tree:point:route_network_point:分类')
    OR (d.column_kind = 'MODEL' AND p.name = '模型列表-route_network_point')
    OR (d.column_kind = 'ENTITY' AND p.name = '实体列表-route_network_point')
  )
  AND (d.props_id IS DISTINCT FROM p.id OR d.column_meta IS DISTINCT FROM CASE d.column_kind
    WHEN 'CATEGORY' THEN jsonb_build_object(
      'label', '路网点位',
      'columnKey', 'point',
      'categoryTypeCode', 'point'
    )
    WHEN 'MODEL' THEN jsonb_build_object(
      'columnSection', 'OBJECT',
      'modelEntityTypeCode', 'point'
    )
    WHEN 'ENTITY' THEN jsonb_build_object(
      'columnSection', 'OBJECT',
      'entityEntityTypeCode', 'point'
    )
    ELSE d.column_meta
  END);

-- 默认筛选边：分类→型号、型号→实体（同底座 point）
INSERT INTO dm_data_tab_column_relation (
  entity_type_code, edge_id, from_column_identity, to_column_identity,
  relation_kind, from_type_code, to_type_code, relation_meta,
  tenant_id, creator, layout_id
)
SELECT
  'route_network_point', v.edge_id, v.from_id, v.to_id,
  v.kind, 'point', 'point',
  '{"edgeRole":"filter","enabledInteractions":[]}'::jsonb,
  1, 'seed', et.data_layout_id
FROM dynamic_entity_type et
CROSS JOIN (
  VALUES
    ('edge-rnp-cm-filter',
     'CATEGORY:point:point-1',
     'MODEL:point', 'CATEGORY_MODEL'),
    ('edge-rnp-me-filter',
     'MODEL:point', 'ENTITY:point', 'MODEL_ENTITY')
) AS v(edge_id, from_id, to_id, kind)
WHERE et.deleted = false AND et.code = 'route_network_point' AND et.data_layout_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM dm_data_tab_column_relation r
    WHERE r.deleted = false AND r.layout_id = et.data_layout_id AND r.edge_id = v.edge_id
  );

-- ---------------------------------------------------------------------------
-- 6b. 模型管理 Tab（point 底座）
-- ---------------------------------------------------------------------------
INSERT INTO dm_model_tab_category (
  tenant_id, entity_type_code, enabled, label, category_type_code,
  props_id, model_list_props_id, creator
)
SELECT
  1, 'point', false, NULL, 'point',
  NULL,
  (
    SELECT p.id
    FROM platformresource.pr_component_props p
    WHERE p.deleted = false
      AND p.name = '模型管理列表-point'
      AND p.component_code = 'list'
    ORDER BY p.id DESC
    LIMIT 1
  ),
  'seed'
WHERE EXISTS (
  SELECT 1
  FROM platformresource.pr_component_props p
  WHERE p.deleted = false
    AND p.name = '模型管理列表-point'
    AND p.component_code = 'list'
)
ON CONFLICT (tenant_id, entity_type_code) WHERE deleted = false
DO UPDATE SET
  model_list_props_id = COALESCE(
    EXCLUDED.model_list_props_id,
    dm_model_tab_category.model_list_props_id
  ),
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 7. 废止 DOMAIN「巡检点」（幂等；防旧库残留）
-- ---------------------------------------------------------------------------
\ir retire_patrol_point_domain.sql
