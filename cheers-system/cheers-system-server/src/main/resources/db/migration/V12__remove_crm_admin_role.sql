-- 业务租户的内置角色只留租户管理员和普通角色。
-- CRM 管理员是旧演示种子，从角色列表删除。已经分给用户的一并去掉。

UPDATE system_user_role
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND role_id IN (
      SELECT id FROM system_role WHERE code = 'crm_admin' AND deleted = false
  );

UPDATE system_role_menu
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND role_id IN (
      SELECT id FROM system_role WHERE code = 'crm_admin' AND deleted = false
  );

UPDATE system_role
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'crm_admin'
  AND deleted = false;

UPDATE system_role
SET type = 1,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code IN ('tenant_admin', 'common')
  AND type <> 1;
