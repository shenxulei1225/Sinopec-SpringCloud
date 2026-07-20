-- 数据迁移验证脚本：eventType和eventSubType迁移到分类ID
-- 日期：2024-12-24
-- 说明：验证数据迁移的完整性和正确性

-- 验证1：统计迁移前后数据量对比
SELECT 
    '迁移前（备份表）' AS phase,
    COUNT(*) AS total_count,
    COUNT(DISTINCT event_type) AS distinct_event_types,
    COUNT(DISTINCT event_sub_type) AS distinct_sub_types,
    COUNT(CASE WHEN event_type IS NULL THEN 1 END) AS null_event_type_count
FROM emergency_event_backup_20241224
WHERE deleted = FALSE

UNION ALL

SELECT 
    '迁移后（当前表）' AS phase,
    COUNT(*) AS total_count,
    COUNT(DISTINCT event_type) AS distinct_category_ids,
    COUNT(DISTINCT event_sub_type) AS distinct_sub_types,
    COUNT(CASE WHEN event_type IS NULL THEN 1 END) AS null_event_type_count
FROM emergency_event
WHERE deleted = FALSE;

-- 验证2：检查未映射的数据
SELECT 
    e.id,
    e.event_code,
    e.title,
    e.event_type AS old_event_type,
    e.event_sub_type AS old_event_sub_type,
    '未映射到分类ID' AS issue
FROM emergency_event e
LEFT JOIN event_type_mapping m 
    ON e.event_type = m.old_event_type 
    AND (e.event_sub_type = m.old_event_sub_type OR (e.event_sub_type IS NULL AND m.old_event_sub_type IS NULL))
WHERE e.deleted = FALSE
  AND m.new_category_id IS NULL
  AND e.event_type IS NOT NULL
LIMIT 100;

-- 验证3：检查迁移后的数据完整性
SELECT 
    '数据完整性检查' AS check_type,
    COUNT(*) AS total_records,
    COUNT(CASE WHEN event_type_new IS NOT NULL THEN 1 END) AS migrated_count,
    COUNT(CASE WHEN event_type_new IS NULL AND event_type IS NOT NULL THEN 1 END) AS unmapped_count,
    COUNT(CASE WHEN event_type IS NULL THEN 1 END) AS null_count
FROM emergency_event
WHERE deleted = FALSE;

-- 验证4：检查分类ID的有效性（需要连接分类管理服务验证）
-- 注意：此验证需要调用基础服务的分类管理模块API
-- SELECT 
--     e.id,
--     e.event_code,
--     e.event_type_new AS category_id,
--     CASE 
--         WHEN EXISTS (SELECT 1 FROM category_table WHERE id = e.event_type_new) THEN '有效'
--         ELSE '无效'
--     END AS validity
-- FROM emergency_event e
-- WHERE e.deleted = FALSE
--   AND e.event_type_new IS NOT NULL
-- LIMIT 100;

-- 验证5：检查数据一致性（备份表与当前表对比）
SELECT 
    '数据一致性检查' AS check_type,
    COUNT(*) AS total_records,
    COUNT(CASE WHEN b.id = e.id THEN 1 END) AS matched_records,
    COUNT(CASE WHEN b.id IS NULL THEN 1 END) AS missing_in_backup,
    COUNT(CASE WHEN e.id IS NULL THEN 1 END) AS missing_in_current
FROM emergency_event e
FULL OUTER JOIN emergency_event_backup_20241224 b ON e.id = b.id
WHERE (e.deleted = FALSE OR b.deleted = FALSE);

-- 验证6：生成迁移报告
DO $$
DECLARE
    total_count INTEGER;
    migrated_count INTEGER;
    unmapped_count INTEGER;
    null_count INTEGER;
BEGIN
    -- 统计总数
    SELECT COUNT(*) INTO total_count 
    FROM emergency_event 
    WHERE deleted = FALSE;
    
    -- 统计已迁移数量（如果有event_type_new字段）
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_event' 
        AND column_name = 'event_type_new'
    ) THEN
        SELECT COUNT(*) INTO migrated_count 
        FROM emergency_event 
        WHERE event_type_new IS NOT NULL 
          AND deleted = FALSE;
        
        SELECT COUNT(*) INTO unmapped_count 
        FROM emergency_event e
        LEFT JOIN event_type_mapping m 
            ON e.event_type = m.old_event_type 
            AND (e.event_sub_type = m.old_event_sub_type OR (e.event_sub_type IS NULL AND m.old_event_sub_type IS NULL))
        WHERE e.deleted = FALSE
          AND e.event_type IS NOT NULL
          AND m.new_category_id IS NULL;
    ELSE
        -- 如果已经完成字段重命名，直接统计event_type
        SELECT COUNT(*) INTO migrated_count 
        FROM emergency_event 
        WHERE event_type IS NOT NULL 
          AND deleted = FALSE;
        
        migrated_count := 0;
        unmapped_count := 0;
    END IF;
    
    -- 统计空值数量
    SELECT COUNT(*) INTO null_count 
    FROM emergency_event 
    WHERE event_type IS NULL 
      AND deleted = FALSE;
    
    -- 输出验证报告
    RAISE NOTICE '========================================';
    RAISE NOTICE '数据迁移验证报告';
    RAISE NOTICE '========================================';
    RAISE NOTICE '总记录数: %', total_count;
    RAISE NOTICE '已迁移记录数: %', migrated_count;
    RAISE NOTICE '未映射记录数: %', unmapped_count;
    RAISE NOTICE '空值记录数: %', null_count;
    RAISE NOTICE '迁移完成率: %%', ROUND(migrated_count * 100.0 / NULLIF(total_count, 0), 2);
    RAISE NOTICE '========================================';
    
    -- 如果有未映射的数据，记录警告
    IF unmapped_count > 0 THEN
        RAISE WARNING '存在 % 条记录无法映射到分类ID，需要手动处理', unmapped_count;
    END IF;
    
    -- 如果迁移完成率低于95%，记录警告
    IF migrated_count * 100.0 / NULLIF(total_count, 0) < 95 THEN
        RAISE WARNING '迁移完成率低于95%%，请检查迁移脚本', migrated_count * 100.0 / NULLIF(total_count, 0);
    END IF;
END $$;








