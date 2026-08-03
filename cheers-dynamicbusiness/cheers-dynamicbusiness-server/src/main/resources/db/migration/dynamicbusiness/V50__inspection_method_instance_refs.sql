-- V50: 检查方法实例挂靠列（equipment_id / inspection_item_id）
-- 模板行保持空；实例行 is_template=false 时填写。编码 = 物理列名。
-- 禁止用本类 model_id 冒充设备挂靠。

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
        t.table_name = 'ent_inspection_method'
        OR t.table_name LIKE 'ent\_inspection\_method\_t%' ESCAPE '\'
      )
    ORDER BY 1
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS equipment_id BIGINT',
      tbl
    );
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS inspection_item_id BIGINT',
      tbl
    );
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.equipment_id IS %L',
      tbl,
      'equipment_id（方法实例所属设备 REF → equipment）；模板为空'
    );
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.inspection_item_id IS %L',
      tbl,
      'inspection_item_id（方法实例对应检查内容 REF → inspection_item）；模板为空'
    );
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS idx_%s_equipment_id ON dynamicbusiness.%I (equipment_id) WHERE deleted = false AND equipment_id IS NOT NULL',
      tbl,
      tbl
    );
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS idx_%s_inspection_item_id ON dynamicbusiness.%I (inspection_item_id) WHERE deleted = false AND inspection_item_id IS NOT NULL',
      tbl,
      tbl
    );
  END LOOP;
END $$;
