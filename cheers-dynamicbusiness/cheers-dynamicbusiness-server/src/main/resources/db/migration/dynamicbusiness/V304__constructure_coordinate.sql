-- 构筑物增加「坐标」。权威是字段库 FLD-LOC-014，整段经纬度写在专用列 fld_loc_014。
-- 没有坐标时三维场景不编点。本迁移不从分区、尺寸或模型抄位置。
-- 不负责：构筑物和模型的关联。那是以后的导入或关联，不在这一版。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT DISTINCT dedicated_table_name
    FROM dynamic_entity_type
    WHERE deleted = false
      AND dedicated_table_name IS NOT NULL
      AND btrim(dedicated_table_name) <> ''
      AND (
        code = 'Constructure'
        OR (entry_kind = 'DOMAIN' AND base_entity_type_code = 'Constructure')
      )
    UNION
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_constructure(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS fld_loc_014 JSONB',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.fld_loc_014 IS %L',
      tbl, '构筑物坐标。字段库 FLD-LOC-014，值是经度、纬度，高度可空。空着表示三维场景不标这一点');
  END LOOP;
END $$;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'Constructure',
  f.id,
  'FLD-LOC-014',
  '坐标',
  'COORDINATE',
  false,
  NULL,
  '这条构筑物在地图上的经纬度。没有就不在三维场景里标点。',
  '{"createVisible":true,"editVisible":true,"detailVisible":true}'::jsonb,
  5,
  1,
  false,
  false,
  false,
  f.tenant_id,
  'v304'
FROM dynamic_field f
WHERE f.code = 'FLD-LOC-014'
  AND f.deleted = false
  AND f.type = 'COORDINATE'
  AND EXISTS (
    SELECT 1
    FROM dynamic_entity_type t
    WHERE t.deleted = false
      AND t.code = 'Constructure'
      AND t.tenant_id = f.tenant_id
  )
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_type_base_field bf
    WHERE bf.entity_type_code = 'Constructure'
      AND bf.field_code = 'FLD-LOC-014'
      AND bf.tenant_id = f.tenant_id
      AND bf.deleted = false
  );

UPDATE dynamic_entity_type_base_field
SET field_name = '坐标',
    data_type = 'COORDINATE',
    required = false,
    library_field_id = f.id,
    description = '这条构筑物在地图上的经纬度。没有就不在三维场景里标点。',
    status = 1,
    is_searchable = false,
    is_filterable = false,
    is_sortable = false,
    deleted = false,
    updater = 'v304',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE dynamic_entity_type_base_field.entity_type_code = 'Constructure'
  AND dynamic_entity_type_base_field.field_code = 'FLD-LOC-014'
  AND dynamic_entity_type_base_field.tenant_id = f.tenant_id
  AND f.code = 'FLD-LOC-014'
  AND f.deleted = false
  AND f.type = 'COORDINATE';

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, validation_rules, tenant_id, creator
)
SELECT
  m.id,
  m.code,
  f.id,
  f.code,
  false,
  false,
  false,
  false,
  5,
  'BASE',
  NULL,
  m.tenant_id,
  'v304'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'FLD-LOC-014'
 AND f.deleted = false
 AND f.type = 'COORDINATE'
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'Constructure'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_id = m.id
      AND a.field_code = 'FLD-LOC-014'
      AND a.tenant_id = m.tenant_id
      AND a.deleted = false
  );

UPDATE dynamic_model_field_assignment a
SET field_source = 'BASE',
    required = false,
    is_searchable = false,
    is_filterable = false,
    is_sortable = false,
    deleted = false,
    updater = 'v304',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.field_code = 'FLD-LOC-014'
  AND EXISTS (
    SELECT 1
    FROM dynamic_model m
    WHERE m.id = a.model_id
      AND m.deleted = false
      AND m.entity_type_code = 'Constructure'
  );

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'Constructure'
   OR entity_type_code IN (
     SELECT code FROM dynamic_entity_type
     WHERE deleted = false
       AND entry_kind = 'DOMAIN'
       AND base_entity_type_code = 'Constructure'
   );
