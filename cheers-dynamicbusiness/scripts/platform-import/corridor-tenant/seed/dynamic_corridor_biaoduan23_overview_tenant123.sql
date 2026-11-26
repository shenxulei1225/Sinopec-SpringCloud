-- 标段2、标段3 里各路自己的说明已经写明的位置、类型和长度。
-- 光谷五路北：北起高新大道北侧，南至流苏街，按桩号计全长 3887.5 米。10#防火区合计长度 93.8 米。
-- 23#写的是本段长度 64 米，并注明接另一设计部分，不覆盖账上 63.9 米。
-- 高科园路北：北起神墩三路，南至高新三路。说明里的长度是约 1370 米、约 1380 米，不覆盖账上 1406.9 米。
-- 1#、2#防火区的桩号区间与已有长度一致。3# 及以后区间长度对不上，不写桩号。
-- 高科园路南：高新三路至虎山东街，南段 K0+040～K1+329.4，长 1329.4 米，支线。
-- 光谷六路北：图签为高新大道至神墩五路。说明没有写出全长，不改账上 1781.5 米。
-- 高新大道、神墩三路、神墩五路、托月路不在这里改。
SET search_path TO dynamicbusiness;

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_segment_location', v.loc, 'ut_length_m', v.len),
    updater = 'corridor-biaoduan23',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (2, '高新大道北侧－流苏街', 3887.500)
) AS v(id, loc, len)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = v.id
  AND z.model_id = 2314;

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_segment_location', v.loc),
    updater = 'corridor-biaoduan23',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (8, '神墩三路－高新三路'),
    (3, '高新大道－神墩五路')
) AS v(id, loc)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = v.id
  AND z.model_id = 2314;

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object(
            'ut_segment_location', '高新三路－虎山东街',
            'ut_gallery_class', '支',
            'ut_length_m', 1329.400),
    updater = 'corridor-biaoduan23',
    update_time = CURRENT_TIMESTAMP
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = 9
  AND z.model_id = 2314;

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_length_m', 93.800),
    updater = 'corridor-biaoduan23',
    update_time = CURRENT_TIMESTAMP
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = 19
  AND z.model_id = 2313
  AND z.parent_id = 2
  AND z.name = '10#防火区';

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('FLD-LOC-006', v.a, 'FLD-LOC-007', v.b),
    updater = 'corridor-biaoduan23',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (107, 'GK0+10.1', 'GK0+200'),
    (108, 'GK0+200', 'GK0+390')
) AS v(id, a, b)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = v.id
  AND z.model_id = 2313
  AND z.parent_id = 8;

UPDATE dynamic_entity_field_index
SET value_number = 3887.500,
    value_string = '3887.500',
    updater = 'corridor-biaoduan23',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND COALESCE(deleted, false) = false
  AND entity_id = 2
  AND model_id = 2314
  AND field_code = 'ut_length_m';

UPDATE dynamic_entity_field_index
SET value_number = 93.800,
    value_string = '93.800',
    updater = 'corridor-biaoduan23',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND COALESCE(deleted, false) = false
  AND entity_id = 19
  AND model_id = 2313
  AND field_code = 'ut_length_m';

INSERT INTO dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, deleted, tenant_id)
SELECT 19, 2313, 'ut_length_m', '93.800', 93.800,
       'corridor-biaoduan23', 'corridor-biaoduan23', false, 123
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index
    WHERE tenant_id = 123 AND COALESCE(deleted, false) = false
      AND entity_id = 19 AND field_code = 'ut_length_m'
);

INSERT INTO dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, deleted, tenant_id)
SELECT 9, 2314, 'ut_length_m', '1329.400', 1329.400,
       'corridor-biaoduan23', 'corridor-biaoduan23', false, 123
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index
    WHERE tenant_id = 123 AND COALESCE(deleted, false) = false
      AND entity_id = 9 AND field_code = 'ut_length_m'
);

INSERT INTO dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string,
    creator, updater, deleted, tenant_id)
SELECT 9, 2314, 'ut_gallery_class', '支',
       'corridor-biaoduan23', 'corridor-biaoduan23', false, 123
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index
    WHERE tenant_id = 123 AND COALESCE(deleted, false) = false
      AND entity_id = 9 AND field_code = 'ut_gallery_class'
);
