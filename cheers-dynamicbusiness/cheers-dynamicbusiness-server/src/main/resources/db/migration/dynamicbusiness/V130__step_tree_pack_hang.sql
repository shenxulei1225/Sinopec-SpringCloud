-- V130: 检查项步骤树挂载从「按手段拆树」收成平台步骤树包
--
-- 负责：目录 type_config 正式 editorKind=step_tree_pack，并写下 packMethods。
-- 不负责：改检查项实体里已存的步骤 JSON（读路径仍认历史 executionMeans/actionTree）。

SET search_path TO dynamicbusiness, public;

UPDATE dynamic_entity_type_base_field bf
SET type_config = jsonb_strip_nulls(
      COALESCE(bf.type_config, '{}'::jsonb)
      || jsonb_build_object(
           'editorKind', 'step_tree_pack',
           'packMethods', '[
             {"key":"MANUAL","label":"人"},
             {"key":"UAV","label":"无人机"},
             {"key":"ROBOT","label":"机器人"},
             {"key":"FIXED_CAMERA","label":"摄像机"}
           ]'::jsonb
         )
    ),
    updater = 'flyway-v130',
    update_time = CURRENT_TIMESTAMP
WHERE bf.deleted = false
  AND bf.entity_type_code = 'inspection_item'
  AND bf.field_code IN ('step_tree_json', 'action_tree_json');

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'inspection_item';
