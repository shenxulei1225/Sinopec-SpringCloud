-- 废止早期「巡检排期」目录注册（patrol_schedule）。
-- 现网任务排期写在任务字段，排期看板读计划点；物理表已不存在，仅残留目录注册与授权。
SET search_path TO dynamicbusiness;

UPDATE dynamic_entity_type
SET deleted = true,
    status = 'inactive',
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'patrol_schedule';

UPDATE dynamic_catalog_role_grant
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND registry_code = 'patrol_schedule';

UPDATE dynamic_catalog_role_station_scope
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND registry_code = 'patrol_schedule';
