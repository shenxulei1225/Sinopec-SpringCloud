-- 神墩三路消防平面上的防火分区长度。图上写了 SS01 到 SS23 和每条的米数。
-- 编号原本已经是 SS01 到 SS23。这里只改和图上米数不一致的 5 条。
-- 不写起终点桩号。图上的桩号是沿线标注，没有给每一条单独写成一对起终点。
SET search_path TO dynamicbusiness;

UPDATE ent_zone_t123 AS z
SET custom_fields = jsonb_set(COALESCE(z.custom_fields, '{}'::jsonb), '{ut_length_m}', to_jsonb(v.len), true),
    updater = 'corridor-sd3-fire-plan',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (45, 165.000),
    (48, 190.000),
    (49, 200.000),
    (50, 178.000),
    (66, 183.000)
) AS v(id, len)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = v.id
  AND z.custom_fields->>'F-736bb7c0d00c4e2b8f590c12d90b6fec' IN ('SS02','SS05','SS06','SS07','SS23');

UPDATE dynamic_entity_field_index AS i
SET value_number = v.len,
    value_string = to_char(v.len, 'FM999990.000'),
    updater = 'corridor-sd3-fire-plan',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (45, 165.000),
    (48, 190.000),
    (49, 200.000),
    (50, 178.000),
    (66, 183.000)
) AS v(id, len)
WHERE i.tenant_id = 123
  AND COALESCE(i.deleted, false) = false
  AND i.entity_id = v.id
  AND i.field_code = 'ut_length_m';
