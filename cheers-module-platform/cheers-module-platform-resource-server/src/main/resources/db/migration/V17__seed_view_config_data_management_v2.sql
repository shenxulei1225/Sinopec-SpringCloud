SET search_path TO platformresource;

-- 数据管理试验页（/dynamic-business/entity-types-v2）绑定的 view-config 空壳种子。
-- 仅当 view_code 尚未存在时插入，不覆盖已有工作台保存结果。
-- config_json 与 ecs-react createLayoutShellWidgetTree('data-management-three-column') 对齐；
-- 变更布局模板后请运行：node ecs-react/scripts/generate-data-management-v2-view-config-seed.mjs

INSERT INTO pr_view_config (
    is_template,
    template_id,
    view_type,
    view_code,
    name,
    description,
    config_json,
    config_override,
    status,
    sort,
    creator,
    updater,
    deleted,
    tenant_id
)
SELECT
    TRUE,
    NULL,
    'CanvasView',
    'dynamic:data-management-v2',
    '数据管理试验（三栏空壳）',
    'Flyway V17：布局模板 data-management-three-column 的空槽壳；槽位由试验页步骤 3 或视图工作台填入。',
    $json${"layoutMode":"canvas","widgetTree":{"rootWidgetId":"panel-outline-root"},"widgets":{"panel-outline-root":{"id":"panel-outline-root","name":"Outline Root","widgetClass":"OutlineRoot","widgetKind":"panel","props":{"editor":{"gridSize":20,"snapToGrid":true,"showGrid":true,"useScrollWheelZoom":true}},"slots":[{"slotId":"panel-outline-root-slot-1","slotClass":"OutlineRootSlot","childWidgetId":"panel-shell-border-v17","props":{"layoutData":{"offsets":{"left":24,"top":24,"right":24,"bottom":24},"anchors":{"minimum":{"x":0,"y":0},"maximum":{"x":1,"y":1}},"alignment":{"x":0,"y":0}},"autoSize":false,"zOrder":1}}]},"panel-shell-border-v17":{"id":"panel-shell-border-v17","name":"数据管理布局","widgetClass":"Border","widgetKind":"panel","props":{"padding":16,"layoutPresetId":"data-management-three-column"},"slots":[{"slotId":"panel-shell-border-v17-slot-1","slotClass":"BorderSlot","childWidgetId":"panel-layout-HorizontalBox-v17","props":{}}]},"panel-layout-HorizontalBox-v17":{"id":"panel-layout-HorizontalBox-v17","name":"数据管理三栏","widgetClass":"HorizontalBox","widgetKind":"panel","props":{"gap":12,"layoutPresetId":"data-management-three-column"},"slots":[{"slotId":"panel-layout-HorizontalBox-v17-slot-1","slotClass":"HorizontalBoxSlot","childWidgetId":"__empty-slot-panel-layout-HorizontalBox-v17-category__","props":{"size":{"rule":"Automatic","value":0},"padding":{"right":12}}},{"slotId":"panel-layout-HorizontalBox-v17-slot-2","slotClass":"HorizontalBoxSlot","childWidgetId":"__empty-slot-panel-layout-HorizontalBox-v17-model__","props":{"size":{"rule":"Automatic","value":0},"padding":{"right":12}}},{"slotId":"panel-layout-HorizontalBox-v17-slot-3","slotClass":"HorizontalBoxSlot","childWidgetId":"__empty-slot-panel-layout-HorizontalBox-v17-entity__","props":{"size":{"rule":"Fill","value":1}}}]}},"components":{},"canvas":{"width":1920,"height":1080,"nodes":[{"id":"panel-outline-root-slot-1","nodeKind":"component","label":"数据管理布局","x":24,"y":24,"width":912,"height":592,"zIndex":1}],"scaleMode":"scaleToFit","dpiScale":1,"gridSize":20,"snapToGrid":true,"showGrid":true,"lockAspectRatio":false}}$json$,
    NULL,
    1,
    100,
    'seed',
    'seed',
    FALSE,
    1
WHERE NOT EXISTS (
    SELECT 1
    FROM pr_view_config
    WHERE view_code = 'dynamic:data-management-v2'
      AND deleted = FALSE
);
