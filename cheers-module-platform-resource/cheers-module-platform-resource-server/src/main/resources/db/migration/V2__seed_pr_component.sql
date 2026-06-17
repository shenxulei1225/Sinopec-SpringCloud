SET search_path TO platformresource;

-- 与前端组件库目录对齐；不含 props / 接口（由 component_props + 业务能力投影提供）
INSERT INTO pr_component (component_code, type, name, icon, status, sort, description)
VALUES
('tree', 'tree', '树', 'tree', 1, 10, '数据组件：基础树'),
('list', 'list', '列表', 'list', 1, 20, '数据组件：基础列表'),
('table', 'table', '表格', 'table', 1, 25, '数据组件：表格'),
('card', 'card', '卡片', 'card', 1, 26, '数据组件：卡片（Grid）'),
('button', 'button', '按钮', 'button', 1, 100, 'shadcn Button'),
('input', 'input', '输入框', 'input', 1, 110, 'shadcn Input'),
('label', 'label', '标签', 'label', 1, 120, 'shadcn Label'),
('separator', 'separator', '分隔线', 'separator', 1, 140, 'shadcn Separator'),
('badge', 'badge', '徽章', 'badge', 1, 150, 'shadcn Badge'),
('tabs', 'tabs', '标签页', 'tabs', 1, 160, 'shadcn Tabs')
ON CONFLICT (component_code) DO UPDATE SET
    type = EXCLUDED.type,
    name = EXCLUDED.name,
    icon = EXCLUDED.icon,
    status = EXCLUDED.status,
    sort = EXCLUDED.sort,
    description = EXCLUDED.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP,
    deleted = FALSE;
