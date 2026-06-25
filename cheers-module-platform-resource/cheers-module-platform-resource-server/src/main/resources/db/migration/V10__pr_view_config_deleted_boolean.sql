SET search_path TO platformresource;

-- BaseDO.deleted 为 Boolean；与 pr_component_props 对齐，避免 smallint = boolean 查询异常
DROP INDEX IF EXISTS uidx_pr_view_config_code;
DROP INDEX IF EXISTS idx_pr_view_config_category_id;

ALTER TABLE pr_view_config
    ALTER COLUMN deleted DROP DEFAULT;

ALTER TABLE pr_view_config
    ALTER COLUMN deleted TYPE boolean USING (COALESCE(deleted, 0)::int <> 0);

ALTER TABLE pr_view_config
    ALTER COLUMN deleted SET DEFAULT false;

COMMENT ON COLUMN pr_view_config.deleted IS '逻辑删除（false=未删 true=已删）';

CREATE UNIQUE INDEX IF NOT EXISTS uidx_pr_view_config_code
    ON pr_view_config (view_code)
    WHERE view_code IS NOT NULL AND deleted = false;

CREATE INDEX IF NOT EXISTS idx_pr_view_config_category_id
    ON pr_view_config (category_id)
    WHERE category_id IS NOT NULL AND deleted = false;
