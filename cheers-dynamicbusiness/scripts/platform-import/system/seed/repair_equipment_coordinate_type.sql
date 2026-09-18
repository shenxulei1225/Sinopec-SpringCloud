-- 设备「GIS坐标 / 三维坐标」收成与字段库「坐标」同一值类型 COORDINATE。
-- 字段码仍是 coordinate_gis / coordinate_3d，不改成 FLD-LOC-014。
-- 物理列从文本改为 jsonb，存 { longitude, latitude, height? }。
-- 本机设备这两列目前为空；非 JSON 文本会在 ALTER 时报错，禁止静默清掉。
-- 幂等：已是 COORDINATE / jsonb 再跑不会改坏。

SET search_path TO dynamicbusiness;

UPDATE dynamic_field
SET type = 'COORDINATE',
    updater = 'repair-equipment-coordinate-type',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code IN (
    'coordinate_gis',
    'coordinate_3d',
    'FLD-BASE-equipment-coordinate_gis',
    'FLD-BASE-equipment-coordinate_3d'
  )
  AND type IS DISTINCT FROM 'COORDINATE';

UPDATE dynamic_entity_type_base_field
SET data_type = 'COORDINATE',
    updater = 'repair-equipment-coordinate-type',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND entity_type_code = 'equipment'
  AND field_code IN (
    'coordinate_gis',
    'coordinate_3d',
    'FLD-BASE-equipment-coordinate_gis',
    'FLD-BASE-equipment-coordinate_3d'
  )
  AND data_type IS DISTINCT FROM 'COORDINATE';

UPDATE dynamic_entity_type_config
SET physical_column_mapping =
      jsonb_set(
        jsonb_set(
          physical_column_mapping::jsonb,
          '{coordinate_gis,type}',
          '"JSONB"'
        ),
        '{coordinate_3d,type}',
        '"JSONB"'
      ),
    updater = 'repair-equipment-coordinate-type',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND entity_type_code = 'equipment'
  AND physical_column_mapping IS NOT NULL
  AND (
    physical_column_mapping::jsonb #>> '{coordinate_gis,type}' IS DISTINCT FROM 'JSONB'
    OR physical_column_mapping::jsonb #>> '{coordinate_3d,type}' IS DISTINCT FROM 'JSONB'
  );

DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT c.table_schema, c.table_name, c.column_name
    FROM information_schema.columns c
    WHERE c.table_schema = 'dynamicbusiness'
      AND c.table_name LIKE 'ent_equipment%'
      AND c.column_name IN ('coordinate_gis', 'coordinate_3d')
      AND c.data_type IN ('character varying', 'text')
  LOOP
    EXECUTE format(
      'ALTER TABLE %I.%I ALTER COLUMN %I TYPE jsonb USING NULLIF(btrim(%I::text), '''')::jsonb',
      r.table_schema, r.table_name, r.column_name, r.column_name
    );
  END LOOP;
END $$;
