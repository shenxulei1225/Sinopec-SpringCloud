SET search_path TO platformresource;

-- Props 模板种子（propsId 与前端 fallback 对齐）
-- 治理：data_source_key 列与 props_json.dataSourceKey 必须一致；dataSource 对象为 HTTP 绑定
-- 演示模板 1001/1002/2001；system 业务模板 3001+ 建议启动后调用 POST .../seed/system-templates 由契约生成

INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source_key,
    template_id, schema_version, props_json, props_override, name, status, sort, description
)
SELECT
    1001,
    TRUE,
    c.id,
    c.key,
    'system:dept',
    NULL,
    'list@1',
    $props$
{
  "dataSourceKey": "system:dept",
  "title": {"title": "部门列表", "showTitle": true},
  "dataSource": {
    "dataSourceEndpoint": {"url": "/system/dept/list", "method": "GET"},
    "createEndpoint": {
      "url": "/system/dept/create",
      "method": "POST",
      "paramStyle": "json-body",
      "requestFields": [
        {"fieldKey": "name", "label": "部门名称", "control": "input", "required": true},
        {"fieldKey": "parentId", "label": "上级部门", "control": "ref-picker", "refTarget": {"queryContractKey": "system:dept", "valueField": "id", "labelField": "name"}},
        {"fieldKey": "sort", "label": "显示顺序", "control": "input", "required": true},
        {"fieldKey": "status", "label": "状态", "control": "dict", "dictType": "common_status"}
      ]
    },
    "updateEndpoint": {
      "url": "/system/dept/update",
      "method": "PUT",
      "paramStyle": "json-body",
      "requestFields": [
        {"fieldKey": "id", "label": "编号", "control": "input", "required": true, "readOnly": true},
        {"fieldKey": "name", "label": "部门名称", "control": "input", "required": true},
        {"fieldKey": "parentId", "label": "上级部门", "control": "ref-picker", "refTarget": {"queryContractKey": "system:dept", "valueField": "id", "labelField": "name"}},
        {"fieldKey": "sort", "label": "显示顺序", "control": "input", "required": true},
        {"fieldKey": "status", "label": "状态", "control": "dict", "dictType": "common_status"}
      ]
    },
    "deleteEndpoint": {
      "url": "/system/dept/delete",
      "method": "DELETE",
      "paramStyle": "query-param",
      "requestFields": [{"fieldKey": "id", "label": "编号", "control": "input", "required": true}]
    }
  },
  "selectMode": "single",
  "search": {"searchPlaceholder": "搜索部门…", "showSearch": true, "searchScopeSelectedOptions": []},
  "toolbar": true,
  "filter": {"externalFilterEnabled": false, "filterSelectedOptions": []},
  "emptyText": "暂无数据",
  "displayContent": ["name", "id", "status"],
  "selectedPanel": true,
  "pagination": false
}
$props$,
    NULL,
    '部门列表（完整）',
    1,
    10,
    'List 演示模板：system:dept，完整功能块'
FROM pr_component c
WHERE c.key = 'list' AND c.deleted = FALSE
ON CONFLICT (id) DO UPDATE SET
    is_template = EXCLUDED.is_template,
    component_id = EXCLUDED.component_id,
    component_code = EXCLUDED.component_code,
    data_source_key = EXCLUDED.data_source_key,
    schema_version = EXCLUDED.schema_version,
    props_json = EXCLUDED.props_json,
    props_override = NULL,
    name = EXCLUDED.name,
    status = EXCLUDED.status,
    sort = EXCLUDED.sort,
    description = EXCLUDED.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP,
    deleted = FALSE;

INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source_key,
    template_id, schema_version, props_json, props_override, name, status, sort, description
)
SELECT
    1002,
    TRUE,
    c.id,
    c.key,
    'system:dept',
    NULL,
    'list@1',
    $props$
{
  "dataSourceKey": "system:dept",
  "title": {"title": "部门列表（精简）", "showTitle": true},
  "dataSource": {
    "dataSourceEndpoint": {"url": "/system/dept/list", "method": "GET"},
    "createEndpoint": {
      "url": "/system/dept/create",
      "method": "POST",
      "paramStyle": "json-body",
      "requestFields": [
        {"fieldKey": "name", "label": "部门名称", "control": "input", "required": true},
        {"fieldKey": "parentId", "label": "上级部门", "control": "ref-picker", "refTarget": {"queryContractKey": "system:dept", "valueField": "id", "labelField": "name"}},
        {"fieldKey": "sort", "label": "显示顺序", "control": "input", "required": true},
        {"fieldKey": "status", "label": "状态", "control": "dict", "dictType": "common_status"}
      ]
    },
    "updateEndpoint": {
      "url": "/system/dept/update",
      "method": "PUT",
      "paramStyle": "json-body",
      "requestFields": [
        {"fieldKey": "id", "label": "编号", "control": "input", "required": true, "readOnly": true},
        {"fieldKey": "name", "label": "部门名称", "control": "input", "required": true},
        {"fieldKey": "parentId", "label": "上级部门", "control": "ref-picker", "refTarget": {"queryContractKey": "system:dept", "valueField": "id", "labelField": "name"}},
        {"fieldKey": "sort", "label": "显示顺序", "control": "input", "required": true},
        {"fieldKey": "status", "label": "状态", "control": "dict", "dictType": "common_status"}
      ]
    },
    "deleteEndpoint": {
      "url": "/system/dept/delete",
      "method": "DELETE",
      "paramStyle": "query-param",
      "requestFields": [{"fieldKey": "id", "label": "编号", "control": "input", "required": true}]
    }
  },
  "selectMode": "single",
  "search": {"searchPlaceholder": "搜索部门…", "showSearch": false, "searchScopeSelectedOptions": []},
  "toolbar": false,
  "filter": {"externalFilterEnabled": false, "filterSelectedOptions": []},
  "emptyText": "暂无数据",
  "displayContent": ["name", "status"],
  "selectedPanel": false,
  "pagination": false
}
$props$,
    NULL,
    '部门列表（精简）',
    1,
    20,
    'List 演示模板：system:dept，精简功能块'
FROM pr_component c
WHERE c.key = 'list' AND c.deleted = FALSE
ON CONFLICT (id) DO UPDATE SET
    is_template = EXCLUDED.is_template,
    component_id = EXCLUDED.component_id,
    component_code = EXCLUDED.component_code,
    data_source_key = EXCLUDED.data_source_key,
    schema_version = EXCLUDED.schema_version,
    props_json = EXCLUDED.props_json,
    props_override = NULL,
    name = EXCLUDED.name,
    status = EXCLUDED.status,
    sort = EXCLUDED.sort,
    description = EXCLUDED.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP,
    deleted = FALSE;

INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source_key,
    template_id, schema_version, props_json, props_override, name, status, sort, description
)
SELECT
    2001,
    TRUE,
    c.id,
    c.key,
    'system:dept',
    NULL,
    'tree@1',
    $props$
{
  "dataSourceKey": "system:dept",
  "selectMode": "single",
  "defaultExpandAll": true,
  "showSearch": true,
  "showToolbar": false,
  "draggable": false,
  "emptyText": "暂无数据",
  "searchPlaceholder": "搜索部门…",
  "autoLoad": true,
  "dataSource": {
    "dataSourceEndpoint": {"url": "/system/dept/list", "method": "GET"}
  }
}
$props$,
    NULL,
    '部门树（默认）',
    1,
    10,
    'Tree 演示模板：system:dept'
FROM pr_component c
WHERE c.key = 'tree' AND c.deleted = FALSE
ON CONFLICT (id) DO UPDATE SET
    is_template = EXCLUDED.is_template,
    component_id = EXCLUDED.component_id,
    component_code = EXCLUDED.component_code,
    data_source_key = EXCLUDED.data_source_key,
    schema_version = EXCLUDED.schema_version,
    props_json = EXCLUDED.props_json,
    props_override = NULL,
    name = EXCLUDED.name,
    status = EXCLUDED.status,
    sort = EXCLUDED.sort,
    description = EXCLUDED.description,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP,
    deleted = FALSE;

SELECT setval(
    pg_get_serial_sequence('pr_component_props', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM pr_component_props)
);
