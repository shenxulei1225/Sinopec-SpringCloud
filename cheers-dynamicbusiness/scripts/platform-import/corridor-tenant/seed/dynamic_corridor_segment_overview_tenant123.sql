-- 工艺设计说明里写明的管廊段位置和全长。
-- 神墩三路：西起光谷四路西，东至光谷七路，全长 4032 米。这一全长包括 51 米监控中心连接通道。
-- 神墩五路：西起芷泉路，东至光谷六路，全长 1280 米。
-- 本工程设计范围 SW0+282.1～SW0+550、长 267.9 米，是这一包的范围，不写入管廊段长度。
-- 托月路全长 786.5 米、位置九峰变电站－光谷五路，账上已有，这里不改。
SET search_path TO dynamicbusiness;

UPDATE ent_zone_t123 AS z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
        || jsonb_build_object('ut_segment_location', v.loc, 'ut_length_m', v.len),
    updater = 'corridor-segment-overview',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
    (4, '光谷四路西－光谷七路', 4032.000),
    (5, '芷泉路－光谷六路', 1280.000)
) AS v(id, loc, len)
WHERE z.tenant_id = 123
  AND COALESCE(z.deleted, false) = false
  AND z.id = v.id
  AND z.model_id = 2314;

UPDATE dynamic_entity_field_index
SET value_number = 4032.000,
    value_string = '4032.000',
    updater = 'corridor-segment-overview',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123
  AND COALESCE(deleted, false) = false
  AND entity_id = 4
  AND field_code = 'ut_length_m';

INSERT INTO dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, deleted, tenant_id)
SELECT 5, 2314, 'ut_length_m', '1280.000', 1280.000,
       'corridor-segment-overview', 'corridor-segment-overview', false, 123
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index
    WHERE tenant_id = 123 AND COALESCE(deleted, false) = false
      AND entity_id = 5 AND field_code = 'ut_length_m'
);
