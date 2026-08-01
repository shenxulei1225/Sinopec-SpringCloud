-- V44: 检查内容（inspection_item）专用表增加 method_template_id（REF → inspection_method）
-- 编码 = 物理列名；列存 BIGINT（实体 id）；禁止 FLD-BASE- / 读路径探列兜底

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND (
        t.table_name = 'ent_inspection_item'
        OR t.table_name LIKE 'ent\_inspection\_item\_t%' ESCAPE '\'
      )
    ORDER BY 1
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS method_template_id BIGINT',
      tbl
    );
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.method_template_id IS %L',
      tbl,
      'method_template_id（检查方法模板 REF → inspection_method）；列存实体 id'
    );
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS idx_%s_method_template_id ON dynamicbusiness.%I (method_template_id) WHERE deleted = false AND method_template_id IS NOT NULL',
      tbl,
      tbl
    );
  END LOOP;
END $$;
