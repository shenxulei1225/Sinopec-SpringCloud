-- 适用手段、接入方式、网关序列号、飞机序列号离开设备类型基础字段。
-- 适用手段只留给机场、飞机、东古小型、东古大型、机器人，并保持可筛选，任务排期仍按它收执行设备。
-- 接入方式、网关序列号、飞机序列号只留给大疆机场和大疆飞机。
-- 已写在这些设备上的值先收进扩展数据，再去掉专用表上的这四列。
-- 不负责：动作上的适用手段；协议字段；挂载点；型号关联。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
  has_means boolean;
  has_access boolean;
  has_gateway boolean;
  has_aircraft boolean;
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
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'execution_means'
    ) INTO has_means;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'cloud_access_mode'
    ) INTO has_access;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'gateway_sn'
    ) INTO has_gateway;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'aircraft_sn'
    ) INTO has_aircraft;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'custom_fields'
    ) INTO has_custom;

    IF has_custom AND has_means THEN
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb)
          || jsonb_build_object('execution_means', e.execution_means)
        FROM dynamicbusiness.dynamic_model m
        WHERE e.model_id = m.id
          AND m.deleted = false
          AND m.entity_type_code = 'equipment'
          AND (
            m.code LIKE 'dji-dock-%%'
            OR m.code LIKE 'dji-m%%'
            OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
            OR (
              m.name LIKE '%%机器人%%'
              AND m.name NOT LIKE '%%无人机%%'
              AND m.code NOT LIKE 'dji-%%'
              AND m.code NOT LIKE 'donggu-%%'
              AND m.code NOT LIKE 'hik-%%'
            )
          )
          AND e.execution_means IS NOT NULL
          AND e.execution_means <> '[]'::jsonb
          AND (
            e.custom_fields->'execution_means' IS NULL
            OR e.custom_fields->'execution_means' = 'null'::jsonb
            OR e.custom_fields->'execution_means' = '[]'::jsonb
          )
      $sql$, tbl);
    END IF;

    IF has_custom AND has_access THEN
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb)
          || jsonb_build_object('cloud_access_mode', e.cloud_access_mode)
        FROM dynamicbusiness.dynamic_model m
        WHERE e.model_id = m.id
          AND m.deleted = false
          AND m.entity_type_code = 'equipment'
          AND (m.code LIKE 'dji-dock-%%' OR m.code LIKE 'dji-m%%')
          AND e.cloud_access_mode IS NOT NULL
          AND btrim(e.cloud_access_mode) <> ''
          AND COALESCE(e.custom_fields->>'cloud_access_mode', '') = ''
      $sql$, tbl);
    END IF;

    IF has_custom AND has_gateway THEN
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb)
          || jsonb_build_object('gateway_sn', e.gateway_sn)
        FROM dynamicbusiness.dynamic_model m
        WHERE e.model_id = m.id
          AND m.deleted = false
          AND m.entity_type_code = 'equipment'
          AND (m.code LIKE 'dji-dock-%%' OR m.code LIKE 'dji-m%%')
          AND e.gateway_sn IS NOT NULL
          AND btrim(e.gateway_sn) <> ''
          AND COALESCE(e.custom_fields->>'gateway_sn', '') = ''
      $sql$, tbl);
    END IF;

    IF has_custom AND has_aircraft THEN
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb)
          || jsonb_build_object('aircraft_sn', e.aircraft_sn)
        FROM dynamicbusiness.dynamic_model m
        WHERE e.model_id = m.id
          AND m.deleted = false
          AND m.entity_type_code = 'equipment'
          AND (m.code LIKE 'dji-dock-%%' OR m.code LIKE 'dji-m%%')
          AND e.aircraft_sn IS NOT NULL
          AND btrim(e.aircraft_sn) <> ''
          AND COALESCE(e.custom_fields->>'aircraft_sn', '') = ''
      $sql$, tbl);
    END IF;

    IF has_means THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I DROP COLUMN execution_means', tbl);
    END IF;
    IF has_access THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I DROP COLUMN cloud_access_mode', tbl);
    END IF;
    IF has_gateway THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I DROP COLUMN gateway_sn', tbl);
    END IF;
    IF has_aircraft THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I DROP COLUMN aircraft_sn', tbl);
    END IF;
  END LOOP;
END $$;

UPDATE dynamic_field
SET source = 'USER',
    index_strategy = 'NONE',
    updater = 'v148',
    update_time = CURRENT_TIMESTAMP
WHERE code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn')
  AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET status = 0,
    deleted = true,
    updater = 'v148',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND field_code IN ('execution_means', 'cloud_access_mode', 'gateway_sn', 'aircraft_sn')
  AND deleted = false;

UPDATE dynamic_model_field_assignment a
SET deleted = true,
    updater = 'v148',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.model_id = m.id
  AND a.deleted = false
  AND a.field_code = 'execution_means'
  AND m.entity_type_code = 'equipment'
  AND NOT (
    m.code LIKE 'dji-dock-%'
    OR m.code LIKE 'dji-m%'
    OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
    OR (
      m.name LIKE '%机器人%'
      AND m.name NOT LIKE '%无人机%'
      AND m.code NOT LIKE 'dji-%'
      AND m.code NOT LIKE 'donggu-%'
      AND m.code NOT LIKE 'hik-%'
    )
  );

UPDATE dynamic_model_field_assignment a
SET deleted = true,
    updater = 'v148',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.model_id = m.id
  AND a.deleted = false
  AND a.field_code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn')
  AND m.entity_type_code = 'equipment'
  AND NOT (m.code LIKE 'dji-dock-%' OR m.code LIKE 'dji-m%');

UPDATE dynamic_model_field_assignment a
SET field_source = 'CUSTOM',
    required = false,
    is_searchable = false,
    is_filterable = true,
    is_sortable = false,
    validation_rules = CASE
      WHEN a.validation_rules IS NULL OR btrim(a.validation_rules) = '' THEN '{"valueShape":"array"}'
      ELSE (a.validation_rules::jsonb || '{"valueShape":"array"}'::jsonb)::text
    END,
    deleted = false,
    updater = 'v148',
    update_time = CURRENT_TIMESTAMP
WHERE a.id IN (
  SELECT DISTINCT ON (a2.model_code, a2.field_code, a2.tenant_id) a2.id
  FROM dynamic_model_field_assignment a2
  JOIN dynamic_model m ON a2.model_id = m.id
  WHERE a2.field_code = 'execution_means'
    AND m.deleted = false
    AND m.entity_type_code = 'equipment'
    AND (
      m.code LIKE 'dji-dock-%'
      OR m.code LIKE 'dji-m%'
      OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
      OR (
        m.name LIKE '%机器人%'
        AND m.name NOT LIKE '%无人机%'
        AND m.code NOT LIKE 'dji-%'
        AND m.code NOT LIKE 'donggu-%'
        AND m.code NOT LIKE 'hik-%'
      )
    )
  ORDER BY a2.model_code, a2.field_code, a2.tenant_id, a2.deleted ASC, a2.id ASC
);

UPDATE dynamic_model_field_assignment a
SET field_source = 'CUSTOM',
    required = false,
    is_searchable = false,
    is_filterable = false,
    is_sortable = false,
    deleted = false,
    updater = 'v148',
    update_time = CURRENT_TIMESTAMP
WHERE a.id IN (
  SELECT DISTINCT ON (a2.model_code, a2.field_code, a2.tenant_id) a2.id
  FROM dynamic_model_field_assignment a2
  JOIN dynamic_model m ON a2.model_id = m.id
  WHERE a2.field_code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn')
    AND m.deleted = false
    AND m.entity_type_code = 'equipment'
    AND (m.code LIKE 'dji-dock-%' OR m.code LIKE 'dji-m%')
  ORDER BY a2.model_code, a2.field_code, a2.tenant_id, a2.deleted ASC, a2.id ASC
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
  20,
  'CUSTOM',
  '{"valueShape":"array"}',
  m.tenant_id,
  'v148'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'execution_means'
 AND f.deleted = false
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (
    m.code LIKE 'dji-dock-%'
    OR m.code LIKE 'dji-m%'
    OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
    OR (
      m.name LIKE '%机器人%'
      AND m.name NOT LIKE '%无人机%'
      AND m.code NOT LIKE 'dji-%'
      AND m.code NOT LIKE 'donggu-%'
      AND m.code NOT LIKE 'hik-%'
    )
  )
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_id = m.id
      AND a.field_code = f.code
      AND a.tenant_id = m.tenant_id
      AND a.deleted = false
  );

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
  CASE f.code
    WHEN 'cloud_access_mode' THEN 21
    WHEN 'gateway_sn' THEN 22
    ELSE 23
  END,
  'CUSTOM',
  m.tenant_id,
  'v148'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn')
 AND f.deleted = false
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (m.code LIKE 'dji-dock-%' OR m.code LIKE 'dji-m%')
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.model_id = m.id
      AND a.field_code = f.code
      AND a.tenant_id = m.tenant_id
      AND a.deleted = false
  );

UPDATE dynamic_entity_type
SET physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
      - 'execution_means' - 'cloud_access_mode' - 'gateway_sn' - 'aircraft_sn',
    updater = 'v148',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'equipment'
  AND deleted = false;

UPDATE dynamic_entity_type_config
SET physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
      - 'execution_means' - 'cloud_access_mode' - 'gateway_sn' - 'aircraft_sn',
    updater = 'v148',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND deleted = false;

DELETE FROM dynamic_entity_field_index
WHERE field_code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn');

DELETE FROM dynamic_entity_field_index_t1
WHERE field_code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn');

DELETE FROM dynamic_entity_field_index_t2
WHERE field_code IN ('cloud_access_mode', 'gateway_sn', 'aircraft_sn');

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'equipment'
   OR entity_type_code IN (
     SELECT code FROM dynamic_entity_type
     WHERE deleted = false
       AND entry_kind = 'DOMAIN'
       AND base_entity_type_code = 'equipment'
   );
