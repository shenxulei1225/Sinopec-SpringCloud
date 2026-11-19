-- 普通角色不是租户管理员。角色页可以删除。
-- 类型改为自定义。租户管理员仍是内置，不能删。

UPDATE system_role
SET type = 2,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'common'
  AND type <> 2;
