-- ============================================================================
-- 智慧站场 · 07 示例实例（可选）
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 约定：不写 surrogate id；幂等键为 code / field_code / page_code。
-- 依赖：smart-station/06_categories.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

-- ============================================================================
-- 智慧站场 · 05 示例实体（东营油库，生产可删）
-- Generated: 2026-07-05 by scripts/export-platform-import.py
--
-- 依赖：04_categories.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

-- §4 示例实体（可选，便于跨环境对照；生产可删）
-- ============================================================================

INSERT INTO biz_region (
    id, tenant_id, business_type_code, model_id, name, code, status,
    parent_id, attrs, custom_fields, creator, create_time, updater, update_time, deleted,
    region_code, region_name, region_type, tree_path, sort,
    boundary_crs, boundary_status, boundary_geojson,
    centroid_lng, centroid_lat, bbox
)
SELECT v.id, 1, 'region', v.model_id, v.name, v.code, 1,
       v.parent_id, '{}'::jsonb, '{}', 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false,
       v.region_code, v.region_name, v.region_type, v.tree_path, v.sort,
       v.boundary_crs, v.boundary_status, v.boundary_geojson::jsonb,
       v.centroid_lng, v.centroid_lat, v.bbox::jsonb
FROM (VALUES
    (900100, 9001, '东营油库', 'REGION-SITE-DY', 0,
     'REGION-SITE-DY', '东营油库', 'site', '/900100/', 1,
     'EPSG:4326', 'draft',
     '{"type":"Polygon","coordinates":[[[118.5800,37.4400],[118.6200,37.4400],[118.6200,37.4700],[118.5800,37.4700],[118.5800,37.4400]]]}',
     118.6000::numeric, 37.4550::numeric, '[118.5800,37.4400,118.6200,37.4700]'),
    (900101, 9002, '1#罐组', 'REGION-TG-01', 900100,
     'REGION-TG-01', '1#罐组', 'tank_group', '/900100/900101/', 1,
     'EPSG:4326', 'none', NULL, NULL::numeric, NULL::numeric, NULL),
    (900102, 9002, '2#罐组', 'REGION-TG-02', 900100,
     'REGION-TG-02', '2#罐组', 'tank_group', '/900100/900102/', 2,
     'EPSG:4326', 'none', NULL, NULL::numeric, NULL::numeric, NULL)
) AS v(id, model_id, name, code, parent_id, region_code, region_name, region_type, tree_path, sort,
       boundary_crs, boundary_status, boundary_geojson, centroid_lng, centroid_lat, bbox)
WHERE NOT EXISTS (
    SELECT 1 FROM biz_region r
    WHERE r.region_code = v.region_code AND r.tenant_id = 1 AND r.deleted = false
);

INSERT INTO dynamic_category (
    id, parent_id, name, code, category_type_code, tree_path, level, sort, status, description,
    creator, create_time, updater, update_time, deleted, tenant_id
)
VALUES
    (900100, 900011, '东营油库', 'REGION-SITE-DY', 'region', '/900010/900011/900100/', 3, 1, 1,
     '示例库区实体', 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 1),
    (900101, 900100, '1#罐组', 'REGION-TG-01', 'region', '/900010/900011/900100/900101/', 4, 1, 1,
     '示例罐组', 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 1),
    (900102, 900100, '2#罐组', 'REGION-TG-02', 'region', '/900010/900011/900100/900102/', 4, 2, 1,
     '示例罐组', 'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 1)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
    parent_id = EXCLUDED.parent_id,
    tree_path = EXCLUDED.tree_path,
    level = EXCLUDED.level,
    name = EXCLUDED.name,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_category_entity_link (
    id, category_id, entity_id, entity_model_id,
    creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT v.id, v.category_id, v.entity_id, v.entity_model_id,
       'seed', CURRENT_TIMESTAMP, 'seed', CURRENT_TIMESTAMP, false, 1
FROM (VALUES
    (900401, 900100, 900100, 9001),
    (900402, 900101, 900101, 9002),
    (900403, 900102, 900102, 9002)
) AS v(id, category_id, entity_id, entity_model_id)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_category_entity_link l
    WHERE l.category_id = v.category_id AND l.entity_id = v.entity_id
      AND l.tenant_id = 1 AND l.deleted = false
);

SELECT setval('dynamicbusiness.dynamic_business_type_base_field_id_seq',
    GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamic_business_type_base_field), 900052), true);
SELECT setval('dynamicbusiness.biz_region_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM biz_region), 900102), true);
SELECT setval('dynamicbusiness.dynamic_model_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamic_model), 9002), true);
SELECT setval('dynamicbusiness.dynamic_category_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamic_category), 900102), true);