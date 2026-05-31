-- =====================================================
-- 删除system_category表中的遗留字段business_type
-- 
-- 问题说明：
-- 1. system_category表中同时存在business_type和business_type_code两个字段
-- 2. business_type字段所有记录都为NULL（遗留字段,不再使用）
-- 3. 所有代码和索引都使用business_type_code字段
-- 4. business_type字段应该被删除
-- 
-- 注意：
-- business_type_code是业务类型编码（如"region"、"equipment"）,
-- 同一个业务类型下可以有多个分类,所以business_type_code本身不需要唯一性约束
-- =====================================================

-- 步骤1：检查business_type字段的使用情况
DO $$
DECLARE
    column_exists BOOLEAN;
    total_count INTEGER;
    business_type_not_null_count INTEGER;
BEGIN
    -- 先检查business_type字段是否存在,避免旧环境中已被手动删除导致错误
    SELECT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'system_category' 
        AND column_name = 'business_type'
    ) INTO column_exists;

    -- 如果字段不存在,直接跳过后续检查逻辑
    IF NOT column_exists THEN
        RAISE NOTICE 'system_category.business_type 字段不存在,跳过使用情况检查';
        RETURN;
    END IF;

    -- 统计总记录数
    SELECT COUNT(*) INTO total_count
    FROM system_category
    WHERE deleted = false;
    
    -- 统计business_type不为空的记录数
    SELECT COUNT(*) INTO business_type_not_null_count
    FROM system_category
    WHERE deleted = false
    AND business_type IS NOT NULL;
    
    -- 如果business_type有非空值,发出警告
    IF business_type_not_null_count > 0 THEN
        RAISE WARNING '发现 % 条记录的business_type字段不为空,请先检查数据', business_type_not_null_count;
    ELSE
        RAISE NOTICE '所有记录的business_type字段都为空,可以安全删除';
    END IF;
    
    RAISE NOTICE '总记录数: %, business_type不为空: %', total_count, business_type_not_null_count;
END $$;

-- 步骤2：删除business_type字段上的索引（如果存在）
DROP INDEX IF EXISTS idx_category_business_type;
DROP INDEX IF EXISTS idx_system_category_business_type;

-- 步骤3：删除business_type字段
ALTER TABLE system_category DROP COLUMN IF EXISTS business_type;

-- 步骤4：添加注释说明
COMMENT ON COLUMN system_category.business_type_code IS '业务类型编码（如"region"、"equipment"）,用于区分不同的业务类型。同一业务类型下可以有多个分类,所以此字段不需要唯一性约束';

-- 验证：查询表结构确认business_type字段已删除
DO $$
DECLARE
    column_exists BOOLEAN;
BEGIN
    SELECT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'system_category' 
        AND column_name = 'business_type'
    ) INTO column_exists;
    
    IF column_exists THEN
        RAISE WARNING 'business_type字段仍然存在,删除失败';
    ELSE
        RAISE NOTICE '✓ business_type字段已成功删除';
    END IF;
END $$;

