SET search_path TO platformresource;

-- 视图配置归属用户自建分类（dynamicbusiness category，categoryTypeCode=view）
ALTER TABLE pr_view_config
    ADD COLUMN IF NOT EXISTS category_id BIGINT;

COMMENT ON COLUMN pr_view_config.category_id IS '视图分类节点 id（Category.categoryTypeCode=view）';

CREATE INDEX IF NOT EXISTS idx_pr_view_config_category_id
    ON pr_view_config (category_id)
    WHERE category_id IS NOT NULL AND deleted = 0;
