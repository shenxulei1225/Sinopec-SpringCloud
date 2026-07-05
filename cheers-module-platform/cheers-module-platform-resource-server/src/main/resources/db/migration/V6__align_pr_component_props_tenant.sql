SET search_path TO platformresource;

-- 种子数据默认 tenant_id=0，与登录态 tenant-id:1 不一致时多租户插件会查不到模板
UPDATE pr_component
SET tenant_id = 1,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 0
  AND deleted = FALSE;

UPDATE pr_component_props
SET tenant_id = 1,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 0
  AND deleted = FALSE;
