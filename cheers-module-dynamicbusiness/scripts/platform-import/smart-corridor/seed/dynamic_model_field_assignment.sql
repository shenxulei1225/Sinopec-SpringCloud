-- ============================================================================
-- 管廊 · dynamic_model_field_assignment
-- Generated: 2026-07-13 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 幂等 upsert；关联字段按 code 解析 id，不写 surrogate id
-- ============================================================================

SET search_path TO dynamicbusiness;

-- dynamic_model_field_assignment: 462 row(s), upsert by model_code + field_code

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
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-DEPOT', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-DEPOT'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
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
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-DEPOT', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-DEPOT'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-DEPOT', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-DEPOT'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-DEPOT', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-DEPOT'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-DEPOT', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-DEPOT'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-DEPOT', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-DEPOT'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
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
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-OFFICE', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-OFFICE'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
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
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-OFFICE', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-OFFICE'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-OFFICE', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-OFFICE'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-OFFICE', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-OFFICE'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-OFFICE', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-OFFICE'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-OFFICE', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-OFFICE'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
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
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-REF_FACILITY', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-REF_FACILITY'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-zone_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-zone_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-description', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-description'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-boundary_geojson', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-boundary_geojson'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-boundary_crs', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-boundary_crs'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-boundary_status', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-boundary_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-min_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  43, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-min_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-max_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  44, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-max_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-centroid_lng', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  45, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-centroid_lng'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-centroid_lat', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  46, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-centroid_lat'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-TANK-GROUP', 'FLD-BASE-zone-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-TANK-GROUP'
  AND f.code = 'FLD-BASE-zone-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
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
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-REF_FACILITY', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-REF_FACILITY'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-zone_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-zone_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-description', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-description'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-boundary_geojson', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-boundary_geojson'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-boundary_crs', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-boundary_crs'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-boundary_status', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-boundary_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-min_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  43, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-min_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-max_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  44, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-max_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-centroid_lng', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  45, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-centroid_lng'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-centroid_lat', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  46, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-centroid_lat'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-WAREHOUSE', 'FLD-BASE-zone-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-WAREHOUSE'
  AND f.code = 'FLD-BASE-zone-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
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
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-REF_FACILITY', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-REF_FACILITY'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-zone_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-zone_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-description', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-description'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-boundary_geojson', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-boundary_geojson'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-boundary_crs', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-boundary_crs'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-boundary_status', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-boundary_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-min_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  43, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-min_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-max_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  44, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-max_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-centroid_lng', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  45, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-centroid_lng'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-centroid_lat', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  46, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-centroid_lat'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-FUNCTIONAL', 'FLD-BASE-zone-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-FUNCTIONAL'
  AND f.code = 'FLD-BASE-zone-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
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
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-REF_FACILITY', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-REF_FACILITY'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-zone_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-zone_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-description', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-description'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-boundary_geojson', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-boundary_geojson'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-boundary_crs', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-boundary_crs'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-boundary_status', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-boundary_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-min_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  43, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-min_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-max_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  44, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-max_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-centroid_lng', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  45, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-centroid_lng'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-centroid_lat', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  46, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-centroid_lat'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ZONE-BUILDING', 'FLD-BASE-zone-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ZONE-BUILDING'
  AND f.code = 'FLD-BASE-zone-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-003', m.id, f.id,
  FALSE, FALSE,
  TRUE, FALSE,
  '巡检', NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-003'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-004', m.id, f.id,
  FALSE, FALSE,
  TRUE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-004'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-005', m.id, f.id,
  FALSE, FALSE,
  TRUE, FALSE,
  NULL, NULL,
  30, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-005'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-022', m.id, f.id,
  FALSE, FALSE,
  TRUE, FALSE,
  'draft', NULL,
  40, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-022'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-028', m.id, f.id,
  TRUE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  50, NULL,
  NULL, NULL,
  NULL, 'patrol_schedule', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-028'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-029', m.id, f.id,
  TRUE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  60, NULL,
  NULL, NULL,
  NULL, 'patrol_object', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-029'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-030', m.id, f.id,
  TRUE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  70, NULL,
  NULL, NULL,
  NULL, 'patrol_point', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-030'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-031', m.id, f.id,
  TRUE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  80, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-031'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-024', m.id, f.id,
  TRUE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  90, NULL,
  NULL, NULL,
  NULL, 'facility', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-024'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-020', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  120, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-020'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-027', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  155, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-027'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-023', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  160, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-023'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-007', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  170, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-007'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-008', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  180, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-008'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-032', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  200, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-032'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-033', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  210, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-033'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_task', 'FLD-TSK-034', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  220, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_task'
  AND f.code = 'FLD-TSK-034'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-region_level', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-region_level'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-admin_code', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-admin_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-region_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-region_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-description', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-description'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-boundary_geojson', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-boundary_geojson'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-boundary_crs', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-boundary_crs'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-boundary_status', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-boundary_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-min_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  43, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-min_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-max_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  44, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-max_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-centroid_lng', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  45, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-centroid_lng'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-centroid_lat', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  46, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-centroid_lat'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-responsible_person', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-responsible_person'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-belong_department', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-belong_department'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-establish_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-establish_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-safety_level', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  80, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-safety_level'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-GROUP', 'FLD-BASE-region-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-GROUP'
  AND f.code = 'FLD-BASE-region-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-region_level', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-region_level'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-admin_code', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-admin_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-region_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-region_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-description', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-description'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-boundary_geojson', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-boundary_geojson'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-boundary_crs', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-boundary_crs'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-boundary_status', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-boundary_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-min_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  43, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-min_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-max_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  44, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-max_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-centroid_lng', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  45, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-centroid_lng'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-centroid_lat', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  46, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-centroid_lat'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-responsible_person', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-responsible_person'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-belong_department', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-belong_department'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-establish_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-establish_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-safety_level', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  80, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-safety_level'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PROVINCIAL', 'FLD-BASE-region-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PROVINCIAL'
  AND f.code = 'FLD-BASE-region-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-region_level', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-region_level'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-admin_code', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-admin_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-region_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-region_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-description', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-description'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-boundary_geojson', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-boundary_geojson'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-boundary_crs', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-boundary_crs'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-boundary_status', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-boundary_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-min_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  43, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-min_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-max_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  44, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-max_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-centroid_lng', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  45, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-centroid_lng'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-centroid_lat', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  46, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-centroid_lat'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-responsible_person', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-responsible_person'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-belong_department', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-belong_department'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-establish_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-establish_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-safety_level', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  80, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-safety_level'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-OPERATION', 'FLD-BASE-region-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-OPERATION'
  AND f.code = 'FLD-BASE-region-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_schedule', 'FLD-BASE-patrol_schedule-is_template', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  0, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_schedule'
  AND f.code = 'FLD-BASE-patrol_schedule-is_template'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_schedule', 'FLD-PSC-001', m.id, f.id,
  TRUE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_schedule'
  AND f.code = 'FLD-PSC-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_schedule', 'FLD-PSC-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_schedule'
  AND f.code = 'FLD-PSC-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_schedule', 'FLD-PSC-003', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  'Asia/Shanghai', NULL,
  30, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_schedule'
  AND f.code = 'FLD-PSC-003'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_schedule', 'FLD-PSC-004', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  40, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_schedule'
  AND f.code = 'FLD-PSC-004'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_object', 'FLD-BASE-patrol_object-is_template', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  0, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_object'
  AND f.code = 'FLD-BASE-patrol_object-is_template'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_object', 'FLD-POB-001', m.id, f.id,
  TRUE, FALSE,
  TRUE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, 'facility', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_object'
  AND f.code = 'FLD-POB-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_object', 'FLD-POB-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_object'
  AND f.code = 'FLD-POB-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_object', 'FLD-POB-003', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  30, NULL,
  NULL, NULL,
  NULL, 'zone', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_object'
  AND f.code = 'FLD-POB-003'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_point', 'FLD-BASE-patrol_point-is_template', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  0, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_point'
  AND f.code = 'FLD-BASE-patrol_point-is_template'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_point', 'FLD-PPT-001', m.id, f.id,
  TRUE, FALSE,
  TRUE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, 'facility', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_point'
  AND f.code = 'FLD-PPT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_point', 'FLD-PPT-002', m.id, f.id,
  TRUE, FALSE,
  TRUE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_point'
  AND f.code = 'FLD-PPT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_point', 'FLD-PPT-003', m.id, f.id,
  TRUE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  30, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_point'
  AND f.code = 'FLD-PPT-003'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'patrol_point', 'FLD-PPT-004', m.id, f.id,
  FALSE, FALSE,
  TRUE, FALSE,
  'ROBOT_GROUND', NULL,
  40, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'patrol_point'
  AND f.code = 'FLD-PPT-004'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'inspection_item', 'FLD-BASE-inspection_item-is_template', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  0, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'inspection_item'
  AND f.code = 'FLD-BASE-inspection_item-is_template'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'inspection_item', 'FLD-INS-001', m.id, f.id,
  TRUE, TRUE,
  TRUE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'inspection_item'
  AND f.code = 'FLD-INS-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'inspection_item', 'FLD-INS-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'inspection_item'
  AND f.code = 'FLD-INS-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9675f9e695be4a5e9debf2e79a062159', 'FLD-BASE-inspection_item-is_template', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  0, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9675f9e695be4a5e9debf2e79a062159'
  AND f.code = 'FLD-BASE-inspection_item-is_template'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-67700b0e8f434a3babad5cdb4efe93a5', 'FLD-BASE-inspection_item-is_template', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  0, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-67700b0e8f434a3babad5cdb4efe93a5'
  AND f.code = 'FLD-BASE-inspection_item-is_template'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-maintenance_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-maintenance_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-priority', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-priority'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-order_status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-order_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-source_type', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  23, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-source_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-plan_time', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  30, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-plan_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-deadline', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  31, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-deadline'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-estimated_duration', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  32, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-estimated_duration'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-maintenance_content', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  33, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-maintenance_content'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-REF_EQUIPMENT', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  34, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-REF_EQUIPMENT'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-executor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-executor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-maintainer_team', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-maintainer_team'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-supervisor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-supervisor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-actual_start_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-actual_start_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-actual_end_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-actual_end_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-estimated_cost', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-estimated_cost'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e', 'FLD-BASE-maintenance-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4c2cee04c26b4ab2b7e03aa617e9d45e'
  AND f.code = 'FLD-BASE-maintenance-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-maintenance_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-maintenance_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-priority', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-priority'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-order_status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-order_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-source_type', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  23, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-source_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-plan_time', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  30, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-plan_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-deadline', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  31, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-deadline'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-estimated_duration', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  32, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-estimated_duration'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-maintenance_content', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  33, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-maintenance_content'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-REF_EQUIPMENT', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  34, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-REF_EQUIPMENT'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-executor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-executor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-maintainer_team', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-maintainer_team'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-supervisor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-supervisor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-actual_start_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-actual_start_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-actual_end_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-actual_end_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-estimated_cost', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-estimated_cost'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f51a5a357bea429dbf34dad1383b2064', 'FLD-BASE-maintenance-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f51a5a357bea429dbf34dad1383b2064'
  AND f.code = 'FLD-BASE-maintenance-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-maintenance_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-maintenance_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-priority', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-priority'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-order_status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-order_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-source_type', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  23, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-source_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-plan_time', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  30, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-plan_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-deadline', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  31, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-deadline'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-estimated_duration', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  32, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-estimated_duration'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-maintenance_content', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  33, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-maintenance_content'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-REF_EQUIPMENT', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  34, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-REF_EQUIPMENT'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-executor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-executor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-maintainer_team', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-maintainer_team'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-supervisor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-supervisor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-actual_start_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-actual_start_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-actual_end_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-actual_end_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-estimated_cost', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-estimated_cost'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-30dec62f219f413d85d1051064a057cc', 'FLD-BASE-maintenance-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-30dec62f219f413d85d1051064a057cc'
  AND f.code = 'FLD-BASE-maintenance-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-maintenance_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-maintenance_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-priority', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-priority'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-order_status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-order_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-source_type', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  23, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-source_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-plan_time', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  30, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-plan_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-deadline', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  31, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-deadline'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-estimated_duration', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  32, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-estimated_duration'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-maintenance_content', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  33, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-maintenance_content'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-REF_EQUIPMENT', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  34, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-REF_EQUIPMENT'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-executor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-executor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-maintainer_team', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-maintainer_team'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-supervisor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-supervisor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-actual_start_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-actual_start_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-actual_end_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-actual_end_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-estimated_cost', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-estimated_cost'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-d6d8373653d94979aed408b4296e5f7f', 'FLD-BASE-maintenance-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-d6d8373653d94979aed408b4296e5f7f'
  AND f.code = 'FLD-BASE-maintenance-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-maintenance_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-maintenance_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-priority', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-priority'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-order_status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-order_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-source_type', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  23, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-source_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-plan_time', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  30, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-plan_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-deadline', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  31, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-deadline'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-estimated_duration', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  32, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-estimated_duration'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-maintenance_content', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  33, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-maintenance_content'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-REF_EQUIPMENT', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  34, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-REF_EQUIPMENT'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-executor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-executor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-maintainer_team', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-maintainer_team'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-supervisor', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-supervisor'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-actual_start_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-actual_start_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-actual_end_time', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-actual_end_time'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-estimated_cost', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-estimated_cost'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9e81dd6505054fc7983a34a7f057179e', 'FLD-BASE-maintenance-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9e81dd6505054fc7983a34a7f057179e'
  AND f.code = 'FLD-BASE-maintenance-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f15c2e2f89c6482ca9fab8c993d7b762', 'FLD-BASE-customer-contact_phone', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f15c2e2f89c6482ca9fab8c993d7b762'
  AND f.code = 'FLD-BASE-customer-contact_phone'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-f15c2e2f89c6482ca9fab8c993d7b762', 'FLD-BASE-customer-REL_EQUIPMENT', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  900, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-f15c2e2f89c6482ca9fab8c993d7b762'
  AND f.code = 'FLD-BASE-customer-REL_EQUIPMENT'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-28c02570bccf40e9aa2824370215a5b9', 'FLD-BASE-customer-contact_phone', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-28c02570bccf40e9aa2824370215a5b9'
  AND f.code = 'FLD-BASE-customer-contact_phone'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-28c02570bccf40e9aa2824370215a5b9', 'FLD-BASE-customer-REL_EQUIPMENT', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  900, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-28c02570bccf40e9aa2824370215a5b9'
  AND f.code = 'FLD-BASE-customer-REL_EQUIPMENT'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-dcc4056fe9c545d6b81afa6357bc576f', 'FLD-BASE-customer-contact_phone', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-dcc4056fe9c545d6b81afa6357bc576f'
  AND f.code = 'FLD-BASE-customer-contact_phone'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-dcc4056fe9c545d6b81afa6357bc576f', 'FLD-BASE-customer-REL_EQUIPMENT', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  900, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-dcc4056fe9c545d6b81afa6357bc576f'
  AND f.code = 'FLD-BASE-customer-REL_EQUIPMENT'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-REFINED-DEPOT', 'FLD-FAC-EXT-004', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-REFINED-DEPOT'
  AND f.code = 'FLD-FAC-EXT-004'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-REFINED-DEPOT', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-REFINED-DEPOT'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-REFINED-DEPOT', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-REFINED-DEPOT'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-REFINED-DEPOT', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-REFINED-DEPOT'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-REFINED-DEPOT', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-REFINED-DEPOT'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-REFINED-DEPOT', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-REFINED-DEPOT'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-REFINED-DEPOT', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-REFINED-DEPOT'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-REFINED-DEPOT', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-REFINED-DEPOT'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-RECEIVING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-RECEIVING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-RECEIVING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-RECEIVING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-RECEIVING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-RECEIVING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-RECEIVING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-RECEIVING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-RECEIVING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-RECEIVING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-RECEIVING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-RECEIVING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-RECEIVING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-RECEIVING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-RECEIVING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-RECEIVING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-region_level', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  15, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-region_level'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-admin_code', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  16, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-admin_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-region_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-region_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-description', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-description'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-boundary_geojson', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  40, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-boundary_geojson'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-boundary_crs', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  41, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-boundary_crs'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-boundary_status', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  42, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-boundary_status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-min_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  43, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-min_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-max_height_m', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  44, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-max_height_m'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-centroid_lng', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  45, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-centroid_lng'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-centroid_lat', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  46, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-centroid_lat'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-responsible_person', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  50, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-responsible_person'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-belong_department', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  51, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-belong_department'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-establish_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  60, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-establish_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-safety_level', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  80, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-safety_level'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-REGION-PIPELINE', 'FLD-BASE-region-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-REGION-PIPELINE'
  AND f.code = 'FLD-BASE-region-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-RECEIVING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-RECEIVING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-RECEIVING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-RECEIVING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-RECEIVING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-RECEIVING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-RECEIVING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-RECEIVING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-RECEIVING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-RECEIVING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-RECEIVING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-RECEIVING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-RECEIVING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-RECEIVING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-RECEIVING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-RECEIVING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-VALVE-CHAMBER'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-VALVE-CHAMBER'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-RECEIVING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-RECEIVING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-RECEIVING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-RECEIVING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-RECEIVING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-RECEIVING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-RECEIVING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-RECEIVING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-RECEIVING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-RECEIVING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-RECEIVING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-RECEIVING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-RECEIVING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-RECEIVING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-RECEIVING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-RECEIVING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-VALVE-CHAMBER'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-VALVE-CHAMBER'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-VALVE-CHAMBER'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-VALVE-CHAMBER'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-VALVE-CHAMBER'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-TERMINAL', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-TERMINAL'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-TERMINAL', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-TERMINAL'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-TERMINAL', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-TERMINAL'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-TERMINAL', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-TERMINAL'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-TERMINAL', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-TERMINAL'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-TERMINAL', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-TERMINAL'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-TERMINAL', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-TERMINAL'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-TERMINAL', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-TERMINAL'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-HEAD', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-HEAD'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-HEAD', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-HEAD'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-HEAD', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-HEAD'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-HEAD', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-HEAD'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-HEAD', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-HEAD'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-HEAD', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-HEAD'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-HEAD', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-HEAD'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-HEAD', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-HEAD'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-PIGGING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-PIGGING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-PIGGING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-PIGGING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-PIGGING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-PIGGING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-PIGGING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-PIGGING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-PIGGING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-PIGGING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-PIGGING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-PIGGING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-PIGGING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-PIGGING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-NG-PIGGING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-NG-PIGGING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-PIGGING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-PIGGING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-PIGGING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-PIGGING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-PIGGING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-PIGGING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-PIGGING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-PIGGING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-PIGGING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-PIGGING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-PIGGING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-PIGGING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-PIGGING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-PIGGING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-PIGGING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-PIGGING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-TERMINAL', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-TERMINAL'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-TERMINAL', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-TERMINAL'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-TERMINAL', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-TERMINAL'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-TERMINAL', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-TERMINAL'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-TERMINAL', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-TERMINAL'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-TERMINAL', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-TERMINAL'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-TERMINAL', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-TERMINAL'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-TERMINAL', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-TERMINAL'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-HEAD', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-HEAD'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-HEAD', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-HEAD'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-HEAD', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-HEAD'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-HEAD', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-HEAD'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-HEAD', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-HEAD'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-HEAD', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-HEAD'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-HEAD', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-HEAD'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CR-HEAD', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CR-HEAD'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-PIGGING', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-PIGGING'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-PIGGING', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-PIGGING'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-PIGGING', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-PIGGING'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-PIGGING', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-PIGGING'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-PIGGING', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-PIGGING'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-PIGGING', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-PIGGING'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-PIGGING', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-PIGGING'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-PIGGING', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-PIGGING'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-TERMINAL', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-TERMINAL'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-TERMINAL', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-TERMINAL'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-TERMINAL', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-TERMINAL'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-TERMINAL', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-TERMINAL'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-TERMINAL', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-TERMINAL'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-TERMINAL', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-TERMINAL'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-TERMINAL', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-TERMINAL'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-TERMINAL', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-TERMINAL'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-HEAD', 'FLD-FAC-EXT-001', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  10, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-HEAD'
  AND f.code = 'FLD-FAC-EXT-001'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-HEAD', 'FLD-BASE-facility-REF_REGION', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  10, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-HEAD'
  AND f.code = 'FLD-BASE-facility-REF_REGION'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-HEAD', 'FLD-FAC-EXT-002', m.id, f.id,
  FALSE, FALSE,
  FALSE, FALSE,
  NULL, NULL,
  20, NULL,
  NULL, NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-HEAD'
  AND f.code = 'FLD-FAC-EXT-002'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-HEAD', 'FLD-BASE-facility-address', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  20, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-HEAD'
  AND f.code = 'FLD-BASE-facility-address'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-HEAD', 'FLD-BASE-facility-longitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  21, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-HEAD'
  AND f.code = 'FLD-BASE-facility-longitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-HEAD', 'FLD-BASE-facility-latitude', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  22, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-HEAD'
  AND f.code = 'FLD-BASE-facility-latitude'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-HEAD', 'FLD-BASE-facility-facility_type', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  25, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-HEAD'
  AND f.code = 'FLD-BASE-facility-facility_type'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-FACILITY-CP-HEAD', 'FLD-BASE-facility-remark', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  91, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-FACILITY-CP-HEAD'
  AND f.code = 'FLD-BASE-facility-remark'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4157c47965794397bfe62560da4ecd3c', 'FLD-BASE-pipeline-pipeline_code', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4157c47965794397bfe62560da4ecd3c'
  AND f.code = 'FLD-BASE-pipeline-pipeline_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4157c47965794397bfe62560da4ecd3c', 'FLD-BASE-pipeline-pipeline_name', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4157c47965794397bfe62560da4ecd3c'
  AND f.code = 'FLD-BASE-pipeline-pipeline_name'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4157c47965794397bfe62560da4ecd3c', 'FLD-BASE-pipeline-install_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4157c47965794397bfe62560da4ecd3c'
  AND f.code = 'FLD-BASE-pipeline-install_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4157c47965794397bfe62560da4ecd3c', 'FLD-BASE-pipeline-status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4157c47965794397bfe62560da4ecd3c'
  AND f.code = 'FLD-BASE-pipeline-status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4157c47965794397bfe62560da4ecd3c', 'FLD-BASE-pipeline-manufacturer', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4157c47965794397bfe62560da4ecd3c'
  AND f.code = 'FLD-BASE-pipeline-manufacturer'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-4157c47965794397bfe62560da4ecd3c', 'FLD-BASE-pipeline-pipeline_model', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-4157c47965794397bfe62560da4ecd3c'
  AND f.code = 'FLD-BASE-pipeline-pipeline_model'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ffe9d3c866ed467495b8f7392f953732', 'FLD-BASE-pipeline-pipeline_code', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ffe9d3c866ed467495b8f7392f953732'
  AND f.code = 'FLD-BASE-pipeline-pipeline_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ffe9d3c866ed467495b8f7392f953732', 'FLD-BASE-pipeline-pipeline_name', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ffe9d3c866ed467495b8f7392f953732'
  AND f.code = 'FLD-BASE-pipeline-pipeline_name'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ffe9d3c866ed467495b8f7392f953732', 'FLD-BASE-pipeline-install_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ffe9d3c866ed467495b8f7392f953732'
  AND f.code = 'FLD-BASE-pipeline-install_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ffe9d3c866ed467495b8f7392f953732', 'FLD-BASE-pipeline-status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ffe9d3c866ed467495b8f7392f953732'
  AND f.code = 'FLD-BASE-pipeline-status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ffe9d3c866ed467495b8f7392f953732', 'FLD-BASE-pipeline-manufacturer', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ffe9d3c866ed467495b8f7392f953732'
  AND f.code = 'FLD-BASE-pipeline-manufacturer'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-ffe9d3c866ed467495b8f7392f953732', 'FLD-BASE-pipeline-pipeline_model', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-ffe9d3c866ed467495b8f7392f953732'
  AND f.code = 'FLD-BASE-pipeline-pipeline_model'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-16f482c6b23c4cedb99253701e6dfa3a', 'FLD-BASE-pipeline-pipeline_code', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-16f482c6b23c4cedb99253701e6dfa3a'
  AND f.code = 'FLD-BASE-pipeline-pipeline_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-16f482c6b23c4cedb99253701e6dfa3a', 'FLD-BASE-pipeline-pipeline_name', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-16f482c6b23c4cedb99253701e6dfa3a'
  AND f.code = 'FLD-BASE-pipeline-pipeline_name'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-16f482c6b23c4cedb99253701e6dfa3a', 'FLD-BASE-pipeline-install_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-16f482c6b23c4cedb99253701e6dfa3a'
  AND f.code = 'FLD-BASE-pipeline-install_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-16f482c6b23c4cedb99253701e6dfa3a', 'FLD-BASE-pipeline-status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-16f482c6b23c4cedb99253701e6dfa3a'
  AND f.code = 'FLD-BASE-pipeline-status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-16f482c6b23c4cedb99253701e6dfa3a', 'FLD-BASE-pipeline-manufacturer', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-16f482c6b23c4cedb99253701e6dfa3a'
  AND f.code = 'FLD-BASE-pipeline-manufacturer'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-16f482c6b23c4cedb99253701e6dfa3a', 'FLD-BASE-pipeline-pipeline_model', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-16f482c6b23c4cedb99253701e6dfa3a'
  AND f.code = 'FLD-BASE-pipeline-pipeline_model'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-fe3add78f41d477b934c79abd431a887', 'FLD-BASE-pipeline-pipeline_code', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fe3add78f41d477b934c79abd431a887'
  AND f.code = 'FLD-BASE-pipeline-pipeline_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-fe3add78f41d477b934c79abd431a887', 'FLD-BASE-pipeline-pipeline_name', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fe3add78f41d477b934c79abd431a887'
  AND f.code = 'FLD-BASE-pipeline-pipeline_name'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-fe3add78f41d477b934c79abd431a887', 'FLD-BASE-pipeline-install_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fe3add78f41d477b934c79abd431a887'
  AND f.code = 'FLD-BASE-pipeline-install_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-fe3add78f41d477b934c79abd431a887', 'FLD-BASE-pipeline-status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fe3add78f41d477b934c79abd431a887'
  AND f.code = 'FLD-BASE-pipeline-status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-fe3add78f41d477b934c79abd431a887', 'FLD-BASE-pipeline-manufacturer', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fe3add78f41d477b934c79abd431a887'
  AND f.code = 'FLD-BASE-pipeline-manufacturer'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-fe3add78f41d477b934c79abd431a887', 'FLD-BASE-pipeline-pipeline_model', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-fe3add78f41d477b934c79abd431a887'
  AND f.code = 'FLD-BASE-pipeline-pipeline_model'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9713a917c525454b8cf0726cb44745aa', 'FLD-BASE-pipeline-pipeline_code', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9713a917c525454b8cf0726cb44745aa'
  AND f.code = 'FLD-BASE-pipeline-pipeline_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9713a917c525454b8cf0726cb44745aa', 'FLD-BASE-pipeline-pipeline_name', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9713a917c525454b8cf0726cb44745aa'
  AND f.code = 'FLD-BASE-pipeline-pipeline_name'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9713a917c525454b8cf0726cb44745aa', 'FLD-BASE-pipeline-install_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9713a917c525454b8cf0726cb44745aa'
  AND f.code = 'FLD-BASE-pipeline-install_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9713a917c525454b8cf0726cb44745aa', 'FLD-BASE-pipeline-status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9713a917c525454b8cf0726cb44745aa'
  AND f.code = 'FLD-BASE-pipeline-status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9713a917c525454b8cf0726cb44745aa', 'FLD-BASE-pipeline-manufacturer', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9713a917c525454b8cf0726cb44745aa'
  AND f.code = 'FLD-BASE-pipeline-manufacturer'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-9713a917c525454b8cf0726cb44745aa', 'FLD-BASE-pipeline-pipeline_model', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-9713a917c525454b8cf0726cb44745aa'
  AND f.code = 'FLD-BASE-pipeline-pipeline_model'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-bf8891bf820a490cad609363471e2b82', 'FLD-BASE-pipeline-pipeline_code', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  1, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bf8891bf820a490cad609363471e2b82'
  AND f.code = 'FLD-BASE-pipeline-pipeline_code'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-bf8891bf820a490cad609363471e2b82', 'FLD-BASE-pipeline-pipeline_name', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  2, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bf8891bf820a490cad609363471e2b82'
  AND f.code = 'FLD-BASE-pipeline-pipeline_name'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-bf8891bf820a490cad609363471e2b82', 'FLD-BASE-pipeline-install_date', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bf8891bf820a490cad609363471e2b82'
  AND f.code = 'FLD-BASE-pipeline-install_date'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-bf8891bf820a490cad609363471e2b82', 'FLD-BASE-pipeline-status', m.id, f.id,
  TRUE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  3, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bf8891bf820a490cad609363471e2b82'
  AND f.code = 'FLD-BASE-pipeline-status'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-bf8891bf820a490cad609363471e2b82', 'FLD-BASE-pipeline-manufacturer', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  4, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bf8891bf820a490cad609363471e2b82'
  AND f.code = 'FLD-BASE-pipeline-manufacturer'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_code, field_code, model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type, tenant_id, creator
)
SELECT
  'MODEL-bf8891bf820a490cad609363471e2b82', 'FLD-BASE-pipeline-pipeline_model', m.id, f.id,
  FALSE, TRUE,
  TRUE, TRUE,
  NULL, NULL,
  5, NULL,
  'BASE', NULL,
  NULL, NULL, 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f ON f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false AND m.tenant_id = 1
  AND m.code = 'MODEL-bf8891bf820a490cad609363471e2b82'
  AND f.code = 'FLD-BASE-pipeline-pipeline_model'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  default_value = EXCLUDED.default_value,
  validation_rules = EXCLUDED.validation_rules,
  sort = EXCLUDED.sort,
  field_group_id = EXCLUDED.field_group_id,
  field_source = EXCLUDED.field_source,
  ref_library_id = EXCLUDED.ref_library_id,
  model_relation_id = EXCLUDED.model_relation_id,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
