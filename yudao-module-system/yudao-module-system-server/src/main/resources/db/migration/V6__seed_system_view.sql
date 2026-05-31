-- 视图种子：设施分类模板视图
-- 依赖 V4__seed_system_component.sql 中的 tree-category-facility 组件

INSERT INTO system_view (
    key,
    label,
    icon,
    composition,
    is_template,
    status,
    sort,
    description,
    creator,
    updater,
    deleted
) VALUES (
    'facility-category-view',
    '设施分类',
    'folder-tree',
    '{
      "layoutSchema": {
        "type": "container",
        "direction": "vertical",
        "props": { "gap": 8 },
        "children": [
          {
            "type": "slot",
            "itemId": "facility-tree",
            "props": { "title": "设施分类", "height": 360 }
          }
        ]
      },
      "items": [
        {
          "id": "facility-tree",
          "kind": "component",
          "componentId": "tree-category-facility",
          "overrides": {
            "uiConfig": {
              "categoryTypeCode": "FACILITY"
            },
            "props": {
              "showSearch": true,
              "editable": true,
              "draggable": true,
              "defaultExpandAll": true
            }
          }
        }
      ]
    }',
    TRUE,
    1,
    100,
    '设施分类模板视图，可在 Library 中复用',
    'system',
    'system',
    FALSE
);