-- 压力表、温湿度传感器的采集点。
-- 用字段库里已有的编号。画面上的名称和单位来自这些字段。
-- 创建、编辑、详情不画出这一格，避免把读数写成设备台账上的一个值。

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, creator, tenant_id, model_code, field_code,
    create_visible, edit_visible, detail_visible, collection_point
)
SELECT
    1739, f.id, false, false, false, false,
    1000, 'USER_ADDED', 'seed', 1, m.code, f.code,
    false, false, false, true
FROM dynamicbusiness.dynamic_field f
JOIN dynamicbusiness.dynamic_model m ON m.id = 1739 AND m.tenant_id = 1 AND m.deleted = false
WHERE f.tenant_id = 1 AND f.deleted = false AND f.code = 'FLD-MON-002'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.model_id = 1739 AND a.field_id = f.id AND a.tenant_id = 1 AND a.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, creator, tenant_id, model_code, field_code,
    create_visible, edit_visible, detail_visible, collection_point
)
SELECT
    41, f.id, false, false, false, false,
    CASE f.code WHEN 'FLD-MON-001' THEN 1000 ELSE 1010 END,
    'USER_ADDED', 'seed', 1, m.code, f.code,
    false, false, false, true
FROM dynamicbusiness.dynamic_field f
JOIN dynamicbusiness.dynamic_model m ON m.id = 41 AND m.tenant_id = 1 AND m.deleted = false
WHERE f.tenant_id = 1 AND f.deleted = false AND f.code IN ('FLD-MON-001', 'FLD-MON-004')
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.model_id = 41 AND a.field_id = f.id AND a.tenant_id = 1 AND a.deleted = false
  );
