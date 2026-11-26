-- 智慧管廊租户 123 · 把还能落到舱室字段上的断面数补进去。
-- 神墩三路宽高来自 智慧管廊/Excel表数据/工作簿8.csv 的舱室信息表。
-- 三段宽高相同，所以这一路下面同一种舱写同一个宽、高。
-- 不管什么：不抄表下方「示例」和「需补充」。不写备注。不新建字段。
-- 舱室长度只在防火区已经有长度时，抄到该舱的长度。没有长度的防火区，下面的舱不编长度。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE ent_zone_t123 z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb) || jsonb_build_object(
      'ut_width_m', v.width_m,
      'ut_height_m', v.height_m
    ),
    updater = 'corridor-cabin-backfill',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('电力专用舱', 2.8, 3.5),
  ('电力信息舱', 2.5, 3.5)
) AS v(model_name, width_m, height_m)
JOIN dynamic_model m
  ON m.name = v.model_name AND m.tenant_id = 123 AND m.deleted = false
JOIN ent_zone_t123 f
  ON f.tenant_id = 123 AND f.deleted = false
JOIN dynamic_model mf
  ON mf.id = f.model_id AND mf.name = '防火区'
JOIN ent_zone_t123 s
  ON s.id = f.parent_id AND s.tenant_id = 123 AND s.deleted = false AND s.name = '神墩三路'
JOIN dynamic_model ms
  ON ms.id = s.model_id AND ms.name = '管廊段'
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.model_id = m.id
  AND z.parent_id = f.id;

UPDATE ent_zone_t123 z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb) || jsonb_build_object(
      'ut_length_m', (f.custom_fields->>'ut_length_m')::numeric
    ),
    updater = 'corridor-cabin-backfill',
    update_time = CURRENT_TIMESTAMP
FROM ent_zone_t123 f
JOIN dynamic_model mf ON mf.id = f.model_id AND mf.name = '防火区'
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.parent_id = f.id
  AND f.tenant_id = 123 AND f.deleted = false
  AND COALESCE(f.custom_fields->>'ut_length_m', '') <> ''
  AND COALESCE(z.custom_fields->>'ut_length_m', '') = ''
  AND z.model_id IN (
    SELECT id FROM dynamic_model
    WHERE tenant_id = 123 AND deleted = false
      AND name IN ('电力专用舱', '电力信息舱', '综合管道仓', '综合管线舱')
  );

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, value_number, tenant_id, creator
)
SELECT z.id, z.model_id, c.field_code,
       z.custom_fields->>c.field_code,
       (z.custom_fields->>c.field_code)::numeric,
       123, 'corridor-cabin-backfill'
FROM ent_zone_t123 z
CROSS JOIN (VALUES ('ut_width_m'), ('ut_height_m'), ('ut_length_m')) AS c(field_code)
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.updater = 'corridor-cabin-backfill'
  AND COALESCE(z.custom_fields->>c.field_code, '') <> ''
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index i
    WHERE i.tenant_id = 123 AND i.deleted = false
      AND i.entity_id = z.id AND i.field_code = c.field_code
  );

COMMIT;
