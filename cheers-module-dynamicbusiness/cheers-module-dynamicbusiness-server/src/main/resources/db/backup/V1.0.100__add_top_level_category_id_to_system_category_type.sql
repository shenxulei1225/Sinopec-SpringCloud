-- =====================================================
-- 为 dynamic_category_type 增加 top_level_category_id 字段
-- 描述：记录每个分类类型的顶层分类节点ID（根节点）
-- =====================================================

ALTER TABLE dynamic_category_type
    ADD COLUMN IF NOT EXISTS top_level_category_id BIGINT;

COMMENT ON COLUMN dynamic_category_type.top_level_category_id IS '顶层分类ID（该分类类型的根节点）';
