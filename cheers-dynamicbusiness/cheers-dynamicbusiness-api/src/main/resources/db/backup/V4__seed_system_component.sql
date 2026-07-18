-- 组件库种子：仅基础 UI 组件（tree / list），不含业务接口与业务字段
-- 业务 dataConfig / apiConfig / uiConfig 由视图或槽位 overrides 注入
-- 支持重复执行

UPDATE system_component
SET deleted      = TRUE,
    updater      = 'seed',
    update_time  = CURRENT_TIMESTAMP
WHERE key NOT IN ('tree', 'list')
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
    NULL,
    1,
    10,
    '基础树组件（TreeProps 默认值）；接口与业务参数由引用方 overrides 提供'
),
(
    'list',
    'list',
    '列表',
    'list',
    '{"selectMode":"single","showSearch":true,"emptyText":"暂无数据","searchPlaceholder":"搜索","idField":"id","nameField":"name","codeField":"code","autoLoad":true}',
    NULL,
    NULL,
    NULL,
    1,
    20,
    '基础列表组件（ListProps 默认值）；接口与业务参数由引用方 overrides 提供'
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
