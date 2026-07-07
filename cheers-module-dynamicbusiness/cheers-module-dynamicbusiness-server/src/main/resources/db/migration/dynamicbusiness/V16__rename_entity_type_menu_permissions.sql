-- 与 system 模块 V8 相同：升级环境若仅跑 dynamicbusiness Flyway，仍同步 public.system_menu 权限码

UPDATE public.system_menu
SET permission = REPLACE(permission, 'system:business-type', 'system:entity-type'),
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND permission LIKE 'system:business-type%';
