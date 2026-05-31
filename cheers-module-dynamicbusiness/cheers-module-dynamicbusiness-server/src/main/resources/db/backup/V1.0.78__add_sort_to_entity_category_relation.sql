-- =====================================================
-- 版本: V1.0.58
-- 日期: 2026-03-22
-- 描述: 为 dynamic_entity_category_relation 增加 sort 字段（分类上下文排序）
-- =====================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'dynamic_entity_category_relation'
          AND column_name = 'sort'
    ) THEN
        ALTER TABLE dynamic_entity_category_relation
            ADD COLUMN sort INTEGER;

        COMMENT ON COLUMN dynamic_entity_category_relation.sort IS '分类视图下排序（同一分类上下文内）';

        CREATE INDEX IF NOT EXISTS idx_entity_category_relation_category_sort
            ON dynamic_entity_category_relation(category_id, sort);

        RAISE NOTICE 'dynamic_entity_category_relation.sort 字段已创建';
    ELSE
        RAISE NOTICE 'dynamic_entity_category_relation.sort 字段已存在，跳过';
    END IF;
END $$;
