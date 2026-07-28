-- ============================================================================
-- 管廊租户 · system 租户 2 + 登录账号 zhgl（开发联调）
-- 与 tenant 1 admin 同密码（现网联调为 123456，非 admin123）
-- ============================================================================

INSERT INTO public.system_tenant (
  id, name, contact_user_id, contact_name, contact_mobile, status,
  websites, package_id, expire_time, account_count, creator
) VALUES (
  2, '智慧管廊', NULL, 'zhgl', '13800000002', 0,
  '', 0, '2099-12-31 23:59:59', 100, 'corridor-seed'
)
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  contact_name = EXCLUDED.contact_name,
  status = EXCLUDED.status,
  expire_time = EXCLUDED.expire_time,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO public.system_users (
  id, username, password, nickname, remark, dept_id, email, mobile, sex,
  avatar, status, creator, tenant_id
) VALUES (
  200, 'zhgl',
  '$2a$10$gce4OT7hxrzMJMuAg7ZyH.S20YPmIO9oK4.6pHhtB7laF7OC5iOuC',
  '管廊管理员', '智慧管廊租户默认账号', NULL, 'zhgl@local.dev', '13800000002', 0,
  '', 0, 'corridor-seed', 2
)
ON CONFLICT (id) DO UPDATE SET
  username = EXCLUDED.username,
  nickname = EXCLUDED.nickname,
  tenant_id = EXCLUDED.tenant_id,
  status = EXCLUDED.status,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- 与 tenant 1 admin 同密码（避免 seed 哈希与现网不一致）
UPDATE public.system_users u
SET password = a.password, dept_id = COALESCE(u.dept_id, a.dept_id), updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
FROM public.system_users a
WHERE u.id = 200 AND a.id = 1;

-- 租户 2 管理员角色（菜单权限对齐 tenant 121 的 tenant_admin）
INSERT INTO public.system_role (
  id, name, code, sort, data_scope, status, type, remark, creator, tenant_id
)
SELECT 201, '租户管理员', 'tenant_admin', 0, 1, 0, 1, '管廊租户管理员', 'corridor-seed', 2
WHERE NOT EXISTS (SELECT 1 FROM public.system_role WHERE id = 201);

INSERT INTO public.system_user_role (id, user_id, role_id, creator, tenant_id)
VALUES (55, 200, 201, 'corridor-seed', 2)
ON CONFLICT (id) DO UPDATE SET
  user_id = EXCLUDED.user_id,
  role_id = EXCLUDED.role_id,
  tenant_id = EXCLUDED.tenant_id,
  deleted = false,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- 菜单权限：从 tenant 121 的 tenant_admin(109) 复制，id 递增
INSERT INTO public.system_role_menu (id, role_id, menu_id, creator, tenant_id)
SELECT
  base.max_id + ROW_NUMBER() OVER (ORDER BY rm.menu_id),
  201,
  rm.menu_id,
  'corridor-seed',
  2
FROM public.system_role_menu rm
CROSS JOIN (SELECT COALESCE(MAX(id), 0) AS max_id FROM public.system_role_menu) base
WHERE rm.role_id = 109 AND rm.deleted = false
  AND NOT EXISTS (
    SELECT 1 FROM public.system_role_menu x
    WHERE x.role_id = 201 AND x.menu_id = rm.menu_id AND x.deleted = false
  );

-- 动态业务接口使用 system:entity-type:* 等权限；库内若未登记对应菜单，仅 super_admin 可访问（与 tenant1 admin 一致）
INSERT INTO public.system_role (
  id, name, code, sort, data_scope, status, type, remark, creator, tenant_id
)
SELECT 202, '超级管理员', 'super_admin', 0, 1, 0, 1, '管廊租户超级管理员', 'corridor-seed', 2
WHERE NOT EXISTS (SELECT 1 FROM public.system_role WHERE tenant_id = 2 AND code = 'super_admin');

INSERT INTO public.system_user_role (id, user_id, role_id, creator, tenant_id)
SELECT 56, 200, r.id, 'corridor-seed', 2
FROM public.system_role r
WHERE r.tenant_id = 2 AND r.code = 'super_admin'
  AND NOT EXISTS (
    SELECT 1 FROM public.system_user_role ur
    WHERE ur.user_id = 200 AND ur.role_id = r.id AND ur.deleted = false
  );

UPDATE public.system_tenant
SET contact_user_id = 200, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE id = 2;
