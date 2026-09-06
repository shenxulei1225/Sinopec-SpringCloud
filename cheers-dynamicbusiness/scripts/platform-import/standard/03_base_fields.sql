-- ============================================================================
-- standard · 03 基础字段挂载（entity_type_code=standard）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'standard',
  f.id,
  v.field_code,
  v.field_name,
  v.data_type,
  v.required,
  v.default_value,
  v.description,
  NULL,
  v.sort_order,
  1,
  1,
  'seed'
FROM dynamic_field f
JOIN (
  VALUES
    ('standard_no', '标准编号', 'STRING', true, NULL, '标准编号（展示用）', 10),
    ('standard_level', '标准级别', 'ENUM', true, NULL, 'NATIONAL/INDUSTRY/ENTERPRISE', 20),
    ('issuing_body', '发布单位', 'STRING', false, NULL, '标准发布单位', 30),
    ('publish_year', '发布年份', 'INTEGER', false, NULL, '发布年份', 40),
    ('summary', '摘要说明', 'TEXT', false, NULL, '用途简述', 50),
    ('source_ref', '引用出处', 'TEXT', false, NULL, '参考资料出处', 60)
) AS v(field_code, field_name, data_type, required, default_value, description, sort_order)
  ON f.code = v.field_code AND f.deleted = false AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  default_value = EXCLUDED.default_value,
  description = EXCLUDED.description,
  sort_order = EXCLUDED.sort_order,
  status = EXCLUDED.status,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;
