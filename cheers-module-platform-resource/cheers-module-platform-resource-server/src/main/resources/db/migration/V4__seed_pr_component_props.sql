SET search_path TO platformresource;

INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source,
    template_id, schema_version, props, props_override, name, status, sort, description
)
SELECT
    1001, TRUE, c.id, c.component_code, NULL, NULL, 'list@1',
    $props$
{"title":{"title":"列表","showTitle":true},"selectMode":"single","search":{"searchPlaceholder":"搜索…","showSearch":true,"searchScopeSelectedOptions":[]},"toolbar":true,"filter":{"externalFilterEnabled":false,"filterSelectedOptions":[]},"emptyText":"暂无数据","displayContent":["name","id"],"selectedPanel":true,"pagination":false}
$props$,
    NULL, '列表（完整）', 1, 10, 'List 演示模板：dataSource 由用户在配置器选择'
FROM pr_component c WHERE c.component_code = 'list' AND c.deleted = FALSE
ON CONFLICT (id) DO UPDATE SET
    component_code = EXCLUDED.component_code, data_source = EXCLUDED.data_source,
    schema_version = EXCLUDED.schema_version, props = EXCLUDED.props, props_override = NULL,
    name = EXCLUDED.name, status = EXCLUDED.status, sort = EXCLUDED.sort,
    description = EXCLUDED.description, updater = 'seed', update_time = CURRENT_TIMESTAMP, deleted = FALSE;

INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source,
    template_id, schema_version, props, props_override, name, status, sort, description
)
SELECT
    1002, TRUE, c.id, c.component_code, NULL, NULL, 'list@1',
    $props$
{"title":{"title":"列表（精简）","showTitle":true},"selectMode":"single","search":{"searchPlaceholder":"搜索…","showSearch":false,"searchScopeSelectedOptions":[]},"toolbar":false,"filter":{"externalFilterEnabled":false,"filterSelectedOptions":[]},"emptyText":"暂无数据","displayContent":["name"],"selectedPanel":false,"pagination":false}
$props$,
    NULL, '列表（精简）', 1, 20, 'List 演示模板：精简功能块'
FROM pr_component c WHERE c.component_code = 'list' AND c.deleted = FALSE
ON CONFLICT (id) DO UPDATE SET
    component_code = EXCLUDED.component_code, data_source = EXCLUDED.data_source,
    schema_version = EXCLUDED.schema_version, props = EXCLUDED.props, props_override = NULL,
    name = EXCLUDED.name, status = EXCLUDED.status, sort = EXCLUDED.sort,
    description = EXCLUDED.description, updater = 'seed', update_time = CURRENT_TIMESTAMP, deleted = FALSE;

INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source,
    template_id, schema_version, props, props_override, name, status, sort, description
)
SELECT
    2001, TRUE, c.id, c.component_code, NULL, NULL, 'tree@1',
    $props$
{"selectMode":"single","search":{"searchPlaceholder":"搜索…","showSearch":true,"searchScopeSelectedOptions":[]},"toolbar":false,"draggable":false,"defaultExpandAll":true,"showCount":false,"autoLoad":true,"emptyText":"暂无数据","displayContent":[]}
$props$,
    NULL, '树（默认）', 1, 10, 'Tree 演示模板'
FROM pr_component c WHERE c.component_code = 'tree' AND c.deleted = FALSE
ON CONFLICT (id) DO UPDATE SET
    component_code = EXCLUDED.component_code, data_source = EXCLUDED.data_source,
    schema_version = EXCLUDED.schema_version, props = EXCLUDED.props, props_override = NULL,
    name = EXCLUDED.name, status = EXCLUDED.status, sort = EXCLUDED.sort,
    description = EXCLUDED.description, updater = 'seed', update_time = CURRENT_TIMESTAMP, deleted = FALSE;

SELECT setval(pg_get_serial_sequence('pr_component_props', 'id'), (SELECT COALESCE(MAX(id), 1) FROM pr_component_props));
