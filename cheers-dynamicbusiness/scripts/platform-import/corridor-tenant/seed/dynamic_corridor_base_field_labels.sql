-- ============================================================================
-- 管廊 tenant 2 · 基础字段中文说明（界面应显示 field_name，FLD-* 为库内编码）
-- psql -v corridor_tenant_id=2 -f dynamic_corridor_base_field_labels.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

\if :{?corridor_tenant_id}
\else
\set corridor_tenant_id 2
\endif

UPDATE dynamic_entity_type_base_field
SET
  field_name = CASE field_code
    WHEN 'FLD-BASE-zone-REF_FACILITY' THEN '所属设施'
    WHEN 'FLD-BASE-zone-zone_type' THEN '分区类型'
    WHEN 'FLD-BASE-zone-description' THEN '说明'
    WHEN 'FLD-BASE-zone-boundary_geojson' THEN '边界几何'
    WHEN 'FLD-BASE-zone-boundary_crs' THEN '坐标系'
    WHEN 'FLD-BASE-zone-boundary_status' THEN '边界状态'
    WHEN 'FLD-BASE-zone-min_height_m' THEN '最小高度(m)'
    WHEN 'FLD-BASE-zone-max_height_m' THEN '最大高度(m)'
    WHEN 'FLD-BASE-zone-centroid_lng' THEN '质心经度'
    WHEN 'FLD-BASE-zone-centroid_lat' THEN '质心纬度'
    WHEN 'FLD-BASE-zone-remark' THEN '备注'
    ELSE field_name
  END,
  description = CASE field_code
    WHEN 'FLD-BASE-zone-REF_FACILITY' THEN '关联管廊工程设施（必填）'
    WHEN 'FLD-BASE-zone-zone_type' THEN '与模型一致的分区类型标识'
    WHEN 'FLD-BASE-zone-description' THEN '分区文字说明'
    WHEN 'FLD-BASE-zone-boundary_geojson' THEN 'GeoJSON 边界（可选）'
    WHEN 'FLD-BASE-zone-boundary_crs' THEN '边界坐标系，默认 EPSG:4326'
    WHEN 'FLD-BASE-zone-boundary_status' THEN '是否已录入边界'
    WHEN 'FLD-BASE-zone-min_height_m' THEN '竖向最小高度（米）'
    WHEN 'FLD-BASE-zone-max_height_m' THEN '竖向最大高度（米）'
    WHEN 'FLD-BASE-zone-centroid_lng' THEN '分区质心经度'
    WHEN 'FLD-BASE-zone-centroid_lat' THEN '分区质心纬度'
    WHEN 'FLD-BASE-zone-remark' THEN '备注'
    ELSE description
  END,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = :corridor_tenant_id
  AND entity_type_code = 'zone'
  AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET
  field_name = CASE field_code
    WHEN 'FLD-BASE-structure-REF_FACILITY' THEN '所属设施'
    WHEN 'FLD-BASE-structure-REF_ZONE' THEN '所属分区'
    WHEN 'FLD-BASE-structure-zone_type' THEN '构筑物类型'
    WHEN 'FLD-BASE-structure-description' THEN '说明'
    WHEN 'FLD-BASE-structure-boundary_geojson' THEN '边界几何'
    WHEN 'FLD-BASE-structure-boundary_crs' THEN '坐标系'
    WHEN 'FLD-BASE-structure-boundary_status' THEN '边界状态'
    WHEN 'FLD-BASE-structure-min_height_m' THEN '最小高度(m)'
    WHEN 'FLD-BASE-structure-max_height_m' THEN '最大高度(m)'
    WHEN 'FLD-BASE-structure-centroid_lng' THEN '质心经度'
    WHEN 'FLD-BASE-structure-centroid_lat' THEN '质心纬度'
    WHEN 'FLD-BASE-structure-remark' THEN '备注'
    ELSE field_name
  END,
  description = CASE field_code
    WHEN 'FLD-BASE-structure-REF_FACILITY' THEN '关联管廊工程设施（必填）'
    WHEN 'FLD-BASE-structure-REF_ZONE' THEN '防火区或舱室分区（与所属分区 REF 一致）'
    WHEN 'FLD-BASE-structure-zone_type' THEN '与模型一致的构筑物细类'
    WHEN 'FLD-BASE-structure-description' THEN '构筑物文字说明'
    ELSE COALESCE(description, '平台基础字段；界面以中文名为准，FLD 开头为系统内部编码')
  END,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = :corridor_tenant_id
  AND entity_type_code = 'structure'
  AND deleted = false;

UPDATE dynamic_field
SET description = COALESCE(description, '') || '（库内编码 ' || code || '；界面显示上方中文名）',
    updater = 'corridor-seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = :corridor_tenant_id
  AND deleted = false
  AND code LIKE 'FLD-UT-%'
  AND (description IS NULL OR description NOT LIKE '%库内编码%');
