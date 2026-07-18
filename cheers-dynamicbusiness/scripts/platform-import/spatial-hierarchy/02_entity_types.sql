-- ============================================================================
-- 空间层级 · 02 实体类型（facility、zone；region 语义更新）
-- Generated: 2026-07-07
-- 依赖：system/01_schema.sql、Flyway V14（ent_facility / ent_zone 表）
-- ============================================================================

SET search_path TO dynamicbusiness;

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
    storage_type = EXCLUDED.storage_type,
    dedicated_table_name = EXCLUDED.dedicated_table_name,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_entity_type
SET name = '运营区域',
    description = '国家管网运营/管理区域；树由分类（category_type=region）维护；Pattern C 分类即实体',
    alias = '区域',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'region' AND tenant_id = 1 AND deleted = false;

INSERT INTO dynamic_entity_type_config (
    entity_type_code, name, storage_type, dedicated_table_name, strategy_bean_name,
    enable_rule_engine, description, status, physical_column_mapping, tenant_id, creator
) VALUES (
    'facility', '设施管理', 'DEDICATED', 'ent_facility', NULL, TRUE,
    '设施点 ent_facility', 1,
    '{"region_id": {"type": "BIGINT", "column": "region_id"}, "address": {"type": "VARCHAR", "column": "address", "length": 500}, "longitude": {"type": "DECIMAL", "scale": 8, "column": "longitude", "precision": 12}, "latitude": {"type": "DECIMAL", "scale": 8, "column": "latitude", "precision": 12}, "facility_type": {"type": "VARCHAR", "column": "facility_type", "length": 100}}',
    1, 'seed'
)
ON CONFLICT (entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    dedicated_table_name = EXCLUDED.dedicated_table_name,
    physical_column_mapping = EXCLUDED.physical_column_mapping,
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
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- 实体类型关联
INSERT INTO dynamic_entity_type_relation (
    source_entity_type_code, target_entity_type_code, relation_name,
    auto_create_field, default_field_name, tenant_id, creator
) VALUES
    ('facility', 'region', '所属区域', TRUE, '所属区域', 1, 'seed'),
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
