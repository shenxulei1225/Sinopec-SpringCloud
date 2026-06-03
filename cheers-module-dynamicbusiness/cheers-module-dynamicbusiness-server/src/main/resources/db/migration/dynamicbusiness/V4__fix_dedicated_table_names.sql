-- 修正 dedicated_table_name 与 zhgl 实际 biz_* 表名不一致的问题
SET search_path TO dynamicbusiness;

UPDATE dynamic_business_type
SET dedicated_table_name = 'biz_spare_part',
    updater              = 'migration',
    update_time          = CURRENT_TIMESTAMP
WHERE code = 'spare_parts'
  AND dedicated_table_name = 'biz_spare_parts';

UPDATE dynamic_business_type
SET dedicated_table_name = 'biz_inspection_point',
    updater              = 'migration',
    update_time          = CURRENT_TIMESTAMP
WHERE code = 'inspection_point'
  AND dedicated_table_name = 'biz_point';

UPDATE dynamic_business_type_config
SET dedicated_table_name = 'biz_spare_part',
    updater              = 'migration',
    update_time          = CURRENT_TIMESTAMP
WHERE business_type_code = 'spare_parts'
  AND dedicated_table_name = 'biz_spare_parts';

UPDATE dynamic_business_type_config
SET dedicated_table_name = 'biz_inspection_point',
    updater              = 'migration',
    update_time          = CURRENT_TIMESTAMP
WHERE business_type_code = 'inspection_point'
  AND dedicated_table_name = 'biz_point';
