-- ========================================
-- 添加模型字段分组关联管理权限
-- 创建日期: 2026-01-27
-- 说明: 添加字段与分组关联操作的权限（作为按钮权限）
-- 注意: 此脚本要求父菜单（permission='system:model-field-assignment:query', type=2）必须存在
--       如果父菜单不存在,脚本会静默跳过,不会报错
-- ========================================

-- 查询权限（按钮权限,添加到模型字段分配菜单下）
INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
)
SELECT 
    '模型字段分组关联查询',
    'system:model-field-group-assignment:query',
    3,  -- 类型：3=按钮
    1,  -- 排序
    (SELECT id FROM dynamic_menu WHERE permission = 'system:model-field-assignment:query' AND type = 2 LIMIT 1),  -- 父菜单：模型字段分配菜单
    '', '', '', '', 0, true, true, false, 'admin', NOW(), 'admin', NOW(), false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_menu WHERE permission = 'system:model-field-group-assignment:query'
)
AND EXISTS (
    SELECT 1 FROM dynamic_menu WHERE permission = 'system:model-field-assignment:query' AND type = 2
);

-- 创建权限（按钮权限）
INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
)
SELECT 
    '模型字段分组关联创建',
    'system:model-field-group-assignment:create',
    3,  -- 类型：3=按钮
    2,  -- 排序
    (SELECT id FROM dynamic_menu WHERE permission = 'system:model-field-assignment:query' AND type = 2 LIMIT 1),
    '', '', '', '', 0, true, true, false, 'admin', NOW(), 'admin', NOW(), false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_menu WHERE permission = 'system:model-field-group-assignment:create'
)
AND EXISTS (
    SELECT 1 FROM dynamic_menu WHERE permission = 'system:model-field-assignment:query' AND type = 2
);

-- 更新权限（按钮权限）
INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
)
SELECT 
    '模型字段分组关联更新',
    'system:model-field-group-assignment:update',
    3,  -- 类型：3=按钮
    3,  -- 排序
    (SELECT id FROM dynamic_menu WHERE permission = 'system:model-field-assignment:query' AND type = 2 LIMIT 1),
    '', '', '', '', 0, true, true, false, 'admin', NOW(), 'admin', NOW(), false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_menu WHERE permission = 'system:model-field-group-assignment:update'
)
AND EXISTS (
    SELECT 1 FROM dynamic_menu WHERE permission = 'system:model-field-assignment:query' AND type = 2
);

-- 删除权限（按钮权限）
INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted
)
SELECT 
    '模型字段分组关联删除',
    'system:model-field-group-assignment:delete',
    3,  -- 类型：3=按钮
    4,  -- 排序
    (SELECT id FROM dynamic_menu WHERE permission = 'system:model-field-assignment:query' AND type = 2 LIMIT 1),
    '', '', '', '', 0, true, true, false, 'admin', NOW(), 'admin', NOW(), false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_menu WHERE permission = 'system:model-field-group-assignment:delete'
)
AND EXISTS (
    SELECT 1 FROM dynamic_menu WHERE permission = 'system:model-field-assignment:query' AND type = 2
);

-- 为管理员角色分配权限（假设管理员角色ID为1）
-- 注意：实际使用时请根据实际情况调整角色ID
INSERT INTO dynamic_role_menu (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 
    1 as role_id,
    id as menu_id,
    'admin' as creator,
    NOW() as create_time,
    'admin' as updater,
    NOW() as update_time,
    false as deleted,
    1 as tenant_id
FROM dynamic_menu
WHERE permission IN (
    'system:model-field-group-assignment:query',
    'system:model-field-group-assignment:create',
    'system:model-field-group-assignment:update',
    'system:model-field-group-assignment:delete'
)
AND NOT EXISTS (
    SELECT 1 FROM dynamic_role_menu srm 
    WHERE srm.role_id = 1 
    AND srm.menu_id = dynamic_menu.id 
    AND srm.tenant_id = 1
);
