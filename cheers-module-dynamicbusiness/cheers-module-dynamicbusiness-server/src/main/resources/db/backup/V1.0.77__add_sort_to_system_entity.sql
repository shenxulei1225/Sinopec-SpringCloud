-- =====================================================
-- 版本: V1.0.57
-- 日期: 2026-03-22
-- 描述: 为 dynamic_entity 表补充 sort 字段（同一父节点内局部排序）
-- =====================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'dynamic_entity'
          AND column_name = 'sort'
    ) THEN
        ALTER TABLE dynamic_entity
            ADD COLUMN sort INTEGER;

        COMMENT ON COLUMN dynamic_entity.sort IS '同一父节点下的局部排序序号（从1开始）';

        -- 可选：常见查询维度索引（父节点 + 排序）
        CREATE INDEX IF NOT EXISTS idx_dynamic_entity_parent_sort
            ON dynamic_entity(parent_id, sort);

        RAISE NOTICE 'dynamic_entity.sort 字段已创建';
    ELSE
        RAISE NOTICE 'dynamic_entity.sort 字段已存在，跳过';
    END IF;
END $$;
