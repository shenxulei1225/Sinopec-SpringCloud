-- 光谷六路南结构设计总说明：设计范围亭台街至高新五路。
-- 出图桩号 NK0+000～NK0+885.4（885.4 米）与 SK0+000～SK0+650.4（650.4 米），两段相加 1535.8 米。
-- 两段前缀不同，不写入管廊段的一对起终点。
-- 标准断面：电力舱净宽 2000 毫米、综合舱净宽 2800 毫米、净高 3500 毫米。
-- 电力舱写入电力专用舱，综合舱写入综合管线舱。外廓和标高差不写入舱室。
-- 防火区还没有长度，舱室长度不编。
SET search_path TO dynamicbusiness;

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object(
            'ut_segment_location', '亭台街－高新五路',
            'ut_length_m', 1535.800),
    updater = 'corridor-gg6s-section',
    update_time = CURRENT_TIMESTAMP
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = 390
  AND z.model_id = 2314
  AND z.name = '光谷六路南';

INSERT INTO dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, deleted, tenant_id)
SELECT 390, 2314, 'ut_length_m', '1535.800', 1535.800,
       'corridor-gg6s-section', 'corridor-gg6s-section', false, 123
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index
    WHERE tenant_id = 123 AND COALESCE(deleted, false) = false
      AND entity_id = 390 AND field_code = 'ut_length_m'
);

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_width_m', v.width_m, 'ut_height_m', v.height_m),
    updater = 'corridor-gg6s-section',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    ('电力专用舱', 2.000, 3.500),
    ('综合管线舱', 2.800, 3.500)
) AS v(model_name, width_m, height_m)
JOIN dynamic_model m
  ON m.name = v.model_name AND m.tenant_id = 123 AND COALESCE(m.deleted, false) = false
JOIN ent_zone_t123 f
  ON f.tenant_id = 123 AND COALESCE(f.deleted, false) = false AND f.model_id = 2313
JOIN ent_zone_t123 s
  ON s.id = f.parent_id AND s.tenant_id = 123 AND COALESCE(s.deleted, false) = false
 AND s.id = 390 AND s.name = '光谷六路南'
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.model_id = m.id
  AND z.parent_id = f.id
  AND COALESCE(z.custom_fields->>'ut_width_m', '') = '';

INSERT INTO dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, deleted, tenant_id)
SELECT z.id, z.model_id, c.field_code,
       to_char((z.custom_fields->>c.field_code)::numeric, 'FM999990.000'),
       (z.custom_fields->>c.field_code)::numeric,
       'corridor-gg6s-section', 'corridor-gg6s-section', false, 123
FROM ent_zone_t123 z
CROSS JOIN (VALUES ('ut_width_m'), ('ut_height_m')) AS c(field_code)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.updater = 'corridor-gg6s-section'
  AND COALESCE(z.custom_fields->>c.field_code, '') <> ''
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index i
    WHERE i.tenant_id = 123 AND COALESCE(i.deleted, false) = false
      AND i.entity_id = z.id AND i.field_code = c.field_code
  );
