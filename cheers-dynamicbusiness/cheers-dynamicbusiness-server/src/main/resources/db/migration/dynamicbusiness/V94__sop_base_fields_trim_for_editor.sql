-- V94: SOP 基础字段收敛（保留动作树权威，去掉实例差量/执行手段等非模板说明字段）
-- 目标：
-- 1) SOP 说明区不再暴露结构 JSON / 维度字段给用户直接编辑；
-- 2) 仅保留 action_tree_json 作为模板步骤树权威字段（由专用步骤树编辑器维护）；
-- 3) 不删物理列，仅读写元数据层收敛，避免历史数据丢失。

SET search_path TO dynamicbusiness, public;

-- 1) SOP 类型基础字段：软删不应在「基础字段」里继续展示的字段。
UPDATE dynamic_entity_type_base_field
SET
  deleted = TRUE,
  status = 0,
  updater = 'flyway-v94',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'sop'
  AND field_code IN (
    'action_tree_override_json',
    'default_params_by_node_json',
    'param_override_json',
    'execution_means',
    'procedure_kind'
  );

-- 2) 对保留的动作树字段明确隐藏（不在通用 CRUD 表单里展示原始 JSON）。
UPDATE dynamic_entity_type_base_field
SET
  type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}',
  updater = 'flyway-v94',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'sop'
  AND field_code = 'action_tree_json';

-- 3) 清理 SOP CRUD 表单缓存，下次按最新字段配置重算。
DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'sop';
