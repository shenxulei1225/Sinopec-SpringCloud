-- 智能分析能力是智能分析动作型号上的一栏。
-- 动作表仍保留这一列，其它动作型号不出现这一栏。
-- 已执行的 V155 把目录显示开成了全部型号可见，这里收回。

UPDATE dynamic_entity_type_base_field
SET
    type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
    description = '智能分析动作引用的平台分析能力编号。只在智能分析动作型号上填写。',
    updater = 'flyway-v160',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'analysis_capability_id'
  AND deleted = FALSE;

UPDATE dynamic_model_field_assignment
SET
    required = TRUE,
    validation_rules = '{"createVisible":true,"editVisible":true,"detailVisible":false}',
    updater = 'flyway-v160',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND model_code = 'action_intelligent_analysis'
  AND field_code = 'analysis_capability_id'
  AND deleted = FALSE;
