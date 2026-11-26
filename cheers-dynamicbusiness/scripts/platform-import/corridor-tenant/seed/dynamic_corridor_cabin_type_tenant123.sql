-- 舱室类型是多选，写在管廊段和防火区自己的字段上。
-- 选项只来自这一条下面已经挂着的舱室型号。不写入备注。
-- 没有舱室的管廊段不编这一项。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_field (
    code, name, type, description, source, status, options,
    creator, updater, deleted, tenant_id, governance_status
)
SELECT
    'ut_cabin_types',
    '舱室类型',
    'MULTI_SELECT',
    '这一条下面有哪些舱。按已挂上的舱室型号多选，不写入备注。',
    'USER',
    1,
    '[{"label":"电力专用舱","value":"电力专用舱"},{"label":"电力信息舱","value":"电力信息舱"},{"label":"综合管道仓","value":"综合管道仓"},{"label":"综合管线舱","value":"综合管线舱"}]',
    'corridor-cabin-type',
    'corridor-cabin-type',
    false,
    123,
    'COMPANY'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_field
    WHERE tenant_id = 123 AND code = 'ut_cabin_types' AND deleted = false
);

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, creator, updater, deleted, tenant_id,
    model_code, field_code, create_visible, edit_visible, detail_visible
)
SELECT
    m.id,
    f.id,
    false,
    false,
    true,
    false,
    55,
    'CUSTOM',
    'corridor-cabin-type',
    'corridor-cabin-type',
    false,
    123,
    m.code,
    f.code,
    true,
    true,
    true
FROM dynamic_model AS m
JOIN dynamic_field AS f
  ON f.tenant_id = 123 AND f.code = 'ut_cabin_types' AND f.deleted = false
WHERE m.id IN (2313, 2314)
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment AS a
      WHERE a.tenant_id = 123
        AND a.model_id = m.id
        AND a.field_id = f.id
        AND a.deleted = false
  );

-- 基本信息里，舱室类型放在备注前面。
UPDATE dynamic_model AS m
SET field_groups_config = jsonb_set(
        m.field_groups_config::jsonb,
        '{groups,0,fields}',
        (
            SELECT jsonb_agg(item ORDER BY (item->>'sort')::int)
            FROM (
                SELECT item
                FROM jsonb_array_elements(m.field_groups_config::jsonb #> '{groups,0,fields}') AS e(item)
                WHERE (item->>'fieldId')::bigint <> f.id
                UNION ALL
                SELECT jsonb_build_object(
                    'sort', CASE WHEN m.id = 2313 THEN 25 ELSE 55 END,
                    'extra', '{}'::jsonb,
                    'fieldId', f.id
                )
            ) AS merged
        ),
        false
    )::text,
    updater = 'corridor-cabin-type',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field AS f
WHERE m.id IN (2313, 2314)
  AND f.tenant_id = 123
  AND f.code = 'ut_cabin_types'
  AND f.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM jsonb_array_elements(m.field_groups_config::jsonb #> '{groups,0,fields}') AS item
      WHERE (item->>'fieldId')::bigint = f.id
  );

-- 防火区：直接下级舱室的型号。
UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_cabin_types', s.types),
    updater = 'corridor-cabin-type',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT cab.parent_id AS entity_id,
           jsonb_agg(DISTINCT dm.name ORDER BY dm.name) AS types
    FROM ent_zone_t123 AS cab
    JOIN dynamic_model AS dm ON dm.id = cab.model_id
    WHERE cab.tenant_id = 123
      AND COALESCE(cab.deleted, false) = false
      AND cab.model_id IN (2316, 2317, 2318, 2319)
    GROUP BY cab.parent_id
) AS s
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.model_id = 2313
  AND z.id = s.entity_id
  AND COALESCE(z.custom_fields->'ut_cabin_types', 'null'::jsonb) = 'null'::jsonb;

-- 管廊段：下面各防火区舱室型号的并集。
UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_cabin_types', s.types),
    updater = 'corridor-cabin-type',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT fire.parent_id AS entity_id,
           jsonb_agg(DISTINCT dm.name ORDER BY dm.name) AS types
    FROM ent_zone_t123 AS fire
    JOIN ent_zone_t123 AS cab
      ON cab.parent_id = fire.id
     AND cab.tenant_id = 123
     AND COALESCE(cab.deleted, false) = false
     AND cab.model_id IN (2316, 2317, 2318, 2319)
    JOIN dynamic_model AS dm ON dm.id = cab.model_id
    WHERE fire.tenant_id = 123
      AND COALESCE(fire.deleted, false) = false
      AND fire.model_id = 2313
    GROUP BY fire.parent_id
) AS s
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.model_id = 2314
  AND z.id = s.entity_id
  AND COALESCE(z.custom_fields->'ut_cabin_types', 'null'::jsonb) = 'null'::jsonb;

INSERT INTO dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string,
    creator, updater, deleted, tenant_id
)
SELECT
    z.id,
    z.model_id,
    'ut_cabin_types',
    (z.custom_fields->'ut_cabin_types')::text,
    'corridor-cabin-type',
    'corridor-cabin-type',
    false,
    123
FROM ent_zone_t123 AS z
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.model_id IN (2313, 2314)
  AND z.updater = 'corridor-cabin-type'
  AND z.custom_fields ? 'ut_cabin_types'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_entity_field_index AS i
      WHERE i.tenant_id = 123
        AND i.entity_id = z.id
        AND i.field_code = 'ut_cabin_types'
        AND COALESCE(i.deleted, false) = false
  );
