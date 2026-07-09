-- =====================================================
-- 完全清理所有区域管理（region）类型的数据
-- 
-- 注意：这是物理删除操作,不可恢复！
-- 清理范围：
--   1. 分类数据（Category）
--   2. 区域实体数据（Entity）
--   3. 业务实体数据（管线、构筑物、设备,如果关联到region分类）
--   4. 关联关系数据
--   5. 业务表数据
-- =====================================================

-- 步骤1：收集所有要删除的category ID（包括子分类）
-- 使用临时表存储所有需要删除的category ID
DROP TABLE IF EXISTS temp_region_category_ids;

CREATE TEMP TABLE temp_region_category_ids AS
WITH RECURSIVE category_tree AS (
    -- 根分类（region类型的根分类）
    SELECT id, parent_id, 1 as level
    FROM system_category
    WHERE entity_type_code = 'region' 
    AND tenant_id = 1
    
    UNION ALL
    
    -- 递归查找所有子分类
    SELECT c.id, c.parent_id, ct.level + 1
    FROM system_category c
    INNER JOIN category_tree ct ON c.parent_id = ct.id
    WHERE c.entity_type_code = 'region' 
    AND c.tenant_id = 1
)
SELECT id FROM category_tree;

-- 步骤2：删除 Model-Category 关联关系
DELETE FROM system_model_category_relation
WHERE category_id IN (SELECT id FROM temp_region_category_ids);

-- 步骤3：删除 biz_region 专用表中的数据
-- 使用 Dedicated 方式存储,区域实体存储在 biz_region 表中
-- 通过 entity_type_code 字段关联到 region 业务类型
DELETE FROM biz_region
WHERE entity_type_code = 'region' 
AND tenant_id = 1;

-- 步骤4：删除 Entity-Category 关联关系（如果使用）
-- 注意：如果使用专用表,可能不需要这个关联表
DELETE FROM system_entity_category_relation
WHERE category_id IN (SELECT id FROM temp_region_category_ids);

-- 步骤5：物理删除所有 region 类型的分类（从叶子节点开始,避免外键约束问题）
DO $$
DECLARE
    deleted_count INTEGER;
    max_iterations INTEGER := 100; -- 防止无限循环
    current_iteration INTEGER := 0;
BEGIN
    LOOP
        current_iteration := current_iteration + 1;
        
        -- 删除所有没有子分类的region类型分类
        DELETE FROM system_category
        WHERE id IN (
            SELECT c.id
            FROM system_category c
            WHERE c.entity_type_code = 'region'
            AND c.tenant_id = 1
            AND NOT EXISTS (
                SELECT 1 
                FROM system_category child
                WHERE child.parent_id = c.id
                AND child.entity_type_code = 'region'
                AND child.tenant_id = 1
            )
        );
        
        GET DIAGNOSTICS deleted_count = ROW_COUNT;
        
        -- 如果没有删除任何记录,说明已经删除完毕
        EXIT WHEN deleted_count = 0;
        
        -- 防止无限循环
        IF current_iteration >= max_iterations THEN
            RAISE EXCEPTION '达到最大迭代次数,可能存在问题';
        END IF;
    END LOOP;
END $$;

-- 步骤6：强制删除所有剩余的region类型分类（防止有循环引用等情况）
DELETE FROM system_category
WHERE entity_type_code = 'region' 
AND tenant_id = 1;

-- 清理临时表
DROP TABLE IF EXISTS temp_region_category_ids;

-- 验证：查询剩余region类型的分类数量（应该为0）
-- SELECT COUNT(*) as remaining_category_count 
-- FROM system_category 
-- WHERE entity_type_code = 'region' 
-- AND tenant_id = 1;
--
-- SELECT COUNT(*) as remaining_entity_count
-- FROM system_entity e
-- INNER JOIN system_category c ON e.category_id = c.id
-- WHERE c.entity_type_code = 'region'
-- AND c.tenant_id = 1;

