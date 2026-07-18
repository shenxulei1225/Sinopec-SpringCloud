-- ============================================================================
-- 系统 · 巡检管理模型（patrol_schedule / patrol_object / patrol_point / inspection_item）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'patrol_schedule', '巡检排期',
  'patrol_schedule',
  '排期模板与实例：周期 cron 或单次 start_at。',
  1, 1,
  '[{"id":"basic","name":"基本信息","sort":1},{"id":"schedule","name":"排期规则","sort":2}]',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'patrol_object', '巡检对象',
  'patrol_object',
  '巡检对象模板：关联设施、设备与可选分区。',
  1, 1,
  '[{"id":"basic","name":"基本信息","sort":1},{"id":"scope","name":"巡检范围","sort":2}]',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'patrol_point', '巡检点',
  'patrol_point',
  '巡检点模板：设施、拓扑版本、停留点 nodeId 列表、机动剖面。',
  1, 1,
  '[{"id":"basic","name":"基本信息","sort":1},{"id":"routing","name":"路径输入","sort":2}]',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES (
  'inspection_item', '检查内容',
  'inspection_item',
  '单条检查项；is_template=true 供任务模板选择。',
  1, 1,
  '[{"id":"basic","name":"检查项","sort":1}]',
  1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- patrol_schedule 字段分配
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, true, true, false, 10, NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-PSC-001'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_schedule'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 20, NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-PSC-002'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_schedule'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 30, 'Asia/Shanghai', NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-PSC-003'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_schedule'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, default_value = EXCLUDED.default_value, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 40, NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-PSC-004'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_schedule'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;

-- patrol_object 字段分配
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, true, false, 10, NULL, 'facility', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-POB-001'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_object'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, target_entity_type = EXCLUDED.target_entity_type, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 20, NULL, 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-POB-002'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_object'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, target_entity_type = EXCLUDED.target_entity_type, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 30, NULL, 'zone', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-POB-003'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_object'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, target_entity_type = EXCLUDED.target_entity_type, updater = 'seed', update_time = CURRENT_TIMESTAMP;

-- patrol_point 字段分配
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, true, false, 10, NULL, 'facility', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-PPT-001'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_point'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, target_entity_type = EXCLUDED.target_entity_type, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, true, false, 20, NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-PPT-002'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_point'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, false, false, false, 30, NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-PPT-003'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_point'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, true, false, 40, 'ROBOT_GROUND', NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-PPT-004'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'patrol_point'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, default_value = EXCLUDED.default_value, updater = 'seed', update_time = CURRENT_TIMESTAMP;

-- inspection_item 最小字段（名称 + 是否必填类检查项）
INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, true, true, true, false, 10, NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-INS-001'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'inspection_item'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, required = EXCLUDED.required, updater = 'seed', update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, target_entity_type, tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code, false, false, false, false, 20, NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1 AND f.code = 'FLD-INS-002'
WHERE m.deleted = false AND m.tenant_id = 1 AND m.code = 'inspection_item'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET sort = EXCLUDED.sort, updater = 'seed', update_time = CURRENT_TIMESTAMP;
