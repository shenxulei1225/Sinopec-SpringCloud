-- =====================================================
-- 修复错误的 topLevelCategoryId
-- 版本: V1.0.46
-- 描述: 
--   修复 menu_id = 5078 的页面配置中,视图 'category' 的 topLevelCategoryId
--   从错误的 4784 修正为正确的 4779（"按分类查看"）
-- =====================================================

-- =====================================================
-- 1. 修复指定页面的 topLevelCategoryId
-- =====================================================
DO $$
DECLARE
    page_config_record RECORD;
    views_array JSONB;
    view_item JSONB;
    view_index INT;
    updated_config JSONB;
    fixed_count INT := 0;
BEGIN
    RAISE NOTICE '开始修复错误的 topLevelCategoryId...';
    
    -- 查找 menu_id = 5078 的页面配置
    SELECT 
        id,
        menu_id,
        config,
        tenant_id
    INTO page_config_record
    FROM dynamic_page_config
    WHERE menu_id = 5078
      AND deleted = false
      AND config IS NOT NULL
      AND config != '{}'::jsonb;
    
    IF page_config_record IS NULL THEN
        RAISE NOTICE '未找到 menu_id = 5078 的页面配置';
        RETURN;
    END IF;
    
    RAISE NOTICE '找到页面配置: ID %, menu_id %', 
        page_config_record.id, page_config_record.menu_id;
    
    -- 检查是否有 views 配置
    IF NOT (page_config_record.config ? 'views') THEN
        RAISE NOTICE '页面配置中没有 views 字段';
        RETURN;
    END IF;
    
    views_array := page_config_record.config->'views';
    IF jsonb_typeof(views_array) != 'array' OR jsonb_array_length(views_array) = 0 THEN
        RAISE NOTICE 'views 数组为空或不是数组类型';
        RETURN;
    END IF;
    
    -- 遍历视图配置,查找 id = 'category' 的视图
    updated_config := page_config_record.config;
    view_index := 0;
    
    FOR view_item IN SELECT * FROM jsonb_array_elements(views_array)
    LOOP
        -- 检查是否是目标视图
        IF view_item->>'id' = 'category' THEN
            -- 检查当前的 topLevelCategoryId
            IF (view_item->>'topLevelCategoryId')::bigint = 4784 THEN
                -- 更新 topLevelCategoryId 为 4779
                view_item := view_item || jsonb_build_object(
                    'topLevelCategoryId', 4779
                );
                
                -- 更新 views 数组中的对应视图
                updated_config := jsonb_set(
                    updated_config,
                    ARRAY['views', view_index::text],
                    view_item
                );
                
                fixed_count := fixed_count + 1;
                RAISE NOTICE '修复视图 "category": topLevelCategoryId 从 4784 改为 4779';
            ELSE
                RAISE NOTICE '视图 "category" 的 topLevelCategoryId 已经是 %,无需修复', 
                    view_item->>'topLevelCategoryId';
            END IF;
        END IF;
        
        view_index := view_index + 1;
    END LOOP;
    
    -- 如果有更新,保存页面配置
    IF fixed_count > 0 AND updated_config != page_config_record.config THEN
        UPDATE dynamic_page_config
        SET config = updated_config,
            updater = 'system',
            update_time = CURRENT_TIMESTAMP
        WHERE id = page_config_record.id;
        
        RAISE NOTICE '✓ 已更新页面配置: ID %, menu_id %, 修复了 % 个视图', 
            page_config_record.id, page_config_record.menu_id, fixed_count;
    ELSE
        RAISE NOTICE '无需更新页面配置';
    END IF;
END $$;

-- =====================================================
-- 2. 验证修复结果
-- =====================================================
DO $$
DECLARE
    current_top_level_category_id BIGINT;
BEGIN
    SELECT (view_item->>'topLevelCategoryId')::bigint
    INTO current_top_level_category_id
    FROM dynamic_page_config,
         jsonb_array_elements(config->'views') AS view_item
    WHERE menu_id = 5078
      AND deleted = false
      AND view_item->>'id' = 'category';
    
    IF current_top_level_category_id IS NULL THEN
        RAISE WARNING '⚠ 未找到视图 "category" 的 topLevelCategoryId';
    ELSIF current_top_level_category_id = 4779 THEN
        RAISE NOTICE '✓ 验证通过: topLevelCategoryId = 4779（正确）';
    ELSE
        RAISE WARNING '⚠ topLevelCategoryId = %,期望值为 4779', current_top_level_category_id;
    END IF;
END $$;
