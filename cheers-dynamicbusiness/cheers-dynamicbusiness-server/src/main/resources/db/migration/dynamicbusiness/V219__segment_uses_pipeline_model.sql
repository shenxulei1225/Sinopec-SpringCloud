-- 管线段沿用已有管线型号。上级选择这条管线。
-- 去掉单独的「管线段」型号和它的字段分配。不改已有管线记录。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_model_field_assignment
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE model_code = 'MODEL-line-segment' AND deleted = false;

UPDATE dynamicbusiness.dynamic_model
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE code = 'MODEL-line-segment' AND deleted = false;
