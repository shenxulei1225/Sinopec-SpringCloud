-- SOP 去模板化后的历史字段清理（模型字段层）
-- 目的：
-- 1) 软删 SOP 型号上仍残留的模板语义字段分配，避免继续进入 model-crud-form。
-- 2) 归一 SOP 基础字段表：deleted=true 的历史行强制 status=0，防止旧数据被误纳入投影。

-- A. SOP 模型字段：软删历史模板字段（按 model.entity_type_code 定位，不依赖固定 model_id）
UPDATE dynamicbusiness.dynamic_model_field_assignment mfa
SET deleted = TRUE,
    update_time = NOW(),
    updater = 'flyway-v98'
FROM dynamicbusiness.dynamic_model m
WHERE m.id = mfa.model_id
  AND m.entity_type_code = 'sop'
  AND mfa.deleted = FALSE
  AND mfa.field_code IN (
    'is_template',
    'sop_template_id',
    'step_override_json',
    'default_steps_json',
    'default_params_json',
    'action_tree_override_json',
    'default_params_by_node_json',
    'param_override_json',
    'execution_means',
    'procedure_kind'
  );

-- B. SOP 基础字段定义：已软删行统一禁用，避免历史 status=1 漏入能力重建
UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET status = 0,
    update_time = NOW(),
    updater = 'flyway-v98'
WHERE entity_type_code = 'sop'
  AND deleted = TRUE
  AND status <> 0;
