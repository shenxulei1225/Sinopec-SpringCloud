SET search_path TO platformresource;

-- 视图库分类树：data_source 表达分类绑定，props 仅保留 UI 偏好（去除 treePreset POC）
UPDATE pr_component_props
SET
    data_source = '{"businessCategory":"category","businessTypeCode":"view","dataKind":"entity"}'::jsonb,
    props = '{"selectMode":"single","search":{"showSearch":true,"searchPlaceholder":"搜索分类","searchScopeSelectedOptions":[]},"toolbar":true,"draggable":true,"defaultExpandAll":true,"autoLoad":true,"emptyText":"暂无分类","displayContent":["name"]}'::jsonb,
    name = '视图库分类树',
    description = 'Tree 模板：分类 data_source(view) + UI 偏好',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE id = 2002 AND component_code = 'tree' AND deleted = FALSE;

-- 树（默认）设施分类：同步为 category data_source（若存在 propsId=2001）
UPDATE pr_component_props
SET
    data_source = '{"businessCategory":"category","businessTypeCode":"facility","dataKind":"entity"}'::jsonb,
    props = '{"selectMode":"single","search":{"showSearch":true,"searchPlaceholder":"搜索分类","searchScopeSelectedOptions":[]},"toolbar":false,"draggable":true,"defaultExpandAll":true,"autoLoad":true,"emptyText":"暂无分类","displayContent":["name"]}'::jsonb,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE id = 2001 AND component_code = 'tree' AND deleted = FALSE;
