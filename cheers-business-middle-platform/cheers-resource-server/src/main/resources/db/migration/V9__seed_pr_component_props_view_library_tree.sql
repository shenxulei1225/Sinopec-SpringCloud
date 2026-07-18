SET search_path TO platformresource;

-- 视图库分类树 Tree 模板（与 viewLibraryCategoryTreeConfig 对齐，propsId=2002）
INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source,
    template_id, schema_version, props, props_override, name, status, sort, description, tenant_id
)
SELECT
    2002, TRUE, c.id, c.component_code, NULL,
    NULL, $txt$tree@1$txt$, $txt${"selectMode":"single","search":{"showSearch":true,"searchPlaceholder":"搜索分类","searchScopeSelectedOptions":[]},"toolbar":true,"draggable":true,"defaultExpandAll":true,"autoLoad":true,"emptyText":"暂无分类","displayContent":["name"],"categoryTypeCode":"view","treePreset":"view-library-category","dataSource":{"dataSourceEndpoint":{"url":"/dynamicbusiness/category/tree","method":"GET","params":{"categoryTypeCode":"view","status":1}}},"selectionContext":{"categoryTypeCode":"view","treeReadKind":"CATEGORY_PLAIN"},"apiConfig":{"dataEndpoint":{"url":"/dynamicbusiness/category/tree","method":"GET","params":{"categoryTypeCode":"view","status":1}},"createEndpoint":{"url":"/dynamicbusiness/category/create","method":"POST","params":{"categoryTypeCode":"view","status":1}},"updateEndpoint":{"url":"/dynamicbusiness/category/update","method":"PUT","params":{"categoryTypeCode":"view","status":1}},"deleteEndpoint":{"url":"/dynamicbusiness/category/delete","method":"DELETE","params":{"categoryTypeCode":"view"}},"dragEndpoint":{"url":"/dynamicbusiness/category/drag","method":"POST","params":{"categoryTypeCode":"view"}}}}$txt$, NULL, $txt$视图库分类树$txt$, 1, 20, $txt$Tree 模板：视图库 categoryTypeCode=view，含搜索与 CRUD toolbar$txt$, 1
FROM pr_component c WHERE c.component_code = $txt$tree$txt$ AND c.deleted = FALSE
ON CONFLICT (id) DO UPDATE SET
    is_template = EXCLUDED.is_template,
    component_id = EXCLUDED.component_id,
    component_code = EXCLUDED.component_code,
    data_source = EXCLUDED.data_source,
    template_id = EXCLUDED.template_id,
    schema_version = EXCLUDED.schema_version,
    props = EXCLUDED.props,
    props_override = EXCLUDED.props_override,
    name = EXCLUDED.name,
    status = EXCLUDED.status,
    sort = EXCLUDED.sort,
    description = EXCLUDED.description,
    tenant_id = EXCLUDED.tenant_id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP,
    deleted = FALSE;

SELECT setval(pg_get_serial_sequence('pr_component_props', 'id'), (SELECT COALESCE(MAX(id), 1) FROM pr_component_props));
