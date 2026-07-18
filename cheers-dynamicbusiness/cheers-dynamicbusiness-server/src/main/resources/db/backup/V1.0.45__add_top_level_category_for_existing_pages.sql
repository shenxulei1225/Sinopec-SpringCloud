-- =====================================================
-- 为已存在的页面添加第一级别分类
-- 版本: V1.0.45
-- 描述: 
--   1. 为需要分类的视图（模式B/C）创建第一级别分类
--   2. 将现有的分类设置为第一级别分类的子分类
--   3. 更新页面配置,添加 topLevelCategoryId 和 categorySystemName
-- =====================================================

-- =====================================================
-- 1. 创建迁移函数
-- =====================================================
CREATE OR REPLACE FUNCTION migrate_top_level_category_for_existing_pages() RETURNS void AS $$
DECLARE
    page_config_record RECORD;
    view_config JSONB;
    view_item JSONB;
    v_entity_type_code TEXT;  -- 使用 v_ 前缀避免与列名冲突
    view_name TEXT;
    view_pattern TEXT;
    top_level_category_id BIGINT;
    category_dynamic_name TEXT;
    category_code TEXT;
    existing_category RECORD;
    updated_config JSONB;
    views_array JSONB;
    view_index INT;
    migration_count INT := 0;
    category_created_count INT := 0;
    category_updated_count INT := 0;
    first_top_level_category_id BIGINT := NULL;  -- 记录第一个创建的第一级别分类ID
BEGIN
    RAISE NOTICE '开始迁移：为已存在的页面添加第一级别分类...';
    
    -- 遍历所有数据管理页面配置
    FOR page_config_record IN
        SELECT 
            id,
            menu_id,
            config,
            tenant_id
        FROM dynamic_page_config
        WHERE page_type = 'data_management'
          AND deleted = false
          AND config IS NOT NULL
          AND config != '{}'::jsonb
    LOOP
        -- 检查是否有 views 配置
        IF NOT (page_config_record.config ? 'views') THEN
            CONTINUE;
        END IF;
        
        views_array := page_config_record.config->'views';
        IF jsonb_typeof(views_array) != 'array' OR jsonb_array_length(views_array) = 0 THEN
            CONTINUE;
        END IF;
        
        -- 获取业务类型代码
        v_entity_type_code := page_config_record.config->>'businessType';
        IF v_entity_type_code IS NULL OR v_entity_type_code = '' THEN
            -- 尝试从第一个视图获取
            IF jsonb_array_length(views_array) > 0 THEN
                view_item := views_array->0;
                v_entity_type_code := view_item->>'leftTreeBusinessType';
            END IF;
        END IF;
        
        IF v_entity_type_code IS NULL OR v_entity_type_code = '' THEN
            RAISE NOTICE '跳过页面配置 % (menu_id: %): 无法确定业务类型代码', 
                page_config_record.id, page_config_record.menu_id;
            CONTINUE;
        END IF;
        
        -- 遍历视图配置
        updated_config := page_config_record.config;
        view_index := 0;
        first_top_level_category_id := NULL;  -- 重置第一个创建的第一级别分类ID
        
        FOR view_item IN SELECT * FROM jsonb_array_elements(views_array)
        LOOP
            view_pattern := view_item->>'pattern';
            view_name := view_item->>'name';
            
            -- 只处理需要分类的模式（B 或 C）
            IF view_pattern NOT IN ('B', 'C') THEN
                view_index := view_index + 1;
                CONTINUE;
            END IF;
            
            -- 如果视图已经有 topLevelCategoryId,跳过
            IF view_item ? 'topLevelCategoryId' AND view_item->>'topLevelCategoryId' IS NOT NULL THEN
                RAISE NOTICE '视图已有第一级别分类,跳过: 页面配置 % (menu_id: %), 视图 "%"', 
                    page_config_record.id, page_config_record.menu_id, view_name;
                view_index := view_index + 1;
                CONTINUE;
            END IF;
            
            IF view_name IS NULL OR view_name = '' THEN
                RAISE NOTICE '视图名称为空,跳过: 页面配置 % (menu_id: %)', 
                    page_config_record.id, page_config_record.menu_id;
                view_index := view_index + 1;
                CONTINUE;
            END IF;
            
            -- 使用视图名称作为分类体系名称
            category_dynamic_name := view_name;
            -- 生成分类代码（简化处理：将中文和特殊字符转换为下划线,然后转小写）
            -- 注意：这里使用简化方法,实际项目中可能需要使用拼音库
            category_code := lower(regexp_replace(
                regexp_replace(category_dynamic_name, '[^\w\u4e00-\u9fa5]', '_', 'g'),
                '_+', '_', 'g'
            ));
            -- 如果代码为空或太长,使用默认值
            IF category_code = '' OR length(category_code) > 50 THEN
                category_code := 'category_' || page_config_record.id || '_' || view_index;
            END IF;
            
            -- 检查是否已存在相同代码的第一级别分类
            -- 注意：使用表别名 sc 来明确区分变量和列名
            SELECT sc.id INTO existing_category
            FROM dynamic_category sc
            WHERE sc.entity_type_code = v_entity_type_code
              AND sc.code = category_code
              AND (sc.parent_id IS NULL OR sc.parent_id = 0)
              AND sc.deleted = false
              AND sc.tenant_id = page_config_record.tenant_id
            LIMIT 1;
            
            IF existing_category IS NOT NULL THEN
                -- 使用已存在的分类
                top_level_category_id := existing_category.id;
                RAISE NOTICE '使用已存在的第一级别分类: 页面配置 % (menu_id: %), 视图 "%", 分类ID %', 
                    page_config_record.id, page_config_record.menu_id, view_name, top_level_category_id;
            ELSE
                -- 创建新的第一级别分类
                INSERT INTO dynamic_category (
                    name,
                    code,
                    entity_type_code,
                    parent_id,
                    status,
                    sort,
                    description,
                    creator,
                    create_time,
                    updater,
                    update_time,
                    deleted,
                    tenant_id
                ) VALUES (
                    category_dynamic_name,
                    category_code,
                    v_entity_type_code,
                    NULL, -- 第一级别分类,parent_id 为 NULL
                    0,    -- 状态：启用
                    0,    -- 排序
                    category_dynamic_name || '分类体系',
                    'system',
                    CURRENT_TIMESTAMP,
                    'system',
                    CURRENT_TIMESTAMP,
                    false,
                    page_config_record.tenant_id
                ) RETURNING id INTO top_level_category_id;
                
                category_created_count := category_created_count + 1;
                RAISE NOTICE '创建第一级别分类: 页面配置 % (menu_id: %), 视图 "%", 分类ID %, 分类名称 "%"', 
                    page_config_record.id, page_config_record.menu_id, view_name, top_level_category_id, category_dynamic_name;
            END IF;
            
            -- 记录第一个创建的第一级别分类ID（用于将原有分类挂到这个分类下）
            IF first_top_level_category_id IS NULL THEN
                first_top_level_category_id := top_level_category_id;
            END IF;
            
            -- 将现有的该业务类型下的所有分类的 parent_id 更新为第一个创建的第一级别分类
            -- 只更新那些 parent_id 为 NULL 或 0 的分类（即原来的根分类）
            -- 注意：如果同一个业务类型有多个视图,所有原来的分类都挂到第一个视图的第一级别分类上
            -- 这样可以保持数据的连续性,后续如果需要调整,用户可以手动移动分类到其他视图的第一级别分类下
            IF view_index = 0 OR first_top_level_category_id = top_level_category_id THEN
                -- 注意：使用表别名 sc 来明确区分变量和列名
                UPDATE dynamic_category sc
                SET parent_id = first_top_level_category_id,
                    updater = 'system',
                    update_time = CURRENT_TIMESTAMP
                WHERE sc.entity_type_code = v_entity_type_code
                  AND (sc.parent_id IS NULL OR sc.parent_id = 0)
                  AND sc.id != first_top_level_category_id  -- 不更新自己
                  AND sc.deleted = false
                  AND sc.tenant_id = page_config_record.tenant_id;
                
                GET DIAGNOSTICS category_updated_count = ROW_COUNT;
                IF category_updated_count > 0 THEN
                    RAISE NOTICE '更新了 % 个分类的 parent_id 为第一级别分类 % (视图: %)', 
                        category_updated_count, first_top_level_category_id, view_name;
                END IF;
            END IF;
            
            -- 更新视图配置,添加 topLevelCategoryId 和 categorySystemName
            view_item := view_item || jsonb_build_object(
                'topLevelCategoryId', top_level_category_id,
                'categorySystemName', category_dynamic_name
            );
            
            -- 更新 views 数组中的对应视图
            updated_config := jsonb_set(
                updated_config,
                ARRAY['views', view_index::text],
                view_item
            );
            
            migration_count := migration_count + 1;
            RAISE NOTICE '更新视图配置: 页面配置 % (menu_id: %), 视图 "%", topLevelCategoryId: %', 
                page_config_record.id, page_config_record.menu_id, view_name, top_level_category_id;
            
            view_index := view_index + 1;
        END LOOP;
        
        -- 如果有更新,保存页面配置
        IF updated_config != page_config_record.config THEN
            UPDATE dynamic_page_config
            SET config = updated_config,
                updater = 'system',
                update_time = CURRENT_TIMESTAMP
            WHERE id = page_config_record.id;
            
            RAISE NOTICE '已更新页面配置: ID %, menu_id %', 
                page_config_record.id, page_config_record.menu_id;
        END IF;
    END LOOP;
    
    RAISE NOTICE '迁移完成: 处理了 % 个视图,创建了 % 个第一级别分类', 
        migration_count, category_created_count;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- 2. 执行迁移
-- =====================================================
SELECT migrate_top_level_category_for_existing_pages();

-- =====================================================
-- 3. 清理迁移函数
-- =====================================================
DROP FUNCTION IF EXISTS migrate_top_level_category_for_existing_pages();

-- =====================================================
-- 4. 验证迁移结果
-- =====================================================
DO $$
DECLARE
    page_count INT;
    view_count INT;
    view_with_top_level_count INT;
BEGIN
    -- 统计需要分类的视图数量
    SELECT COUNT(*)
    INTO view_count
    FROM dynamic_page_config,
         jsonb_array_elements(config->'views') AS view_item
    WHERE page_type = 'data_management'
      AND deleted = false
      AND (view_item->>'pattern') IN ('B', 'C');
    
    -- 统计已有 topLevelCategoryId 的视图数量
    SELECT COUNT(*)
    INTO view_with_top_level_count
    FROM dynamic_page_config,
         jsonb_array_elements(config->'views') AS view_item
    WHERE page_type = 'data_management'
      AND deleted = false
      AND (view_item->>'pattern') IN ('B', 'C')
      AND view_item ? 'topLevelCategoryId'
      AND view_item->>'topLevelCategoryId' IS NOT NULL;
    
    RAISE NOTICE '验证结果:';
    RAISE NOTICE '  - 需要分类的视图总数: %', view_count;
    RAISE NOTICE '  - 已有第一级别分类的视图数: %', view_with_top_level_count;
    
    IF view_count > 0 AND view_with_top_level_count = view_count THEN
        RAISE NOTICE '✓ 所有需要分类的视图都已具备第一级别分类';
    ELSIF view_count > view_with_top_level_count THEN
        RAISE WARNING '⚠ 仍有 % 个视图缺少第一级别分类', view_count - view_with_top_level_count;
    END IF;
END $$;
