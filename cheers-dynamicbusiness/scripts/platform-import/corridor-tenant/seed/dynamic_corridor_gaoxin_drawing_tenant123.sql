-- 智慧管廊租户 123 · 只按施工图设计说明核对高新大道。
-- 图号 RGE541511 DDS.21-1《高新大道（光谷三路-星辰路）设计说明》。
-- 管廊段长度用第 1 页工程概况三段长度之和。防火区长度用第 3 页节点表里写明的桩号区间。
-- 不管什么：不改其他道路。不把节点表的桩号跨度写成管廊段那一条长度。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, model_code, field_code, tenant_id, creator,
  create_visible, edit_visible, detail_visible, collection_point
)
SELECT
  m.id, f.id, false, false, false, false,
  v.sort_ord, 'CUSTOM', m.code, f.code, 123, 'corridor-gaoxin-drawing',
  true, true, true, false
FROM (VALUES
  ('管廊段', 'FLD-LOC-006', 230),
  ('管廊段', 'FLD-LOC-007', 240),
  ('防火区', 'FLD-LOC-006', 230),
  ('防火区', 'FLD-LOC-007', 240)
) AS v(model_name, field_code, sort_ord)
JOIN dynamic_model m
  ON m.name = v.model_name AND m.tenant_id = 123 AND m.deleted = false
JOIN dynamic_field f
  ON f.code = v.field_code AND f.tenant_id = 123 AND f.deleted = false
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_model_field_assignment x
  WHERE x.tenant_id = 123 AND x.deleted = false
    AND x.model_id = m.id AND x.field_code = f.code
);

UPDATE dynamic_model m
SET field_groups_config = (
  SELECT jsonb_set(
    m.field_groups_config::jsonb,
    '{groups}',
    (
      SELECT jsonb_agg(
        CASE
          WHEN g->>'id' = 'ut-structure'
           AND NOT EXISTS (
             SELECT 1 FROM jsonb_array_elements(g->'fields') e
             WHERE (e->>'fieldId')::int IN (9433, 9434)
           )
          THEN jsonb_set(
            g,
            '{fields}',
            (g->'fields') || jsonb_build_array(
              jsonb_build_object('sort', 40, 'extra', '{}'::jsonb, 'fieldId', 9433),
              jsonb_build_object('sort', 50, 'extra', '{}'::jsonb, 'fieldId', 9434)
            )
          )
          ELSE g
        END
      )
      FROM jsonb_array_elements(m.field_groups_config::jsonb->'groups') g
    )
  )::text
),
    updater = 'corridor-gaoxin-drawing',
    update_time = CURRENT_TIMESTAMP
WHERE m.tenant_id = 123 AND m.deleted = false AND m.name IN ('管廊段', '防火区');

UPDATE ent_zone_t123 z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb) || jsonb_build_object(
      'ut_segment_location', '光谷三路－星辰路',
      'ut_gallery_class', '支/电力专用',
      'ut_length_m', 3290,
      'FLD-LOC-006', 'GX0+000',
      'FLD-LOC-007', 'GX3+344'
    ),
    remark = NULL,
    updater = 'corridor-gaoxin-drawing',
    update_time = CURRENT_TIMESTAMP
WHERE z.tenant_id = 123 AND z.deleted = false AND z.name = '高新大道'
  AND z.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '管廊段');

CREATE TEMP TABLE _gx_fire (
  fire_no int PRIMARY KEY,
  start_stake text NOT NULL,
  end_stake text NOT NULL,
  length_m numeric NOT NULL
);

INSERT INTO _gx_fire VALUES
  (1,  'GX0+070',  'GX0+215',  145),
  (2,  'GX0+215',  'GX0+400',  185),
  (3,  'GX0+400',  'GX0+590',  190),
  (4,  'GX0+590',  'GX0+770',  180),
  (5,  'GX0+770',  'GX0+910',  140),
  (6,  'GX0+910',  'GX1+060',  150),
  (7,  'GX1+060',  'GX1+260',  200),
  (8,  'GX1+260',  'GX1+450',  190),
  (9,  'GX1+450',  'GX1+650',  200),
  (10, 'GX1+650',  'GX1+845',  195),
  (11, 'GX1+845',  'GX2+045',  200),
  (12, 'GX2+045',  'GX2+240',  195),
  (13, 'GX2+240',  'GX2+390',  150),
  (14, 'GX2+390',  'GX2+545',  155),
  (15, 'GX2+545',  'GX2+695',  150),
  (16, 'GX2+695',  'GX2+840',  145),
  (17, 'GX2+840',  'GX3+035',  195),
  (18, 'GX3+035',  'GX3+215',  180),
  (19, 'GX3+215',  'GX3+344',  129);

UPDATE ent_zone_t123 z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb) || jsonb_build_object(
      'ut_length_m', v.length_m,
      'FLD-LOC-006', v.start_stake,
      'FLD-LOC-007', v.end_stake
    ),
    updater = 'corridor-gaoxin-drawing',
    update_time = CURRENT_TIMESTAMP
FROM _gx_fire v
JOIN ent_zone_t123 p
  ON p.tenant_id = 123 AND p.deleted = false AND p.name = '高新大道'
 AND p.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '管廊段')
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.parent_id = p.id
  AND z.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '防火区')
  AND z.name = v.fire_no::text || '#防火区';

-- 标准段三舱的净尺寸写在各舱的宽度、高度上。不写进管廊段备注。
-- 工艺说明里的高压电力专用舱对应电力专用舱，管道舱对应综合管道仓，电力电信舱对应电力信息舱。
UPDATE ent_zone_t123 z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb) || jsonb_build_object(
      'ut_width_m', v.width_m,
      'ut_height_m', v.height_m
    ),
    updater = 'corridor-gaoxin-drawing',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('电力专用舱', 2.0, 3.5),
  ('综合管道仓', 4.2, 3.5),
  ('电力信息舱', 2.5, 3.5)
) AS v(model_name, width_m, height_m)
JOIN dynamic_model m
  ON m.name = v.model_name AND m.tenant_id = 123 AND m.deleted = false
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.model_id = m.id
  AND z.parent_id IN (
    SELECT f.id
    FROM ent_zone_t123 f
    JOIN ent_zone_t123 s ON s.id = f.parent_id AND s.deleted = false AND s.tenant_id = 123
    WHERE f.tenant_id = 123 AND f.deleted = false AND s.name = '高新大道'
      AND f.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '防火区')
      AND s.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '管廊段')
  );

-- 舱室长度用它所在防火区的长度。图纸没有另给一套舱室长度。
UPDATE ent_zone_t123 z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb) || jsonb_build_object(
      'ut_length_m', (f.custom_fields->>'ut_length_m')::numeric
    ),
    updater = 'corridor-gaoxin-drawing',
    update_time = CURRENT_TIMESTAMP
FROM ent_zone_t123 f
JOIN ent_zone_t123 s
  ON s.id = f.parent_id AND s.deleted = false AND s.tenant_id = 123
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.parent_id = f.id
  AND f.tenant_id = 123 AND f.deleted = false
  AND s.name = '高新大道'
  AND f.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '防火区')
  AND s.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '管廊段')
  AND z.model_id IN (
    SELECT id FROM dynamic_model
    WHERE tenant_id = 123 AND deleted = false
      AND name IN ('电力专用舱', '综合管道仓', '电力信息舱')
  )
  AND COALESCE(f.custom_fields->>'ut_length_m', '') <> '';

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, value_number, tenant_id, creator
)
SELECT z.id, z.model_id, c.field_code,
       z.custom_fields->>c.field_code,
       (z.custom_fields->>c.field_code)::numeric,
       123, 'corridor-gaoxin-drawing'
FROM ent_zone_t123 z
CROSS JOIN (VALUES ('ut_width_m'), ('ut_height_m'), ('ut_length_m')) AS c(field_code)
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.updater = 'corridor-gaoxin-drawing'
  AND COALESCE(z.custom_fields->>c.field_code, '') <> ''
  AND z.model_id IN (
    SELECT id FROM dynamic_model
    WHERE tenant_id = 123 AND deleted = false
      AND name IN ('电力专用舱', '综合管道仓', '电力信息舱')
  )
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index i
    WHERE i.tenant_id = 123 AND i.deleted = false
      AND i.entity_id = z.id AND i.field_code = c.field_code
  );

UPDATE dynamic_entity_field_index i
SET value_number = (z.custom_fields->>'ut_length_m')::numeric,
    value_string = z.custom_fields->>'ut_length_m',
    updater = 'corridor-gaoxin-drawing',
    update_time = CURRENT_TIMESTAMP
FROM ent_zone_t123 z
WHERE i.tenant_id = 123 AND i.deleted = false AND i.field_code = 'ut_length_m'
  AND i.entity_id = z.id
  AND z.updater = 'corridor-gaoxin-drawing'
  AND COALESCE(z.custom_fields->>'ut_length_m', '') <> '';

COMMIT;
