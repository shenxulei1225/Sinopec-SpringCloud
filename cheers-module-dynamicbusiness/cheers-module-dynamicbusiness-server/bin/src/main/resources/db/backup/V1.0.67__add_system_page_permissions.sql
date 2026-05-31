-- ========================================
-- 添加页面管理菜单与权限
-- 创建日期: 2026-01-27
-- 说明: 在系统管理下新增“页面管理”菜单及按钮权限
-- ========================================

-- 查找系统管理父菜单（目录）
WITH parent_menu AS (
    SELECT id
    FROM system_menu
    WHERE name = '系统管理'
      AND type = 1
      AND deleted = false
    LIMIT 1
),
page_menu AS (
    SELECT id
    FROM system_menu
    WHERE permission = 'system:page:query'
      AND type = 2
      AND deleted = false
    LIMIT 1
)
-- 新增页面管理菜单
INSERT INTO system_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
)
SELECT
    '页面管理',
    'system:page:query',
    2,
    50,
    (SELECT id FROM parent_menu),
    'page',
    'ep:document',
    'system/page/index',
    'SystemPage',
    0,
    true,
    true,
    false,
    'admin',
    NOW(),
    'admin',
    NOW(),
    false
WHERE NOT EXISTS (
    SELECT 1 FROM page_menu
)
AND EXISTS (
    SELECT 1 FROM parent_menu
);

-- 创建权限（按钮）
INSERT INTO system_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
)
SELECT
    '页面管理创建',
    'system:page:create',
    3,
    1,
    (SELECT id FROM system_menu WHERE permission = 'system:page:query' AND type = 2 LIMIT 1),
    '', '', '', '', 0, true, true, false, 'admin', NOW(), 'admin', NOW(), false
WHERE NOT EXISTS (
    SELECT 1 FROM system_menu WHERE permission = 'system:page:create'
)
AND EXISTS (
    SELECT 1 FROM system_menu WHERE permission = 'system:page:query' AND type = 2
);

-- 更新权限（按钮）
INSERT INTO system_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
)
SELECT
    '页面管理更新',
    'system:page:update',
    3,
    2,
    (SELECT id FROM system_menu WHERE permission = 'system:page:query' AND type = 2 LIMIT 1),
    '', '', '', '', 0, true, true, false, 'admin', NOW(), 'admin', NOW(), false
WHERE NOT EXISTS (
    SELECT 1 FROM system_menu WHERE permission = 'system:page:update'
)
AND EXISTS (
    SELECT 1 FROM system_menu WHERE permission = 'system:page:query' AND type = 2
);

-- 删除权限（按钮）
INSERT INTO system_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
)
SELECT
    '页面管理删除',
    'system:page:delete',
    3,
    3,
    (SELECT id FROM system_menu WHERE permission = 'system:page:query' AND type = 2 LIMIT 1),
    '', '', '', '', 0, true, true, false, 'admin', NOW(), 'admin', NOW(), false
WHERE NOT EXISTS (
    SELECT 1 FROM system_menu WHERE permission = 'system:page:delete'
)
AND EXISTS (
    SELECT 1 FROM system_menu WHERE permission = 'system:page:query' AND type = 2
);

-- 为管理员角色分配权限（假设管理员角色ID为1）
INSERT INTO system_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT
    1 as role_id,
    id as menu_id,
    'admin' as creator,
    NOW() as create_time,
    'admin' as updater,
    NOW() as update_time,
    false as deleted,
    1 as tenant_id
FROM system_menu
WHERE permission IN (
    'system:page:query',
    'system:page:create',
    'system:page:update',
    'system:page:delete'
)
AND NOT EXISTS (
    SELECT 1 FROM system_role_menu srm
    WHERE srm.role_id = 1
      AND srm.menu_id = system_menu.id
      AND srm.tenant_id = 1
);
