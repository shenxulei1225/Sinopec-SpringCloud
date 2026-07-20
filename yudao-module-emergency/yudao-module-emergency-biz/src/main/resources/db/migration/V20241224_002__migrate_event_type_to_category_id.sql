-- 数据迁移脚本：eventType和eventSubType迁移到分类ID
-- 日期：2024-12-24
-- 说明：将现有的eventType（VARCHAR）和eventSubType（VARCHAR）组合映射到分类ID（BIGINT），存储在eventType字段中

-- 步骤1：创建备份表
CREATE TABLE IF NOT EXISTS emergency_event_backup_20241224 AS 
SELECT * FROM emergency_event;

-- 步骤2：创建临时映射表（需要根据实际分类管理模块的数据填充）
-- 注意：此映射表需要根据实际分类管理模块的数据进行填充
CREATE TEMP TABLE IF NOT EXISTS event_type_mapping (
    old_event_type VARCHAR(50),
    old_event_sub_type VARCHAR(50),
    new_category_id BIGINT,
    mapping_rule VARCHAR(200)
);

-- 示例映射数据（需要根据实际分类管理模块的数据填充）
-- INSERT INTO event_type_mapping VALUES
-- ('生产安全事故', '火灾', 1001, '生产安全事故-火灾'),
-- ('生产安全事故', '爆炸', 1002, '生产安全事故-爆炸'),
-- ('自然灾害', '地震', 2001, '自然灾害-地震'),
-- ('自然灾害', '洪水', 2002, '自然灾害-洪水');
-- ... 更多映射规则

-- 步骤3：检查event_type字段类型
-- 如果字段类型不是bigint，需要先修改字段类型
DO $$
BEGIN
    -- 检查event_type字段类型
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_event' 
        AND column_name = 'event_type' 
        AND data_type != 'bigint'
    ) THEN
        -- 如果字段类型不是bigint，先添加临时字段
        IF NOT EXISTS (
            SELECT 1 
            FROM information_schema.columns 
            WHERE table_name = 'emergency_event' 
            AND column_name = 'event_type_new'
        ) THEN
            ALTER TABLE emergency_event ADD COLUMN event_type_new BIGINT;
        END IF;
        
        -- 数据迁移（分批处理，每次1000条）
        DECLARE
            batch_size INTEGER := 1000;
            offset_val INTEGER := 0;
            affected_rows INTEGER;
        BEGIN
            LOOP
                -- 更新当前批次
                UPDATE emergency_event e
                SET event_type_new = m.new_category_id
                FROM event_type_mapping m
                WHERE e.event_type = m.old_event_type
                  AND (e.event_sub_type = m.old_event_sub_type OR (e.event_sub_type IS NULL AND m.old_event_sub_type IS NULL))
                  AND e.event_type_new IS NULL
                  AND e.deleted = FALSE
                LIMIT batch_size;
                
                GET DIAGNOSTICS affected_rows = ROW_COUNT;
                
                -- 如果没有更多行需要更新，退出循环
                EXIT WHEN affected_rows = 0;
                
                -- 记录进度
                RAISE NOTICE '已迁移 % 条记录', offset_val + affected_rows;
                
                offset_val := offset_val + batch_size;
                
                -- 短暂延迟，避免锁表时间过长
                PERFORM pg_sleep(0.1);
            END LOOP;
        END;
        
        -- 验证数据完整性
        DECLARE
            total_count INTEGER;
            migrated_count INTEGER;
            unmapped_count INTEGER;
        BEGIN
            -- 统计总数
            SELECT COUNT(*) INTO total_count 
            FROM emergency_event 
            WHERE deleted = FALSE;
            
            -- 统计已迁移数量
            SELECT COUNT(*) INTO migrated_count 
            FROM emergency_event 
            WHERE event_type_new IS NOT NULL 
              AND deleted = FALSE;
            
            -- 统计未映射数量
            SELECT COUNT(*) INTO unmapped_count 
            FROM emergency_event e
            LEFT JOIN event_type_mapping m 
                ON e.event_type = m.old_event_type 
                AND (e.event_sub_type = m.old_event_sub_type OR (e.event_sub_type IS NULL AND m.old_event_sub_type IS NULL))
            WHERE e.deleted = FALSE
              AND m.new_category_id IS NULL;
            
            RAISE NOTICE '迁移统计：总数=%，已迁移=%，未映射=%', total_count, migrated_count, unmapped_count;
            
            -- 如果有未映射的数据，记录警告
            IF unmapped_count > 0 THEN
                RAISE WARNING '存在 % 条记录无法映射到分类ID，需要手动处理', unmapped_count;
            END IF;
        END;
        
        -- 注意：字段重命名需要在确认迁移成功后手动执行
        -- ALTER TABLE emergency_event RENAME COLUMN event_type TO event_type_old;
        -- ALTER TABLE emergency_event RENAME COLUMN event_type_new TO event_type;
        -- ALTER TABLE emergency_event DROP COLUMN event_type_old;
    ELSE
        RAISE NOTICE 'event_type字段已经是BIGINT类型，无需迁移';
    END IF;
END $$;

-- 步骤4：添加迁移注释
COMMENT ON TABLE emergency_event_backup_20241224 IS '事件表备份（2024-12-24数据迁移前）';








