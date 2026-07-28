-- ============================================================================
-- 管廊 tenant 2 · 分区 / 构筑物模型字段（依据参考资料/区域分类字段说明、数据字段说明）
-- psql -v corridor_tenant_id=2 -f dynamic_corridor_model_fields.sql
-- 须在 dynamic_corridor_spatial_entity_types.sql、dynamic_corridor_structure.sql 之后执行
-- ============================================================================

SET search_path TO dynamicbusiness;

\if :{?corridor_tenant_id}
\else
\set corridor_tenant_id 2
\endif

-- ---------- 移除误从 tenant1 region 复制的管网组织字段 ----------

UPDATE dynamic_model_field_assignment
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = :corridor_tenant_id
  AND deleted = false
  AND field_code LIKE 'FLD-BASE-region-%';

-- ---------- 构筑物类型 · 基础 REF 分区（展示「所属分区」） ----------

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required,
  default_value, description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'structure',
  NULL,
  'FLD-BASE-structure-REF_ZONE',
  '所属分区',
  'ENTITY_REF',
  false,
  NULL,
  '构筑物所在站内分区（防火区或舱室）',
  '{"refField": "F-spatial-structure-ref-zone"}'::jsonb,
  2,
  1,
  :corridor_tenant_id,
  'corridor-seed'
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_entity_type_base_field b
  WHERE b.tenant_id = :corridor_tenant_id
    AND b.entity_type_code = 'structure'
    AND b.field_code = 'FLD-BASE-structure-REF_ZONE'
    AND b.deleted = false
);

-- ---------- 管廊专用字段库（tenant 2） ----------

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, tenant_id, creator
) VALUES
  ('FLD-UT-zone-ue_entity_code', 'UE实体编码', 'TEXT', NULL,
   'Region.* 或 UE GUID，与分类 code / 三维实体对照', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-zone-segment_road_name', '所在路段', 'TEXT', NULL,
   '管廊段所在路段名称（如光谷五路北）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-fire_compartment_no', '防火区编号', 'TEXT', NULL,
   '防火区业务编号（如 16、SS01）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-fire_compartment_code', '防火区编码', 'TEXT', NULL,
   '系统编码（如 Region.GG5LB.16）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-route_length_m', '路长', 'DECIMAL', 'm',
   '防火区路长（米）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-pipeline_total', '管线总数', 'INTEGER', NULL,
   '防火区内管线总数', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-structure_total', '构筑物总数', 'INTEGER', NULL,
   '防火区内构筑物总数', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-structure_type_stats', '构筑物类型统计', 'JSON', NULL,
   '各类型构筑物数量（JSON 对象）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-pipeline_count', '管线数量', 'INTEGER', NULL,
   '舱室内管线数量', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-legacy_biz_region_id', '源区域ID', 'INTEGER', NULL,
   'zhgl biz_region 主键，导入追溯', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-structure-length_mm', '长', 'DECIMAL', 'mm',
   '构筑物长度（UE/测绘，毫米）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-structure-width_mm', '宽', 'DECIMAL', 'mm',
   '构筑物宽度（毫米）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-structure-height_mm', '高', 'DECIMAL', 'mm',
   '构筑物高度（毫米）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-structure-floor_clearance_mm', '离地距离', 'DECIMAL', 'mm',
   '构筑物离地距离（毫米）', 'USER', 1, :corridor_tenant_id, 'corridor-seed'),
  ('FLD-UT-structure-fire_compartment_no', '防火区编号', 'INTEGER', NULL,
   'UE 构筑物列表·防火区编号', 'USER', 1, :corridor_tenant_id, 'corridor-seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 模型 ↔ 字段 映射（model_code, field_code, sort） ----------

CREATE TEMP TABLE _corridor_model_field (
  model_code varchar(128) NOT NULL,
  field_code varchar(64) NOT NULL,
  sort_ord int NOT NULL,
  PRIMARY KEY (model_code, field_code)
);

-- 设施：与 tenant1 站场模型一致，挂设施基础字段
INSERT INTO _corridor_model_field (model_code, field_code, sort_ord)
SELECT m.code, v.field_code, v.sort_ord
FROM dynamic_model m
CROSS JOIN (VALUES
  ('FLD-BASE-facility-REF_REGION', 10),
  ('FLD-BASE-facility-address', 20),
  ('FLD-BASE-facility-longitude', 21),
  ('FLD-BASE-facility-latitude', 22),
  ('FLD-BASE-facility-facility_type', 25),
  ('FLD-BASE-facility-remark', 91)
) AS v(field_code, sort_ord)
WHERE m.tenant_id = :corridor_tenant_id
  AND m.entity_type_code = 'facility'
  AND m.deleted = false
  AND m.code IN ('MODEL-FACILITY-UT-TUNNEL', 'MODEL-FACILITY-UT-MONITOR');

-- 全部活跃 zone 模型：分区基础字段（对齐 tenant1 zone 模型）
INSERT INTO _corridor_model_field (model_code, field_code, sort_ord)
SELECT m.code, v.field_code, v.sort_ord
FROM dynamic_model m
CROSS JOIN (VALUES
  ('FLD-BASE-zone-REF_FACILITY', 10),
  ('FLD-BASE-zone-zone_type', 20),
  ('FLD-BASE-zone-description', 21),
  ('FLD-BASE-zone-boundary_geojson', 40),
  ('FLD-BASE-zone-boundary_crs', 41),
  ('FLD-BASE-zone-boundary_status', 42),
  ('FLD-BASE-zone-min_height_m', 43),
  ('FLD-BASE-zone-max_height_m', 44),
  ('FLD-BASE-zone-centroid_lng', 45),
  ('FLD-BASE-zone-centroid_lat', 46),
  ('FLD-BASE-zone-remark', 91)
) AS v(field_code, sort_ord)
WHERE m.tenant_id = :corridor_tenant_id
  AND m.entity_type_code = 'zone'
  AND m.deleted = false;

-- 空间根 · 管廊展示字段
INSERT INTO _corridor_model_field VALUES
  ('MODEL-ZONE-UT-CORRIDOR-ROOT', 'FLD-UT-zone-ue_entity_code', 110);

-- 管廊段
INSERT INTO _corridor_model_field VALUES
  ('MODEL-ZONE-UT-TUNNEL-SEGMENT', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-TUNNEL-SEGMENT', 'FLD-UT-zone-segment_road_name', 120);

-- 防火区
INSERT INTO _corridor_model_field VALUES
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', 'FLD-UT-fire_compartment_no', 120),
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', 'FLD-UT-fire_compartment_code', 130),
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', 'FLD-UT-route_length_m', 140),
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', 'FLD-UT-pipeline_total', 150),
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', 'FLD-UT-structure_total', 160),
  ('MODEL-ZONE-UT-FIRE-COMPARTMENT', 'FLD-UT-structure_type_stats', 170);

-- 舱段 + 各类舱室
INSERT INTO _corridor_model_field (model_code, field_code, sort_ord)
SELECT m.code, v.field_code, v.sort_ord
FROM (VALUES
  ('MODEL-ZONE-UT-CABIN-SEGMENT', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-CABIN-SEGMENT', 'FLD-UT-pipeline_count', 120),
  ('MODEL-ZONE-UT-INTEGRATED-PIPE-CABIN', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-INTEGRATED-PIPE-CABIN', 'FLD-UT-pipeline_count', 120),
  ('MODEL-ZONE-UT-POWER-DEDICATED-CABIN', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-POWER-DEDICATED-CABIN', 'FLD-UT-pipeline_count', 120),
  ('MODEL-ZONE-UT-POWER-INFO-CABIN', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-POWER-INFO-CABIN', 'FLD-UT-pipeline_count', 120),
  ('MODEL-ZONE-UT-PIPE-CABIN', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-PIPE-CABIN', 'FLD-UT-pipeline_count', 120),
  ('MODEL-ZONE-UT-GAS-CABIN', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-GAS-CABIN', 'FLD-UT-pipeline_count', 120),
  ('MODEL-ZONE-UT-SEWAGE-CABIN', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-SEWAGE-CABIN', 'FLD-UT-pipeline_count', 120),
  ('MODEL-ZONE-UT-WATER-SUPPLY-CABIN', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-WATER-SUPPLY-CABIN', 'FLD-UT-pipeline_count', 120),
  ('MODEL-ZONE-UT-TELECOM-CABIN', 'FLD-UT-zone-ue_entity_code', 110),
  ('MODEL-ZONE-UT-TELECOM-CABIN', 'FLD-UT-pipeline_count', 120)
) AS v(model_code, field_code, sort_ord)
JOIN dynamic_model m
  ON m.code = v.model_code
 AND m.tenant_id = :corridor_tenant_id
 AND m.entity_type_code = 'zone'
 AND m.deleted = false;

-- 构筑物：挂分区基础字段（字段库仅有 FLD-BASE-zone-*；REF_ZONE 走类型基础字段）
-- + 管廊展示字段
INSERT INTO _corridor_model_field (model_code, field_code, sort_ord)
SELECT m.code, v.field_code, v.sort_ord
FROM dynamic_model m
CROSS JOIN (VALUES
  ('FLD-BASE-zone-REF_FACILITY', 10),
  ('FLD-BASE-zone-description', 21),
  ('FLD-BASE-zone-boundary_geojson', 40),
  ('FLD-BASE-zone-boundary_crs', 41),
  ('FLD-BASE-zone-boundary_status', 42),
  ('FLD-BASE-zone-min_height_m', 43),
  ('FLD-BASE-zone-max_height_m', 44),
  ('FLD-BASE-zone-centroid_lng', 45),
  ('FLD-BASE-zone-centroid_lat', 46),
  ('FLD-BASE-zone-remark', 91),
  ('FLD-UT-legacy_biz_region_id', 105),
  ('FLD-UT-zone-segment_road_name', 110),
  ('FLD-UT-structure-fire_compartment_no', 115),
  ('FLD-UT-structure-length_mm', 120),
  ('FLD-UT-structure-width_mm', 130),
  ('FLD-UT-structure-height_mm', 140),
  ('FLD-UT-structure-floor_clearance_mm', 150)
) AS v(field_code, sort_ord)
WHERE m.tenant_id = :corridor_tenant_id
  AND m.entity_type_code = 'structure'
  AND m.deleted = false
  AND m.code IN (
    'MODEL-STRUCTURE-UT-INTERSECTION',
    'MODEL-STRUCTURE-UT-PERSONNEL-ACCESS',
    'MODEL-STRUCTURE-UT-INVERTED-SIPHON',
    'MODEL-STRUCTURE-UT-SUBSTATION',
    'MODEL-STRUCTURE-UT-HOIST-PORT',
    'MODEL-STRUCTURE-UT-LEAD-OUT-DOOR',
    'MODEL-STRUCTURE-UT-MECH-EXHAUST',
    'MODEL-STRUCTURE-UT-MECH-AIR-INLET',
    'MODEL-STRUCTURE-UT-END-SHAFT',
    'MODEL-STRUCTURE-UT-NATURAL-AIR-INLET',
    'MODEL-STRUCTURE-UT-SUMP-PIT',
    'MODEL-STRUCTURE-UT-DRAIN-PIT'
  );

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, target_entity_type,
  model_code, field_code, tenant_id, creator
)
SELECT
  m.id,
  f.id,
  false,
  true,
  true,
  false,
  map.sort_ord,
  'LIBRARY',
  NULL,
  m.code,
  f.code,
  :corridor_tenant_id,
  'corridor-seed'
FROM _corridor_model_field map
JOIN dynamic_model m
  ON m.code = map.model_code
 AND m.tenant_id = :corridor_tenant_id
 AND m.deleted = false
JOIN dynamic_field f
  ON f.code = map.field_code
 AND f.tenant_id = :corridor_tenant_id
 AND f.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  sort = EXCLUDED.sort,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  deleted = false,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

DROP TABLE _corridor_model_field;
