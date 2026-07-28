-- ============================================================================
-- 管廊租户 · 空间三层数据类型：运营区域（region）、设施（facility）、分区（zone）
-- psql -v corridor_tenant_id=2 -f dynamic_corridor_spatial_entity_types.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

\if :{?corridor_tenant_id}
\else
\set corridor_tenant_id 2
\endif

-- ---------- entity types ----------

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, entry_kind, tenant_id, creator
) VALUES
  (
    'region', '运营区域', NULL,
    '管廊运营/管理区域；分类树维护（与管网组织 tenant 的 region 语义独立）',
    'businessIcon:区域管理.png', '区域', 1, 'active', 'USER', '{}',
    'DEDICATED', 'ent_region', FALSE, NULL, 'NATIVE', :corridor_tenant_id, 'corridor-seed'
  ),
  (
    'facility', '管廊设施', NULL,
    '管廊工程、监控中心等设施点；REF_REGION 挂运营区域',
    'businessIcon:设施管理.png', '设施', 2, 'active', 'USER', '{}',
    'DEDICATED', 'ent_facility', FALSE, NULL, 'NATIVE', :corridor_tenant_id, 'corridor-seed'
  ),
  (
    'zone', '分区', NULL,
    '管廊设施内空间分区：防火区、管廊段、舱段等；REF_FACILITY 必填',
    'businessIcon:区域管理.png', '分区', 3, 'active', 'USER', '{}',
    'DEDICATED', 'ent_zone', FALSE, NULL, 'NATIVE', :corridor_tenant_id, 'corridor-seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  alias = EXCLUDED.alias,
  sort = EXCLUDED.sort,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- configs（物理列映射与 tenant 1 对齐） ----------

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
)
SELECT
  c.entity_type_code,
  CASE c.entity_type_code
    WHEN 'region' THEN '运营区域'
    WHEN 'facility' THEN '管廊设施'
    WHEN 'zone' THEN '分区'
  END,
  c.storage_type, c.dedicated_table_name, c.strategy_bean_name,
  c.enable_rule_engine,
  '管廊租户 · ' || c.entity_type_code,
  c.status, c.physical_column_mapping,
  :corridor_tenant_id, 'corridor-seed'
FROM dynamic_entity_type_config c
WHERE c.deleted = false AND c.tenant_id = 1
  AND c.entity_type_code IN ('region', 'facility', 'zone')
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 类型间 REF 关系 ----------

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
)
SELECT
  r.source_entity_type_code, r.target_entity_type_code, r.relation_name,
  r.auto_create_field, r.default_field_name,
  :corridor_tenant_id, 'corridor-seed'
FROM dynamic_entity_type_relation r
WHERE r.deleted = false AND r.tenant_id = 1
  AND (
    (r.source_entity_type_code, r.target_entity_type_code) IN (
      ('facility', 'region'),
      ('zone', 'facility'),
      ('facility', 'zone')
    )
  )
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 基础字段（从 tenant 1 复制 region / facility / zone） ----------

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required,
  default_value, description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  b.entity_type_code, b.library_field_id, b.field_code, b.field_name, b.data_type, b.required,
  b.default_value, b.description, b.type_config, b.sort_order, b.status,
  :corridor_tenant_id, 'corridor-seed'
FROM dynamic_entity_type_base_field b
WHERE b.deleted = false AND b.tenant_id = 1
  AND b.entity_type_code IN ('region', 'facility', 'zone')
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 模型（zone：根/段/防火区/舱室；口部风亭坑等仅 structure，见 dynamic_corridor_structure.sql） ----------

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES
  ('MODEL-REGION-UT-AREA', '运营区域', 'region', '管廊运营区域节点（本租户通常无实例）', 1, 1, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-FACILITY-UT-TUNNEL', '管廊工程', 'facility', '管廊主体工程；tenant 2 仅一条实例', 1, 1, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-FACILITY-UT-MONITOR', '监控中心', 'facility', '管廊监控中心', 1, 2, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-CORRIDOR-ROOT', '管廊（空间根）', 'zone', '与唯一 facility 同名的根 zone', 1, 1, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', '防火区', 'zone', '管廊防火分区', 1, 10, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-TUNNEL-SEGMENT', '管廊段', 'zone', '管廊段分区', 1, 20, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-CABIN-SEGMENT', '舱段', 'zone', '舱室区段', 1, 30, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-INTEGRATED-PIPE-CABIN', '综合管道舱', 'zone', '管廊 · 综合管道舱', 1, 40, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-POWER-DEDICATED-CABIN', '电力专用舱', 'zone', '管廊 · 电力专用舱', 1, 50, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-POWER-INFO-CABIN', '电力信息仓', 'zone', '管廊 · 电力信息仓', 1, 60, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-PIPE-CABIN', '管道舱', 'zone', '管廊 · 管道舱', 1, 70, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-GAS-CABIN', '燃气舱', 'zone', '管廊 · 燃气舱', 1, 80, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-SEWAGE-CABIN', '污水舱', 'zone', '管廊 · 污水舱', 1, 90, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-WATER-SUPPLY-CABIN', '给水舱', 'zone', '管廊 · 给水舱', 1, 100, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-TELECOM-CABIN', '通信舱', 'zone', '管廊 · 通信舱', 1, 110, :corridor_tenant_id, 'corridor-seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  sort = EXCLUDED.sort,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 字段库 + 模型字段（与 tenant 1 退役 region 模型对齐，按模型显示名匹配） ----------

WITH legacy_field_codes AS (
  SELECT DISTINCT a.field_code
  FROM dynamic_model_field_assignment a
  INNER JOIN dynamic_model m ON m.id = a.model_id
  WHERE a.deleted = false
    AND a.tenant_id = 1
    AND m.entity_type_code = 'region'
    AND m.tenant_id = 1
    AND m.deleted = true
    AND m.code NOT LIKE 'MODEL-REGION-%'
    AND a.field_code NOT IN (
      'FLD-BASE-region-cover_url', 'FLD-BASE-region-hq_location', 'FLD-BASE-region-mission_summary',
      'FLD-BASE-region-pipeline_km_total', 'FLD-BASE-region-pipeline_km_ng', 'FLD-BASE-region-pipeline_km_cr',
      'FLD-BASE-region-pipeline_km_cp', 'FLD-BASE-region-storage_count', 'FLD-BASE-region-lng_terminal_count',
      'FLD-BASE-region-coverage_note', 'FLD-BASE-region-org_mode_note', 'FLD-BASE-region-service_radius_km'
    )
)
INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  f.code, f.name, f.type, f.unit, f.description, f.source, f.status, f.max_relations,
  f.index_strategy, f.options, f.provider_code, f.semantic_type,
  :corridor_tenant_id, 'corridor-seed'
FROM dynamic_field f
INNER JOIN legacy_field_codes l ON l.field_code = f.code
WHERE f.deleted = false
  AND f.tenant_id = 1
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  options = EXCLUDED.options,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type,
  model_code, field_code, tenant_id, creator
)
SELECT
  tgt.id,
  cf.id,
  src.required,
  src.is_searchable,
  src.is_filterable,
  src.is_sortable,
  src.default_value,
  src.validation_rules,
  src.sort,
  src.field_group_id,
  src.field_source,
  src.ref_library_id,
  src.model_relation_id,
  src.target_entity_type,
  tgt.code,
  src.field_code,
  :corridor_tenant_id,
  'corridor-seed'
FROM dynamic_model_field_assignment src
INNER JOIN dynamic_model src_m ON src_m.id = src.model_id
INNER JOIN dynamic_model tgt
  ON tgt.tenant_id = :corridor_tenant_id
 AND tgt.entity_type_code = 'zone'
 AND tgt.deleted = false
 AND tgt.name = src_m.name
INNER JOIN dynamic_field cf
  ON cf.code = src.field_code
 AND cf.tenant_id = :corridor_tenant_id
 AND cf.deleted = false
WHERE src.deleted = false
  AND src.tenant_id = 1
  AND src_m.entity_type_code = 'region'
  AND src_m.tenant_id = 1
  AND src_m.deleted = true
  AND src_m.code NOT LIKE 'MODEL-REGION-%'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  target_entity_type = EXCLUDED.target_entity_type,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;
