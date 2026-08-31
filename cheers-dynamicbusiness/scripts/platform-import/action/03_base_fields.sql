-- ============================================================================
-- action · 03 基础字段挂载（entity_type_code=action）
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'action',
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
    ('execution_means', '执行手段', 'TEXT', false, NULL, 'MANUAL/UAV/ROBOT/FIXED_CAMERA 等', 10),
    ('param_slots_json', '参数槽定义', 'TEXT', false, '[]', '动作参数槽 schema JSON 数组', 20),
    ('child_action_ids_json', '子动作列表', 'TEXT', false, '[]', '复合动作有序子动作 code 列表', 30),
    ('is_composite', '是否复合动作', 'BOOLEAN', false, 'false', 'true=开跑时展开子动作', 40)
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
