-- 焊缝编号只在焊缝型号上必填。类型上的必填会挡住管子、弯头、变径管和三通保存。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET required = false, updater = 'line-digit', update_time = CURRENT_TIMESTAMP
WHERE field_code = 'weld_no'
  AND entity_type_code = 'pipeline_pipe'
  AND deleted = false
  AND required = true;
