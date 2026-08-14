-- ============================================================================
-- sop · 03 基础字段挂载（entity_type_code=sop）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'sop',
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
    ('version_no', '版本号', 'INTEGER', true, '1', 'SOP 版本号；同 code 下递增', 10),
    ('publish_status', '发布状态', 'TEXT', false, 'DRAFT', 'DRAFT=草稿；PUBLISHED=已发布', 20),
    ('steps_json', '步骤定义', 'TEXT', false, NULL, '有序步骤 JSON 数组（怎么做唯一正文）', 30)
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
