-- =====================================================
-- 版本: V1.0.57
-- 日期: 2026-03-22
-- 描述: 为 system_entity 表补充 sort 字段（同一父节点内局部排序）
-- =====================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'system_entity'
          AND column_name = 'sort'
    ) THEN
        ALTER TABLE system_entity
            ADD COLUMN sort INTEGER;

        COMMENT ON COLUMN system_entity.sort IS '同一父节点下的局部排序序号（从1开始）';

        -- 可选：常见查询维度索引（父节点 + 排序）
        CREATE INDEX IF NOT EXISTS idx_system_entity_parent_sort
            ON system_entity(parent_id, sort);

        RAISE NOTICE 'system_entity.sort 字段已创建';
    ELSE
        RAISE NOTICE 'system_entity.sort 字段已存在，跳过';
    END IF;
END $$;
