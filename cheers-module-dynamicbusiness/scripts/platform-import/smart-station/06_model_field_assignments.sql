-- ============================================================================
-- 智慧站场产品包 · Generated: 2026-07-08 by scripts/generate-smart-station-import.py
--
-- 约定：幂等键 model.code / field.code；关联 INSERT 解析 id，不写 surrogate id。
-- 依赖：先导入 platform-import/system/（实体类型、基础字段、设备模型等）
-- ============================================================================

SET search_path TO dynamicbusiness;
-- dynamic_model_field_assignment: 12 row(s), upsert by model_code + field_code

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-STATION', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, TRUE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-STATION'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-STATION', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, TRUE,
  FALSE, TRUE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-STATION'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-PLANT', 'FLD-FAC-EXT-003', m.id, f.id,
  FALSE, FALSE,
  TRUE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-PLANT'
  AND f.code = 'FLD-FAC-EXT-003'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-PLANT', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, TRUE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-PLANT'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-DEPOT', 'FLD-FAC-EXT-004', m.id, f.id,
  FALSE, TRUE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-DEPOT'
  AND f.code = 'FLD-FAC-EXT-004'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-DEPOT', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, TRUE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-DEPOT'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-OFFICE', 'FLD-FAC-EXT-005', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-OFFICE'
  AND f.code = 'FLD-FAC-EXT-005'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-OFFICE', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, TRUE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-OFFICE'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-ZONE-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-ZONE-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-ZONE-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-ZONE-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-ZONE-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-ZONE-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-ZONE-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  'USER_ADDED', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-ZONE-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

