-- =====================================================
-- 区域管理页面配置初始化
-- 
-- 为区域管理页面配置 Pattern C 模式
-- Pattern C: 分类详情模式,分类本身即实体,右侧显示关联的设备数据
-- =====================================================

-- 插入区域管理页面配置（menuId=5040 - 旧区域管理）
INSERT INTO dynamic_page_config (
    menu_id,
    page_type,
    config,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id
) VALUES (
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
        "entitySourceBusinessType": "equipment"
    }'::jsonb,
    'system',
    NOW(),
    'system',
    NOW(),
    false,
    1
) ON CONFLICT DO NOTHING;

-- 插入动态业务区域管理页面配置（menuId=5076 - 新动态业务区域管理）
INSERT INTO dynamic_page_config (
    menu_id,
    page_type,
    config,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id
) VALUES (
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
        "entitySourceBusinessType": "equipment"
    }'::jsonb,
    'system',
    NOW(),
    'system',
    NOW(),
    false,
    1
) ON CONFLICT DO NOTHING;

-- 说明：
-- pattern: C - 分类详情模式
-- leftTreeType: category - 左侧只显示分类树（不合并模型）
-- rightContentType: entity-list-with-detail - 右侧双Tab（分类详情 + 关联数据）
-- dragBehavior: entity-category - 拖拽实体到分类
-- addChildBehavior: entity-category - 添加子节点时创建分类实体
-- showModelManage: true - 显示模型管理按钮
-- showDeviceTypeSelect: false - 不显示设备类型选择（区域不需要）
-- categoryDrawerEntityMode: true - 分类抽屉启用实体模式
-- entitySourceBusinessType: equipment - 关联数据Tab显示设备业务类型的实体
