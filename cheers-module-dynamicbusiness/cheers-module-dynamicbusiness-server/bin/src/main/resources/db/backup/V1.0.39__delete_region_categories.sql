-- =====================================================
-- 物理删除所有区域管理（region）类型的分类数据
-- 
-- 注意：这是物理删除操作,不可恢复！
-- 只删除category数据,model和字段定义不受影响
-- =====================================================

-- 步骤1：收集所有要删除的category ID（包括子分类）
-- 使用临时表存储所有需要删除的category ID
-- 注意：不检查deleted字段,强制删除所有region类型的分类
DROP TABLE IF EXISTS temp_region_category_ids;

CREATE TEMP TABLE temp_region_category_ids AS
WITH RECURSIVE category_tree AS (
    -- 根分类（region类型的根分类,不检查deleted字段）
    SELECT id, parent_id, 1 as level
    FROM system_category
    WHERE business_type_code = 'region' 
    AND tenant_id = 1
    
    UNION ALL
    
    -- 递归查找所有子分类（不检查deleted字段）
    SELECT c.id, c.parent_id, ct.level + 1
    FROM system_category c
    INNER JOIN category_tree ct ON c.parent_id = ct.id
    WHERE c.business_type_code = 'region' 
    AND c.tenant_id = 1
)
SELECT id FROM category_tree;

-- 步骤2：删除 Model-Category 关联关系（不检查deleted字段）
DELETE FROM system_model_category_relation
WHERE category_id IN (SELECT id FROM temp_region_category_ids);

-- 步骤3：删除 Entity-Category 关联关系（不检查deleted字段）
DELETE FROM system_entity_category_relation
WHERE category_id IN (SELECT id FROM temp_region_category_ids);

-- 步骤4：删除关联的 Entity 数据（如果category是实体,不检查deleted字段）
DELETE FROM system_entity
WHERE id IN (
    SELECT entity_id 
    FROM system_category 
    WHERE id IN (SELECT id FROM temp_region_category_ids)
    AND entity_id IS NOT NULL
);

-- 步骤5：物理删除所有 region 类型的分类（从叶子节点开始,避免外键约束问题）
-- 先删除子分类,再删除父分类
-- 由于PostgreSQL不支持在DELETE中使用递归CTE,我们分多次删除
-- 先删除最深层的分类,然后逐层向上删除
-- 注意：不检查deleted字段,强制删除

DO $$
DECLARE
    deleted_count INTEGER;
    max_iterations INTEGER := 100; -- 防止无限循环
    current_iteration INTEGER := 0;
BEGIN
    LOOP
        current_iteration := current_iteration + 1;
        
        -- 删除所有没有子分类的region类型分类（不检查deleted字段）
        DELETE FROM system_category
        WHERE id IN (
            SELECT c.id
            FROM system_category c
            WHERE c.business_type_code = 'region'
            AND c.tenant_id = 1
            AND NOT EXISTS (
                SELECT 1 
                FROM system_category child
                WHERE child.parent_id = c.id
                AND child.business_type_code = 'region'
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
-- 直接删除所有region类型的分类,不管是否有子分类
DELETE FROM system_category
WHERE business_type_code = 'region' 
AND tenant_id = 1;

-- 清理临时表
DROP TABLE IF EXISTS temp_region_category_ids;

-- 验证：查询剩余region类型的分类数量（应该为0）
-- SELECT COUNT(*) as remaining_count 
-- FROM system_category 
-- WHERE business_type_code = 'region' 
-- AND tenant_id = 1;

