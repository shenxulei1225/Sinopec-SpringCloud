-- 智慧管廊站内应急资源的目录行写着巡检租户的专用表 ent_emergency_resource_t1。
-- 本租户的器材在 ent_emergency_resource_t123。
-- 表名已经带 _t1 时，打开数据页会原样查那张表，不会改到当前租户，列表就是空的。

UPDATE dynamicbusiness.dynamic_entity_type
SET dedicated_table_name = 'ent_emergency_resource_t123',
    updater = 'V269',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND code = 'emergency_resource'
  AND deleted = false
  AND dedicated_table_name = 'ent_emergency_resource_t1';
