-- =====================================================
-- 多视图架构重设计 - 更新页面配置
-- 
-- 功能说明：
-- 1. 更新设备管理页面配置（menuId=5040）,添加多视图支持
-- 2. 验证并更新区域管理页面配置（menuId=5076）,确保模式C配置正确
-- 
-- 需求：FR-003 多视图支持, FR-005 模式C API修复
-- =====================================================

-- =====================================================
-- 任务 10.1: 更新设备管理页面配置（menuId: 5040）
-- 添加多视图配置（分类视图 + 区域视图）
-- 设置默认视图为分类视图
-- =====================================================

-- 更新 menuId=5040 的配置,添加多视图支持
-- 注意：5040 是"区域管理"页面,根据任务要求为其添加多视图配置
UPDATE system_page_config 
SET config = '{
    "pattern": "C",
    "leftTreeType": "category",
    "rightContentType": "entity-list-with-detail",
    "dragBehavior": "entity-category",
    "addChildBehavior": "entity-category",
    "showModelManage": true,
    "showDeviceTypeSelect": false,
    "categoryDrawerEntityMode": true,
    "leftTreeBusinessType": "region",
    "views": [
        {
            "id": "category",
            "name": "分类视图",
            "leftTreeBusinessType": null,
            "pattern": "B"
        },
        {
            "id": "region",
            "name": "区域视图",
            "leftTreeBusinessType": "region",
            "pattern": "C",
            "templateId": "region-view"
        }
    ],
    "defaultView": "category"
}'::jsonb,
    update_time = NOW(),
    updater = 'migration'
WHERE menu_id = 5040
  AND deleted = false;

-- 如果 menuId=5040 的配置不存在,则插入新配置
INSERT INTO system_page_config (
    menu_id,
    page_type,
    config,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id
) 
SELECT 
    5040,
    'data_management',
    '{
        "pattern": "C",
        "leftTreeType": "category",
        "rightContentType": "entity-list-with-detail",
        "dragBehavior": "entity-category",
        "addChildBehavior": "entity-category",
        "showModelManage": true,
        "showDeviceTypeSelect": false,
        "categoryDrawerEntityMode": true,
        "leftTreeBusinessType": "region",
        "views": [
            {
                "id": "category",
                "name": "分类视图",
                "leftTreeBusinessType": null,
                "pattern": "B"
            },
            {
                "id": "region",
                "name": "区域视图",
                "leftTreeBusinessType": "region",
                "pattern": "C",
                "templateId": "region-view"
            }
        ],
        "defaultView": "category"
    }'::jsonb,
    'migration',
    NOW(),
    'migration',
    NOW(),
    false,
    1
WHERE NOT EXISTS (
    SELECT 1 FROM system_page_config WHERE menu_id = 5040 AND deleted = false
);

-- =====================================================
-- 任务 10.2: 验证区域管理页面配置（menuId: 5076）
-- 确认模式C配置正确
-- 确认 leftTreeBusinessType 设置为 'region'
-- =====================================================

-- 更新 menuId=5076 的配置,确保使用新字段名 leftTreeBusinessType
UPDATE system_page_config 
SET config = '{
    "pattern": "C",
    "leftTreeType": "category",
    "rightContentType": "entity-list-with-detail",
    "dragBehavior": "entity-category",
    "addChildBehavior": "entity-category",
    "showModelManage": true,
    "showDeviceTypeSelect": false,
    "categoryDrawerEntityMode": true,
    "leftTreeBusinessType": "region",
    "views": [
        {
            "id": "default",
            "name": "区域视图",
            "leftTreeBusinessType": "region",
            "pattern": "C"
        }
    ],
    "defaultView": "default"
}'::jsonb,
    update_time = NOW(),
    updater = 'migration'
WHERE menu_id = 5076
  AND deleted = false;

-- 如果 menuId=5076 的配置不存在,则插入新配置
INSERT INTO system_page_config (
    menu_id,
    page_type,
    config,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id
) 
SELECT 
    5076,
    'data_management',
    '{
        "pattern": "C",
        "leftTreeType": "category",
        "rightContentType": "entity-list-with-detail",
        "dragBehavior": "entity-category",
        "addChildBehavior": "entity-category",
        "showModelManage": true,
        "showDeviceTypeSelect": false,
        "categoryDrawerEntityMode": true,
        "leftTreeBusinessType": "region",
        "views": [
            {
                "id": "default",
                "name": "区域视图",
                "leftTreeBusinessType": "region",
                "pattern": "C"
            }
        ],
        "defaultView": "default"
    }'::jsonb,
    'migration',
    NOW(),
    'migration',
    NOW(),
    false,
    1
WHERE NOT EXISTS (
    SELECT 1 FROM system_page_config WHERE menu_id = 5076 AND deleted = false
);

-- =====================================================
-- 迁移说明
-- =====================================================
-- 
-- menuId=5040 (区域管理页面) 配置说明：
-- - 添加了多视图支持,包含"分类视图"和"区域视图"两个Tab
-- - 默认视图为"分类视图"（pattern: B）
-- - 区域视图使用模式C,leftTreeBusinessType 设置为 'region'
-- 
-- menuId=5076 (动态业务区域管理页面) 配置说明：
-- - 使用模式C（分类即实体）
-- - leftTreeBusinessType 设置为 'region'
-- - 只有一个默认视图,不显示Tab切换
-- 
-- 配置字段说明：
-- - leftTreeBusinessType: 左侧树的业务类型（替代旧的 entitySourceBusinessType）
-- - views: 视图配置数组,支持多视图切换
-- - defaultView: 默认激活的视图ID
-- 
-- =====================================================

