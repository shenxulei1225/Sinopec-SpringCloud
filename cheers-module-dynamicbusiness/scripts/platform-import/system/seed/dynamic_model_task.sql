-- ============================================================================
-- 系统 · task 实体模型（patrol_task MVP）
-- 实体类型 task（ent_task）；巡检类任务定义，排期/对象/资源策略以 JSON 聚合存储。
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'patrol_task', '巡检任务',
  'task',
  '巡检类任务定义：schedule_config / inspection_content / resource_policy 为 JSON；route_id 关联路线主数据。',
  1, 1,
  '[{"id":"basic","name":"任务属性","sort":1},{"id":"schedule","name":"排期配置","sort":2},{"id":"content","name":"巡检内容","sort":3},{"id":"resource","name":"资源策略","sort":4}]',
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

-- patrol_task 字段分配（model_code + field_code 幂等键，V2 迁移约定）

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
DO UPDATE SET
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  is_filterable = EXCLUDED.is_filterable,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  false, false, true, false, 20,
  NULL, 1, 'seed'
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
SELECT m.id, f.id, m.code, f.code,
  false, false, true, false, 30,
  NULL, 1, 'seed'
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
SELECT m.id, f.id, m.code, f.code,
  false, false, true, false, 40,
  'draft', NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-022'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  is_filterable = EXCLUDED.is_filterable,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  true, false, false, false, 100,
  NULL, 1, 'seed'
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
SELECT m.id, f.id, m.code, f.code,
  false, false, false, false, 110,
  NULL, 1, 'seed'
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
SELECT m.id, f.id, m.code, f.code,
  false, false, false, false, 120,
  NULL, 1, 'seed'
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
SELECT m.id, f.id, m.code, f.code,
  false, false, true, false, 130,
  'route', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-021'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  sort = EXCLUDED.sort,
  target_entity_type = EXCLUDED.target_entity_type,
  is_filterable = EXCLUDED.is_filterable,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  false, false, false, false, 140,
  NULL, 1, 'seed'
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
SELECT m.id, f.id, m.code, f.code,
  false, false, false, false, 150,
  NULL, 1, 'seed'
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
SELECT m.id, f.id, m.code, f.code,
  false, false, false, false, 160,
  NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-TSK-008'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_task'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;
