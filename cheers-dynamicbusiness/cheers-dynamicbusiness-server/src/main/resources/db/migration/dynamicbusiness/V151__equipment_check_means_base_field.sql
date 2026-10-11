-- 检查方式回到设备类型基础字段。每个设备型号都有，和动作库的执行方式是同一个字段 execution_means。
-- 设备上的显示名是检查方式。动作库仍称执行方式。不新建第二份字段，不改四档选项。
-- 已收进扩展数据的勾选写回专用列，扩展数据里不再留同一份。
-- 不负责：接入方式、网关序列号、飞机序列号。这三项仍只在对应型号的扩展字段里。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
  has_means boolean;
  has_custom boolean;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_equipment(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS execution_means JSONB NOT NULL DEFAULT ''[]''::jsonb',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.execution_means IS %L',
      tbl, '设备检查方式编码数组（MANUAL/UAV/ROBOT/FIXED_CAMERA）。与动作库执行方式同一字段，可多选');

    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'custom_fields'
    ) INTO has_custom;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'execution_means'
    ) INTO has_means;

    IF has_custom AND has_means THEN
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET execution_means = e.custom_fields->'execution_means'
        WHERE jsonb_typeof(e.custom_fields->'execution_means') = 'array'
      $sql$, tbl);
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET custom_fields = e.custom_fields - 'execution_means'
        WHERE e.custom_fields ? 'execution_means'
      $sql$, tbl);
    END IF;
  END LOOP;
END $$;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  'execution_means',
  src.name,
  src.type,
  src.unit,
  src.description,
  src.source,
  1,
  src.max_relations,
  src.index_strategy,
  src.options,
  src.provider_code,
  src.semantic_type,
  t.tenant_id,
  'v151'
FROM (
  SELECT DISTINCT tenant_id
  FROM dynamic_model
  WHERE deleted = false
    AND entity_type_code = 'equipment'
) t
JOIN dynamic_field src
  ON src.code = 'execution_means'
 AND src.deleted = false
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_field f
  WHERE f.code = 'execution_means'
    AND f.tenant_id = t.tenant_id
    AND f.deleted = false
);

UPDATE dynamic_entity_type_base_field
SET field_name = '检查方式',
    data_type = 'ENUM',
    required = false,
    description = '这台设备能用哪些检查方式，可多选。和动作库的执行方式是同一个字段。任务排期只列包含当前方式的设备。',
    type_config = '{"valueShape":"array","createVisible":true,"editVisible":true,"detailVisible":true}'::jsonb,
    sort_order = 8,
    status = 1,
    is_searchable = false,
    is_filterable = true,
    is_sortable = false,
    deleted = false,
    updater = 'v151',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND field_code = 'execution_means';

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'equipment',
  f.id,
  'execution_means',
  '检查方式',
  'ENUM',
  false,
  NULL,
  '这台设备能用哪些检查方式，可多选。和动作库的执行方式是同一个字段。任务排期只列包含当前方式的设备。',
  '{"valueShape":"array","createVisible":true,"editVisible":true,"detailVisible":true}'::jsonb,
  8,
  1,
  false,
  true,
  false,
  f.tenant_id,
  'v151'
FROM dynamic_field f
WHERE f.code = 'execution_means'
  AND f.deleted = false
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_type_base_field bf
    WHERE bf.entity_type_code = 'equipment'
      AND bf.field_code = 'execution_means'
      AND bf.tenant_id = f.tenant_id
      AND bf.deleted = false
  );

UPDATE dynamic_model_field_assignment a
SET field_source = 'BASE',
    required = false,
    is_searchable = false,
    is_filterable = true,
    is_sortable = false,
    validation_rules = CASE
      WHEN a.validation_rules IS NULL OR btrim(a.validation_rules) = '' THEN '{"valueShape":"array"}'
      ELSE (a.validation_rules::jsonb || '{"valueShape":"array"}'::jsonb)::text
    END,
    deleted = false,
    updater = 'v151',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.field_code = 'execution_means'
  AND EXISTS (
    SELECT 1
    FROM dynamic_model m
    WHERE m.id = a.model_id
      AND m.deleted = false
      AND m.entity_type_code = 'equipment'
  );

UPDATE dynamic_model_field_assignment a
SET field_source = 'BASE',
    required = false,
    is_searchable = false,
    is_filterable = true,
    is_sortable = false,
    validation_rules = CASE
      WHEN a.validation_rules IS NULL OR btrim(a.validation_rules) = '' THEN '{"valueShape":"array"}'
      ELSE (a.validation_rules::jsonb || '{"valueShape":"array"}'::jsonb)::text
    END,
    deleted = false,
    updater = 'v151',
    update_time = CURRENT_TIMESTAMP
WHERE a.id IN (
  SELECT DISTINCT ON (a2.model_id, a2.field_code, a2.tenant_id) a2.id
  FROM dynamic_model_field_assignment a2
  JOIN dynamic_model m ON m.id = a2.model_id
  WHERE a2.field_code = 'execution_means'
    AND m.deleted = false
    AND m.entity_type_code = 'equipment'
    AND NOT EXISTS (
      SELECT 1
      FROM dynamic_model_field_assignment live
      WHERE live.model_id = a2.model_id
        AND live.field_code = 'execution_means'
        AND live.tenant_id = a2.tenant_id
        AND live.deleted = false
    )
  ORDER BY a2.model_id, a2.field_code, a2.tenant_id, a2.id
);

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
  true,
  false,
  8,
  'BASE',
  '{"valueShape":"array"}',
  m.tenant_id,
  'v151'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'execution_means'
 AND f.deleted = false
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_id = m.id
      AND a.field_code = 'execution_means'
      AND a.tenant_id = m.tenant_id
      AND a.deleted = false
  );

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'equipment'
   OR entity_type_code IN (
     SELECT code FROM dynamic_entity_type
     WHERE deleted = false
       AND entry_kind = 'DOMAIN'
       AND base_entity_type_code = 'equipment'
   );
