-- 废止早期「巡检路线」域目录注册（patrol_route）。
-- 权威：路线随任务草稿/已发布写入，不另写路线方案表；场站底座「路线」(route) 不在此废止。
SET search_path TO dynamicbusiness;

UPDATE dynamic_entity_type
SET deleted = true,
    status = 'inactive',
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'patrol_route';

UPDATE dynamic_catalog_role_grant
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND registry_code = 'patrol_route';

UPDATE dynamic_catalog_role_station_scope
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND registry_code = 'patrol_route';
