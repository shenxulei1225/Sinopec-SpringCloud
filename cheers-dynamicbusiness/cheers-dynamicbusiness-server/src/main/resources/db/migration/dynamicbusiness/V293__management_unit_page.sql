-- 租户底座包增加「管理单元与业务启用」页面编码（从数据资源管理概念中拆出）。
SET search_path TO dynamicbusiness;

UPDATE tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',management-unit',
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'tenant-foundation'
  AND opened_page_codes IS NOT NULL
  AND opened_page_codes <> ''
  AND position('management-unit' IN opened_page_codes) = 0;