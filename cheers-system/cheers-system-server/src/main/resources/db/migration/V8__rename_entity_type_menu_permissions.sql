-- 权限标识对齐实体类型（entity type）重命名：system:business-type* → system:entity-type*
-- 影响 system_menu.permission，与 @PreAuthorize('system:entity-type*') 保持一致

UPDATE public.system_menu
SET permission = REPLACE(permission, 'system:business-type', 'system:entity-type'),
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND permission LIKE 'system:business-type%';
