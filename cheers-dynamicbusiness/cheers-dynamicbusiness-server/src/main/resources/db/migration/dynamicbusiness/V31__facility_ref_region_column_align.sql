-- V31: 设施所属区域列对齐字段库编码（废除捷径列 region_id）
-- FLD-BASE-facility-REF_REGION → fld_base_facility_ref_region

-- 1) 重命名物理列（幂等）
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'ent_facility'
      AND column_name = 'region_id'
  ) AND NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'ent_facility'
      AND column_name = 'fld_base_facility_ref_region'
  ) THEN
    ALTER TABLE dynamicbusiness.ent_facility
      RENAME COLUMN region_id TO fld_base_facility_ref_region;
  END IF;
END $$;

COMMENT ON COLUMN dynamicbusiness.ent_facility.fld_base_facility_ref_region IS
  '基础字段 FLD-BASE-facility-REF_REGION（所属区域）；存 ent_region.id';

-- 2) 索引：旧名 → 新名
DROP INDEX IF EXISTS dynamicbusiness.idx_ent_facility_region;
CREATE INDEX IF NOT EXISTS idx_ent_facility_fld_base_facility_ref_region
  ON dynamicbusiness.ent_facility (fld_base_facility_ref_region)
  WHERE deleted = false;

-- 3) 物理列映射：键与列改为字段编码标准名（去掉 region_id 捷径）
UPDATE dynamicbusiness.dynamic_entity_type_config
SET physical_column_mapping = (
      COALESCE(physical_column_mapping, '{}'::jsonb)
      - 'region_id'
    ) || jsonb_build_object(
      'FLD-BASE-facility-REF_REGION',
      jsonb_build_object(
        'type', 'BIGINT',
        'column', 'fld_base_facility_ref_region'
      )
    ),
    updater = 'flyway-v31',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'facility'
  AND deleted = false;

-- 4) 若 JSON 里曾误写入 REF，且标准列为空，则回填一次（不双读旧列）
UPDATE dynamicbusiness.ent_facility e
SET fld_base_facility_ref_region = CASE
      WHEN jsonb_typeof(e.custom_fields -> 'FLD-BASE-facility-REF_REGION') = 'object'
        THEN NULLIF((e.custom_fields -> 'FLD-BASE-facility-REF_REGION' ->> 'id'), '')::bigint
      WHEN jsonb_typeof(e.custom_fields -> 'FLD-BASE-facility-REF_REGION') = 'number'
        THEN (e.custom_fields ->> 'FLD-BASE-facility-REF_REGION')::bigint
      WHEN (e.custom_fields ->> 'FLD-BASE-facility-REF_REGION') ~ '^[0-9]+$'
        THEN (e.custom_fields ->> 'FLD-BASE-facility-REF_REGION')::bigint
      ELSE e.fld_base_facility_ref_region
    END,
    custom_fields = e.custom_fields - 'FLD-BASE-facility-REF_REGION',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.custom_fields ? 'FLD-BASE-facility-REF_REGION'
  AND e.fld_base_facility_ref_region IS NULL;
