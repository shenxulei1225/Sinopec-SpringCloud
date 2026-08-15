-- V58: 运营区域介绍页基础字段补物理列
-- 原因：dynamic_region_intro_fields.sql 只写入基础字段元数据，未走 createBaseField → addColumn，
--       读实体时仍按字段编码 SELECT fld_base_region_*，表上无列则详情 500。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT c.relname AS table_name
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND c.relname ~ '^ent_region(_t[0-9]+)?$'
  LOOP
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_cover_url TEXT', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_hq_location TEXT', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_mission_summary TEXT', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_pipeline_km_total NUMERIC(18,4)', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_pipeline_km_ng NUMERIC(18,4)', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_pipeline_km_cr NUMERIC(18,4)', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_pipeline_km_cp NUMERIC(18,4)', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_storage_count BIGINT', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_lng_terminal_count BIGINT', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_coverage_note TEXT', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_org_mode_note TEXT', r.table_name);
    EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_base_region_service_radius_km NUMERIC(18,4)', r.table_name);

    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_cover_url IS %L', r.table_name, 'FLD-BASE-region-cover_url（头图）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_hq_location IS %L', r.table_name, 'FLD-BASE-region-hq_location（驻地）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_mission_summary IS %L', r.table_name, 'FLD-BASE-region-mission_summary（宗旨定位）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_pipeline_km_total IS %L', r.table_name, 'FLD-BASE-region-pipeline_km_total（管线里程）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_pipeline_km_ng IS %L', r.table_name, 'FLD-BASE-region-pipeline_km_ng（天然气里程）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_pipeline_km_cr IS %L', r.table_name, 'FLD-BASE-region-pipeline_km_cr（原油里程）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_pipeline_km_cp IS %L', r.table_name, 'FLD-BASE-region-pipeline_km_cp（成品油里程）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_storage_count IS %L', r.table_name, 'FLD-BASE-region-storage_count（储气库数量）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_lng_terminal_count IS %L', r.table_name, 'FLD-BASE-region-lng_terminal_count（LNG接收站数量）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_coverage_note IS %L', r.table_name, 'FLD-BASE-region-coverage_note（覆盖范围说明）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_org_mode_note IS %L', r.table_name, 'FLD-BASE-region-org_mode_note（管理模式说明）');
    EXECUTE format('COMMENT ON COLUMN dynamicbusiness.%I.fld_base_region_service_radius_km IS %L', r.table_name, 'FLD-BASE-region-service_radius_km（管辖半径）');
  END LOOP;
END $$;

-- 从本表 custom_fields 以及模板表 ent_region 同 id 的 JSON 回填（seed 曾只写 JSON、且部分写在基表）
DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT c.relname AS table_name
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND c.relname ~ '^ent_region(_t[0-9]+)?$'
  LOOP
    EXECUTE format($sql$
      UPDATE dynamicbusiness.%I t
      SET
        fld_base_region_cover_url = COALESCE(
          t.fld_base_region_cover_url,
          NULLIF(t.custom_fields->>'FLD-BASE-region-cover_url', ''),
          NULLIF(b.custom_fields->>'FLD-BASE-region-cover_url', '')),
        fld_base_region_hq_location = COALESCE(
          t.fld_base_region_hq_location,
          NULLIF(t.custom_fields->>'FLD-BASE-region-hq_location', ''),
          NULLIF(b.custom_fields->>'FLD-BASE-region-hq_location', '')),
        fld_base_region_mission_summary = COALESCE(
          t.fld_base_region_mission_summary,
          NULLIF(t.custom_fields->>'FLD-BASE-region-mission_summary', ''),
          NULLIF(b.custom_fields->>'FLD-BASE-region-mission_summary', '')),
        fld_base_region_pipeline_km_total = COALESCE(
          t.fld_base_region_pipeline_km_total,
          NULLIF(t.custom_fields->>'FLD-BASE-region-pipeline_km_total', '')::numeric,
          NULLIF(b.custom_fields->>'FLD-BASE-region-pipeline_km_total', '')::numeric),
        fld_base_region_pipeline_km_ng = COALESCE(
          t.fld_base_region_pipeline_km_ng,
          NULLIF(t.custom_fields->>'FLD-BASE-region-pipeline_km_ng', '')::numeric,
          NULLIF(b.custom_fields->>'FLD-BASE-region-pipeline_km_ng', '')::numeric),
        fld_base_region_pipeline_km_cr = COALESCE(
          t.fld_base_region_pipeline_km_cr,
          NULLIF(t.custom_fields->>'FLD-BASE-region-pipeline_km_cr', '')::numeric,
          NULLIF(b.custom_fields->>'FLD-BASE-region-pipeline_km_cr', '')::numeric),
        fld_base_region_pipeline_km_cp = COALESCE(
          t.fld_base_region_pipeline_km_cp,
          NULLIF(t.custom_fields->>'FLD-BASE-region-pipeline_km_cp', '')::numeric,
          NULLIF(b.custom_fields->>'FLD-BASE-region-pipeline_km_cp', '')::numeric),
        fld_base_region_storage_count = COALESCE(
          t.fld_base_region_storage_count,
          NULLIF(t.custom_fields->>'FLD-BASE-region-storage_count', '')::bigint,
          NULLIF(b.custom_fields->>'FLD-BASE-region-storage_count', '')::bigint),
        fld_base_region_lng_terminal_count = COALESCE(
          t.fld_base_region_lng_terminal_count,
          NULLIF(t.custom_fields->>'FLD-BASE-region-lng_terminal_count', '')::bigint,
          NULLIF(b.custom_fields->>'FLD-BASE-region-lng_terminal_count', '')::bigint),
        fld_base_region_coverage_note = COALESCE(
          t.fld_base_region_coverage_note,
          NULLIF(t.custom_fields->>'FLD-BASE-region-coverage_note', ''),
          NULLIF(b.custom_fields->>'FLD-BASE-region-coverage_note', '')),
        fld_base_region_org_mode_note = COALESCE(
          t.fld_base_region_org_mode_note,
          NULLIF(t.custom_fields->>'FLD-BASE-region-org_mode_note', ''),
          NULLIF(b.custom_fields->>'FLD-BASE-region-org_mode_note', '')),
        fld_base_region_service_radius_km = COALESCE(
          t.fld_base_region_service_radius_km,
          NULLIF(t.custom_fields->>'FLD-BASE-region-service_radius_km', '')::numeric,
          NULLIF(b.custom_fields->>'FLD-BASE-region-service_radius_km', '')::numeric)
      FROM dynamicbusiness.ent_region b
      WHERE t.id = b.id
    $sql$, r.table_name);
  END LOOP;
END $$;
