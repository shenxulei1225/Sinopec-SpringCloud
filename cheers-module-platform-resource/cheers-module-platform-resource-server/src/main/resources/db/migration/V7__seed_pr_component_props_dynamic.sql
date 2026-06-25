SET search_path TO platformresource;

-- Mac 开发库导出的组件 props（含 data_source 绑定动态/系统业务）
-- 生成：cheers-module-platform-resource/scripts/generate-seed-component-props.py
-- 覆盖 V4 演示模板中已绑定业务数据来源的行（如 equipment + entity）

-- propsId=1001 list 列表（完整）
INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source,
    template_id, schema_version, props, props_override, name, status, sort, description, tenant_id
)
SELECT
    1001, TRUE, c.id, c.component_code, $txt${"businessCategory":"dynamic","businessTypeCode":"equipment","dataKind":"entity"}$txt$,
    NULL, $txt$list@1$txt$, $txt${"title":{"title":"列表","showTitle":true,"titleOnOwnRow":true},"selectMode":"single","search":{"searchPlaceholder":"搜索…","showSearch":true,"searchScopeSelectedOptions":["id","name","code","status"],"searchScopeLabelByKey":{"F-559d5ad091e644a798aa9efef8d06c9c":"测量范围","F-4651095e38ca4f1cbe8cb32beb8c2142":"测量精度","F-66264ae122c54504a8fa0679079772fc":"分辨率","F-06cf3dcb998f4c4392cb6a5a67fa1289":"镜头类型","F-94807db40a4b4e59a482888fca4b40df":"夜视功能","id":"ID","F-dba78a08bcb04c3ca6a6a07c62dfd671":"功率","F-2407b65c556643adb72f94c0efbbba47":"容量","F-51b7d63aa97942439611cff6370fe65e":"管径","F-4d6aa265bbff4ed69227aa623f9dae8f":"电压等级","F-59e5799b2eaa4b0aa9c164f8d440409b":"检测范围","F-18af5bff1c894e2da694e5fa35d8c7af":"设备编号","name":"名称","F-e0c3c64565e748d3b5a811014384e1e6":"电压","F-57d6bdf06b284821bd003d0fdba16157":"材质","F-43ba10972c084c44a24a22ffb0b2185f":"电流","F-3d7fedf4c352424abeb1b11805dfd331":"精度","F-e18365a3a3ec4485ab699b46d6f820c2":"规格型号","code":"编码","F-1ee037ca81bb475f94b3526134ea85bb":"工作温度","F-49523f29afa94650ac591f607043dd65":"压力等级","F-b42b6e004eb944e993e00f5fa734a37a":"响应时间","F-5b561c0ac2fe41d59a7b1831a93d1637":"品牌","F-d55683af52444a7587242fbb3542a932":"工作湿度","F-4b75b7f9889d4f1d90afd19c03c934af":"运行状态","F-58b6dbbc32f1438b96188087aa68ae4e":"安装日期","F-672e3aca3c9c46cb8f0c8fe85217b708":"供电方式","F-c140d56fbb634e88a6a2feeb63f3b764":"维保到期","F-f2bbdc83208843e28026f785c7195ce5":"通信方式","F-cc746ce0224145af88d5428d0b03213a":"所属区域","F-984f2b3cfb6b4931a4dbc4506c116522":"IP地址","status":"状态"}},"toolbar":true,"filter":{"externalFilterEnabled":false,"filterSelectedOptions":[]},"emptyText":"暂无数据","displayContent":["id","name","code","status"],"selectedPanel":true,"pagination":false,"draggable":false,"autoCrud":true,"selectedPanelDetail":false}$txt$, NULL, $txt$列表（完整）$txt$, 1, 10, $txt$List 演示模板：dataSource 由用户在配置器选择$txt$, 1
FROM pr_component c WHERE c.component_code = $txt$list$txt$ AND c.deleted = FALSE
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

-- propsId=1002 list 列表（精简）
INSERT INTO pr_component_props (
    id, is_template, component_id, component_code, data_source,
    template_id, schema_version, props, props_override, name, status, sort, description, tenant_id
)
SELECT
    1002, TRUE, c.id, c.component_code, $txt${"businessCategory":"dynamic","businessTypeCode":"equipment","dataKind":"entity"}$txt$,
    NULL, $txt$list@1$txt$, $txt${"title":{"title":"","showTitle":true,"titleOnOwnRow":false},"selectMode":"single","search":{"searchPlaceholder":"搜索…","showSearch":false,"searchScopeSelectedOptions":["F-559d5ad091e644a798aa9efef8d06c9c","F-4651095e38ca4f1cbe8cb32beb8c2142","F-66264ae122c54504a8fa0679079772fc","F-06cf3dcb998f4c4392cb6a5a67fa1289","F-94807db40a4b4e59a482888fca4b40df","id","F-dba78a08bcb04c3ca6a6a07c62dfd671","F-2407b65c556643adb72f94c0efbbba47","F-51b7d63aa97942439611cff6370fe65e","F-4d6aa265bbff4ed69227aa623f9dae8f","F-59e5799b2eaa4b0aa9c164f8d440409b","F-18af5bff1c894e2da694e5fa35d8c7af","name","F-e0c3c64565e748d3b5a811014384e1e6","F-57d6bdf06b284821bd003d0fdba16157","F-43ba10972c084c44a24a22ffb0b2185f","F-3d7fedf4c352424abeb1b11805dfd331","F-e18365a3a3ec4485ab699b46d6f820c2","code","F-1ee037ca81bb475f94b3526134ea85bb","F-49523f29afa94650ac591f607043dd65","F-b42b6e004eb944e993e00f5fa734a37a","F-5b561c0ac2fe41d59a7b1831a93d1637","F-d55683af52444a7587242fbb3542a932","F-4b75b7f9889d4f1d90afd19c03c934af","F-58b6dbbc32f1438b96188087aa68ae4e","F-672e3aca3c9c46cb8f0c8fe85217b708","F-c140d56fbb634e88a6a2feeb63f3b764","F-f2bbdc83208843e28026f785c7195ce5","F-cc746ce0224145af88d5428d0b03213a","F-984f2b3cfb6b4931a4dbc4506c116522"],"searchScopeLabelByKey":{"F-559d5ad091e644a798aa9efef8d06c9c":"测量范围","F-4651095e38ca4f1cbe8cb32beb8c2142":"测量精度","F-66264ae122c54504a8fa0679079772fc":"分辨率","F-06cf3dcb998f4c4392cb6a5a67fa1289":"镜头类型","F-94807db40a4b4e59a482888fca4b40df":"夜视功能","id":"ID","F-dba78a08bcb04c3ca6a6a07c62dfd671":"功率","F-2407b65c556643adb72f94c0efbbba47":"容量","F-51b7d63aa97942439611cff6370fe65e":"管径","F-4d6aa265bbff4ed69227aa623f9dae8f":"电压等级","F-59e5799b2eaa4b0aa9c164f8d440409b":"检测范围","F-18af5bff1c894e2da694e5fa35d8c7af":"设备编号","name":"名称","F-e0c3c64565e748d3b5a811014384e1e6":"电压","F-57d6bdf06b284821bd003d0fdba16157":"材质","F-43ba10972c084c44a24a22ffb0b2185f":"电流","F-3d7fedf4c352424abeb1b11805dfd331":"精度","F-e18365a3a3ec4485ab699b46d6f820c2":"规格型号","code":"编码","F-1ee037ca81bb475f94b3526134ea85bb":"工作温度","F-49523f29afa94650ac591f607043dd65":"压力等级","F-b42b6e004eb944e993e00f5fa734a37a":"响应时间","F-5b561c0ac2fe41d59a7b1831a93d1637":"品牌","F-d55683af52444a7587242fbb3542a932":"工作湿度","F-4b75b7f9889d4f1d90afd19c03c934af":"运行状态","F-58b6dbbc32f1438b96188087aa68ae4e":"安装日期","F-672e3aca3c9c46cb8f0c8fe85217b708":"供电方式","F-c140d56fbb634e88a6a2feeb63f3b764":"维保到期","F-f2bbdc83208843e28026f785c7195ce5":"通信方式","F-cc746ce0224145af88d5428d0b03213a":"所属区域","F-984f2b3cfb6b4931a4dbc4506c116522":"IP地址"}},"toolbar":false,"filter":{"externalFilterEnabled":false,"filterSelectedOptions":[]},"emptyText":"暂无数据","displayContent":["F-559d5ad091e644a798aa9efef8d06c9c","F-4651095e38ca4f1cbe8cb32beb8c2142","F-66264ae122c54504a8fa0679079772fc","F-06cf3dcb998f4c4392cb6a5a67fa1289","F-94807db40a4b4e59a482888fca4b40df","id","F-dba78a08bcb04c3ca6a6a07c62dfd671","F-2407b65c556643adb72f94c0efbbba47","F-51b7d63aa97942439611cff6370fe65e","F-4d6aa265bbff4ed69227aa623f9dae8f","F-59e5799b2eaa4b0aa9c164f8d440409b","F-18af5bff1c894e2da694e5fa35d8c7af","name","F-e0c3c64565e748d3b5a811014384e1e6","F-57d6bdf06b284821bd003d0fdba16157","F-43ba10972c084c44a24a22ffb0b2185f","F-3d7fedf4c352424abeb1b11805dfd331","F-e18365a3a3ec4485ab699b46d6f820c2","code","F-1ee037ca81bb475f94b3526134ea85bb","F-49523f29afa94650ac591f607043dd65","F-b42b6e004eb944e993e00f5fa734a37a","F-5b561c0ac2fe41d59a7b1831a93d1637","F-d55683af52444a7587242fbb3542a932","F-4b75b7f9889d4f1d90afd19c03c934af","F-58b6dbbc32f1438b96188087aa68ae4e","F-672e3aca3c9c46cb8f0c8fe85217b708","F-c140d56fbb634e88a6a2feeb63f3b764","F-f2bbdc83208843e28026f785c7195ce5","F-cc746ce0224145af88d5428d0b03213a","F-984f2b3cfb6b4931a4dbc4506c116522"],"selectedPanel":true,"pagination":true,"draggable":false,"autoCrud":true,"selectedPanelDetail":false}$txt$, NULL, $txt$列表（精简）$txt$, 1, 20, $txt$List 演示模板：精简功能块$txt$, 1
FROM pr_component c WHERE c.component_code = $txt$list$txt$ AND c.deleted = FALSE
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
