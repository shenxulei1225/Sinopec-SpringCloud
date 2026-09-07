-- V96: SOP 去模板化（基础字段层）
-- 目标：界面与元数据不再暴露 is_template / sop_template_id 等模板实例语义字段。
-- 说明：仅收敛基础字段配置，不删除物理列，保障历史数据可回溯。

SET search_path TO dynamicbusiness, public;

UPDATE dynamic_entity_type_base_field
SET
  deleted = TRUE,
  status = 0,
  updater = 'flyway-v96',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'sop'
  AND field_code IN (
    'is_template',
    'sop_template_id',
    'default_steps_json',
    'default_params_json',
    'step_override_json',
    'action_tree_override_json',
    'param_override_json'
  );

-- 动作树与流程图字段由专用解析器展示，不走通用基础字段表单。
UPDATE dynamic_entity_type_base_field
SET
  type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}',
  updater = 'flyway-v96',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'sop'
  AND field_code IN ('action_tree_json', 'flow_graph_json');

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'sop';
