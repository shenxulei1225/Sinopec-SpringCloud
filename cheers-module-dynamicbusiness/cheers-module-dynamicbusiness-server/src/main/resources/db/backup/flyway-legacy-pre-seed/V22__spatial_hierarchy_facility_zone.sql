-- ============================================================================
-- 地理区域 / 设施 / 站内分区（2026-07-07 定稿）
-- region = 行政区划；facility = 站场/厂区；zone = 站内分区
-- 依赖：V1 schema、V21 及既有 dynamic_entity_type
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 1. 专用表：ent_facility、ent_zone
-- ---------------------------------------------------------------------------

CREATE SEQUENCE IF NOT EXISTS ent_facility_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE SEQUENCE IF NOT EXISTS ent_zone_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS ent_facility (
    id              bigint DEFAULT nextval('ent_facility_id_seq'::regclass) NOT NULL,
    tenant_id       bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64) DEFAULT 'facility'::character varying,
    model_id        bigint NOT NULL,
    name            character varying(255) NOT NULL,
    code            character varying(100) NOT NULL,
    status          integer DEFAULT 1,
    parent_id       bigint DEFAULT 0,
    attrs           jsonb DEFAULT '{}'::jsonb,
    custom_fields   text,
    creator         character varying(64),
    create_time     timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater         character varying(64),
    update_time     timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted         boolean DEFAULT false,
    tree_path       character varying(500),
    sort            integer DEFAULT 0,
    region_id       bigint,
    address         character varying(500),
    longitude       numeric(12, 8),
    latitude        numeric(12, 8),
    facility_type   character varying(100),
    CONSTRAINT ent_facility_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_ent_facility_tenant ON ent_facility (tenant_id) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_facility_model ON ent_facility (model_id) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_facility_region ON ent_facility (region_id) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_facility_code ON ent_facility (code, tenant_id) WHERE deleted = false;
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_facility_code_tenant
    ON ent_facility (code, tenant_id) WHERE deleted = false AND code IS NOT NULL;

COMMENT ON TABLE ent_facility IS '设施点（站场/厂区等）；entityTypeCode=facility';

CREATE TABLE IF NOT EXISTS ent_zone (
    id              bigint DEFAULT nextval('ent_zone_id_seq'::regclass) NOT NULL,
    tenant_id       bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64) DEFAULT 'zone'::character varying,
    model_id        bigint NOT NULL,
    name            character varying(255) NOT NULL,
    code            character varying(100) NOT NULL,
    status          integer DEFAULT 1,
    parent_id       bigint DEFAULT 0,
    attrs           jsonb DEFAULT '{}'::jsonb,
    custom_fields   text,
    creator         character varying(64),
    create_time     timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater         character varying(64),
    update_time     timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted         boolean DEFAULT false,
    tree_path       character varying(500),
    sort            integer DEFAULT 0,
    facility_id     bigint NOT NULL,
    zone_type       character varying(100),
    description     character varying(500),
    boundary_geojson jsonb,
    boundary_crs    character varying(32) DEFAULT 'EPSG:4326',
    boundary_status character varying(32) DEFAULT 'none',
    min_height_m    numeric(10, 3),
    max_height_m    numeric(10, 3),
    centroid_lng    numeric(12, 8),
    centroid_lat    numeric(12, 8),
    CONSTRAINT ent_zone_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_ent_zone_tenant ON ent_zone (tenant_id) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_zone_facility ON ent_zone (facility_id) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_zone_parent ON ent_zone (parent_id) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_zone_model ON ent_zone (model_id) WHERE deleted = false;
CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_zone_code_tenant
    ON ent_zone (code, tenant_id) WHERE deleted = false AND code IS NOT NULL;

COMMENT ON TABLE ent_zone IS '站内空间分区（罐组等）；entityTypeCode=zone';

-- equipment：设施/分区物理列（REF 持久化）
ALTER TABLE ent_equipment ADD COLUMN IF NOT EXISTS facility_id bigint;
ALTER TABLE ent_equipment ADD COLUMN IF NOT EXISTS zone_id bigint;
CREATE INDEX IF NOT EXISTS idx_ent_equipment_facility ON ent_equipment (facility_id) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_equipment_zone ON ent_equipment (zone_id) WHERE deleted = false;

-- region：行政区划扩展列
ALTER TABLE ent_region ADD COLUMN IF NOT EXISTS region_level character varying(32);
ALTER TABLE ent_region ADD COLUMN IF NOT EXISTS admin_code character varying(64);

-- ---------------------------------------------------------------------------
-- 2. 实体类型：facility、zone；region 语义更新
-- ---------------------------------------------------------------------------

INSERT INTO dynamic_entity_type (
    code, name, parent_id, description, icon, alias, sort, status, type_level,
    association_fields, storage_type, dedicated_table_name, enable_rule_engine,
    physical_column_mapping, tenant_id, creator
) VALUES (
    'facility', '设施管理', NULL,
    '行业标准设施点：站场、厂区等；多 Model 区分类型；REF_REGION 挂行政区划',
    'businessIcon:设施管理.png', '设施', 2, 'active', 'USER', '{}',
    'DEDICATED', 'ent_facility', FALSE, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    icon = EXCLUDED.icon,
    alias = EXCLUDED.alias,
    sort = EXCLUDED.sort,
    storage_type = EXCLUDED.storage_type,
    dedicated_table_name = EXCLUDED.dedicated_table_name,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type (
    code, name, parent_id, description, icon, alias, sort, status, type_level,
    association_fields, storage_type, dedicated_table_name, enable_rule_engine,
    physical_column_mapping, tenant_id, creator
) VALUES (
    'zone', '空间分区', NULL,
    '设施内部空间单元：罐组、库棚、功能分区等；REF_FACILITY 必填',
    'businessIcon:区域管理.png', '分区', 2, 'active', 'USER', '{}',
    'DEDICATED', 'ent_zone', FALSE, NULL, 1, 'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    icon = EXCLUDED.icon,
    alias = EXCLUDED.alias,
    sort = EXCLUDED.sort,
    storage_type = EXCLUDED.storage_type,
    dedicated_table_name = EXCLUDED.dedicated_table_name,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type
SET name = '地理区域',
    description = '行政区划边界：全国/省/地市/区；parent_id 树',
    alias = '区划',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'region' AND tenant_id = 1 AND deleted = false;

-- dynamic_entity_type_config
INSERT INTO dynamic_entity_type_config (
    entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
    enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
    'facility', '设施管理', 'DEDICATED', 'ent_facility', NULL, TRUE,
    '设施点 ent_facility；站场/厂区多 Model', 1,
    '{"region_id": {"type": "BIGINT", "column": "region_id"}, "address": {"type": "VARCHAR", "column": "address", "length": 500}, "longitude": {"type": "DECIMAL", "scale": 8, "column": "longitude", "precision": 12}, "latitude": {"type": "DECIMAL", "scale": 8, "column": "latitude", "precision": 12}, "facility_type": {"type": "VARCHAR", "column": "facility_type", "length": 100}}',
    1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    dedicated_table_name = EXCLUDED.dedicated_table_name,
    physical_column_mapping = EXCLUDED.physical_column_mapping,
    description = EXCLUDED.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_config (
    entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
    enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
    'zone', '空间分区', 'DEDICATED', 'ent_zone', NULL, TRUE,
    '站内分区 ent_zone', 1,
    '{"facility_id": {"type": "BIGINT", "column": "facility_id"}, "zone_type": {"type": "VARCHAR", "column": "zone_type", "length": 100}, "description": {"type": "VARCHAR", "column": "description", "length": 500}, "boundary_geojson": {"type": "JSONB", "column": "boundary_geojson"}, "boundary_crs": {"type": "VARCHAR", "column": "boundary_crs", "length": 32}, "boundary_status": {"type": "VARCHAR", "column": "boundary_status", "length": 32}, "min_height_m": {"type": "DECIMAL", "scale": 3, "column": "min_height_m", "precision": 10}, "max_height_m": {"type": "DECIMAL", "scale": 3, "column": "max_height_m", "precision": 10}, "centroid_lng": {"type": "DECIMAL", "scale": 8, "column": "centroid_lng", "precision": 12}, "centroid_lat": {"type": "DECIMAL", "scale": 8, "column": "centroid_lat", "precision": 12}}',
    1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    dedicated_table_name = EXCLUDED.dedicated_table_name,
    physical_column_mapping = EXCLUDED.physical_column_mapping,
    description = EXCLUDED.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type_config
SET name = '地理区域',
    description = '行政区划 ent_region；全国/省/市/区',
    physical_column_mapping = COALESCE(physical_column_mapping, '{}'::jsonb)
        || '{"region_level": {"type": "VARCHAR", "column": "region_level", "length": 32}, "admin_code": {"type": "VARCHAR", "column": "admin_code", "length": 64}}'::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'region' AND tenant_id = 1 AND deleted = false;

-- ---------------------------------------------------------------------------
-- 3. 字段库：REF 用 ENTITY_REF
-- ---------------------------------------------------------------------------

INSERT INTO dynamic_field (
    code, name, type, unit, description, source, status, max_relations,
    index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
    ('F-spatial-facility-ref-region', '所属区划', 'ENTITY_REF', NULL,
     '设施所属行政区划（region）', 'USER', 1, NULL, 'GIN', NULL, NULL, NULL, 1, 'seed'),
    ('F-spatial-zone-ref-facility', '所属设施', 'ENTITY_REF', NULL,
     '站内分区所属设施（facility）', 'USER', 1, NULL, 'GIN', NULL, NULL, NULL, 1, 'seed'),
    ('F-spatial-equipment-ref-facility', '所属设施', 'ENTITY_REF', NULL,
     '设备所属设施（facility）', 'USER', 1, NULL, 'GIN', NULL, NULL, NULL, 1, 'seed'),
    ('F-spatial-equipment-ref-zone', '所属分区', 'ENTITY_REF', NULL,
     '设备所属站内分区（zone）', 'USER', 1, NULL, 'GIN', NULL, NULL, NULL, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
    name = EXCLUDED.name,
    type = EXCLUDED.type,
    description = EXCLUDED.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 4. 基础字段：region 修订、facility、zone；equipment REF 增补
-- ---------------------------------------------------------------------------

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, tenant_id, creator
) VALUES
    ('region', 'region_level', '区划级别', 'ENUM', TRUE, NULL,
     'country/province/city/district；与 model 对齐', NULL, 15, 1, 1, 'seed'),
    ('region', 'admin_code', '区划代码', 'TEXT', FALSE, NULL,
     '国标行政区划码', NULL, 16, 1, 1, 'seed')
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    field_name = EXCLUDED.field_name,
    data_type = EXCLUDED.data_type,
    required = EXCLUDED.required,
    description = EXCLUDED.description,
    sort_order = EXCLUDED.sort_order,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, tenant_id, creator
) VALUES
    ('facility', 'REF_REGION', '所属区划', 'REF', TRUE, NULL,
     '设施挂在哪一级行政区划下', '{"refField": "F-spatial-facility-ref-region"}', 10, 1, 1, 'seed'),
    ('facility', 'address', '地址', 'TEXT', FALSE, NULL, NULL, NULL, 20, 1, 1, 'seed'),
    ('facility', 'longitude', '经度', 'NUMBER', FALSE, NULL, NULL, NULL, 21, 1, 1, 'seed'),
    ('facility', 'latitude', '纬度', 'NUMBER', FALSE, NULL, NULL, NULL, 22, 1, 1, 'seed'),
    ('facility', 'facility_type', '设施类型', 'ENUM', TRUE, NULL,
     '与 model 或分类对齐', NULL, 25, 1, 1, 'seed'),
    ('facility', 'remark', '备注', 'TEXT', FALSE, NULL, NULL, NULL, 91, 1, 1, 'seed')
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    field_name = EXCLUDED.field_name,
    data_type = EXCLUDED.data_type,
    required = EXCLUDED.required,
    type_config = EXCLUDED.type_config,
    sort_order = EXCLUDED.sort_order,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, tenant_id, creator
) VALUES
    ('zone', 'REF_FACILITY', '所属设施', 'REF', TRUE, NULL,
     '分区归属设施点', '{"refField": "F-spatial-zone-ref-facility"}', 10, 1, 1, 'seed'),
    ('zone', 'zone_type', '分区类型', 'ENUM', TRUE, NULL,
     '与 model 对齐', NULL, 20, 1, 1, 'seed'),
    ('zone', 'description', '分区说明', 'TEXT', FALSE, NULL, NULL, NULL, 21, 1, 1, 'seed'),
    ('zone', 'boundary_geojson', '边界几何', 'JSON', FALSE, NULL, NULL, NULL, 40, 1, 1, 'seed'),
    ('zone', 'boundary_crs', '坐标系', 'TEXT', FALSE, NULL, NULL, NULL, 41, 1, 1, 'seed'),
    ('zone', 'boundary_status', '边界状态', 'ENUM', FALSE, NULL, NULL, NULL, 42, 1, 1, 'seed'),
    ('zone', 'min_height_m', '最小高度(m)', 'NUMBER', FALSE, NULL, NULL, NULL, 43, 1, 1, 'seed'),
    ('zone', 'max_height_m', '最大高度(m)', 'NUMBER', FALSE, NULL, NULL, NULL, 44, 1, 1, 'seed'),
    ('zone', 'centroid_lng', '质心经度', 'NUMBER', FALSE, NULL, NULL, NULL, 45, 1, 1, 'seed'),
    ('zone', 'centroid_lat', '质心纬度', 'NUMBER', FALSE, NULL, NULL, NULL, 46, 1, 1, 'seed'),
    ('zone', 'remark', '备注', 'TEXT', FALSE, NULL, NULL, NULL, 91, 1, 1, 'seed')
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    field_name = EXCLUDED.field_name,
    data_type = EXCLUDED.data_type,
    required = EXCLUDED.required,
    type_config = EXCLUDED.type_config,
    sort_order = EXCLUDED.sort_order,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, tenant_id, creator
) VALUES
    ('equipment', 'REF_FACILITY', '所属设施', 'REF', TRUE, NULL,
     '设备归属设施点；列表批量补全名称', '{"refField": "F-spatial-equipment-ref-facility"}', 42, 1, 1, 'seed'),
    ('equipment', 'REF_ZONE', '所属分区', 'REF', FALSE, NULL,
     '可选站内精细定位', '{"refField": "F-spatial-equipment-ref-zone"}', 43, 1, 1, 'seed')
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    field_name = EXCLUDED.field_name,
    data_type = EXCLUDED.data_type,
    required = EXCLUDED.required,
    type_config = EXCLUDED.type_config,
    description = EXCLUDED.description,
    sort_order = EXCLUDED.sort_order,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 5. 实体类型关联
-- ---------------------------------------------------------------------------

INSERT INTO dynamic_entity_type_relation (
    source_entity_type_code, target_entity_type_code, relation_name,
    auto_create_field, default_field_name, tenant_id, creator
) VALUES
    ('facility', 'region', '所属区划', TRUE, '所属区划', 1, 'seed'),
    ('zone', 'facility', '所属设施', TRUE, '所属设施', 1, 'seed'),
    ('equipment', 'facility', '所属设施', TRUE, '所属设施', 1, 'seed'),
    ('equipment', 'zone', '所属分区', TRUE, '所属分区', 1, 'seed'),
    ('facility', 'zone', '设施-分区', TRUE, '站内分区', 1, 'seed'),
    ('zone', 'equipment', '分区-设备', TRUE, '所属设备管理', 1, 'seed')
ON CONFLICT (source_entity_type_code, target_entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    relation_name = EXCLUDED.relation_name,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- ---------------------------------------------------------------------------
-- 6. 模型
-- ---------------------------------------------------------------------------

INSERT INTO dynamic_model (
    code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES
    ('MODEL-REGION-COUNTRY', '全国', 'region', '行政区划：全国根节点', 1, 1, NULL, 1, 'seed'),
    ('MODEL-REGION-PROVINCE', '省', 'region', '行政区划：省级', 1, 2, NULL, 1, 'seed'),
    ('MODEL-REGION-CITY', '地市', 'region', '行政区划：地级市', 1, 3, NULL, 1, 'seed'),
    ('MODEL-REGION-DISTRICT', '区', 'region', '行政区划：区县级', 1, 4, NULL, 1, 'seed'),
    ('MODEL-FACILITY-STATION', '站场', 'facility', '油气储运站场等设施点', 1, 1, NULL, 1, 'seed'),
    ('MODEL-FACILITY-PLANT', '厂区', 'facility', '工厂、化工园区厂区', 1, 2, NULL, 1, 'seed'),
    ('MODEL-FACILITY-DEPOT', '油库', 'facility', '油库设施点', 1, 3, NULL, 1, 'seed'),
    ('MODEL-FACILITY-OFFICE', '机关/楼宇', 'facility', '机关办公与楼宇设施点', 1, 4, NULL, 1, 'seed'),
    ('MODEL-ZONE-TANK-GROUP', '罐组', 'zone', '储罐分区', 1, 1, NULL, 1, 'seed'),
    ('MODEL-ZONE-WAREHOUSE', '库棚', 'zone', '仓储分区', 1, 2, NULL, 1, 'seed'),
    ('MODEL-ZONE-FUNCTIONAL', '功能分区', 'zone', '装卸区、消防分区等', 1, 3, NULL, 1, 'seed'),
    ('MODEL-ZONE-BUILDING', '楼栋', 'zone', '厂区内楼栋', 1, 4, NULL, 1, 'seed')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
    name = EXCLUDED.name,
    entity_type_code = EXCLUDED.entity_type_code,
    description = EXCLUDED.description,
    status = EXCLUDED.status,
    sort = EXCLUDED.sort,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- 废弃智慧站场旧 region 模型语义（保留行避免破坏外键，置为停用）
UPDATE dynamic_model
SET status = 0,
    description = COALESCE(description, '') || ' [已废弃 2026-07-07：改用 MODEL-FACILITY-* / MODEL-ZONE-*]',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code IN ('MODEL-REGION-SITE', 'MODEL-REGION-TANK-GROUP')
  AND tenant_id = 1
  AND deleted = false;

-- equipment 物理列映射增补
UPDATE dynamic_entity_type_config
SET physical_column_mapping = COALESCE(physical_column_mapping, '{}'::jsonb)
    || '{"facility_id": {"type": "BIGINT", "column": "facility_id"}, "zone_id": {"type": "BIGINT", "column": "zone_id"}}'::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment' AND tenant_id = 1 AND deleted = false;
