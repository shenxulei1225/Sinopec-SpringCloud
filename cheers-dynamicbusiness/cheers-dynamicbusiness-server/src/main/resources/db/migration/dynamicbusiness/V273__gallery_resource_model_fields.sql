-- 应急资源型号的字段按器材种类收口。
-- 每一种型号都有放置位置、单位、是否可用。
-- 车牌、载重、证照只在车辆上。药剂类型只在灭火器上。量程只在气体检测仪上。
-- 资源分类、当前位置、安全库存不再出现在型号表单里。分类用预案，位置用放置位置。

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET type_config = '{"createVisible": false, "editVisible": false, "detailVisible": false}'::jsonb,
    updater = 'V273',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND entity_type_code = 'emergency_resource'
  AND deleted = false
  AND field_code IN (
      'agent_type', 'measure_range', 'plate_no', 'vehicle_load', 'license_valid_until',
      'resource_category', 'current_location', 'safety_stock'
  );

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, sort, field_source, creator, tenant_id, model_code, field_code,
    detail_visible, create_visible, edit_visible
)
SELECT m.id, f.id, false, 20, 'CUSTOM', 'V273', 123, m.code, 'unit',
       true, true, true
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_field f
  ON f.tenant_id = 123 AND f.code = 'unit' AND f.deleted = false
WHERE m.tenant_id = 123
  AND m.deleted = false
  AND m.entity_type_code = 'emergency_resource'
  AND m.creator = 'V271'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.tenant_id = 123 AND a.deleted = false
        AND a.model_id = m.id AND a.field_code = 'unit'
  );

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, sort, field_source, creator, tenant_id, model_code, field_code,
    detail_visible, create_visible, edit_visible
)
SELECT m.id, b.library_field_id, false, 30, 'BASE', 'V273', 123, m.code, b.field_code,
       true, true, true
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_entity_type_base_field b
  ON b.tenant_id = 123 AND b.entity_type_code = 'emergency_resource' AND b.deleted = false
 AND b.field_code IN ('plate_no', 'vehicle_load', 'license_valid_until')
WHERE m.tenant_id = 123 AND m.deleted = false AND m.code IN ('er-type-07', 'er-type-22')
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.tenant_id = 123 AND a.deleted = false
        AND a.model_id = m.id AND a.field_code = b.field_code
  );

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, sort, field_source, creator, tenant_id, model_code, field_code,
    detail_visible, create_visible, edit_visible
)
SELECT m.id, b.library_field_id, false, 30, 'BASE', 'V273', 123, m.code, 'agent_type',
       true, true, true
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_entity_type_base_field b
  ON b.tenant_id = 123 AND b.entity_type_code = 'emergency_resource' AND b.deleted = false
 AND b.field_code = 'agent_type'
WHERE m.tenant_id = 123 AND m.deleted = false AND m.code IN ('er-type-05', 'er-type-33')
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.tenant_id = 123 AND a.deleted = false
        AND a.model_id = m.id AND a.field_code = 'agent_type'
  );

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, sort, field_source, creator, tenant_id, model_code, field_code,
    detail_visible, create_visible, edit_visible
)
SELECT m.id, b.library_field_id, false, 30, 'BASE', 'V273', 123, m.code, 'measure_range',
       true, true, true
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_entity_type_base_field b
  ON b.tenant_id = 123 AND b.entity_type_code = 'emergency_resource' AND b.deleted = false
 AND b.field_code = 'measure_range'
WHERE m.tenant_id = 123 AND m.deleted = false AND m.code = 'er-type-16'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.tenant_id = 123 AND a.deleted = false
        AND a.model_id = m.id AND a.field_code = 'measure_range'
  );
