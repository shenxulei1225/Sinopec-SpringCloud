-- V97: SOP 标准文档链接字段（standard_pdf_url）
-- 目的：支持 SOP 页面「标准 PDF 预览/下载」。

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
      AND t.table_name ~ '^ent_sop(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS standard_pdf_url VARCHAR(1024)',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.standard_pdf_url IS %L',
      tbl,
      'SOP 标准文档 PDF 地址（用于预览与下载）');
  END LOOP;
END $$;

-- 通用表单默认隐藏，交由 SOP 专用面板展示。
UPDATE dynamic_entity_type_base_field
SET
  type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}',
  updater = 'flyway-v97',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'sop'
  AND field_code = 'standard_pdf_url';

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'sop';
