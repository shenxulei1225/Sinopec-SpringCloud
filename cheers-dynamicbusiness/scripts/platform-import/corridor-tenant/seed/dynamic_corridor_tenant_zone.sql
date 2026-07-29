-- **已废弃**：构筑物细类已迁至 dynamic_corridor_structure.sql（entity_type=structure）。
-- 请使用 corridor-tenant/import.sh（默认 tenant 2）。

SET search_path TO dynamicbusiness;

\if :{?corridor_tenant_id}
\else
\set corridor_tenant_id 121
\endif

-- --------------------------------------------------------------------------
-- 1. 数据类型：管廊租户 zone（分区台账）
-- --------------------------------------------------------------------------

INSERT INTO dynamic_entity_type (
  code, name, parent_id, description, icon, alias, sort, status, type_level,
  association_fields, storage_type, dedicated_table_name, enable_rule_engine,
  physical_column_mapping, tenant_id, creator
) VALUES (
  'zone', '管廊分区', NULL,
  '管廊设施内部空间单元：舱室、防火区、口部构筑物等；REF_FACILITY 必填',
  'businessIcon:区域管理.png', '分区', 2, 'active', 'USER', '{}',
  'DEDICATED', 'ent_zone', FALSE, NULL,
  :corridor_tenant_id, 'corridor-seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  alias = EXCLUDED.alias,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
  entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
  enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
  'zone', '管廊分区',
  'DEDICATED', 'ent_zone',
  NULL, TRUE,
  '管廊租户 ent_zone；舱室/防火区/口部构筑物', 1,
  '{"zone_type": {"type": "VARCHAR", "column": "zone_type", "length": 100}, "description": {"type": "VARCHAR", "column": "description", "length": 500}, "FLD-BASE-zone-REF_FACILITY": {"type": "BIGINT", "column": "fld_base_zone_ref_facility"}, "boundary_crs": {"type": "VARCHAR", "column": "boundary_crs", "length": 32}, "centroid_lat": {"type": "DECIMAL", "scale": 8, "column": "centroid_lat", "precision": 12}, "centroid_lng": {"type": "DECIMAL", "scale": 8, "column": "centroid_lng", "precision": 12}, "max_height_m": {"type": "DECIMAL", "scale": 3, "column": "max_height_m", "precision": 10}, "min_height_m": {"type": "DECIMAL", "scale": 3, "column": "min_height_m", "precision": 10}, "boundary_status": {"type": "VARCHAR", "column": "boundary_status", "length": 32}, "boundary_geojson": {"type": "JSONB", "column": "boundary_geojson"}}',
  :corridor_tenant_id, 'corridor-seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- --------------------------------------------------------------------------
-- 2. zone 模型（MODEL-ZONE-UT-*；显示名与原误挂 region 模型一致）
-- --------------------------------------------------------------------------

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES
  ('MODEL-ZONE-UT-INTEGRATED-PIPE-CABIN', '综合管道舱', 'zone', '管廊 · 综合管道舱', 1, 10, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-PERSONNEL-ACCESS', '人员出入口', 'zone', '管廊 · 人员出入口', 1, 20, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-PIPE-CABIN', '管道舱', 'zone', '管廊 · 管道舱', 1, 30, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-DRAIN-PIT', '排水坑', 'zone', '管廊 · 排水坑', 1, 40, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-MECH-EXHAUST', '机械排风口', 'zone', '管廊 · 机械排风口', 1, 50, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-POWER-DEDICATED-CABIN', '电力专用舱', 'zone', '管廊 · 电力专用舱', 1, 60, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-GAS-CABIN', '燃气舱', 'zone', '管廊 · 燃气舱', 1, 70, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-WATER-SUPPLY-CABIN', '给水舱', 'zone', '管廊 · 给水舱', 1, 80, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', '防火区', 'zone', '管廊 · 防火区', 1, 90, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-NATURAL-AIR-INLET', '自然进风口', 'zone', '管廊 · 自然进风口', 1, 100, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-INTERSECTION', '交叉口', 'zone', '管廊 · 交叉口', 1, 110, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-SEWAGE-CABIN', '污水舱', 'zone', '管廊 · 污水舱', 1, 120, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-MECH-AIR-INLET', '机械通风口', 'zone', '管廊 · 机械通风口', 1, 130, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-END-SHAFT', '端井', 'zone', '管廊 · 端井', 1, 140, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-TUNNEL-SEGMENT', '管廊段', 'zone', '管廊 · 管廊段', 1, 150, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-INVERTED-SIPHON', '倒虹', 'zone', '管廊 · 倒虹', 1, 160, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-SUBSTATION', '分变电所', 'zone', '管廊 · 分变电所', 1, 170, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-HOIST-PORT', '吊装口', 'zone', '管廊 · 吊装口', 1, 180, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-LEAD-OUT-DOOR', '引出门', 'zone', '管廊 · 引出门', 1, 190, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-POWER-INFO-CABIN', '电力信息仓', 'zone', '管廊 · 电力信息仓', 1, 200, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-TELECOM-CABIN', '通信舱', 'zone', '管廊 · 通信舱', 1, 210, :corridor_tenant_id, 'corridor-seed'),
  ('MODEL-ZONE-UT-SUMP-PIT', '集水坑', 'zone', '管廊 · 集水坑', 1, 220, :corridor_tenant_id, 'corridor-seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- --------------------------------------------------------------------------
-- 3. 字段库：从 tenant 1 误挂 region 模型涉及的 field_code 复制到管廊租户
-- --------------------------------------------------------------------------

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

-- --------------------------------------------------------------------------
-- 4. 模型字段分配：按显示名从 tenant 1 退役 region 模型复制到管廊 zone 模型
-- --------------------------------------------------------------------------

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
