-- 动态业务 > 单位管理 菜单与权限

INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
)
SELECT
    '单位管理',
    'system:unit:query',
    2,
    20,
    p.id,
    'unit-management',
    'ep:set-up',
    'dynamicBusiness/unit-management/index',
    'DynamicBusinessUnitManagement',
    0,
    TRUE,
    TRUE,
    FALSE,
    '1', NOW(), '1', NOW(), FALSE
FROM dynamic_menu p
WHERE p.path = '/dynamicBusiness' AND p.deleted = FALSE
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_menu c
      WHERE c.parent_id = p.id
        AND c.path = 'unit-management'
        AND c.deleted = FALSE
  );

INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
)
SELECT
    '单位查询', 'system:unit:query', 3, 1, m.id,
    '', '#', '', NULL, 0,
    TRUE, TRUE, FALSE,
    '1', NOW(), '1', NOW(), FALSE
FROM dynamic_menu m
WHERE m.component_name = 'DynamicBusinessUnitManagement' AND m.deleted = FALSE
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_menu b
      WHERE b.parent_id = m.id AND b.permission = 'system:unit:query' AND b.deleted = FALSE
  );

INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
)
SELECT
    '单位创建', 'system:unit:create', 3, 2, m.id,
    '', '#', '', NULL, 0,
    TRUE, TRUE, FALSE,
    '1', NOW(), '1', NOW(), FALSE
FROM dynamic_menu m
WHERE m.component_name = 'DynamicBusinessUnitManagement' AND m.deleted = FALSE
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_menu b
      WHERE b.parent_id = m.id AND b.permission = 'system:unit:create' AND b.deleted = FALSE
  );

INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
)
SELECT
    '单位更新', 'system:unit:update', 3, 3, m.id,
    '', '#', '', NULL, 0,
    TRUE, TRUE, FALSE,
    '1', NOW(), '1', NOW(), FALSE
FROM dynamic_menu m
WHERE m.component_name = 'DynamicBusinessUnitManagement' AND m.deleted = FALSE
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_menu b
      WHERE b.parent_id = m.id AND b.permission = 'system:unit:update' AND b.deleted = FALSE
  );

INSERT INTO dynamic_menu (
    name, permission, type, sort, parent_id,
    path, icon, component, component_name, status,
    visible, keep_alive, always_show,
    creator, create_time, updater, update_time, deleted
)
SELECT
    '单位删除', 'system:unit:delete', 3, 4, m.id,
    '', '#', '', NULL, 0,
    TRUE, TRUE, FALSE,
    '1', NOW(), '1', NOW(), FALSE
FROM dynamic_menu m
WHERE m.component_name = 'DynamicBusinessUnitManagement' AND m.deleted = FALSE
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_menu b
      WHERE b.parent_id = m.id AND b.permission = 'system:unit:delete' AND b.deleted = FALSE
  );
