-- 执行方式：动作库用这一项限定指令适用哪些执行方式。
-- 显示名从设备检查方法改为执行方式。选项仍是人工、无人机、机器人、固定摄像机。
-- 不负责：设备检查参数、分类树。禁止再把分类树当这份词表。

SET search_path TO dynamicbusiness, public;

UPDATE dynamic_field
SET name = '执行方式',
    description = '执行方式词表。动作库用它限定一条指令适用哪些执行方式。检查项方法 Tab、任务选方式、设备多选读同一份选项。禁止再用分类树当词表。',
    updater = 'v150',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'execution_means'
  AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET field_name = '执行方式',
    updater = 'v150',
    update_time = CURRENT_TIMESTAMP
WHERE field_code = 'execution_means'
  AND deleted = false;

UPDATE dynamic_field
SET description = '每台设备上的检查参数。一条是一个检查项加一种执行方式，步骤上填的数写在这一条里。',
    updater = 'v150',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'host_sop_param_pack'
  AND deleted = false
  AND description LIKE '%设备检查方法%';

UPDATE dynamic_entity_type_base_field
SET description = '每台设备上的检查参数。一条是一个检查项加一种执行方式，步骤上填的数写在这一条里。',
    updater = 'v150',
    update_time = CURRENT_TIMESTAMP
WHERE field_code = 'host_sop_param_pack'
  AND deleted = false
  AND description LIKE '%设备检查方法%';

DELETE FROM model_crud_form_definition
WHERE entity_type_code IN ('equipment', 'action', 'inspection_item')
   OR entity_type_code IN (
     SELECT code FROM dynamic_entity_type
     WHERE deleted = false
       AND entry_kind = 'DOMAIN'
       AND base_entity_type_code IN ('equipment', 'action', 'inspection_item')
   );
