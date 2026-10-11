-- 挂载点是能挂负载的设备型号上的扩展字段，不进设备类型基础字段。
-- 飞机、机场、东古无人机、机器人各记自己的多行。每一行是挂载点编号和选中的设备。
-- 摄像机型号上的负载位置撤掉。已经写在子设备上的编号先收到上级的挂载点里。
-- 不负责：机场相机的舱内舱外（dji_camera_position 仍在摄像机上）。

SET search_path TO dynamicbusiness, public;

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  'payload_mounts',
  '挂载点',
  'JSON',
  NULL,
  '这台设备上的挂载点。每一行填挂载点编号，再选一台要挂上的设备。保存时把选中设备的上级写成这台设备。',
  'USER',
  1,
  NULL,
  'NONE',
  NULL,
  NULL,
  'payload_mounts',
  m.tenant_id,
  'v144'
FROM dynamic_model m
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (
    m.code LIKE 'dji-m%'
    OR m.code LIKE 'dji-dock-%'
    OR m.code IN ('donggu-uav-small', 'donggu-uav-large')
    OR (
      m.name LIKE '%机器人%'
      AND m.name NOT LIKE '%无人机%'
      AND m.code NOT LIKE 'dji-%'
      AND m.code NOT LIKE 'donggu-%'
      AND m.code NOT LIKE 'hik-%'
    )
  )
GROUP BY m.tenant_id
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  index_strategy = EXCLUDED.index_strategy,
  status = 1,
  deleted = false,
  updater = 'v144',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, false, false, 8,
  'CUSTOM', m.tenant_id, 'v144'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'payload_mounts'
 AND f.deleted = false
 AND f.tenant_id = m.tenant_id
WHERE m.deleted = false
  AND m.entity_type_code = 'equipment'
  AND (
    m.code LIKE 'dji-m%'
    OR m.code LIKE 'dji-dock-%'
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

DO $$
DECLARE
  tbl text;
  has_custom boolean;
  has_parent boolean;
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
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'custom_fields'
    ) INTO has_custom;
    SELECT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness' AND c.table_name = tbl AND c.column_name = 'parent_id'
    ) INTO has_parent;
    IF NOT has_custom OR NOT has_parent THEN
      CONTINUE;
    END IF;

    EXECUTE format($sql$
      UPDATE dynamicbusiness.%I h
      SET custom_fields = COALESCE(h.custom_fields, '{}'::jsonb) || jsonb_build_object(
        'payload_mounts',
        (
          SELECT jsonb_agg(
            jsonb_build_object(
              'mountPoint',
              CASE
                WHEN btrim(c.custom_fields->>'dji_gimbal_index') ~ '^\d+$'
                  THEN (btrim(c.custom_fields->>'dji_gimbal_index')::int)::text
                ELSE btrim(c.custom_fields->>'dji_gimbal_index')
              END,
              'equipmentId', c.id
            )
            ORDER BY c.id
          )
          FROM dynamicbusiness.%I c
          WHERE c.parent_id = h.id
            AND c.deleted = false
            AND COALESCE(btrim(c.custom_fields->>'dji_gimbal_index'), '') <> ''
        )
      )
      WHERE h.custom_fields->'payload_mounts' IS NULL
        AND EXISTS (
          SELECT 1
          FROM dynamicbusiness.%I c
          WHERE c.parent_id = h.id
            AND c.deleted = false
            AND COALESCE(btrim(c.custom_fields->>'dji_gimbal_index'), '') <> ''
        )
    $sql$, tbl, tbl, tbl);

    EXECUTE format($sql$
      UPDATE dynamicbusiness.%I
      SET custom_fields = custom_fields - 'dji_gimbal_index'
      WHERE custom_fields ? 'dji_gimbal_index'
    $sql$, tbl);
  END LOOP;
END $$;

UPDATE dynamic_model_field_assignment
SET deleted = true,
    updater = 'v144',
    update_time = CURRENT_TIMESTAMP
WHERE field_code = 'dji_gimbal_index'
  AND deleted = false;

UPDATE dynamic_field
SET deleted = true,
    status = 0,
    updater = 'v144',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'dji_gimbal_index'
  AND deleted = false;

DELETE FROM dynamic_entity_field_index WHERE field_code = 'dji_gimbal_index';
DELETE FROM dynamic_entity_field_index_t1 WHERE field_code = 'dji_gimbal_index';
DELETE FROM dynamic_entity_field_index_t2 WHERE field_code = 'dji_gimbal_index';

UPDATE dynamic_group_relation
SET deleted = true,
    updater = 'v144',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND group_type = 'FIELD'
  AND target_code = 'dji_gimbal_index';

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT 'FIELD', g.code, f.code, g.id, f.id, 156, f.tenant_id, 'v144'
FROM dynamic_group g
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = g.tenant_id AND f.code = 'payload_mounts'
WHERE g.deleted = false AND g.group_type = 'FIELD' AND g.code = 'FG-DEV-UAV'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation r
    WHERE r.deleted = false AND r.group_type = 'FIELD'
      AND r.tenant_id = f.tenant_id
      AND r.group_code = g.code AND r.target_code = f.code
  );

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'equipment'
   OR entity_type_code IN (
     SELECT code FROM dynamic_entity_type
     WHERE deleted = false
       AND entry_kind = 'DOMAIN'
       AND base_entity_type_code = 'equipment'
   );
