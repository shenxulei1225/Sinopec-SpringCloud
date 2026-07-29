-- ============================================================================
-- 管廊租户 · 构筑物（structure）数据类型 + 模型 + 字段（挂设施）
-- 细类模型自 tenant 1 误挂 region 模型迁移；实体表 ent_structure（Flyway V27）
-- psql -v corridor_tenant_id=2 -f dynamic_corridor_structure.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

\if :{?corridor_tenant_id}
\else
\set corridor_tenant_id 2
\endif

-- ---------- 构筑物 entity type ----------

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, entry_kind, tenant_id, creator
) VALUES (
  'structure', '构筑物', NULL,
  '管廊设施上的物理构筑物：舱室、口部、风亭等；REF_FACILITY 必填，可选 REF_ZONE',
  'businessIcon:设施管理.png', '构筑物', 4, 'active', 'USER', '{}',
  'DEDICATED', 'ent_structure', FALSE, NULL, 'NATIVE', :corridor_tenant_id, 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'structure', '构筑物',
  'DEDICATED', 'ent_structure',
  NULL, TRUE,
  '管廊构筑物 ent_structure', 1,
  '{"structure_type": {"type": "VARCHAR", "column": "structure_type", "length": 100}, "description": {"type": "VARCHAR", "column": "description", "length": 500}, "FLD-BASE-structure-REF_FACILITY": {"type": "BIGINT", "column": "fld_base_structure_ref_facility"}, "FLD-BASE-structure-REF_ZONE": {"type": "BIGINT", "column": "fld_base_structure_ref_zone"}, "boundary_crs": {"type": "VARCHAR", "column": "boundary_crs", "length": 32}, "centroid_lat": {"type": "DECIMAL", "scale": 8, "column": "centroid_lat", "precision": 12}, "centroid_lng": {"type": "DECIMAL", "scale": 8, "column": "centroid_lng", "precision": 12}, "max_height_m": {"type": "DECIMAL", "scale": 3, "column": "max_height_m", "precision": 10}, "min_height_m": {"type": "DECIMAL", "scale": 3, "column": "min_height_m", "precision": 10}, "boundary_status": {"type": "VARCHAR", "column": "boundary_status", "length": 32}, "boundary_geojson": {"type": "JSONB", "column": "boundary_geojson"}}',
  :corridor_tenant_id, 'corridor-seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  physical_column_mapping = EXCLUDED.physical_column_mapping,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- REF：构筑物 → 设施 / 分区 ----------

INSERT INTO dynamic_entity_type_relation (
  source_entity_type_code, target_entity_type_code, relation_name,
  auto_create_field, default_field_name, tenant_id, creator
) VALUES
  ('structure', 'facility', '所属设施', TRUE, '所属设施', :corridor_tenant_id, 'corridor-seed'),
  ('structure', 'zone', '所属分区', TRUE, '所属分区', :corridor_tenant_id, 'corridor-seed'),
  ('facility', 'structure', '设施-构筑物', TRUE, '构筑物', :corridor_tenant_id, 'corridor-seed')
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  relation_name = EXCLUDED.relation_name,
  auto_create_field = EXCLUDED.auto_create_field,
  default_field_name = EXCLUDED.default_field_name,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 基础字段（由 zone 模板复制为 structure） ----------

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required,
  default_value, description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'structure',
  b.library_field_id,
  REPLACE(b.field_code, 'FLD-BASE-zone-', 'FLD-BASE-structure-'),
  b.field_name, b.data_type, b.required,
  b.default_value,
  REPLACE(COALESCE(b.description, ''), 'zone', 'structure'),
  b.type_config, b.sort_order, b.status,
  :corridor_tenant_id, 'corridor-seed'
FROM dynamic_entity_type_base_field b
WHERE b.deleted = false AND b.tenant_id = :corridor_tenant_id
  AND b.entity_type_code = 'zone'
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_name = EXCLUDED.field_name,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 构筑物模型（22 细类） ----------

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES
  ('MODEL-STRUCTURE-UT-INTEGRATED-PIPE-CABIN', '综合管道舱', 'structure', '管廊构筑物 · 综合管道舱', 1, 10, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-PERSONNEL-ACCESS', '人员出入口', 'structure', '管廊构筑物 · 人员出入口', 1, 20, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-PIPE-CABIN', '管道舱', 'structure', '管廊构筑物 · 管道舱', 1, 30, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-DRAIN-PIT', '排水坑', 'structure', '管廊构筑物 · 排水坑', 1, 40, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-MECH-EXHAUST', '机械排风口', 'structure', '管廊构筑物 · 机械排风口', 1, 50, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-POWER-DEDICATED-CABIN', '电力专用舱', 'structure', '管廊构筑物 · 电力专用舱', 1, 60, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-GAS-CABIN', '燃气舱', 'structure', '管廊构筑物 · 燃气舱', 1, 70, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-WATER-SUPPLY-CABIN', '给水舱', 'structure', '管廊构筑物 · 给水舱', 1, 80, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-FIRE-COMPARTMENT', '防火区', 'structure', '管廊构筑物 · 防火区', 1, 90, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-NATURAL-AIR-INLET', '自然进风口', 'structure', '管廊构筑物 · 自然进风口', 1, 100, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-INTERSECTION', '交叉口', 'structure', '管廊构筑物 · 交叉口', 1, 110, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-SEWAGE-CABIN', '污水舱', 'structure', '管廊构筑物 · 污水舱', 1, 120, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-MECH-AIR-INLET', '机械通风口', 'structure', '管廊构筑物 · 机械通风口', 1, 130, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-END-SHAFT', '端井', 'structure', '管廊构筑物 · 端井', 1, 140, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-TUNNEL-SEGMENT', '管廊段', 'structure', '管廊构筑物 · 管廊段', 1, 150, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-INVERTED-SIPHON', '倒虹', 'structure', '管廊构筑物 · 倒虹', 1, 160, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-SUBSTATION', '分变电所', 'structure', '管廊构筑物 · 分变电所', 1, 170, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-HOIST-PORT', '吊装口', 'structure', '管廊构筑物 · 吊装口', 1, 180, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-LEAD-OUT-DOOR', '引出门', 'structure', '管廊构筑物 · 引出门', 1, 190, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-POWER-INFO-CABIN', '电力信息仓', 'structure', '管廊构筑物 · 电力信息仓', 1, 200, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-TELECOM-CABIN', '通信舱', 'structure', '管廊构筑物 · 通信舱', 1, 210, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-STRUCTURE-UT-SUMP-PIT', '集水坑', 'structure', '管廊构筑物 · 集水坑', 1, 220, :corridor_tenant_id, 'corridor-seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  sort = EXCLUDED.sort,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 字段库 + 模型分配（自 tenant 1 退役 region 模型） ----------

WITH legacy_field_codes AS (
  SELECT DISTINCT a.field_code
  FROM dynamic_model_field_assignment a
  INNER JOIN dynamic_model m ON m.id = a.model_id
  WHERE a.deleted = false AND a.tenant_id = 1
    AND m.entity_type_code = 'region' AND m.tenant_id = 1 AND m.deleted = true
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
WHERE f.deleted = false AND f.tenant_id = 1
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  default_value, validation_rules, sort, field_group_id, field_source,
  ref_library_id, model_relation_id, target_entity_type,
  model_code, field_code, tenant_id, creator
)
SELECT
  tgt.id, cf.id,
  src.required, src.is_searchable, src.is_filterable, src.is_sortable,
  src.default_value, src.validation_rules, src.sort,
  src.field_group_id, src.field_source,
  src.ref_library_id, src.model_relation_id, src.target_entity_type,
  tgt.code, src.field_code,
  :corridor_tenant_id, 'corridor-seed'
FROM dynamic_model_field_assignment src
INNER JOIN dynamic_model src_m ON src_m.id = src.model_id
INNER JOIN dynamic_model tgt
  ON tgt.tenant_id = :corridor_tenant_id
 AND tgt.entity_type_code = 'structure'
 AND tgt.deleted = false
 AND tgt.name = src_m.name
INNER JOIN dynamic_field cf
  ON cf.code = src.field_code AND cf.tenant_id = :corridor_tenant_id AND cf.deleted = false
WHERE src.deleted = false AND src.tenant_id = 1
  AND src_m.entity_type_code = 'region' AND src_m.tenant_id = 1
  AND src_m.deleted = true AND src_m.code NOT LIKE 'MODEL-REGION-%'
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  sort = EXCLUDED.sort,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;
