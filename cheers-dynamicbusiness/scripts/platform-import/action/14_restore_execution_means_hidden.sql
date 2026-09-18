-- ============================================================================
-- action · 14 恢复隐藏基础字段 execution_means
--
-- 给已执行旧版 01/12（软删适用手段）的库用。新库由 01+03 直接挂上。
-- 物理列 jsonb 化由 Flyway V126 负责。
-- ============================================================================

SET search_path TO dynamicbusiness;

UPDATE dynamic_field
SET
  deleted = false,
  status = 1,
  type = 'JSON',
  name = '适用手段',
  description = '动作适用的执行手段编码数组。详情勾选；新建/编辑表单不展示。',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code = 'execution_means';

UPDATE dynamic_entity_type_base_field
SET
  deleted = false,
  status = 1,
  data_type = 'JSON',
  field_name = '适用手段',
  required = false,
  default_value = '[]',
  type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'execution_means';

UPDATE dynamic_model_field_assignment
SET
  deleted = false,
  field_source = 'BASE',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND model_code = 'action'
  AND field_code = 'execution_means';

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'action';
