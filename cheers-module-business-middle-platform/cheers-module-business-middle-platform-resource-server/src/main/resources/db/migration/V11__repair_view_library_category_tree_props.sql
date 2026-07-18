SET search_path TO platformresource;

-- 修复被其它业务 enrich 污染的视图库分类树模板（propsId=2002）
UPDATE pr_component_props
SET
    data_source = NULL,
    schema_version = 'tree@1',
    props = '{"selectMode":"single","search":{"showSearch":true,"searchPlaceholder":"搜索分类","searchScopeSelectedOptions":[]},"toolbar":true,"draggable":true,"defaultExpandAll":true,"autoLoad":true,"emptyText":"暂无分类","displayContent":["name"],"categoryTypeCode":"view","treePreset":"view-library-category","dataSource":{"dataSourceEndpoint":{"url":"/dynamicbusiness/category/tree","method":"GET","params":{"categoryTypeCode":"view","status":1}}},"selectionContext":{"categoryTypeCode":"view","treeReadKind":"CATEGORY_PLAIN"},"apiConfig":{"dataEndpoint":{"url":"/dynamicbusiness/category/tree","method":"GET","params":{"categoryTypeCode":"view","status":1}},"createEndpoint":{"url":"/dynamicbusiness/category/create","method":"POST","params":{"categoryTypeCode":"view","status":1}},"updateEndpoint":{"url":"/dynamicbusiness/category/update","method":"PUT","params":{"categoryTypeCode":"view","status":1}},"deleteEndpoint":{"url":"/dynamicbusiness/category/delete","method":"DELETE","params":{"categoryTypeCode":"view"}},"dragEndpoint":{"url":"/dynamicbusiness/category/drag","method":"POST","params":{"categoryTypeCode":"view"}}}}',
    props_override = NULL,
    name = '视图库分类树',
    description = 'Tree 模板：视图库 categoryTypeCode=view，含搜索与 CRUD toolbar',
    updater = 'repair',
    update_time = CURRENT_TIMESTAMP,
    deleted = FALSE
WHERE id = 2002;
