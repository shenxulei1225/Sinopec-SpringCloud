-- 入廊客户（customer）应为根业务；误挂到设备管理下会导致门户左侧不可见

SET search_path TO dynamicbusiness;

UPDATE dynamicbusiness.dynamic_business
SET parent_id = NULL,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'customer'
  AND parent_id IS NOT NULL;
