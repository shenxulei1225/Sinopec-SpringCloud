-- UMG 式标准组件库 Phase 1：data（tree/list）+ primitive/layout（shadcn）
-- dataConfig / apiConfig 可为空；业务配置由 view composition overrides 注入

UPDATE system_component
SET deleted      = TRUE,
    updater      = 'seed',
    update_time  = CURRENT_TIMESTAMP
WHERE key NOT IN ('tree', 'list', 'button', 'input', 'label', 'card', 'separator', 'badge', 'tabs')
  AND deleted = FALSE;

INSERT INTO system_component (key, type, name, icon, props, data_config, api_config, ui_config, status, sort, description)
VALUES
(
    'tree',
    'tree',
    '树',
    'tree',
    '{"selectMode":"single","defaultExpandAll":true,"showSearch":true,"showToolbar":false,"draggable":false,"emptyText":"暂无数据","searchPlaceholder":"搜索","autoLoad":true}',
    NULL,
    NULL,
    '{"category":"data","importPath":"@/modules/common/components/Tree"}',
    1,
    10,
    '数据组件：基础树（TreeProps 默认）；接口由引用方 overrides 提供'
),
(
    'list',
    'list',
    '列表',
    'list',
    '{"selectMode":"single","showSearch":true,"emptyText":"暂无数据","searchPlaceholder":"搜索","idField":"id","nameField":"name","codeField":"code","autoLoad":true}',
    NULL,
    NULL,
    '{"category":"data","importPath":"@/modules/common/components/List"}',
    1,
    20,
    '数据组件：基础列表（ListProps 默认）；接口由引用方 overrides 提供'
),
(
    'button',
    'button',
    '按钮',
    'button',
    '{"variant":"default","size":"default","children":"按钮","disabled":false}',
    NULL,
    NULL,
    '{"category":"primitive","importPath":"@/components/ui/button","propSchema":{"variant":{"type":"enum","options":["default","outline","secondary","ghost","destructive","link"]},"size":{"type":"enum","options":["default","xs","sm","lg","icon"]},"children":{"type":"string","label":"文本"},"disabled":{"type":"boolean"}}}',
    1,
    100,
    'shadcn Button'
),
(
    'input',
    'input',
    '输入框',
    'input',
    '{"type":"text","placeholder":"请输入","disabled":false}',
    NULL,
    NULL,
    '{"category":"primitive","importPath":"@/components/ui/input","propSchema":{"type":{"type":"enum","options":["text","password","email","number"]},"placeholder":{"type":"string"},"disabled":{"type":"boolean"}}}',
    1,
    110,
    'shadcn Input'
),
(
    'label',
    'label',
    '标签',
    'label',
    '{"children":"标签"}',
    NULL,
    NULL,
    '{"category":"primitive","importPath":"@/components/ui/label","propSchema":{"children":{"type":"string","label":"文本"}}}',
    1,
    120,
    'shadcn Label'
),
(
    'card',
    'card',
    '卡片',
    'card',
    '{"size":"default","title":"卡片标题","description":"","content":""}',
    NULL,
    NULL,
    '{"category":"layout","importPath":"@/components/ui/card","propSchema":{"size":{"type":"enum","options":["default","sm"]},"title":{"type":"string"},"description":{"type":"string"},"content":{"type":"string","label":"正文"}}}',
    1,
    130,
    'shadcn Card（title / description / content 由 props 驱动）'
),
(
    'separator',
    'separator',
    '分隔线',
    'separator',
    '{"orientation":"horizontal"}',
    NULL,
    NULL,
    '{"category":"layout","importPath":"@/components/ui/separator","propSchema":{"orientation":{"type":"enum","options":["horizontal","vertical"]}}}',
    1,
    140,
    'shadcn Separator'
),
(
    'badge',
    'badge',
    '徽章',
    'badge',
    '{"variant":"default","children":"Badge"}',
    NULL,
    NULL,
    '{"category":"primitive","importPath":"@/components/ui/badge","propSchema":{"variant":{"type":"enum","options":["default","secondary","destructive","outline","ghost","link"]},"children":{"type":"string","label":"文本"}}}',
    1,
    150,
    'shadcn Badge'
),
(
    'tabs',
    'tabs',
    '标签页',
    'tabs',
    '{"defaultValue":"tab-1","tabs":[{"value":"tab-1","label":"Tab 1","content":"内容 1"},{"value":"tab-2","label":"Tab 2","content":"内容 2"}]}',
    NULL,
    NULL,
    '{"category":"layout","importPath":"@/components/ui/tabs","propSchema":{"defaultValue":{"type":"string"},"tabs":{"type":"array","label":"页签列表"}}}',
    1,
    160,
    'shadcn Tabs（tabs[] 配置页签）'
)
ON CONFLICT (key) DO UPDATE SET
    type         = EXCLUDED.type,
    name         = EXCLUDED.name,
    icon         = EXCLUDED.icon,
    props        = EXCLUDED.props,
    data_config  = EXCLUDED.data_config,
    api_config   = EXCLUDED.api_config,
    ui_config    = EXCLUDED.ui_config,
    status       = EXCLUDED.status,
    sort         = EXCLUDED.sort,
    description  = EXCLUDED.description,
    updater      = 'seed',
    update_time  = CURRENT_TIMESTAMP,
    deleted      = FALSE;
