-- 设备检查参数是设备类型的基础字段，每个设备型号都有。
-- 已经写在扩展数据里的检查参数收进这一列，扩展数据里不再留同一份。
-- 适用手段的显示名改为设备检查方法。选项仍是人工、无人机、机器人、固定摄像机。
-- 不负责：介绍区摊开这份 JSON。设备检查配置读写这一列。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
  has_pack boolean;
  has_custom boolean;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_equipment(_t[0-9]+)?$'
  LOOP
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'host_sop_param_pack'
    ) INTO has_pack;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'custom_fields'
    ) INTO has_custom;

    IF has_pack AND has_custom THEN
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET host_sop_param_pack = e.custom_fields->'host_sop_param_pack'
        WHERE jsonb_typeof(e.custom_fields->'host_sop_param_pack'->'entries') = 'array'
          AND EXISTS (
            SELECT 1
            FROM jsonb_array_elements(e.custom_fields->'host_sop_param_pack'->'entries') entry
            WHERE jsonb_typeof(entry->'paramsByNode') = 'object'
              AND entry->'paramsByNode' <> '{}'::jsonb
          )
      $sql$, tbl);
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET custom_fields = e.custom_fields - 'host_sop_param_pack'
        WHERE e.custom_fields ? 'host_sop_param_pack'
      $sql$, tbl);
    END IF;
  END LOOP;
END $$;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  'host_sop_param_pack',
  '设备检查参数',
  'JSON',
  NULL,
  '每台设备上的检查参数。一条是一个检查项加一种设备检查方法，步骤上填的数写在这一条里。',
  'BASE',
  1,
  NULL,
  'NONE',
  NULL,
  NULL,
  'host_sop_param_pack',
  t.tenant_id,
  'v149'
FROM (
  SELECT DISTINCT tenant_id
  FROM dynamic_model
  WHERE deleted = false
    AND entity_type_code = 'equipment'
) t
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  semantic_type = EXCLUDED.semantic_type,
  index_strategy = EXCLUDED.index_strategy,
  status = 1,
  deleted = false,
  updater = 'v149',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'equipment',
  f.id,
  'host_sop_param_pack',
  '设备检查参数',
  'JSON',
  false,
  '{"version":1,"entries":[]}',
  '每台设备上的检查参数。一条是一个检查项加一种设备检查方法，步骤上填的数写在这一条里。',
  '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
  200,
  1,
  false,
  false,
  false,
  f.tenant_id,
  'v149'
FROM dynamic_field f
WHERE f.code = 'host_sop_param_pack'
  AND f.deleted = false
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  default_value = EXCLUDED.default_value,
  description = EXCLUDED.description,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  status = 1,
  is_searchable = false,
  is_filterable = false,
  is_sortable = false,
  deleted = false,
  updater = 'v149',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
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
  200,
  'BASE',
  m.tenant_id,
  'v149'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'host_sop_param_pack'
 AND f.deleted = false
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_id = m.id
      AND a.field_code = f.code
      AND a.tenant_id = m.tenant_id
      AND a.deleted = false
  );

UPDATE dynamic_field
SET name = '设备检查方法',
    updater = 'v149',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'execution_means'
  AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET field_name = '设备检查方法',
    updater = 'v149',
    update_time = CURRENT_TIMESTAMP
WHERE field_code = 'execution_means'
  AND deleted = false;

DELETE FROM model_crud_form_definition
WHERE entity_type_code IN ('equipment', 'action', 'inspection_item')
   OR entity_type_code IN (
     SELECT code FROM dynamic_entity_type
     WHERE deleted = false
       AND entry_kind = 'DOMAIN'
       AND base_entity_type_code IN ('equipment', 'action', 'inspection_item')
   );
