-- V90: 规范标准（entityTypeCode=standard）专用表补业务固定列
-- 元数据 / 样例实体：scripts/platform-import/standard/
-- 字段编码 = 物理列名（见 .cursor/rules/dynamicbusiness-seed-standard.mdc）

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
      AND t.table_name ~ '^ent_standard(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS standard_no VARCHAR(128)',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS standard_level VARCHAR(32)',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS issuing_body VARCHAR(255)',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS publish_year INTEGER',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS summary TEXT',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS source_ref TEXT',
      tbl);

    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.standard_no IS %L',
      tbl, '标准编号（如 GB50251-2015；展示用业务号）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.standard_level IS %L',
      tbl, '标准级别：NATIONAL=国标；INDUSTRY=行标；ENTERPRISE=企标');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.issuing_body IS %L',
      tbl, '发布单位');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.publish_year IS %L',
      tbl, '发布年份');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.summary IS %L',
      tbl, '摘要说明（参考资料用途简述）');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.source_ref IS %L',
      tbl, '引用出处（规格书/招标文件名）');
  END LOOP;
END $$;
