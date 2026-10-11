-- 设备默认使用的协议、支持的协议是扩展字段。
-- 分给机场、飞机、东古无人机、机器人。不进设备类型基础字段，储罐和摄像机不继承。
-- 已写在无人机设备上的值先收进扩展数据，再去掉专用表上的这两列。
-- 不负责：接入方式、网关序列号、飞机序列号。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
  has_protocol boolean;
  has_supported boolean;
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
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'platform_protocol'
    ) INTO has_protocol;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'protocol_versions'
    ) INTO has_supported;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'custom_fields'
    ) INTO has_custom;

    IF has_custom AND has_protocol THEN
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb)
          || jsonb_build_object('platform_protocol', e.platform_protocol)
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
          AND e.platform_protocol IS NOT NULL
          AND btrim(e.platform_protocol) <> ''
          AND COALESCE(e.custom_fields->>'platform_protocol', '') = ''
      $sql$, tbl);
    END IF;

    IF has_custom AND has_supported THEN
      EXECUTE format($sql$
        UPDATE dynamicbusiness.%I e
        SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb)
          || jsonb_build_object('protocol_versions', e.protocol_versions)
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
          AND e.protocol_versions IS NOT NULL
          AND e.protocol_versions <> '[]'::jsonb
          AND (
            e.custom_fields->'protocol_versions' IS NULL
            OR e.custom_fields->'protocol_versions' = 'null'::jsonb
            OR e.custom_fields->'protocol_versions' = '[]'::jsonb
          )
      $sql$, tbl);
    END IF;

    IF has_protocol THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I DROP COLUMN platform_protocol', tbl);
    END IF;
    IF has_supported THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I DROP COLUMN protocol_versions', tbl);
    END IF;
  END LOOP;
END $$;

UPDATE dynamic_field
SET source = 'USER',
    index_strategy = 'NONE',
    updater = 'v143',
    update_time = CURRENT_TIMESTAMP
WHERE code IN ('platform_protocol', 'protocol_versions')
  AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET status = 0,
    deleted = true,
    updater = 'v143',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND field_code IN ('platform_protocol', 'protocol_versions')
  AND deleted = false;

UPDATE dynamic_model_field_assignment a
SET deleted = true,
    updater = 'v143',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.model_id = m.id
  AND a.deleted = false
  AND a.field_code IN ('platform_protocol', 'protocol_versions')
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
SET field_source = 'CUSTOM',
    is_searchable = false,
    is_filterable = false,
    is_sortable = false,
    deleted = false,
    updater = 'v143',
    update_time = CURRENT_TIMESTAMP
WHERE a.id IN (
  SELECT DISTINCT ON (a2.model_code, a2.field_code, a2.tenant_id) a2.id
  FROM dynamic_model_field_assignment a2
  JOIN dynamic_model m ON a2.model_id = m.id
  WHERE a2.field_code IN ('platform_protocol', 'protocol_versions')
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
  CASE WHEN f.code = 'platform_protocol' THEN 6 ELSE 7 END,
  'CUSTOM',
  m.tenant_id,
  'v143'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code IN ('platform_protocol', 'protocol_versions')
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

UPDATE dynamic_entity_type
SET physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
      - 'platform_protocol' - 'protocol_versions',
    updater = 'v143',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'equipment'
  AND deleted = false;

UPDATE dynamic_entity_type_config
SET physical_column_mapping = COALESCE(physical_column_mapping::jsonb, '{}'::jsonb)
      - 'platform_protocol' - 'protocol_versions',
    updater = 'v143',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'equipment'
  AND deleted = false;

DELETE FROM dynamic_entity_field_index
WHERE field_code IN ('platform_protocol', 'protocol_versions');

DELETE FROM dynamic_entity_field_index_t1
WHERE field_code IN ('platform_protocol', 'protocol_versions');

DELETE FROM dynamic_entity_field_index_t2
WHERE field_code IN ('platform_protocol', 'protocol_versions');
