-- 门户业务权限码（与 system:business:* @PreAuthorize 对齐）

UPDATE public.system_menu
SET permission = REPLACE(permission, 'system:business-type', 'system:business'),
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND permission LIKE 'system:business-type%'
  AND permission NOT LIKE 'system:business-type-base-field%';

-- 若仅有 entity-type 门户权限而无 business，实施方在菜单管理补 system:business:*；
-- 开发环境 mock 鉴权时可忽略。
