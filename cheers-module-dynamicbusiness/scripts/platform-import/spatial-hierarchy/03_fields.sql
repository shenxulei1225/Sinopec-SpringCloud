-- ============================================================================
-- 空间层级 · 03 基础字段与字段库
-- Generated: 2026-07-07
-- 依赖：spatial-hierarchy/02_entity_types.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
    code, name, type, unit, description, source, status, max_relations,
    index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES
    ('F-spatial-facility-ref-region', '所属区域', 'ENTITY_REF', NULL,
     '设施所属管网运营区域（region）', 'USER', 1, NULL, 'GIN', NULL, NULL, NULL, 1, 'seed'),
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

-- facility
INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, tenant_id, creator
) VALUES
    ('facility', 'REF_REGION', '所属区域', 'REF', TRUE, NULL,
     '设施所属管网运营区域（ent_region.id）', '{"refField": "F-spatial-facility-ref-region"}', 10, 1, 1, 'seed'),
    ('facility', 'address', '地址', 'TEXT', FALSE, NULL, NULL, NULL, 20, 1, 1, 'seed'),
    ('facility', 'longitude', '经度', 'NUMBER', FALSE, NULL, NULL, NULL, 21, 1, 1, 'seed'),
    ('facility', 'latitude', '纬度', 'NUMBER', FALSE, NULL, NULL, NULL, 22, 1, 1, 'seed'),
    ('facility', 'facility_type', '设施类型', 'ENUM', TRUE, NULL,
     '与 model 对齐', NULL, 25, 1, 1, 'seed'),
    ('facility', 'remark', '备注', 'TEXT', FALSE, NULL, NULL, NULL, 91, 1, 1, 'seed')
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    field_name = EXCLUDED.field_name,
    data_type = EXCLUDED.data_type,
    required = EXCLUDED.required,
    type_config = EXCLUDED.type_config,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- zone
INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, tenant_id, creator
) VALUES
    ('zone', 'REF_FACILITY', '所属设施', 'REF', TRUE, NULL,
     '分区归属设施点', '{"refField": "F-spatial-zone-ref-facility"}', 10, 1, 1, 'seed'),
    ('zone', 'zone_type', '分区类型', 'ENUM', TRUE, NULL, '与 model 对齐', NULL, 20, 1, 1, 'seed'),
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
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- equipment REF 增补
INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, tenant_id, creator
) VALUES
    ('equipment', 'REF_FACILITY', '所属设施', 'REF', TRUE, NULL,
     '设备归属设施点', '{"refField": "F-spatial-equipment-ref-facility"}', 42, 1, 1, 'seed'),
    ('equipment', 'REF_ZONE', '所属分区', 'REF', FALSE, NULL,
     '可选站内精细定位', '{"refField": "F-spatial-equipment-ref-zone"}', 43, 1, 1, 'seed')
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
    field_name = EXCLUDED.field_name,
    data_type = EXCLUDED.data_type,
    required = EXCLUDED.required,
    type_config = EXCLUDED.type_config,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

-- equipment 物理列映射增补
UPDATE dynamic_entity_type_config
SET physical_column_mapping = COALESCE(physical_column_mapping, '{}'::jsonb)
    || '{"facility_id": {"type": "BIGINT", "column": "facility_id"}, "zone_id": {"type": "BIGINT", "column": "zone_id"}}'::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment' AND tenant_id = 1 AND deleted = false;
