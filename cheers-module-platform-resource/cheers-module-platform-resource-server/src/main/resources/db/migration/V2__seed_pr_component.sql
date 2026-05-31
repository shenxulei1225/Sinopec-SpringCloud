SET search_path TO platformresource;

-- 与前端 defaultComponentLibrary.ts 对齐的基础组件种子
INSERT INTO pr_component ("key", type, name, icon, props, ui_config, status, sort, description)
VALUES
(
    'tree', 'tree', '树', 'tree',
    '{"selectMode":"single","defaultExpandAll":true,"showSearch":true,"showToolbar":false,"draggable":false,"emptyText":"暂无数据","searchPlaceholder":"搜索","autoLoad":true}',
    '{"category":"data","importPath":"@/modules/common/components/Tree"}',
    1, 10, '数据组件：基础树'
),
(
    'list', 'list', '列表', 'list',
    '{"selectMode":"single","emptyText":"暂无数据","idField":"id","nameField":"name","codeField":"code","autoLoad":true}',
    '{"category":"data","importPath":"@/modules/common/components/List"}',
    1, 20, '数据组件：基础列表'
),
(
    'button', 'button', '按钮', 'button',
    '{"variant":"default","size":"default","children":"按钮","disabled":false}',
    '{"category":"primitive","importPath":"@/components/ui/button"}',
    1, 100, 'shadcn Button'
),
(
    'input', 'input', '输入框', 'input',
    '{"type":"text","placeholder":"请输入","disabled":false}',
    '{"category":"primitive","importPath":"@/components/ui/input"}',
    1, 110, 'shadcn Input'
),
(
    'label', 'label', '标签', 'label',
    '{"children":"标签"}',
    '{"category":"primitive","importPath":"@/components/ui/label"}',
    1, 120, 'shadcn Label'
),
(
    'card', 'card', '卡片', 'card',
    '{"size":"default","title":"卡片标题","description":"","content":""}',
    '{"category":"layout","importPath":"@/components/ui/card"}',
    1, 130, 'shadcn Card'
),
(
    'separator', 'separator', '分隔线', 'separator',
    '{"orientation":"horizontal"}',
    '{"category":"layout","importPath":"@/components/ui/separator"}',
    1, 140, 'shadcn Separator'
),
(
    'badge', 'badge', '徽章', 'badge',
    '{"variant":"default","children":"Badge"}',
    '{"category":"primitive","importPath":"@/components/ui/badge"}',
    1, 150, 'shadcn Badge'
),
(
    'tabs', 'tabs', '标签页', 'tabs',
    '{"defaultValue":"tab-1","tabs":[{"value":"tab-1","label":"Tab 1","content":"内容 1"},{"value":"tab-2","label":"Tab 2","content":"内容 2"}]}',
    '{"category":"layout","importPath":"@/components/ui/tabs"}',
    1, 160, 'shadcn Tabs'
)
ON CONFLICT ("key") DO UPDATE SET
    type = EXCLUDED.type,
    name = EXCLUDED.name,
    icon = EXCLUDED.icon,
    props = EXCLUDED.props,
    ui_config = EXCLUDED.ui_config,
    status = EXCLUDED.status,
    sort = EXCLUDED.sort,
    description = EXCLUDED.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP,
    deleted = FALSE;
