-- 废止数据目录「应急响应」（emergency_response）。
-- 启动、改级别、步骤完成和上报都记在应急事件上。指挥页的步骤读预案。
-- 不再保留这本目录、它的型号、归属、授权，以及目录下已有记录。
SET search_path TO dynamicbusiness;

UPDATE dynamic_entity_type
SET deleted = true,
    status = 'inactive',
    updater = 'V247',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'emergency_response';

UPDATE dynamic_model
SET deleted = true,
    status = 0,
    updater = 'V247',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code = 'emergency_response';

UPDATE dynamic_catalog_ownership
SET deleted = true,
    updater = 'V247',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (registry_code = 'emergency_response' OR target_registry_code = 'emergency_response');

UPDATE dynamic_catalog_role_grant
SET deleted = true,
    updater = 'V247',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND registry_code = 'emergency_response';

UPDATE dynamic_management_method
SET deleted = true,
    updater = 'V247',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND registry_code = 'emergency_response';

UPDATE dynamic_management_unit_catalog
SET deleted = true,
    updater = 'V247',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND registry_code = 'emergency_response';

UPDATE ent_emergency_response_t1
SET deleted = true,
    updater = 'V247',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false;

UPDATE ent_emergency_response_t123
SET deleted = true,
    updater = 'V247',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false;
