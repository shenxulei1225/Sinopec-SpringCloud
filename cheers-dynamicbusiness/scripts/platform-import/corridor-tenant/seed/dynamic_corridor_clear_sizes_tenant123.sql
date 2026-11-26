-- 高科园路北建筑说明里，桩号区间与同一行米数一致的防火区。
-- 5#：GK0+796～GK0+996，相减 200 米。8#：GK1+280～GK1+420，相减 140 米。
-- 3#、4#、6#、7# 的区间与同一行米数不一致，不写桩号。
-- 虎山东街标准断面（一）（二）（三）舱室净尺寸相同。
-- 管道舱净宽 4.2 米、净高 3.5 米，写入综合管道仓。
-- 图上的电力舱没有写成电力专用舱或电力信息舱，不写入这两种舱。
SET search_path TO dynamicbusiness;

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('FLD-LOC-006', v.a, 'FLD-LOC-007', v.b),
    updater = 'corridor-clear-sizes',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (111, 'GK0+796', 'GK0+996'),
    (114, 'GK1+280', 'GK1+420')
) AS v(id, a, b)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = v.id
  AND z.model_id = 2313
  AND z.parent_id = 8
  AND COALESCE(z.custom_fields->>'FLD-LOC-006', '') = '';

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_width_m', 4.200, 'ut_height_m', 3.500),
    updater = 'corridor-clear-sizes',
    update_time = CURRENT_TIMESTAMP
FROM ent_zone_t123 f
JOIN ent_zone_t123 s
  ON s.id = f.parent_id AND s.tenant_id = 123 AND COALESCE(s.deleted, false) = false
 AND s.id = 6 AND s.name = '虎山东街'
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.model_id = 2319
  AND z.name = '综合管道仓'
  AND z.parent_id = f.id
  AND f.tenant_id = 123
  AND COALESCE(f.deleted, false) = false
  AND f.model_id = 2313
  AND COALESCE(z.custom_fields->>'ut_width_m', '') = '';

INSERT INTO dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, deleted, tenant_id)
SELECT z.id, z.model_id, c.field_code,
       to_char((z.custom_fields->>c.field_code)::numeric, 'FM999990.000'),
       (z.custom_fields->>c.field_code)::numeric,
       'corridor-clear-sizes', 'corridor-clear-sizes', false, 123
FROM ent_zone_t123 z
CROSS JOIN (VALUES ('ut_width_m'), ('ut_height_m')) AS c(field_code)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.updater = 'corridor-clear-sizes'
  AND z.name = '综合管道仓'
  AND COALESCE(z.custom_fields->>c.field_code, '') <> ''
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index i
    WHERE i.tenant_id = 123 AND COALESCE(i.deleted, false) = false
      AND i.entity_id = z.id AND i.field_code = c.field_code
  );
