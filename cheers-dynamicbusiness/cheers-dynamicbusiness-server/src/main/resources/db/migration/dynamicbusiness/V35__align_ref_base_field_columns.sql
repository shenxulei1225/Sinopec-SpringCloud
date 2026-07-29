-- V35: 废除专用表 REF 捷径列 zone_id / region_id / facility_id
-- 对齐字段库编码 → 固定列名（fld_base_*）
-- 例：FLD-BASE-equipment-REF_ZONE → fld_base_equipment_ref_zone

SET search_path TO dynamicbusiness;

-- ---------------------------------------------------------------------------
-- 工具：幂等重命名列
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION dynamicbusiness._v35_rename_col(
  p_table text, p_from text, p_to text
) RETURNS void LANGUAGE plpgsql AS $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = p_table
      AND column_name = p_from
  ) AND NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = p_table
      AND column_name = p_to
  ) THEN
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I RENAME COLUMN %I TO %I',
      p_table, p_from, p_to
    );
  END IF;
END $$;

-- ---------------------------------------------------------------------------
-- 1) 设备 ent_equipment
-- ---------------------------------------------------------------------------
SELECT dynamicbusiness._v35_rename_col('ent_equipment', 'zone_id', 'fld_base_equipment_ref_zone');
SELECT dynamicbusiness._v35_rename_col('ent_equipment', 'region_id', 'fld_base_equipment_ref_region');
SELECT dynamicbusiness._v35_rename_col('ent_equipment', 'facility_id', 'fld_base_equipment_ref_facility');

COMMENT ON COLUMN dynamicbusiness.ent_equipment.fld_base_equipment_ref_zone IS
  '基础字段 FLD-BASE-equipment-REF_ZONE（所属分区）；存 ent_zone.id';
COMMENT ON COLUMN dynamicbusiness.ent_equipment.fld_base_equipment_ref_region IS
  '基础字段 FLD-BASE-equipment-REF_REGION（所属区域）；存 ent_region.id';
COMMENT ON COLUMN dynamicbusiness.ent_equipment.fld_base_equipment_ref_facility IS
  '基础字段 FLD-BASE-equipment-REF_FACILITY（所属设施）；存 ent_facility.id';

DROP INDEX IF EXISTS dynamicbusiness.idx_ent_equipment_zone;
DROP INDEX IF EXISTS dynamicbusiness.idx_ent_equipment_facility;
CREATE INDEX IF NOT EXISTS idx_ent_equipment_fld_base_equipment_ref_zone
  ON dynamicbusiness.ent_equipment (fld_base_equipment_ref_zone) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_equipment_fld_base_equipment_ref_facility
  ON dynamicbusiness.ent_equipment (fld_base_equipment_ref_facility) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_equipment_fld_base_equipment_ref_region
  ON dynamicbusiness.ent_equipment (fld_base_equipment_ref_region) WHERE deleted = false;

-- ---------------------------------------------------------------------------
-- 2) 分区 ent_zone
-- ---------------------------------------------------------------------------
SELECT dynamicbusiness._v35_rename_col('ent_zone', 'facility_id', 'fld_base_zone_ref_facility');

COMMENT ON COLUMN dynamicbusiness.ent_zone.fld_base_zone_ref_facility IS
  '基础字段 FLD-BASE-zone-REF_FACILITY（所属设施）；存 ent_facility.id';

DROP INDEX IF EXISTS dynamicbusiness.idx_ent_zone_facility;
CREATE INDEX IF NOT EXISTS idx_ent_zone_fld_base_zone_ref_facility
  ON dynamicbusiness.ent_zone (fld_base_zone_ref_facility) WHERE deleted = false;

-- ---------------------------------------------------------------------------
-- 3) 构筑物 ent_structure
-- ---------------------------------------------------------------------------
SELECT dynamicbusiness._v35_rename_col('ent_structure', 'facility_id', 'fld_base_structure_ref_facility');
SELECT dynamicbusiness._v35_rename_col('ent_structure', 'zone_id', 'fld_base_structure_ref_zone');

COMMENT ON COLUMN dynamicbusiness.ent_structure.fld_base_structure_ref_facility IS
  '基础字段 FLD-BASE-structure-REF_FACILITY（所属设施）；存 ent_facility.id';
COMMENT ON COLUMN dynamicbusiness.ent_structure.fld_base_structure_ref_zone IS
  '基础字段 FLD-BASE-structure-REF_ZONE（所属分区）；存 ent_zone.id';

DROP INDEX IF EXISTS dynamicbusiness.idx_ent_structure_facility;
DROP INDEX IF EXISTS dynamicbusiness.idx_ent_structure_zone;
CREATE INDEX IF NOT EXISTS idx_ent_structure_fld_base_structure_ref_facility
  ON dynamicbusiness.ent_structure (fld_base_structure_ref_facility) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_ent_structure_fld_base_structure_ref_zone
  ON dynamicbusiness.ent_structure (fld_base_structure_ref_zone) WHERE deleted = false;

-- ---------------------------------------------------------------------------
-- 4) 三维场景 ent_scene（无基础字段编码时仍废除捷径列名）
-- ---------------------------------------------------------------------------
SELECT dynamicbusiness._v35_rename_col('ent_scene', 'facility_id', 'fld_base_scene_ref_facility');

COMMENT ON COLUMN dynamicbusiness.ent_scene.fld_base_scene_ref_facility IS
  '绑定站场 ent_facility.id（原 facility_id；列名对齐 fld_base_* 约定）';

DROP INDEX IF EXISTS dynamicbusiness.idx_ent_scene_facility;
CREATE INDEX IF NOT EXISTS idx_ent_scene_fld_base_scene_ref_facility
  ON dynamicbusiness.ent_scene (fld_base_scene_ref_facility) WHERE deleted = false;

-- ---------------------------------------------------------------------------
-- 5) 物理列映射：去掉捷径键，写入标准字段编码 → 列名
-- ---------------------------------------------------------------------------
UPDATE dynamicbusiness.dynamic_entity_type_config
SET physical_column_mapping = (
      COALESCE(physical_column_mapping, '{}'::jsonb)
      - 'zone_id' - 'region_id' - 'facility_id'
    ) || jsonb_build_object(
      'FLD-BASE-equipment-REF_ZONE',
      jsonb_build_object('type', 'BIGINT', 'column', 'fld_base_equipment_ref_zone'),
      'FLD-BASE-equipment-REF_REGION',
      jsonb_build_object('type', 'BIGINT', 'column', 'fld_base_equipment_ref_region'),
      'FLD-BASE-equipment-REF_FACILITY',
      jsonb_build_object('type', 'BIGINT', 'column', 'fld_base_equipment_ref_facility')
    ),
    updater = 'flyway-v35',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND deleted = false;

UPDATE dynamicbusiness.dynamic_entity_type_config
SET physical_column_mapping = (
      COALESCE(physical_column_mapping, '{}'::jsonb)
      - 'facility_id' - 'zone_id' - 'region_id'
    ) || jsonb_build_object(
      'FLD-BASE-zone-REF_FACILITY',
      jsonb_build_object('type', 'BIGINT', 'column', 'fld_base_zone_ref_facility')
    ),
    updater = 'flyway-v35',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'zone'
  AND deleted = false;

UPDATE dynamicbusiness.dynamic_entity_type_config
SET physical_column_mapping = (
      COALESCE(physical_column_mapping, '{}'::jsonb)
      - 'facility_id' - 'zone_id' - 'region_id'
    ) || jsonb_build_object(
      'FLD-BASE-structure-REF_FACILITY',
      jsonb_build_object('type', 'BIGINT', 'column', 'fld_base_structure_ref_facility'),
      'FLD-BASE-structure-REF_ZONE',
      jsonb_build_object('type', 'BIGINT', 'column', 'fld_base_structure_ref_zone')
    ),
    updater = 'flyway-v35',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'structure'
  AND deleted = false;

UPDATE dynamicbusiness.dynamic_entity_type_config
SET physical_column_mapping = (
      COALESCE(physical_column_mapping, '{}'::jsonb)
      - 'region_id' - 'facility_id' - 'zone_id'
    ) || jsonb_build_object(
      'FLD-BASE-facility-REF_REGION',
      jsonb_build_object('type', 'BIGINT', 'column', 'fld_base_facility_ref_region')
    ),
    updater = 'flyway-v35',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'facility'
  AND deleted = false;

-- ---------------------------------------------------------------------------
-- 6) custom_fields 误写 REF 时回填标准列（标准列为空才填）
-- ---------------------------------------------------------------------------
UPDATE dynamicbusiness.ent_equipment e
SET fld_base_equipment_ref_zone = COALESCE(
      e.fld_base_equipment_ref_zone,
      CASE
        WHEN jsonb_typeof(e.custom_fields -> 'FLD-BASE-equipment-REF_ZONE') = 'object'
          THEN NULLIF((e.custom_fields -> 'FLD-BASE-equipment-REF_ZONE' ->> 'id'), '')::bigint
        WHEN (e.custom_fields ->> 'FLD-BASE-equipment-REF_ZONE') ~ '^[0-9]+$'
          THEN (e.custom_fields ->> 'FLD-BASE-equipment-REF_ZONE')::bigint
        ELSE NULL
      END
    ),
    custom_fields = e.custom_fields - 'FLD-BASE-equipment-REF_ZONE',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.custom_fields ? 'FLD-BASE-equipment-REF_ZONE'
  AND e.fld_base_equipment_ref_zone IS NULL;

UPDATE dynamicbusiness.ent_equipment e
SET fld_base_equipment_ref_facility = COALESCE(
      e.fld_base_equipment_ref_facility,
      CASE
        WHEN jsonb_typeof(e.custom_fields -> 'FLD-BASE-equipment-REF_FACILITY') = 'object'
          THEN NULLIF((e.custom_fields -> 'FLD-BASE-equipment-REF_FACILITY' ->> 'id'), '')::bigint
        WHEN (e.custom_fields ->> 'FLD-BASE-equipment-REF_FACILITY') ~ '^[0-9]+$'
          THEN (e.custom_fields ->> 'FLD-BASE-equipment-REF_FACILITY')::bigint
        ELSE NULL
      END
    ),
    custom_fields = e.custom_fields - 'FLD-BASE-equipment-REF_FACILITY',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.custom_fields ? 'FLD-BASE-equipment-REF_FACILITY'
  AND e.fld_base_equipment_ref_facility IS NULL;

UPDATE dynamicbusiness.ent_equipment e
SET fld_base_equipment_ref_region = COALESCE(
      e.fld_base_equipment_ref_region,
      CASE
        WHEN jsonb_typeof(e.custom_fields -> 'FLD-BASE-equipment-REF_REGION') = 'object'
          THEN NULLIF((e.custom_fields -> 'FLD-BASE-equipment-REF_REGION' ->> 'id'), '')::bigint
        WHEN (e.custom_fields ->> 'FLD-BASE-equipment-REF_REGION') ~ '^[0-9]+$'
          THEN (e.custom_fields ->> 'FLD-BASE-equipment-REF_REGION')::bigint
        ELSE NULL
      END
    ),
    custom_fields = e.custom_fields - 'FLD-BASE-equipment-REF_REGION',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.custom_fields ? 'FLD-BASE-equipment-REF_REGION'
  AND e.fld_base_equipment_ref_region IS NULL;

UPDATE dynamicbusiness.ent_zone e
SET fld_base_zone_ref_facility = COALESCE(
      e.fld_base_zone_ref_facility,
      CASE
        WHEN jsonb_typeof(e.custom_fields -> 'FLD-BASE-zone-REF_FACILITY') = 'object'
          THEN NULLIF((e.custom_fields -> 'FLD-BASE-zone-REF_FACILITY' ->> 'id'), '')::bigint
        WHEN (e.custom_fields ->> 'FLD-BASE-zone-REF_FACILITY') ~ '^[0-9]+$'
          THEN (e.custom_fields ->> 'FLD-BASE-zone-REF_FACILITY')::bigint
        ELSE NULL
      END
    ),
    custom_fields = e.custom_fields - 'FLD-BASE-zone-REF_FACILITY',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.custom_fields ? 'FLD-BASE-zone-REF_FACILITY'
  AND e.fld_base_zone_ref_facility IS NULL;

UPDATE dynamicbusiness.ent_structure e
SET fld_base_structure_ref_facility = COALESCE(
      e.fld_base_structure_ref_facility,
      CASE
        WHEN jsonb_typeof(e.custom_fields -> 'FLD-BASE-structure-REF_FACILITY') = 'object'
          THEN NULLIF((e.custom_fields -> 'FLD-BASE-structure-REF_FACILITY' ->> 'id'), '')::bigint
        WHEN (e.custom_fields ->> 'FLD-BASE-structure-REF_FACILITY') ~ '^[0-9]+$'
          THEN (e.custom_fields ->> 'FLD-BASE-structure-REF_FACILITY')::bigint
        ELSE NULL
      END
    ),
    custom_fields = e.custom_fields - 'FLD-BASE-structure-REF_FACILITY',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.custom_fields ? 'FLD-BASE-structure-REF_FACILITY'
  AND e.fld_base_structure_ref_facility IS NULL;

UPDATE dynamicbusiness.ent_structure e
SET fld_base_structure_ref_zone = COALESCE(
      e.fld_base_structure_ref_zone,
      CASE
        WHEN jsonb_typeof(e.custom_fields -> 'FLD-BASE-structure-REF_ZONE') = 'object'
          THEN NULLIF((e.custom_fields -> 'FLD-BASE-structure-REF_ZONE' ->> 'id'), '')::bigint
        WHEN (e.custom_fields ->> 'FLD-BASE-structure-REF_ZONE') ~ '^[0-9]+$'
          THEN (e.custom_fields ->> 'FLD-BASE-structure-REF_ZONE')::bigint
        ELSE NULL
      END
    ),
    custom_fields = e.custom_fields - 'FLD-BASE-structure-REF_ZONE',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.custom_fields ? 'FLD-BASE-structure-REF_ZONE'
  AND e.fld_base_structure_ref_zone IS NULL;

DROP FUNCTION IF EXISTS dynamicbusiness._v35_rename_col(text, text, text);
