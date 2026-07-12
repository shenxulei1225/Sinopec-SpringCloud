-- ============================================================================
-- 系统 · task 实体模型（patrol_task）
-- 实体类型 task（ent_task）；巡检任务定义。
-- 路径拓扑在 platform-topology；任务通过 topologyRef、stopIds 引用，经 platform-routing 规划。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'patrol_task', '巡检任务',
  'task',
  '巡检任务：只存排期/对象/巡检点/检查项模板 id + 启用快照 + planned_route 结果。',
  1, 1,
  '[{"id":"basic","name":"任务属性","sort":1},{"id":"refs","name":"模板引用","sort":2},{"id":"resource","name":"资源策略","sort":3},{"id":"snapshots","name":"运行快照","sort":4},{"id":"routing","name":"路径规划","sort":5}]',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- patrol_task 字段分配（model_code + field_code 幂等键）

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  false, false, true, false, 10,
  '巡检', NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-003'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, default_value = EXCLUDED.default_value, is_filterable = EXCLUDED.is_filterable, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, true, false, 20, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-004'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, is_filterable = EXCLUDED.is_filterable, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, true, false, 30, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-005'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, is_filterable = EXCLUDED.is_filterable, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, true, false, 40, 'draft', NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-022'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, default_value = EXCLUDED.default_value, is_filterable = EXCLUDED.is_filterable, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, false, false, 90, 'facility', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-024'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, target_entity_type = EXCLUDED.target_entity_type, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, false, false, 100, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-018'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 110, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-019'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 120, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-020'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, true, false, 130, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-021'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, target_entity_type = NULL, is_filterable = EXCLUDED.is_filterable, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 140, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-025'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, true, false, 150, 'ROBOT_GROUND', NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-026'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, default_value = EXCLUDED.default_value, is_filterable = EXCLUDED.is_filterable, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 155, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-027'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 160, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-023'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 170, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-007'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 180, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-008'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


-- 路径规划字段挂 FG-BIZ-TASK（表单分组）

INSERT INTO dynamic_group_relation (group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1015, 1, 'seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-024'
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-TASK'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_group_relation (group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1016, 1, 'seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-025'
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-TASK'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_group_relation (group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1017, 1, 'seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-026'
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-TASK'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;


INSERT INTO dynamic_group_relation (group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 1018, 1, 'seed'
FROM dynamic_group g
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-027'
WHERE g.deleted = false AND g.tenant_id = 1 AND g.group_type = 'FIELD' AND g.code = 'FG-BIZ-TASK'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;

-- 模板引用（替代 POC JSON 字段）
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, false, false, 50, 'patrol_schedule', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-028'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, target_entity_type = EXCLUDED.target_entity_type, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, false, false, 60, 'patrol_object', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-029'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, target_entity_type = EXCLUDED.target_entity_type, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, false, false, 70, 'patrol_point', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-030'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, target_entity_type = EXCLUDED.target_entity_type, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, false, false, 80, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-031'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 200, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-032'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 210, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-033'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 220, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-034'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;

-- 退役 POC 内联 JSON / 路径输入字段（改由 patrol_point_id 间接引用）
UPDATE dynamic_model_field_assignment
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND model_code = 'patrol_task'
  AND field_code IN ('FLD-TSK-018', 'FLD-TSK-019', 'FLD-TSK-021', 'FLD-TSK-025', 'FLD-TSK-026');

UPDATE dynamic_group_relation
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND group_type = 'FIELD' AND group_code = 'FG-BIZ-TASK'
  AND target_code IN ('FLD-TSK-018', 'FLD-TSK-019', 'FLD-TSK-021', 'FLD-TSK-025', 'FLD-TSK-026');

