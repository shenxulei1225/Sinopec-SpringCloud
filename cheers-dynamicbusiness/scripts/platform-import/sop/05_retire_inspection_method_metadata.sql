-- ============================================================================
-- sop · 05 退役 inspection_method 元数据（软删类型/字段/型号；停用 method_template_id）
-- 前置：Flyway V55 已删除物理表与 method_template_id 列
-- ============================================================================

SET search_path TO dynamicbusiness;

UPDATE dynamic_entity_type
SET deleted = true, status = 'inactive', updater = 'seed-retire', update_time = CURRENT_TIMESTAMP
WHERE code = 'inspection_method' AND deleted = false;

UPDATE dynamic_entity_type_config
SET deleted = true, status = 0, updater = 'seed-retire', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_method' AND deleted = false;

UPDATE dynamic_model
SET deleted = true, updater = 'seed-retire', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_method' AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET deleted = true, status = 0, updater = 'seed-retire', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_method' AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET deleted = true, status = 0, updater = 'seed-retire', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'inspection_item'
  AND field_code = 'method_template_id'
  AND deleted = false;

UPDATE dynamic_model_field_assignment a
SET deleted = true, updater = 'seed-retire', update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.model_code = m.code
  AND a.tenant_id = m.tenant_id
  AND m.entity_type_code = 'inspection_item'
  AND m.deleted = false
  AND a.field_code = 'method_template_id'
  AND a.deleted = false;

UPDATE dynamic_field
SET status = 0, description = COALESCE(description, '') || ' [已废弃：inspection_method 退役]',
    updater = 'seed-retire', update_time = CURRENT_TIMESTAMP
WHERE code IN ('is_template', 'action_duration_sec', 'method_template_id')
  AND tenant_id = 1
  AND deleted = false;

-- 分类种类 inspection_method（若存在）
UPDATE dynamic_category_type
SET deleted = true, updater = 'seed-retire', update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'inspection_method' AND deleted = false;
